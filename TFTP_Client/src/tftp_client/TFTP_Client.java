package tftp_clientserveur;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 *
 * @author cebrail
 */
public class TFTP_Client {

    /*
	 * TFTP Protocol As per RFC 1350 opcode - operation 1 - Read request (RRQ) 2
	 * - Write request (WRQ) 3 - Data (DATA) 4 - Acknowledgment (ACK) 5 - Error
	 * (ERROR)
     */
    private static final String TFTP_SERVER_IP = "127.0.0.1";
    private static final int TFTP_DEFAULT_PORT = 69;

    // TFTP OP Code
    private static final byte OP_RRQ = 1;
    private static final byte OP_DATAPACKET = 3;
    private static final byte OP_ACK = 4;
    private static final byte OP_ERROR = 5;

    private final static int TAILLE_PAQUET = 516;

    private DatagramSocket datagramSocket = null;
    private InetAddress inetAddress = null;
    private byte[] requestByteArray;
    private byte[] bufferByteArray;
    private DatagramPacket datagramPacket_sortie;
    private DatagramPacket datagramPacket_entree;

    private void get(String fileName) throws IOException {

        inetAddress = InetAddress.getByName(TFTP_SERVER_IP);
        datagramSocket = new DatagramSocket();
        requestByteArray = creerRequete(OP_RRQ, fileName, "octet");
        //creation du datagramPacket a envoyer avec la RRQ
        datagramPacket_sortie = new DatagramPacket(requestByteArray,
                requestByteArray.length, inetAddress, TFTP_DEFAULT_PORT);
        //ETAPE 1: envoi du datagramme
        datagramSocket.send(datagramPacket_sortie);

        //ETAPE 2: attend la reponse du serveur, recupere la reponse depuis un flot avec filtrage (permet d'avoir plusieurs paquets)
        ByteArrayOutputStream byteOutOS = receiveFile();

        //ETAPE 3: ecriture du fichier sur le disque local
        writeFile(byteOutOS, fileName);
    }

    private ByteArrayOutputStream receiveFile() throws IOException {
        ByteArrayOutputStream byteOutOS = new ByteArrayOutputStream();
        int noBlock = 1;

        do {
            System.out.println("Numero paquet TFTP: " + noBlock);
            noBlock++;
            //buffer de reception de donnees
            bufferByteArray = new byte[TAILLE_PAQUET];
            //datagramPacket pour recevoir les paquets du serveur
            datagramPacket_entree = new DatagramPacket(bufferByteArray,
                    bufferByteArray.length, inetAddress,
                    datagramSocket.getLocalPort());
            System.out.println("IP reception: " + inetAddress + " port: " + datagramSocket.getLocalPort());
            //ETAPE 2.1: le client attend un paquet du serveur 
            datagramSocket.receive(datagramPacket_entree);
            // Recupere le code operatoire 2 premiers octets du DATA
            byte[] opCode = {bufferByteArray[0], bufferByteArray[1]};

            if (opCode[1] == OP_ERROR) {//s'il s'agit d'un OP Error on la reporte
                rapportErreur();
            } else if (opCode[1] == OP_DATAPACKET) {//s'il s'agit d'un paquet de donnee
                // recupere le numero de block (sur 2 octets), 3 et 4 eme octets
                byte[] numeroBlock = {bufferByteArray[2], bufferByteArray[3]};

                //ecrit les donnees sur le flot de sortie
                DataOutputStream dos = new DataOutputStream(byteOutOS);
                //ecriture du contenu du paquet
                System.out.println("");
                //DATA contient le code operatoire ainsi que le numero de blocs qui occupent 4 octets
                //on ecrit donc les donnes a partir du 4eme octets les 512 octets de donnees max.
                dos.write(datagramPacket_entree.getData(), 4,
                        datagramPacket_entree.getLength()-4);
                System.out.println("ecriture de "+(datagramPacket_entree.getLength()-4)+" octets");

                //ETAPE 2.2: envoi acquitement au serveur
                sendAcknowledgment(numeroBlock);
            }

        } while (datagramPacket_entree.getLength() >= TAILLE_PAQUET);
        return byteOutOS;
    }

    //extrait l'erreur du datagramme puis l'affiche
    private void rapportErreur() {
        //OPerror contient le code operatoire (5) sur les 2 premiers octets
        String errorCode = new String(bufferByteArray, 3, 1);
        String errorText = new String(bufferByteArray, 4,
                datagramPacket_entree.getLength() - 4);
        System.err.println("Erreur: " + errorCode + " " + errorText);
    }

    //ecriture du fichier recu
    private void writeFile(ByteArrayOutputStream baoStream, String fileName) {
        try {
            //flot de sortie (fichier)
            OutputStream outputStream = new FileOutputStream(fileName);
            baoStream.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAcknowledgment(byte[] blockNumber) {

        //paquet ACK compose du code operatoire (0) et du numero du bloc a acquite
        byte[] ACK = {0, OP_ACK, blockNumber[0], blockNumber[1]};

        // Server TFTP communique en retour via un nouveau port
        // on recupere donc ce port depuis le datagramPacket 
        // puis on envoi l'acquitement
        DatagramPacket ack = new DatagramPacket(ACK, ACK.length, inetAddress,
                datagramPacket_entree.getPort());
        System.out.println("Envoi ACK sur port: " + datagramPacket_entree.getPort());
        try {
            datagramSocket.send(ack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
	 * format generique : RRQ / WRQ 
	 * 
	 * 2 bytes - Opcode; string - nom du fichier; 1 byte - 0; string - mode; 1 byte -
	 * 0;
     */
    byte[] creerRequete(byte opCode, String nomFichier, String mode) {
        
        //taille buffer= 1 (0) + 1 (opcode) + longueur de nomFichier + 1 (0) + longueur du mode + 1 (0)
        int longueurBufferRequete = 2 + nomFichier.length() + 1 + mode.length() + 1;
        //initialise le buffer contenant la requete
        byte[] rrq_buffer = new byte[longueurBufferRequete];

        //position dans le buffer afin de le remplir
        int position = 0;
        //ajoute l'octet 0
        rrq_buffer[position] = (byte)0;
        position++;
        //ajoute le code operatoire
        rrq_buffer[position] = opCode;
        position++;
        //ajoute octet par octet le nom du fichier a demander
        for (int i = 0; i < nomFichier.length(); i++) {
            rrq_buffer[position] = (byte) nomFichier.charAt(i);
            position++;
        }
        //ajoute l'octet 0
        rrq_buffer[position] = (byte)0;
        position++;
        //ajoute octet par octet le nom du mode
        for (int i = 0; i < mode.length(); i++) {
            rrq_buffer[position] = (byte) mode.charAt(i);
            position++;
        }
        //ajoute l'octet 0
        rrq_buffer[position] = (byte)0;
        return rrq_buffer;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenue !");
        do {
            System.out.println("Veuillez saisir le nom du fichier a recuperer en precisant l'extention.\nEntrez /exit pour sortir fermer le programme.");
            fileName = sc.nextLine();
            if (!fileName.equals("/exit")) {
                TFTP_Client tFTPClient = new TFTP_Client();
                tFTPClient.get(fileName);
            }

        } while (!fileName.equals("/exit"));
    }

}

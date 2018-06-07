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

    private final static int TAILLE_PAQUET = 512;

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
        datagramSocket.send(datagramPacket_sortie);

        //attend la reponse du serveur, recupere la reponse depuis un flot avec filtrage (permet d'avoir plusieurs paquets
        ByteArrayOutputStream byteOutOS = receiveFile();

        // ETAPE3: : ecriture du fichier sur le disque local
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
            // Recupere le code operatoire
            byte[] opCode = {bufferByteArray[0], bufferByteArray[1]};

            if (opCode[1] == OP_ERROR) {//s'il s'agit d'un OP Error on la reporte
                //reportError();
                System.out.println("erreur ");
            } else if (opCode[1] == OP_DATAPACKET) {//s'il s'agit d'un paquet de donnee
                // recupere le numero de block
                byte[] numeroBlock = {bufferByteArray[2], bufferByteArray[3]};

                //ecrit les donnees sur le flot de sortie
                DataOutputStream dos = new DataOutputStream(byteOutOS);
                //ecriture du contenu du paquet
                dos.write(datagramPacket_entree.getData(), 4,
                        datagramPacket_entree.getLength() - 4);

                //ETAPE 2.2: envoi acquitement au serveur
                sendAcknowledgment(numeroBlock);
            }

        } while (datagramPacket_entree.getLength() < 512);
        return byteOutOS;
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

        byte[] ACK = {0, OP_ACK, blockNumber[0], blockNumber[1]};

        // Server TFTP communique en retour via un nouveau port
        // on recupere donc ce port depuis le datagramPacket 
        // puis on envoi l'acquitement
        DatagramPacket ack = new DatagramPacket(ACK, ACK.length, inetAddress,
                datagramPacket_entree.getPort());
        try {
            datagramSocket.send(ack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
	 * RRQ / WRQ packet format
	 * 
	 * 2 bytes - Opcode; string - filename; 1 byte - 0; string - mode; 1 byte -
	 * 0;
     */
    byte[] creerRequete(byte opCode, String nomFichier, String mode) {
        //initialisation buffers
        //zeroByte stock un byte correspondant a la valeur 0 de la RRQ/WRQ
        byte[] zeroByte = new byte[1];
        zeroByte[0] = (byte) 0;
        //opCodeByte stock un byte correspondant au code operatoire
        byte[] opCodeByte = new byte[1];
        opCodeByte[0] = opCode;

        //buffer contenant la RRQ en byte
        byte[] rrq = new byte[512];

        //creer le buffer content le nom du fichier
        int longueurNomFichier = (byte) nomFichier.length();
        byte[] nomFichierByte = new byte[longueurNomFichier];
        nomFichierByte = nomFichier.getBytes();

        //creer le buffer contenant le mode
        int longueurMode = (byte) nomFichier.length();
        byte[] ModeByte = new byte[longueurMode];
        ModeByte = nomFichier.getBytes();

        //Creation de la requete en assemblant chaque buffer
        int pos = 0;
        //ajout du 0
        System.arraycopy(zeroByte, 0, rrq, pos, 1);
        pos = 1;
        //ajout du code operatoire
        System.arraycopy(opCodeByte, 0, rrq, pos, 1);
        pos = 2;
        //ajout du nom du fichier
        System.arraycopy(nomFichierByte, 0, rrq, pos, nomFichierByte.length);
        pos = pos + nomFichierByte.length;
        //ajout du 0
        System.arraycopy(zeroByte, 0, rrq, pos, 1);
        pos = pos + 1;
        //ajout du mode
        System.arraycopy(ModeByte, 0, rrq, pos, ModeByte.length);
        pos = pos + ModeByte.length;
        //ajout du 0
        System.arraycopy(zeroByte, 0, rrq, pos, 1);
        pos = pos + ModeByte.length;

        return rrq;
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

package tftp_clientserveur;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

    private final static int PACKET_SIZE = 512;

    private DatagramSocket datagramSocket = null;
    private InetAddress inetAddress = null;
    private byte[] requestByteArray;
    private byte[] bufferByteArray;
    private DatagramPacket outBoundDatagramPacket;
    private DatagramPacket inBoundDatagramPacket;

    private void get(String fileName) throws IOException {

        inetAddress = InetAddress.getByName(TFTP_SERVER_IP);
        datagramSocket = new DatagramSocket();
        requestByteArray = creerRequete(OP_RRQ, fileName, "octet");
        //creation du datagramPacket a envoyer avec la RRQ
        outBoundDatagramPacket = new DatagramPacket(requestByteArray,
                requestByteArray.length, inetAddress, TFTP_DEFAULT_PORT);
        datagramSocket.send(outBoundDatagramPacket);

        //ByteArrayOutputStream byteOutOS = receiveFile();

    }

    /*
	 * RRQ / WRQ packet format
	 * 
	 * 2 bytes - Opcode; string - filename; 1 byte - 0; string - mode; 1 byte -
	 * 0;
     */
    byte[] creerRequete(byte opCode, String nomFichier, String mode) {
        //initialisation buffer
        byte zeroByte = 0;
        int position=0;
        int longueurRRQ = 2 + nomFichier.length() + 1 + mode.length() + 1;
        byte[] rrq = new byte[longueurRRQ];
        
        int longueurNomFichier=(byte)nomFichier.length();
        byte[] nomFichierByte=new byte[longueurNomFichier];
        nomFichierByte=nomFichier.getBytes();
        
        int longueurMode=(byte)nomFichier.length();
        byte[] ModeByte=new byte[longueurMode];
        
        int pos=0;
        ModeByte=nomFichier.getBytes();
        
        System.arraycopy(zeroByte,0,rrq,pos,1);
        pos=1;
        System.arraycopy(opCode,0,rrq,pos,1);
        pos=2;
        System.arraycopy(nomFichierByte,0,rrq,pos,nomFichierByte.length);
        pos=pos+nomFichierByte.length;
        
        System.arraycopy(zeroByte,0,rrq,pos,1);
        pos=pos+1;
        
        System.arraycopy(ModeByte,0,rrq,pos,ModeByte.length);
        pos=pos+ModeByte.length;
        
        System.arraycopy(zeroByte,0,rrq,pos,1);
        pos=pos+ModeByte.length;
        
        return rrq;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "data.txt";
        TFTP_Client tFTPClient = new TFTP_Client();
        tFTPClient.get(fileName);
    }

}

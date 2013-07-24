import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

/*
 * if(this.programm instanceof SkyNet)
 * {
 *   	programm.selfdestroy();
 * 		exit;
 * }else{
 * 		programm.main();
 * }
 * 
 */


public class UDPServer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// Ports kšnnen zum gleichzeitigen lauschen von UDP und TCP genutzt werden
		DatagramSocket udpServerSocket = new DatagramSocket(31898);

		while (true)
	    {	
			// Auf Anfrage warten
			DatagramPacket paket = new DatagramPacket(new byte[55], 55);
	      	udpServerSocket.receive(paket);
	      	
	      	// hier kann ggf die Clientadresse und der Port hardcoded geaendert werden
	      	// aktuell wird automatisch die Absenderadresse und Port vom Client genutzt
			InetAddress zieladresse = paket.getAddress();
			
			int port = paket.getPort();

      		// Paketgroesse 55 Byte. Traceroute auf dem Mac nutzt 54 und Ping 56 Byte
			byte[] udpPaket = new byte[55];
			
			DatagramPacket sendungsPaket = new DatagramPacket(udpPaket, udpPaket.length, zieladresse, port);
			
			udpServerSocket.send(sendungsPaket);
	    }
	}
}

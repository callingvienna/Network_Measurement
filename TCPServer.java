import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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



public class TCPServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		ServerSocket serverSocket = null; 

	    serverSocket = new ServerSocket(31898); 

		while(true){
		   
		    Socket clientSocket = null; 
	
		    clientSocket = serverSocket.accept(); 
	
		    DataOutputStream tcpOut = new DataOutputStream(clientSocket.getOutputStream());
		    
		    DataInputStream tcpIn = new DataInputStream(clientSocket.getInputStream());
		    
		    // Paketgrš§e 55 Byte. Traceroute auf dem Mac nutzt 54 und Ping 56 Byte
			byte[] tcpPaket = new byte[55];
			
			tcpIn.read(tcpPaket);
	
			// Antwort
		    tcpOut.write(tcpPaket);
		
		    // Verbindung freigeben
		    tcpOut.close(); 
		    tcpIn.close(); 
		    clientSocket.close(); 
	   }
	}
}

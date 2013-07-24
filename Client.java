import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;


/*
 * Java Client-Server-Programm-Paar. 
 * Der Server wartet auf ankommende UDP- oder TCP-Pakete und schickt Antworten 
 * an den Sender zurueck. Der Client schickt eine Serie von n TCP- bzw. UDP-Paketen
 * (bei Programmstart auszusuchen) an den Server, misst die benoetigte Zeit 
 * zwischen Abschicken des Anfragepakets und Ankunft der Antwort und berechnet 
 * arithmetisches Mittel, Median und Standardabweichung der Messserie.
 */

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

public class Client {

	
	public static int auswahl(int art) throws IOException{
		if(art==1){
			System.out.println("Bitte waehlen Sie die Paketart aus:");
			System.out.println("(1):  UDP");
			System.out.println("(2):  TCP");
		}
		if(art==2){
			System.out.println("Bitte geben Sie die Anzahl der zu sendenen Pakete an:");
		}
		
		Scanner eingabeScanner = new Scanner(System.in);

		String eingabeTemp = null;
		
		// Userauswahl abfangen
		if(eingabeScanner.hasNextInt()){
			eingabeTemp = eingabeScanner.next();
			int eingabe = Integer.parseInt(eingabeTemp);
			return eingabe;
		}
		
		return 0;
	}
	
	public static void auswertung(int anzahlWiederholungen, long[] rtt){
		System.out.println("Analyse der RTT:");
		System.out.println("-------------------------------------------------------");
		
		// arithmetisches Mittel, Median und Standardabweichung
		double aMittel =0;
		double median =0;
		double sAbweichung =0;
		double min = 0;
		double max = 0;
		
		// arithmetisches Mittel
		for (int i = 0; i < anzahlWiederholungen; i++) {
			aMittel = aMittel + rtt[i];
		}
		aMittel = aMittel/anzahlWiederholungen;
		
		// Standardabweichung
		for(int i = 0; i < anzahlWiederholungen; i++){
			// Summe vom Quadrat aller Differenzen zwischen aMittel und den Einzelwerten
			sAbweichung = sAbweichung + (rtt[i] - aMittel) * (rtt[i] - aMittel);
		}
		// wurzel aus sAbweichung/(aMittel-1) 
	    sAbweichung = Math.sqrt(sAbweichung / (anzahlWiederholungen - 1.0));
		
	    // Median
	    // Median ist der mittlere Wert der sortierten Messreihe
	    Arrays.sort(rtt);
	    
	    // Hier nun das Handling, wenn ich keine genaue Mitte habe
	     if (anzahlWiederholungen % 2 != 0) {
	        median = rtt[anzahlWiederholungen/2];
	     } else {
	        median = (rtt[anzahlWiederholungen/2-1]+rtt[anzahlWiederholungen/2])/2;
	     }
	    min = rtt[0];
	    max = rtt[anzahlWiederholungen-1];
	     
	    System.out.println("Min.:"+min+" ms");
		System.out.println("Arithmetische Mittel: "+aMittel+" ms");
		System.out.println("Median/Zentralwert:"+median+" ms");
		System.out.println("Max.:"+max+" ms");
		System.out.println("Standardabweichung:"+sAbweichung+" ms");
		System.out.println("-------------------------------------------------------");
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		int[] auswahl = new int[2];
		// Abfrage ob UDP o. TCP
		auswahl[0] = auswahl(1);
		
		// Wenn falsche Eingabe
		while(auswahl[0] != 1 && auswahl[0] != 2){
			System.out.println("Bitte eine gueltige Zahl eingeben!");
			auswahl[0] = auswahl(1);
		}
		//System.out.println("auswahl: "+auswahl[0]);
		
		// Abfragen wieviel Pakete zu senden
		auswahl[1] = auswahl(2);
		while(auswahl[1] == 0){
			System.out.println("Bitte eine gueltige Zahl eingeben!");
			auswahl[1] = auswahl(2);
		}
		System.out.println("Es werden nun "+auswahl[1]+" "+ (auswahl[0]==1 ? "UDP" : "TCP") +" Pakete zum Server gesendet.");
		
		// Senden der UDP Pakete
		if(auswahl[0]==1){
			// Paketgroesse 55 Byte. Traceroute auf dem Mac nutzt 54 und Ping 56 Byte
			byte[] udpPaket = new byte[55];

			// Verbindung vorbereiten
			DatagramSocket udpClientSocket = new DatagramSocket();
			
			// hier kann ggf. die Adresse geaendert werden
			InetAddress zieladresse = InetAddress.getByName("5.9.6.27");
			//InetAddress.getLocalHost()
			long[] sendezeit = new long[auswahl[1]];
			long[] empfangszeit = new long[auswahl[1]];
			// Rount Trip Time ( Zeit vom absenden des Pakets bis zur Ankunft der Antwort
			long[] rtt = new long[auswahl[1]];
			
			// Alle gewuenschte Pakete losschicken
			for (int i = 0; i < auswahl[1]; i++) {
				
				// Port gewählt, da Zugriff auf Uniserver gegeben
				DatagramPacket paket = new DatagramPacket(udpPaket, udpPaket.length, zieladresse, 31898);
				
				// Aktuelle Zeit in Milisekunden
				long timestamp = new Date().getTime();  
				sendezeit[i] = timestamp;
				udpClientSocket.send(paket);
				//System.out.println("Paket "+i+" gesendet.");
				

				boolean datenEmpfangen = false;
			    while (!datenEmpfangen)
			    {
					// Auf Anfrage warten
			    		
					DatagramPacket empfangsPaket = new DatagramPacket( new byte[55], 55 );
					udpClientSocket.receive(empfangsPaket);
					  
					if (empfangsPaket.getAddress() != null) {
						//System.out.println("Antwortpaket "+i+" empfangen");
						timestamp = new Date().getTime();  
						empfangszeit[i] = timestamp;
						
						rtt[i] = empfangszeit[i] - sendezeit[i];
						//System.out.println("RTT: "+rtt[i]+" ms");
						datenEmpfangen = true;
					}
			    }
			}
			udpClientSocket.close();
			
			System.out.println(" ");
			System.out.println("Messerie ueber "+auswahl[1]+" UDP Pakete erfolgreich abgeschlossen!");
			
			auswertung(auswahl[1], rtt);
			
		}else{
			// Senden der TCP Pakete
			
			// Rount Trip Time (Zeit vom absenden des Pakets bis zur Ankunft der Antwort)
			long[] rtt = new long[auswahl[1]];
			
			// Alle gewuenschte Pakete losschicken
			for (int i = 0; i < auswahl[1]; i++) {

				// hier kann ggf die Adresse geaendert werden weg vom 127.0.0.1
				InetAddress zieladresse = InetAddress.getByName("5.9.6.27");
				
				Socket tcpSocket = new Socket(zieladresse, 31898);
				
				// Paketgroesse 55 Byte. Traceroute auf dem Mac nutzt 54 und Ping 56 Byte
				byte[] tcpPaket = new byte[55];
				
				DataOutputStream tcpOut = new DataOutputStream(tcpSocket.getOutputStream());
				DataInputStream tcpIn = new DataInputStream(tcpSocket.getInputStream());
				
				long[] sendezeit = new long[auswahl[1]];
				long[] empfangszeit = new long[auswahl[1]];
				
				// Aktuelle Zeit in Milisekunden
				long timestamp = new Date().getTime();  
				sendezeit[i] = timestamp;
				tcpOut.write(tcpPaket);
							
				tcpIn.read(tcpPaket);
				
				//System.out.println("Antwortpaket "+i+" empfangen");
				timestamp = new Date().getTime();  
				empfangszeit[i] = timestamp;
				
				rtt[i] = empfangszeit[i] - sendezeit[i];

				//System.out.print(rtt[i]+";");
				
				tcpOut.close();
				tcpIn.close();
				tcpSocket.close();
				
			}
			System.out.println(" ");
			System.out.println("Messerie ueber "+auswahl[1]+" TCP Pakete erfolgreich abgeschlossen!");			
			auswertung(auswahl[1], rtt);
		}
	}
}

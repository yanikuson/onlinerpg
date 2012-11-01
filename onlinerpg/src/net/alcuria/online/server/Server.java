package net.alcuria.online.server;
/*
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
*/
public class Server {

	public static void main(String args[]) throws Exception	{
		
		//DatagramSocket serverSocket = new DatagramSocket(9876);
//		byte[] receiveData = new byte[1024];
//		byte[] sendData = new byte[1024];
//		int received = 0;
		
		System.out.println("Starting UDP Server.");
		
		//while(true){
			
			//received++;
			// receive a response
			//DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			//serverSocket.receive(receivePacket);
			//String sentence = new String( receivePacket.getData(), 0, receivePacket.getLength());
			//System.out.println("RECEIVED PACKET #" + received + ": " + sentence);

			// send a response
			//InetAddress IPAddress = receivePacket.getAddress();
			//int port = receivePacket.getPort();
			//String capitalizedSentence = sentence.toUpperCase();
			//sendData = capitalizedSentence.getBytes();
			//DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			//serverSocket.send(sendPacket);
		//}
		
	}

}

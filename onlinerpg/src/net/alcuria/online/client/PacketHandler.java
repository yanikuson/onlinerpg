package net.alcuria.online.client;

/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
*/
public class PacketHandler {

	float counter = 0;
	
	final float sendDelay = 0.5f;
	
	public PacketHandler(){
		
	}
	
	// this is commented out so html5 compile works without errors
	
	/*
	public void send(String input) throws IOException{

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		sendData = input.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		clientSocket.send(sendPacket);
		
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
		
		//System.out.println("FROM SERVER:" + modifiedSentence);
		clientSocket.close();
		
	}
	
	public void update(float deltaTime) {
		
		
		counter += deltaTime;
		if (counter > sendDelay) {
			// attempt to send a packet when enough time has passed
			try {
				//System.out.println("Sending...");
				send("x");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			counter -= sendDelay;
		}
		
	}
	*/
	
	
}

package Laval1;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class PortListener extends Thread{
	

	// this thread is created when the server object is instantiated
	// it checks incoming messages is reserveTimeslot or checkTimeSlot

		DatagramSocket m_socket = null;
		Replica m_server;

		PortListener( Replica server){
			m_server = server;
			this.start();
		}
		
		//this function contains the logic of a thread/ is called in run()
		void Connection(){
			try {
				
				m_socket = new DatagramSocket(m_server.getListeningPort());
				while(true){
					byte[] buffer = new byte[1000];
					try {
						DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length); 
						m_socket.receive(receivePacket);
						
						String receivedRequest = new String(receivePacket.getData());
						receivedRequest = receivedRequest.trim();
						System.out.println(m_server.getReplicaName() + ": port listener received: " + receivedRequest);
						////message format for reserve/cancel 
						//messageID[0]_Sender[1]_Operation[2]_medicarID[3]_timeSlot[4]_clinicID[5]						
						String[] arguments = parseRequestMessage(receivedRequest);
											
						String reply = null;
						if(arguments != null && arguments.length > 1)
						{//receives a UDP request from a server to reserve an appointment for a given client
						
							if(arguments[2].equals("reserve") && arguments.length == 6 ){
								
								//String subString =  arguments[2] + "_" + arguments[3] + "_" + arguments[4] + "_" + arguments[5];
								if(m_server.reserve(arguments[3],arguments[4])){
									reply = "0";
								}
								else {
									reply = "1";
								}
							}							
							//message format for check
							//messageID[0]_Sender[1]_Operation[2]_timeSlot[3]
							else if(arguments[2].equals("check") && arguments.length == 4){
								if(m_server.check(arguments[3])){
									reply = "0";
								}
								else{
									reply = "1";
								}
							}
							else if(arguments[2].equals("cancel") && arguments.length == 6){
								if(m_server.cancel(arguments[3],arguments[4])){
									reply = "0";
								}
								else {
									reply = "1";
								}
								
							}
							//clinicID_timeSlote_statuce
								//reply =  arguments[0] + "_" + arguments[1] + "_" + arguments[2] + "_" + reply;
						}
					replyMessage(receivePacket.getAddress(), receivePacket.getPort(), reply);	

					}
						
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		// when the computation is done,this method creates datagram packet and send a reply back
		// using the same port and IP address 
		void replyMessage(InetAddress host, int port, String content){
			DatagramSocket socket = null;
			byte[] buffer = content.getBytes();
		
			try {
				socket = new DatagramSocket();
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length, host, port);
				socket.send(reply);

			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

		
		public void run(){
			Connection();
		}

		//request structure operation_parameter1_parameter2....
		String[] parseRequestMessage(String input){
			return input.split("[_]");
		}
		
		
		boolean requestReturnTimeSloteStatuse(String timeSlot){
			return m_server.checkAvailability(timeSlot);
		}
			



}

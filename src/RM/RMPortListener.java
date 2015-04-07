package RM;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;

import javax.swing.text.html.HTMLEditorKit.Parser;

import Util.Message;
import Util.Pair;
import Util.ResultValue;
import Util.Sequencer;




public class RMPortListener extends Thread {

		DatagramSocket m_socket = null;
		ReplicaManager m_rm;
		
		RMPortListener( ReplicaManager rm){
			m_rm = rm;
			this.start();
		}
		
			void Connection(){
		
				
				try {
					m_socket = new DatagramSocket(m_rm.getListeningPort());
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(true){
					byte[] buffer = new byte[1000];

						DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length); 
						try {
							m_socket.receive(receivePacket);
							Pair feInfo = new Pair(receivePacket.getAddress().getHostName(), receivePacket.getPort());
							String receivedRequest = new String(receivePacket.getData());
							receivedRequest = receivedRequest.trim();
							Message msg = null;
							msg = Util.Parser.parseStringIntoMessage(receivedRequest);
							msg.sequence = Sequencer.generateSequence(msg.messageID);
							if(msg.requestingObject.equals("FE")){ 
									if((msg.process.equals("reserve")|| msg.process.equals("cancel"))){
										//message format messageID_Sender_Operation_medicarID_timeSlot_clinicID
										Hashtable<String, ResultValue> computation = m_rm.forwardMessageToReplica(receivedRequest);
										String finalData = m_rm.analizeResult(computation);
										m_rm.replyMessage(finalData,feInfo);
										
										}
									else  if(msg.process.equals("change")) {	
										
										//parameterList: medicareID_apptTimeSlot_apptClinicID_desiredTimeSlot_desiredClinicID
										// medicareID = param[0] apptTimeSlot = param[1] apptClinicID = param[2] desiredTimeSlot = param[3] desiredClinicID= param[4]
										String finalData = "1";
										Message changeAptmsg = new Message();
										changeAptmsg.messageID = msg.messageID;
										changeAptmsg.requestingObject = "RM";
										changeAptmsg.process = "reserve";
										changeAptmsg.parameters.add(msg.parameters.get(0));// medicarID
										changeAptmsg.parameters.add(msg.parameters.get(3));//desiredTimeSlot
										changeAptmsg.parameters.add(msg.parameters.get(4));//desiredClinicID
										Pair rmInfo = new Pair();
										String requestMessage = Util.Parser.convertMessageIntoString(changeAptmsg);
										rmInfo.m_hostName = m_rm.m_ReplicaManagerList.get(msg.parameters.get(4)).m_hostName;
										rmInfo.m_ListenigPort = m_rm.m_ReplicaManagerList.get(msg.parameters.get(4)).m_ListenigPort;
										Hashtable<String, ResultValue> finalValue = m_rm.forwardMessageToReplicaManager(msg.parameters.get(4), requestMessage, rmInfo);
										if(finalValue.get(msg.parameters.get(4)).value.equals("0")){ // ifcase if reservation is true
											Message cancelMsg = new Message();
											cancelMsg.messageID = msg.messageID;
											cancelMsg.requestingObject = "RM";
											cancelMsg.process = "cancel";
											cancelMsg.parameters.add(msg.parameters.get(0));// medicarID
											cancelMsg.parameters.add(msg.parameters.get(1));//apptTimeSlot
											cancelMsg.parameters.add(msg.parameters.get(2));//apptClinicID
											requestMessage = Util.Parser.convertMessageIntoString(cancelMsg);
											
											Hashtable<String, ResultValue> computation = m_rm.forwardMessageToReplica(requestMessage);
											finalData = m_rm.analizeResult(computation);
										System.out.println("--- cancel message is sent");											
										}
										
										m_rm.replyMessage(finalData, feInfo);
											
										
									}
									else if(msg.process.equals("check")){}//qqqqqqqqqqqqqqqqqq  Do later
							}
							
							else if(msg.requestingObject.equals("RM")){
								if(msg.process.equals("reserve")){
									Hashtable<String, ResultValue> computation = m_rm.forwardMessageToReplica(receivedRequest);
									String finalData = m_rm.analizeResult(computation);
									m_rm.replyMessage(finalData,feInfo);

								}
								else if(msg.process.equals("check")){
									// tobe added later qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq
								}
							}
							



							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

				}
		}
	
							
		
							
		
		// when the computation is done,this method creates datagram packet and send a reply back
		// using the same port and IP address 
	/*	void replyMessage(InetAddress host, int port, String content){
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
		
		*/
		// finds server's parameters using clinicID
		
		public void run(){
			Connection();
		}


		
		
	}
		


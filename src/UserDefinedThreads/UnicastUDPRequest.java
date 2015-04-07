package UserDefinedThreads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import javax.naming.spi.DirStateFactory.Result;

import Util.Pair;
import Util.ResultValue;

public class UnicastUDPRequest extends Thread{
	
		String requestMessage = null;
		Pair replicaParams;
		CountDownLatch finishedLatch;
		Hashtable<String,ResultValue> result;
		String replica;
		
		public UnicastUDPRequest(String replica, String requestMessage, Pair replicaParams, CountDownLatch finishedLatch, Hashtable<String, ResultValue> result) {
			this.requestMessage = requestMessage;
			this.replicaParams = replicaParams;
			this.finishedLatch = finishedLatch;
			this.result = result;
			this.replica = replica;
			this.start();
		}
		
		@Override
		
		
		
		
		
		public void run() {
			ResultValue resultVal = new ResultValue();
			if((resultVal.value = startProcessing())!= null) result.put(replica, resultVal);
			if(finishedLatch != null)finishedLatch.countDown();			
		}
		
		
		String startProcessing(){
			
			String returnValue = null;
			DatagramSocket socket = null;
			
			try{
				socket = new DatagramSocket();
				byte[] sendBuffer = requestMessage .getBytes();
				InetAddress address = InetAddress.getByName(replicaParams.m_hostName);
				DatagramPacket request = new DatagramPacket(sendBuffer, sendBuffer.length, address, replicaParams.m_ListenigPort);
				socket.send(request);
				 
				byte[] receiveBuffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(receiveBuffer,receiveBuffer.length);
				socket.receive(reply);
				//return value format- initialSender = FE or RM, operation= the inital operation that was indicated in request message
				// messageID_initialSender_operation_result => result = "true" or "false"
				returnValue = new String(reply.getData()).trim();
				
			} catch (SocketException se) {
				se.printStackTrace();
			}
			catch (UnknownHostException ue) {
				ue.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
	finally {
				if(socket != null)
					socket.close();
			}
			
			return returnValue;

			
		}
		
			}

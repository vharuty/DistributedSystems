package Montreal3;
//This class contains the logic of server implementation

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author vika
 *
 */
public class Replica {
	
	boolean run = true;
	Hashtable<String, Value> m_appointmentData = new Hashtable<String, Value>();// local data for timeslote
	
	
	int m_listeningPort = 1003; // listening port of a server 3000 is just a default value
	String m_replicaName = "MONTREAL3";

	
	//when the server object is instantiated m_appointmentData has to be filled 
	private void initializeTimeslots() {
		
		SimpleDateFormat format = new SimpleDateFormat("MMddyyHHmm");
		Calendar timeSlot = Calendar.getInstance();
		timeSlot.set(Calendar.HOUR_OF_DAY, 9);
		timeSlot.set(Calendar.MINUTE, 0);
		timeSlot.set(Calendar.SECOND, 0);
		
		for(int i = 0; i < 10; i++)
		{
			// Assigns "available" status to all the time slot at the beginning  
			m_appointmentData.put(format.format(timeSlot.getTime()), new Value("available"));
			timeSlot.add(Calendar.MINUTE, 30);
		}
	}
	
	
	int getListeningPort(){
		return m_listeningPort;
	}
	
	String getReplicaName(){
		return m_replicaName;
	}
	
	// class type is used in m_appointmentData
	class Value{
		String statuse = "available";
		
		Value(String medicareID){
			statuse = medicareID;
		}
	}
	
	// contructor
	public Replica(String clinicID, String listeningPort){
		
		m_listeningPort = Integer.parseInt(listeningPort);
		System.out.println("listening port is : "  + m_listeningPort);
		m_replicaName = clinicID;
		new PortListener(this);
		initializeTimeslots();
	}
	
		
	// contains the logic of reserve method of interface
	boolean reserve(String medicareID, String timeSlot){
		boolean result = false;
		Value current = m_appointmentData.get(timeSlot);
		synchronized(current){
			if(current.statuse.equals("available")){
				current.statuse = medicareID;
				result = true;
			}
			else if(current.statuse.equals("medicareID")) result = true;
		}
		
	//	if(result)
	//		successReserveCounter.addAndGet(1);
		//else
	//		failReserveCounter.addAndGet(1);
		
		
		System.out.println(m_replicaName + ": reserve - medicare id: " + medicareID + ", timeSlot: " + timeSlot + ", result: " + result);
		return result;
	}
	
	//contains the logic of cancel method of interface
	boolean cancel(String medicareID, String timeSlot){
		boolean result = false;
		Value current = m_appointmentData.get(timeSlot);
		synchronized(current){
			if(current.statuse.equals(medicareID)){
				current.statuse = "available";
				result = true;
			}
		}
		
		System.out.println(m_replicaName + ": cancel - medicare id: " + medicareID + ", timeSlot: " + timeSlot + ", result: " + result);
		return result;
	}
	
	////contains the logic of checkTimeSlot method of interface
	boolean  check(String timeSlot){
		boolean result = false; 
		if(checkAvailability(timeSlot)) result = true;
        
        return result;
	}

	
	//checks availability of a given timrSlote in a particular clinic 
	 boolean checkAvailability(String timeSlote){
		 boolean result = false;
		 System.out.println(timeSlote + " : " + m_appointmentData.get(timeSlote).statuse);
		 if(m_appointmentData.get(timeSlote).statuse.equals("available")){
			 result = true;
		 }
		 
		 return result;
	 }
	 

	 
	 
		//statistics. Is used for testing part
		private static AtomicInteger successReserveCounter = new AtomicInteger();
		public static AtomicInteger getSuccessReserveCounter() {
			return successReserveCounter;
		}

		private static AtomicInteger failReserveCounter = new AtomicInteger();
		public static AtomicInteger getFailReserveCounter() {
			return failReserveCounter;
		}
		
		public static void main(String args[]){
			
			new Replica("Montreal1", "30011");
			
			while(true)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
}

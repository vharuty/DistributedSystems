package lasal2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;

import Util.Message;
import Util.Parser;


public class Replica implements Runnable {
	
	private String ClinicID = "LASALL2";
	private Hashtable<String, MedicareID> AppointmentTable;
	private int listenToPort = 2002;
	//public int[] ports = {6001,6002,6003};
	private String hostName = "localhost";
	Thread udpComm;
	
	public Replica(String name, int port){
		ClinicID = name;
		listenToPort = port;
		AppointmentTable = new Hashtable<String, MedicareID>();
		addTimeSlots();
		udpComm = new Thread(this);
		udpComm.start();
	}
	
	private void addTimeSlots(){
        AppointmentTable.put("1010130000", new MedicareID("0"));
        AppointmentTable.put("1010130030", new MedicareID("0"));
        AppointmentTable.put("1010130100", new MedicareID("0"));
        AppointmentTable.put("1010130130", new MedicareID("0"));
        AppointmentTable.put("1010130200", new MedicareID("0"));
        AppointmentTable.put("1010130230", new MedicareID("0"));
        AppointmentTable.put("1010130300", new MedicareID("0"));
        AppointmentTable.put("1010130330", new MedicareID("0"));
        AppointmentTable.put("1010130400", new MedicareID("0"));
        AppointmentTable.put("1010130430", new MedicareID("0"));
        AppointmentTable.put("1010130500", new MedicareID("0"));
        AppointmentTable.put("1010130530", new MedicareID("0"));
        AppointmentTable.put("1010130600", new MedicareID("0"));
        AppointmentTable.put("1010130630", new MedicareID("0"));
        AppointmentTable.put("1010130700", new MedicareID("0"));
        AppointmentTable.put("1010130730", new MedicareID("0"));
        AppointmentTable.put("1010130800", new MedicareID("0"));
        AppointmentTable.put("1010130830", new MedicareID("0"));
        AppointmentTable.put("1010130900", new MedicareID("0"));
        AppointmentTable.put("1010130930", new MedicareID("0"));
        AppointmentTable.put("1010131000", new MedicareID("0"));
        AppointmentTable.put("1010131030", new MedicareID("0"));
        AppointmentTable.put("1010131100", new MedicareID("0"));
        AppointmentTable.put("1010131130", new MedicareID("0"));
        AppointmentTable.put("1010131200", new MedicareID("0"));
        AppointmentTable.put("1010131230", new MedicareID("0"));
        AppointmentTable.put("1010131300", new MedicareID("0"));
        AppointmentTable.put("1010131330", new MedicareID("0"));
        AppointmentTable.put("1010131400", new MedicareID("0"));
        AppointmentTable.put("1010131430", new MedicareID("0"));
        AppointmentTable.put("1010131500", new MedicareID("0"));
        AppointmentTable.put("1010131530", new MedicareID("0"));
        AppointmentTable.put("1010131600", new MedicareID("0"));
        AppointmentTable.put("1010131630", new MedicareID("0"));
        AppointmentTable.put("1010131700", new MedicareID("0"));
        AppointmentTable.put("1010131730", new MedicareID("0"));
        AppointmentTable.put("1010131800", new MedicareID("0"));
        AppointmentTable.put("1010131830", new MedicareID("0"));
        AppointmentTable.put("1010131900", new MedicareID("0"));
        AppointmentTable.put("1010131930", new MedicareID("0"));
        AppointmentTable.put("1010132000", new MedicareID("0"));
        AppointmentTable.put("1010132030", new MedicareID("0"));
        AppointmentTable.put("1010132100", new MedicareID("0"));
        AppointmentTable.put("1010132130", new MedicareID("0"));
        AppointmentTable.put("1010132200", new MedicareID("0"));
        AppointmentTable.put("1010132230", new MedicareID("0"));
        AppointmentTable.put("1010132300", new MedicareID("0"));
        AppointmentTable.put("1010132330", new MedicareID("0"));
    }
	
	public int reserve(String medicareID, String timeSlot, String clinicID) {
	//	System.out.println(medicareID+","+","+timeSlot+","+clinicID);
	 	if(!AppointmentTable.containsKey(timeSlot))
            return 2;
 		MedicareID obj;
 		synchronized (obj = AppointmentTable.get(timeSlot)){ // Thread-safe block of code 
        if(obj==null){
            obj = new MedicareID(medicareID);
            return 0;
        }
        if(!"0".equalsIgnoreCase(obj.getMedicareID()))
            return 1;
        obj.setMedicareID(medicareID);
            return 0;
        }
	}
	
	public int cancel(String medicareID, String timeSlot, String clinicID) {
		if(!AppointmentTable.containsKey(timeSlot))
            return 3;
        MedicareID obj;
        synchronized(obj = AppointmentTable.get(timeSlot)){ // Thread-safe block of code
            if(obj==null)
                return 1;
            if("0".equalsIgnoreCase(obj.getMedicareID()))
                return 1;
            if(!obj.getMedicareID().equalsIgnoreCase(medicareID))
                return 2;
            obj.setMedicareID("0");
            return 0;
        }
	}

	public String check(String timeSlot) {
		if(!AppointmentTable.containsKey(timeSlot))
            return "Time Slot doesn't exist for appointment";	
		MedicareID obj;
        synchronized (obj = AppointmentTable.get(timeSlot)){
            if(obj==null || "0".equals(obj.getMedicareID()))
                return "The timeslot is available in "+ClinicID;
            return "The timeslot is NOT available in "+ClinicID;
        }
	}
	

	
	
	
	public void run(){
		DatagramSocket socket = null;
		try{
			socket = new DatagramSocket(listenToPort);			
			while(true){
				byte[] buffer = new byte[1000];
				byte[] replybuffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);
				replybuffer = getMessageReply(buffer);
				DatagramPacket reply = new DatagramPacket(replybuffer, replybuffer.length, request.getAddress(), request.getPort());
				socket.send(reply);
			}
		}
		catch(SocketException e)
		{
			System.out.println("Socket exception : " + e.getMessage() );
		} catch (IOException e) {
			System.out.println("I/O exception : " + e.getMessage() );
		}
		finally{
			if(socket!=null) socket.close();
		}
	}
	
	private byte[] getMessageReply(byte[] buffer){
		int result=-1;
		try {
			System.out.println(new String(buffer).trim());
			Message msg = Parser.parseStringIntoMessage(new String(buffer));
			if(msg.process.equalsIgnoreCase("reserve"))
				result = reserve(msg.parameters.get(0),msg.parameters.get(1), msg.parameters.get(2));
			else
			if(msg.process.equalsIgnoreCase("cancel"))
				result = cancel(msg.parameters.get(0),msg.parameters.get(1), msg.parameters.get(2));
			return Integer.toString(result).getBytes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	class MedicareID{
		String param;
		MedicareID(String param){
			this.param = param;
		}
		
		String getMedicareID(){
			return param;
		};
		void setMedicareID(String param){
			this.param = param;
			
		}
	}
	
	public static void send(String str){
		DatagramSocket socket = null;
	
	byte[] buffer = str.getBytes();

	try {
		socket = new DatagramSocket();
		InetAddress address = InetAddress.getLocalHost();

		DatagramPacket reply = new DatagramPacket(buffer, buffer.length, address,10020);
		socket.send(reply);

	} catch (SocketException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Replica("Lasal2", 10020);
		send("10_FE_reserve_absd11_1010130000_Lasal2");
		
		System.out.println(java.util.UUID.randomUUID());
	
	//	send("10_FE_cancel_absd11_0406131100_Lasal2");

	//	send("10_FE_change_absd11_0406131100_Montreal_0406131100_Lasal2");////MMDDYYHHMM
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

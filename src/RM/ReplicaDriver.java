package RM;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Server.Replica;

public class ReplicaDriver {
	public static void send(String str){
		DatagramSocket socket = null;
	
	byte[] buffer = str.getBytes();

	try {
		socket = new DatagramSocket();
		InetAddress address = InetAddress.getLocalHost();

		DatagramPacket reply = new DatagramPacket(buffer, buffer.length, address,30010);
		socket.send(reply);

	} catch (SocketException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

	
	public static void main(String args[]){
		//reserve=>  messageID_Sender_Operation_medicarID_timeSlot_clinicID
		/*
		new RM.ReplicaManager();
		
		new Replica("Montreal1", "30011");
		new Replica("Montreal2", "30012");
		new Replica("Montreal3", "30013");
		
		new RMLaval.ReplicaManager();
		
		new Replica("Laval1", "30031");
		new Replica("Laval2", "30032");
		new Replica("Laval3", "30033");
		
		send("10_FE_reserve_absd11_0406131100_Montreal"); //MMDDYYHHMM
		
		
		//parameterList: medicareID_apptTimeSlot_apptClinicID_desiredTimeSlot_desiredClinicID
		// medicareID = param[0] apptTimeSlot = param[1] apptClinicID = param[2] desiredTimeSlot = param[3] desiredClinicID= param[4]
		
		send("10_FE_change_absd11_0406131100_Montreal_0406131100_Laval");////MMDDYYHHMM
		send("10_FE_cancel_absd11_0406131100_Laval");
		*/
		
		new RMMontreal.ReplicaManager();
		
		new Montreal1.Replica("MONTREAL1", "1001");
		new Montreal2.Replica("MONTREAL2", 1002	);
		new Montreal3.Replica("MONTREAL3", "1003");
		
		
		new RMLasal.ReplicaManager();
		
		new lasal1.Replica("LASALL1", "2001");
		new lasal2.Replica("LASALL2", 2002);
		new Lasal3.Replica("LASALL3", "2003");
		
		
		new RMLaval.ReplicaManager();
		
		new Laval1.Replica("LAVAL1", "3001");
		new Laval2.Replica("LAVAL2", 3002	);
		new Laval3.Replica("LAVAL3", "3003");
		
		
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

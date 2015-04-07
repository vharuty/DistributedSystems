package RM;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import Server.Replica;
import UserDefinedThreads.UnicastUDPRequest;
import Util.Pair;
import Util.ResultValue;

public class ReplicaManager implements RMInterface {
	
	int m_listeningPort = 1000;
	String m_RMName = "MONTREAL"; //standard: replica name is the same as clinicID received from client
	Hashtable<String, Pair> m_Replica = new Hashtable<String, Pair>(); // string parameter is replica name (ex. Montreal1, Montreal2, Montreal3)
	Hashtable<String, Pair> m_ReplicaManagerList =  new Hashtable<String, Pair>(); //strijng parameter is Replica managers names (Montreal, Laval, Lasal)
	
	ReplicaManager(){
		RMPortListener portLitener = new RMPortListener(this);
		m_Replica.put("Montreal1", new Pair("Localhost", 1001));
		m_Replica.put("Montreal2", new Pair("Localhost", 1002));
		m_Replica.put("Montreal3", new Pair("Localhost", 1003));
		
		m_ReplicaManagerList.put("Laval", new Pair("Localhost",30030));
		m_ReplicaManagerList.put("Lasal", new Pair("Localhost",30020));
	}
	
	int getListeningPort(){
		return m_listeningPort;
	}
	
	

	@Override
	public Hashtable<String, ResultValue> forwardMessageToReplica(String requestMessage) {
		CountDownLatch latch = new CountDownLatch(m_Replica.size());
		
		Set<String> replicas = m_Replica.keySet();
        Iterator<String> it = replicas.iterator();

        Hashtable<String, ResultValue> result = new Hashtable<String, ResultValue>();// String parameter represents replica name (ex. Montreal1)qqqqqqqq
        
        while(it.hasNext()){
        	String replica = it.next();
        	Pair replicaParams = m_Replica.get(replica);
        	result.put(replica, new ResultValue()); //qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq how to use result
        	UnicastUDPRequest thread = new UnicastUDPRequest(replica, requestMessage, replicaParams,latch, result );
		}
		
        try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        return result;		
	}

	//qqqqqqqqqqqqqqqqqqqq modify later send the entire message
	@Override
	public void replyMessage(String str, Pair feInfo) {
		DatagramSocket socket = null;
		byte[] buffer = str.getBytes();
	
		try {
			socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(feInfo.m_hostName);

			DatagramPacket reply = new DatagramPacket(buffer, buffer.length, address, feInfo.m_ListenigPort);
			socket.send(reply);

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	
		
	}
	
	Hashtable<String, ResultValue> forwardMessageToReplicaManager(String replicaManager, String requestMessage, Pair rmInfo){
		Hashtable<String, ResultValue> result = new Hashtable<String, ResultValue>();
       	CountDownLatch latch = new CountDownLatch(1);
		UnicastUDPRequest thread = new UnicastUDPRequest(replicaManager, requestMessage, rmInfo, latch, result );
        try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       	return result;
	}
	
	
	
	String analizeResult(Hashtable<String, ResultValue> data){
		String returnValue = null;
		Hashtable<String, ResultValue> stats = new Hashtable<String, ResultValue>();
		
		Set<String> keys = data.keySet();
		Iterator<String> it = keys.iterator();
        while(it.hasNext()){
        	String key = it.next();
        	ResultValue val = data.get(key);
        	if(!stats.containsKey(val.value))
        	{
        		ResultValue v = new ResultValue();
        		v.count = 1;
        		stats.put(val.value, v);
        	}
        	else
        	{
        		stats.get(val.value).count++;
        	}
		}

        keys = data.keySet();
		it = keys.iterator();
        while(it.hasNext()){
        	String key = it.next();
        	ResultValue val = data.get(key);
        	val.count = stats.get(val.value).count;
        }
        
        keys = data.keySet();
		it = keys.iterator();
        while(it.hasNext()){
        	String key = it.next();
        	if(data.get(key).count>=2)
        		returnValue =  data.get(key).value;
        	else if(data.get(key).count<=1) System.out.println("Wrong result for replica:" + key );//qqqqqqqqqqqqqqqqqqqqqqqqqqqqqq
        	
        }

        return returnValue;
	}
	
	
	public static void main(String args[]){
		new ReplicaManager();
		
		new Replica("Montreal1", "1001");
		new Replica("Montreal2", "1002");
		new Replica("Montreal3", "1003");
		
		while(true)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

		
	

}

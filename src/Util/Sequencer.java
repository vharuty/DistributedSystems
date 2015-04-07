package Util;

import java.util.Hashtable;



public class Sequencer {
	private static Hashtable<String, String> records  = new Hashtable<String, String>();
	private static int sequence = 0;
	
	static public String generateSequence(String MessageID){
		
		if(records.containsKey(MessageID))
			return records.get(MessageID);
		else{
			String seq = Integer.toString(++sequence);
			records.put(MessageID,seq);
			return seq;
		}
	}	
}

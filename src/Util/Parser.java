package Util;

import Util.Message;

public class Parser {
	
	public static  Message  parseStringIntoMessage(String input) throws Exception{
		Message msg = new Message();
		String[] partitions =   input.split("[_]");
		if(partitions.length < 3) 
			throw new Exception(" Wromg Message format");
			msg.messageID = partitions[0];
		msg.requestingObject = partitions[1];
		msg.process = partitions[2];
		for(int i = 3; i<partitions.length; i++)
			msg.parameters.add(partitions[i]);
		
		if(msg.messageID==null || msg.requestingObject == null || msg.requestingObject == null || msg.process == null) throw new Exception("Wromg Message format");
		
		return msg;
	}
	
	public static String convertMessageIntoString(Message msg) throws Exception{
		if(msg == null || msg.messageID==null || msg.requestingObject == null || msg.process == null) throw new Exception("Invalid Message: null ");
		String str = "";
		str = msg.messageID + "_" + msg.requestingObject + "_" + msg.process;
		
		for(int i=0; i < msg.parameters.size(); i++){
			str = str + "_" + msg.parameters.get(i);
		}
		return str;
		
	}

}

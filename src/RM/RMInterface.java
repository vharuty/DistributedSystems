package RM;

import java.util.Hashtable;

import Util.Pair;
import Util.ResultValue;

public interface  RMInterface {
	
	public Hashtable<String, ResultValue> forwardMessageToReplica(String requestMessage);
	public void replyMessage(String str, Pair feInfo);

}

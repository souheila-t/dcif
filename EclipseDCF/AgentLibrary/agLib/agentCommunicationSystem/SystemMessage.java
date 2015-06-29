package agLib.agentCommunicationSystem;
/*
* Ajouter un Message
*  1. ajouter son code
*  2. indiquer sa taille (avec argument) dans getTaille
*  3. indiquer le texte correspondant dans le toString
*/

public class SystemMessage extends MessageBase<Object> implements SystemMessageTypes{
		
	protected int code;
	protected CanalComm sender;
	
	public SystemMessage(int codeMessage, CanalComm sender) {
		super(codeMessage,null,sender);
	}
	public SystemMessage(){
		super();
	}
	
	public void finalize(){
		sender=null;
	}
	public Object getArgument() {
		return null;
	}

	public int getSize(){
		//int n=10; // type message codable sur 4bits + sender sur 6 bits(pour moins de 128 ag) 
		return 0;
	}
	
	public String toString(){
		String result;
		
		result = new String("{");
		switch(getCode()){

		case SYS_NEW_NEIGHBOUR:
		    result =  result.concat("New neighbour");
		    break;
		case SYS_DISCONNECT:
		    result =  result.concat("Disconnect");
		    break;
		case SYS_RECONNECT:
		    result =  result.concat("Reconnect");
		    break;
		case SYS_CRASH:
		    result =  result.concat("Crash");
		    break;
		case SYS_STARTED:
		    result =  result.concat("Started");
		    break;
		case SYS_FINISH:
		    result =  result.concat("Finish");
		    break;
		case SYS_TIMEUP:
		    result =  result.concat("Time up!");
		    break;
		case SYS_START:
		    result =  result.concat("Start");
		    break;
		case SYS_LAUNCH:
		    result =  result.concat("Launch");
		    break;
		default:
	    result = result.concat("Unknown Message");
		break;
		}     
		result = result.concat("}");
		
		return result;
	}
	
	
}

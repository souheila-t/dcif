package agLib.agentCommunicationSystem.protocols.consensus;

import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.MessageBase;

public class GlobalWeightedMessage extends MessageBase<Double> implements GlobalWeightedMessageTypes{

	public GlobalWeightedMessage(int code, Double argument, CanalComm sender) {
		super(code, argument, sender);
	}
	public GlobalWeightedMessage(int code, Double argument) {
		super(code, argument);
	}
	public GlobalWeightedMessage(){
		super();
	}
	
	@Override
	public int getSize() {
		int s=10;
		if (argument!=null){
			s=s+8;
		}
		return s;
	}

	@Override
	public String toString() {
		String res="{";
		switch(getCode()){
		case GWM_WEIGHTEDREQUEST:
			res+="Request";
		break;			
		case GWM_ACCEPTREQUEST:
			res+="Accept Request";
		break;			
		case GWM_ENDLOCALCONV:
			res+="End of local conversation";
		break;			
		default:
			res+="Unknown Message";
		}
		if (argument!=null){
			res+="("+argument.toString()+")";
		}
		res+="}";
		return res;
	}

}

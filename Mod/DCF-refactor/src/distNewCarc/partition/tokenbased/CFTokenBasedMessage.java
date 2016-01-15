package distNewCarc.partition.tokenbased;

import agLib.agentCommunicationSystem.MessageBase;

public class CFTokenBasedMessage extends MessageBase<TokenMessageArgument> implements CFTokenBasedMessageTypes{

	public CFTokenBasedMessage(int code, TokenMessageArgument argument){
		super(code, argument);
	}

	@Override
	public int getSize() {		
		return argument.getSentCl().size();
	}

	@Override
	public String toString() {
		switch(code){
		case PBM_SEND_CLAUSES:
			return "Send Clauses: "+argument.toString();
		}
		return "Unknown message";
	}

}

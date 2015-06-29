package mars.bilateralProtocol;

import java.util.Collection;

import org.nabelab.solar.parser.ParseException;

import tools.Arguments;


import agentCommunicationSystem.CanalComm;
import agentCommunicationSystem.MessageBase;

public class DiagMessage extends MessageBase<Arguments> implements DiagMessageTypes {

	public DiagMessage(){
		super();
	}
	public DiagMessage(int code){
		super(code, null);
	}
	public DiagMessage(int code, Collection<? extends Object> argument){
		super(code, new Arguments(argument));
	}
	public DiagMessage(int code, String argument) throws ParseException{
		super(code, Arguments.parse(argument));
	}
	
	public DiagMessage(int code, Arguments argument, CanalComm sender){
		super(code, argument,sender);
	}
	
	public DiagMessage(int code, Arguments argument){
		super(code,argument);
	}

	public int getSize() {
		//in octets
		int s=2;// sender & message type
		if (argument!=null){
			for (String cl:argument){
				s+=cl.length();
				/*for (Literal lit:cl.getLiterals()){
					s++;
					s+=lit.getTerm().size(false)*8;
				}*/
			}
		}
		return s;
	}
	
	public String toString(){
		String res="{";
		switch(getCode()){
			case DGM_PROPOSE:
				res+="Propose";
			break;
			case DGM_PROPOSEAGAIN:
				res+="Propose again";
			break;
			case DGM_ACCEPT:
				res+="Accept";
			break;
			case DGM_WITHDRAW:
				res+="Withdraw";
			break;
			case DGM_HASBETTERHYP:
				res+="Has Better Hyp";
			break;
			case DGM_DENY:
				res+="Deny";
			break;
			case DGM_COUNTEREXAMPLE:
				res+="Counter-example";
			break;
			case DGM_COUNTEREXAMPLE_COH:
				res+="Coherence counter-example";
			break;
			case DGM_COUNTEREXAMPLE_COMP:
				res+="Completeness counter-example";
			break;
			case DGM_CHALLENGE:
				res+="Challenge";
			break;
			case DGM_ARGUE:
				res+="Argue";
			break;
			case DGM_ASK:
				res+="Ask";
			break;
			case DGM_INFORM:
				res+="Inform";
			break;
			case DGM_ACK:
				res+="Acknowledge";
			break;	
			case DGM_ACK_INFORM:
	 			res+="Acknowledge inform";
	 		break;
			case DGM_CHCK_CTXT:
				res+="Check Context";
			break;
			case DGM_CHCK_CTXT_FRST:
				res+="Check Context (first)";
			break;
			case DGM_CONFIRM_CTXT:
				res+="Confirm context";
			break;
			case DGM_ACK_CONFIRM:
				res+="Acknowledge confirmation";
			break;	
			case DGM_WITHDRAW_INCOMPLETE:
				res+="Withdraw incomplete";
			break;	
			case DGM_ASK_OTHERHYP:
				res+="Ask other hypothesis";
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

/**

 * 

 */

package communication;

import genLib.tools.Arguments;

import java.util.Collection;

import org.nabelab.solar.parser.ParseException;

import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.MessageBase;


/**
 * @author Viel Charlotte
 *
 */
public class CooperativeMessage extends MessageBase<Arguments>
		implements CooperativeMessageTypes {

	public CooperativeMessage(){
		super();
	}
	public CooperativeMessage(int code){
		super(code, null);
	}
	public CooperativeMessage(int code, Collection<? extends Object> argument){
		super(code, new Arguments(argument));
	}
	public CooperativeMessage(int code, String argument) throws ParseException{
		super(code, Arguments.parse(argument));
	}
	
	public CooperativeMessage(int code, Arguments argument, CanalComm sender){
		super(code, argument,sender);
	}
	
	public CooperativeMessage(int code, Arguments argument){
		super(code,argument);
	}
	
	
	/* (non-Javadoc)
	 * @see agentCommunicationSystem.MessageBase#getSize()
	 */
	@Override
	public int getSize() {
		//in octets
		int s = 2;// sender & message type
		if (argument != null){
			for (String cl:argument){
				s+=cl.length();
			}
		}
		return s;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.MessageBase#toString()
	 */
	@Override
	public String toString() {

		String res = "{";
		switch(getCode()){
		case CM_ASK_ALL_LTERALS :
			res += "Ask all literals";
			break;
		case CM_SEND_ALL_LTERALS :
			res += "all literals";
			break;
		case CM_SEND_CLAUSE :
			res += "send clause";
			break;
		default:
			res += "Unknown Message";
		}
		if (argument != null){
			res += "("+argument.toString()+")";
		}
		res += "}";
		return res;
	}

}


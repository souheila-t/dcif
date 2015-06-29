/**

 * 

 */

package communication;



import genLib.tools.Arguments;

import java.util.Collection;



import org.nabelab.solar.parser.ParseException;




import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.MessageBase;
import agLib.agentCommunicationSystem.SystemMessageTypes;





/**

 * @author Viel Charlotte

 *

 */

public class PBMessage extends MessageBase<Arguments> implements PBMessageTypes, SystemMessageTypes {



	public PBMessage(){

		super();

	}

	public PBMessage(int code){

		super(code, null);

	}

	public PBMessage(int code, Collection<? extends Object> argument){

		super(code, new Arguments(argument));

	}

	public PBMessage(int code, String argument) throws ParseException{

		super(code, Arguments.parse(argument));

	}

	

	public PBMessage(int code, Arguments argument, CanalComm sender){

		super(code, argument,sender);

	}

	

	public PBMessage(int code, Arguments argument){

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

		case PBM_END :

			res += "End all batch";

			break;

		case PBM_ENDBATCH :

			res += "End current batch";

			break;

		case PBM_BEGIN :

			res += "Begin";

			break;

		case PBM_SEND_CLAUSE :

			res += "Send clause";

			break;

		case PBM_SEND_CONSEQ :

			res += "Send consequence";

			break;

		case SYS_FINISH :

			res += "End (finish)";

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


package base;

import java.util.Collection;

import org.nabelab.solar.Clause;

import logicLanguage.IndepClause;
import agLib.agentCommunicationSystem.MessageBase;

public class CFMessage extends MessageBase<Collection<Clause>> implements CFMessageTypes{

	public CFMessage(int code, Collection<Clause> argument){
		super(code, argument);
	}

	@Override
	public int getSize() {		
		return argument.size();
	}

	@Override
	public String toString() {
		switch(code){
		case PBM_SEND_CLAUSES:
			return "Send Clauses : "+argument.toString();
		case PBM_SEND_CONSEQS:
			return "Send Consequences : "+argument.toString();
		}
		return "Unknown message";
	}

}

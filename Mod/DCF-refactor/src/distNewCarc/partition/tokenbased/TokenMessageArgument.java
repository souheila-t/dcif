package distNewCarc.partition.tokenbased;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.nabelab.solar.Clause;

import agLib.agentCommunicationSystem.CanalComm;
import logicLanguage.IndepClause;

public class TokenMessageArgument {
	

	public TokenMessageArgument(){
		sentCl=new ArrayList<Clause>();
		direction=1;
		currentFlags=new HashSet<CanalComm>();
		nextFlags=new HashSet<CanalComm>();
	}
	
	public TokenMessageArgument(Collection<Clause> clauses, int dir, HashSet<CanalComm> curr, HashSet<CanalComm> next){
		sentCl=clauses;
		direction=dir;
		currentFlags=curr;
		nextFlags=next;
	}
	
	public Collection<Clause> getSentCl() {
		return sentCl;
	}
	public int getDirection() {
		return direction;
	}
	public HashSet<CanalComm> getCurrentFlags() {
		return currentFlags;
	}
	public HashSet<CanalComm> getNextFlags() {
		return nextFlags;
	}
	
	
	
	public String toString(){
		String res="";
		if (direction>0)
			res+="Forward ";
		else
			res+="Backward ";
		if (!currentFlags.isEmpty())
			res+="{C "+currentFlags.toString()+"} ";
		if (!nextFlags.isEmpty())
			res+="{N "+nextFlags.toString()+"} ";
		res+="Clauses = "+sentCl;
		
		return res;
	}
	
	protected Collection<Clause> sentCl;
	protected int direction;
	protected HashSet<CanalComm> currentFlags;
	protected HashSet<CanalComm> nextFlags;
}

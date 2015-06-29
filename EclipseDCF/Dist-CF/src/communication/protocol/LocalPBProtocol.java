/**

 * 

 */
package communication.protocol;

import genLib.tools.Arguments;

import java.util.ArrayList;
import java.util.List;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommStatsUpdater;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.SystemMessage;
import agLib.agentCommunicationSystem.SystemMessageTypes;
import agLib.agentCommunicationSystem.protocols.LocalProtocol;
import agLib.agentCommunicationSystem.protocols.consensus.GlobalWeightedMessage;
import agLib.agentCommunicationSystem.protocols.consensus.GlobalWeightedMessageTypes;
import agents.PBDiagnoser;

import communication.PBMessage;
import communication.PBMessageTypes;

/**
 * @author Viel Charlotte
 *
 */
public class LocalPBProtocol implements LocalProtocol, PBMessageTypes,
		SystemMessageTypes, GlobalWeightedMessageTypes {

	public LocalPBProtocol(CommunicationModule cAg) {
		this.cAg = cAg;
		nbFilsEnd = 0;
	}

	public LocalPBProtocol(CommunicationModule cAg, PBDiagnoser<CNF> ag){
		this.cAg = cAg;
		this.ag = ag;
		nbFilsEnd = 0;
	}
	
	public LocalPBProtocol(LocalPBProtocol base, boolean critic){
		this.cAg = base.cAg;
//		this.target = target;
		this.ag = base.ag;
		this.critic = critic;
		nbFilsEnd = 0;
	}
	
	
	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#acceptProtocol(agentCommunicationSystem.CanalComm, agentCommunicationSystem.CommStatsUpdater)
	 */
	public LocalProtocol acceptProtocol(CanalComm target, CommStatsUpdater cs) {
		cs.acceptLocalProtocol();
		return new LocalPBProtocol(this, true);		
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#disconnect()
	 */
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	
	public void setTypeConsFinding(int type) {
		typeConsFinding = type;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#gereParProtocol(agentCommunicationSystem.Message)
	 */
	public boolean gereParProtocol(Message<?> m) {
		if (m!=null)
			if (m instanceof PBMessage) {
				switch (m.getCode()){
				case PBM_BEGIN :
				case PBM_END :
				case PBM_ENDBATCH :
				case PBM_SEND_CLAUSE :
	//			case PBM_SEND_CONSEQ : // gere au niveau du system
					return true;
				}
			}
			if (m instanceof GlobalWeightedMessage) {
				switch (m.getCode()){
				case GWM_ENDLOCALCONV :
					return critic;
				}
			}
			if(m instanceof SystemMessage) {
				switch (m.getCode()) {
				case SYS_FINISH :
				case SYS_TIMEUP :
					return true;
				}
			}
		return false;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#initProtocol(agentCommunicationSystem.CanalComm, agentCommunicationSystem.CommStatsUpdater)
	 */

	public LocalProtocol initProtocol(CanalComm target, CommStatsUpdater cs) {if (verbose) {
		System.out.println("\n"+"Local Exchange between "+cAg.getComm().toString()+" and "+target.toString());
//		System.out.println("        val of "+ag+": "+ag.getOwnCssValue(target)
//							+" / last val conf by "+target+": "+tracker.getLastValConfirmedBy(target)+"\n");
	}
	cs.initLocalProtocol();
	return new LocalPBProtocol(this, false);
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#receiveMessage(agentCommunicationSystem.Message)
	 */
	public void receiveMessage(Message<?> m) {
		ag.setDormant(false);

		if (m != null && m instanceof PBMessage) {
			PBMessage pbm = (PBMessage) m;
			Arguments arg = pbm.getArgument();
			CanalComm sender = pbm.getSender();
			switch(pbm.getCode()){
			case PBM_BEGIN :
				receiveBegin();
				break;
			case PBM_END :
				receiveEnd(sender);
				break;
			case PBM_ENDBATCH :
				receiveEndBatch(sender);
				break;
			case PBM_SEND_CLAUSE :
				receiveSendClause(new IndepClause(arg.toString()), sender);
				break;
			}
		}
		if (m!=null && m instanceof SystemMessage){
			switch(m.getCode()){
			case SYS_FINISH:
			case SYS_TIMEUP:
				receiveFinish();
			break;
			}
		}
	}
	
	private void receiveBegin() {
		ag.consFinding(new ArrayList<IndepClause>(), true, false); //TODO check if true is better
	}
	
	private void receiveFinish() {
		System.out.println("Fin "+this.ag);
		// TODO effacer les print
	}
	
	private void receiveSendClause(IndepClause clause, CanalComm sender) {
		clauseBuffer.add(clause);
	}
	
	private void obsoleteReceiveSendClause(IndepClause clause, CanalComm sender) {
		CNF topClauses;
		switch(typeConsFinding) {
		case SEQUENTIAL :
			ag.addToTheory(new CNF(clause), sender);
			break;
			
		case HYBRID :
			if(!ag.isCarcDone()) {
				ag.addToTheory(new CNF(clause), sender);
				ag.consFinding(new ArrayList<IndepClause>(),true, false);
			} else {
				topClauses = new CNF();
				topClauses.add(clause);
				ag.consFinding(topClauses,true, false);
				ag.addToTheory(topClauses, sender);
			}
			if(nbFilsEnd == ag.getSons().size()) {
				ag.sendEndToParent();
			}
			break;
			
		case PARALLEL :
			topClauses = new CNF();
			topClauses.add(clause);
			ag.consFinding(topClauses,true, false);
			ag.addToTheory(topClauses, sender);
			break;
		}
	}
	
	private void receiveEnd(CanalComm sender) {
		if(!sender.equals(cAg.commSystem)) {
			ag.incNbEndReceived();
		}
		switch(typeConsFinding) {
		case SEQUENTIAL :
			if(ag.getNbEndReceived() == ag.getSons().size()) {
				ag.consFinding(new ArrayList<IndepClause>(),true, false);
			}
			//break;
		case HYBRID :
		case PARALLEL :
			if(ag.getNbEndReceived() == ag.getSons().size()) {
				ag.sendEndToParent();
			}
			break;
		}
	}

	private void receiveEndBatch(CanalComm sender) {
		
//		System.out.println(this.cAg.getComm()+" buffer = "+clauseBuffer);
		
		switch(typeConsFinding) {
		case SEQUENTIAL :
			ag.addToTheory(clauseBuffer, sender);
			break;
			
		case HYBRID :
			if(!ag.isCarcDone()) {
				ag.addToTheory(clauseBuffer, sender);
				ag.consFinding(new ArrayList<IndepClause>(),true, false);
			} else {
				ag.consFinding(clauseBuffer,true, false);
				ag.addToTheory(clauseBuffer, sender);
			}
//			if(nbFilsEnd == ag.getSons().size()) {
//				ag.sendEndToParent();
//			}
			break;
			
		case PARALLEL :
			ag.consFinding(clauseBuffer,true, false);
			ag.addToTheory(clauseBuffer, sender);
			break;
		}
		clauseBuffer.clear();
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#start()
	 */

	public void start() {
		ag.consFinding(new ArrayList<IndepClause>(), true, false);
	}
	
	private int nbFilsEnd;
	public static boolean verbose = true;
//	private ValTracker<FullHypothesis> tracker = null;
	private CommunicationModule cAg;
//	private CanalComm target = null;
	private PBDiagnoser<CNF> ag;
	private boolean critic = false;
//	private CNF arguments = new CNF();
	private int typeConsFinding = 0;
	
	private CNF clauseBuffer=new CNF();
	
	/** Waits until the agent has received the "begin" from all of his sons. */
	public static final int SEQUENTIAL = 0;
	/** Computes Carc for the first "begin", and NewCarc for the others. */
	public static final int HYBRID = 1;
	/** Computes Carc at the beginning of the consequence finding. After that,
	 * use only NewCarc. */
	public static final int PARALLEL = 2;
	public static final int CARC = 0;
	public static final int NEW_CARC = 1;	
	public static final int COOPERARTIVE_CF_AGENT=3;
}
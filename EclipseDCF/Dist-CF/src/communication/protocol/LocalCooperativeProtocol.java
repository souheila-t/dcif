/**

 * 

 */

package communication.protocol;

import genLib.tools.Arguments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import obsolete.tools.Tools;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import logicLanguage.IndepLiteral;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommStatsUpdater;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.protocols.LocalProtocol;
import agents.CooperativeDiagnoser;

import communication.CooperativeMessage;
import communication.CooperativeMessageTypes;

/**
 * @author Viel Charlotte
 *
 */
public class LocalCooperativeProtocol implements LocalProtocol, 
	CooperativeMessageTypes {

	
	public LocalCooperativeProtocol(CommunicationModule cAg, 
			CooperativeDiagnoser<CNF> ag){
		this.cAg = cAg;
		this.ag = ag;
		nbAllLitReceived = 0;
	}
	
	public LocalCooperativeProtocol(LocalCooperativeProtocol base){
		this.cAg = base.cAg;
		this.ag = base.ag;
		nbAllLitReceived = 0;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#acceptProtocol(agentCommunicationSystem.CanalComm, agentCommunicationSystem.CommStatsUpdater)
	 */

	public LocalProtocol acceptProtocol(CanalComm target, CommStatsUpdater ctr) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#disconnect()
	 */

	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#gereParProtocol(agentCommunicationSystem.Message)
	 */

	public boolean gereParProtocol(Message<?> m) {
		switch (m.getCode()) {
		case CM_SEND_ALL_LTERALS :
		case CM_SEND_CLAUSE :
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#initProtocol(agentCommunicationSystem.CanalComm, agentCommunicationSystem.CommStatsUpdater)
	 */

	public LocalProtocol initProtocol(CanalComm target, CommStatsUpdater cs) {
		if (verbose) {
			System.out.println("\n"+"Local Exchange between "+cAg.getComm().toString()+" and "+target.toString());
		}
		cs.initLocalProtocol();
		return new LocalCooperativeProtocol(this);
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#receiveMessage(agentCommunicationSystem.Message)
	 */

	public void receiveMessage(Message<?> m) {
		ag.setDormant(false);
		
		if (m != null && m instanceof CooperativeMessage) {
			CooperativeMessage pbm = (CooperativeMessage) m;
			Arguments arg = pbm.getArgument();
			CanalComm sender = pbm.getSender();
			switch (m.getCode()) {
			case CM_ASK_ALL_LTERALS : 
				receiveAskAllLiterals(sender);
				break;
			case CM_SEND_ALL_LTERALS :
				receiveSendAllLiterals(arg, sender);
				break;
			case CM_SEND_CLAUSE :
				receiveSendClause(new IndepClause(arg.toString()), sender);
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.LocalProtocol#start()
	 */
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	public void receiveAskAllLiterals(CanalComm sender) {
		ag.sendAllResolvableLiterals(sender);
	}
	
	public void receiveSendAllLiterals(Arguments arg, CanalComm sender) {
		nbAllLitReceived++;
		List<IndepLiteral> liste = Tools.ArgumentsToLiterals(arg);
		//HashSet<String> listeLit = new HashSet<String>();
		ag.updateCommLanguage(sender, liste);
		if(nbAllLitReceived == ag.getNbNeighbors()) {
			ag.consFinding(new ArrayList<IndepClause>(),true,true);
		} else {
			ag.setDormant(cAg.getComm().isEmpty());
		}
	}
	
	public void receiveSendClause(IndepClause clause, CanalComm sender) {
		ArrayList<IndepClause> list = new ArrayList<IndepClause>();
		list.add(clause);
		ag.consFinding(list,true,false);
		ag.addToTheory(new CNF(clause), sender);
	}
	
	/**
	 * @return nbAllLitReceived
	 */
	public int getNbAllLitReceived() {
		return nbAllLitReceived;
	}

	//private ValTracker<FullHypothesis> tracker = null;
	private CooperativeDiagnoser<CNF> ag;
	public static boolean verbose = true;
	private CommunicationModule cAg;
	private int nbAllLitReceived;
	public static final int CARC = 0;
	public static final int NEW_CARC = 1;	

}


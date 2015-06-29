/**
 * 
 */
package communication.protocol;

import logicLanguage.CNF;

import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.SystemMessage;
import agLib.agentCommunicationSystem.SystemMessageTypes;
import agLib.agentCommunicationSystem.protocols.LocalProtocol;
import agLib.agentCommunicationSystem.protocols.consensus.HypTrackingMainProtocol;






import agents.PBDiagnoser;



import communication.CooperativeMessage;

import communication.CooperativeMessageTypes;


/**
 * @author Viel Charlotte
 *
 */
public class MainCooperativeProtocol extends HypTrackingMainProtocol<CNF> implements
		SystemMessageTypes, CooperativeMessageTypes {

	/**
	 * @param cAg
	 */
	public MainCooperativeProtocol(CommunicationModule cAg) {
		super(cAg);
	}
	
	/**
	 * @param cAg
	 */
	public MainCooperativeProtocol(CommunicationModule cAg, LocalProtocol localProtocol) {
		super(cAg);
		this.localProtocol = localProtocol;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.consensus.HypTrackingMainProtocol#gereParProtocol(agentCommunicationSystem.Message)
	 */
	@Override
	public boolean gereParProtocol(Message<?> m) {
		switch (m.getCode()) {
		case SYS_START :
		case SYS_FINISH :
		case CM_ASK_ALL_LTERALS : 
		case CM_SEND_ALL_LTERALS :
		case CM_SEND_CLAUSE :
			return true;
		default : return false;
		}
	}


	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.consensus.HypTrackingMainProtocol#receiveMessage(agentCommunicationSystem.Message)
	 */
	@Override
	public void receiveMessage(Message<?> m) {
		if (m==null) return;
		if (m!=null && m instanceof SystemMessage) {
			SystemMessage gm = (SystemMessage) m;
			switch(gm.getCode()) {
			case SYS_START :
				receiveStart();
				break;
			}
		} else if (m!=null && m instanceof CooperativeMessage) {
			CooperativeMessage gm = (CooperativeMessage) m;
			switch(gm.getCode()) {
			case CM_ASK_ALL_LTERALS :
				localProtocol.receiveMessage(gm);
				break;
			case CM_SEND_ALL_LTERALS :
				localProtocol.receiveMessage(gm);
				break;
			case CM_SEND_CLAUSE :
				localProtocol.receiveMessage(gm);
				break;
			}
		}
	}


	public void receiveStart() {
		if(verbose) System.out.println("Start");
		// TODO effacer les print
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.protocols.BaseMainProtocol#initProtocol()
	 */
	@Override
	public void initProtocol() {
		// TODO Auto-generated method stub
	}

	protected PBDiagnoser<CNF> ag;
	protected LocalProtocol localProtocol;
	protected boolean verbose = true;

}


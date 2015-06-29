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



import communication.PBMessage;

import communication.PBMessageTypes;





/**

 * @author Viel Charlotte

 *

 */

public class MainPBProtocol extends HypTrackingMainProtocol<CNF> implements 

		SystemMessageTypes, PBMessageTypes {



	/**

	 * @param cAg

	 */

	public MainPBProtocol(CommunicationModule cAg) {

		super(cAg);

	}

	

	/**

	 * @param cAg

	 */

	public MainPBProtocol(CommunicationModule cAg, LocalProtocol localProtocol) {

		super(cAg);

		this.localProtocol = localProtocol;

	}



	/* (non-Javadoc)

	 * @see agentCommunicationSystem.protocols.BaseMainProtocol#initProtocol()

	 */

	@Override

	public void initProtocol() {

		// TODO Auto-generated method stub

		

	}



	/* (non-Javadoc)

	 * @see agentCommunicationSystem.protocols.BaseMainProtocol#receiveMessage(agentCommunicationSystem.Message)

	 */

	@Override

	public void receiveMessage(Message<?> m) {

		if (m==null) return;

//		ag.setDormant(false);

//		if (localMessage(m))

//			currentConv.receiveMessage(m);

		if (m!=null && m instanceof SystemMessage) {

			SystemMessage gm = (SystemMessage) m;

			switch(gm.getCode()) {

			case SYS_START :

				receiveStart();

				break;

			}

		} else if (m!=null && m instanceof PBMessage) {

			PBMessage gm = (PBMessage) m;

			switch(gm.getCode()) {

			case PBM_BEGIN :
			case PBM_END :
			case PBM_ENDBATCH :
			case PBM_SEND_CLAUSE :

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

	 * @see agentCommunicationSystem.protocols.consensus.HypTrackingMainProtocol#gereParProtocol(agentCommunicationSystem.Message)

	 */

	@Override

	public boolean gereParProtocol(Message<?> m) {

		if (m instanceof SystemMessage)
			switch (m.getCode()) {
			case SYS_START :
			case SYS_FINISH :
			case SYS_TIMEUP :
				return true;
			}
		if (m instanceof PBMessage)
		switch (m.getCode()) {
		case PBM_BEGIN :
		case PBM_END : 
		case PBM_ENDBATCH : 
		case PBM_SEND_CLAUSE :
			return true;
		}
		return false;
	}



	protected PBDiagnoser<CNF> ag;

	protected LocalProtocol localProtocol;

	protected boolean verbose = false;

}


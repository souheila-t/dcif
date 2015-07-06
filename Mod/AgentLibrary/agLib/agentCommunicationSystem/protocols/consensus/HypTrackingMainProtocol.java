package agLib.agentCommunicationSystem.protocols.consensus;

import java.util.Vector;

import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.protocols.BaseMainProtocol;

public abstract class HypTrackingMainProtocol<Hypothesis> extends BaseMainProtocol implements ValTracker<Hypothesis> {

	//Paramètres
	
//	public StatAgent stats;
//	protected Hypothesis emptyHypothesis;
	protected  Vector <CanalComm> agentsCompetents=new Vector <CanalComm>();
	protected Vector <Hypothesis> confirmationsHyp=new Vector<Hypothesis>();	
	protected Vector <Hypothesis> knownCurrentHyp=new Vector<Hypothesis>();
	protected Vector <CanalComm> disconnectedAgentsCompetents=new Vector <CanalComm>();
	protected Vector <Hypothesis> disconnectecKnownCurrentHyp=new Vector<Hypothesis>();

	
	public HypTrackingMainProtocol(CommunicationModule cAg){
		super(cAg);
		agentsCompetents=new Vector <CanalComm>();
		confirmationsHyp=new Vector<Hypothesis>();
		knownCurrentHyp=new Vector<Hypothesis>();
	}
	
	public int identifier(CanalComm inconnu){
		int indice = agentsCompetents.indexOf(inconnu);
		if ((indice==-1) && (inconnu.equals(commSystem)) ) {
				indice = CommunicationModule.INDICE_SYSTEM;
		}
		return indice;
	}
	
	public Vector<CanalComm> getRelevantNeighbours(){
		return agentsCompetents;
	}
	
	public void addNewNeighbour(CanalComm ag){
		/* n'ajoute que si l'agent n'est pas deja dans la liste) */
		if (identifier(ag)==-1) {
			agentsCompetents.add(ag);
			confirmationsHyp.add(null);
			knownCurrentHyp.add(null);
		}
	}
	public void disconnectNeighbour(CanalComm ag){
		int id=identifier(ag);
		if (id>-1) {
			disconnectedAgentsCompetents.add(ag);
			disconnectecKnownCurrentHyp.add(knownCurrentHyp.get(id));
			agentsCompetents.remove(ag);
			confirmationsHyp.remove(id);
			knownCurrentHyp.remove(id);
		}
	}
	public void reconnectNeighbour(CanalComm ag){
		int indice = disconnectedAgentsCompetents.indexOf(ag);
		if (indice>-1){
			agentsCompetents.add(ag);
			knownCurrentHyp.add(disconnectecKnownCurrentHyp.get(indice));
			confirmationsHyp.add(null);
		}	
	}
	public void destroyNeighbour(CanalComm ag){
		int id=identifier(ag);
		if (id>-1) {
			agentsCompetents.remove(ag);
			confirmationsHyp.remove(id);
			knownCurrentHyp.remove(id);
		}
	}
	
	
	public abstract boolean gereParProtocol(Message<?> m);
	
	
	public abstract void receiveMessage(Message<?> m);

	public void newValReceived(Hypothesis Hyp, CanalComm sender){
		int id=identifier(sender);
		if (id>-1) {
			knownCurrentHyp.set(id,Hyp);
		}
	}
	
	public void newConfirmationReceived(Hypothesis Hyp, CanalComm sender){
		int id=identifier(sender);
		if (id>-1) {
			confirmationsHyp.set(id,Hyp);
		}
	}

	public Hypothesis getLastValOf(CanalComm target) {
		int id=identifier(target);
		if (id>-1) {
			return knownCurrentHyp.get(id);
		}
		return null;
	}
	
	public Hypothesis getLastValConfirmedBy(CanalComm target) {
		int id=identifier(target);
		if (id>-1) {
			return confirmationsHyp.get(id);
		}
		return null;
	}

	public void resetAllConfirmations(){
		for (int i=0;i<confirmationsHyp.size();i++){
			confirmationsHyp.set(i, null);
		}
	}


	




}

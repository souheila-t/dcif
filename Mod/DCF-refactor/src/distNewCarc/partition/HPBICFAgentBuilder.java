package distNewCarc.partition;

import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.Network;
import solarInterface.SolProblem;
import stats.ConsFindingAgentStats;
import systemStructure.AgentBuilder;

public class HPBICFAgentBuilder implements AgentBuilder<HierarchicalIncConsFindingAgent> {
	public HPBICFAgentBuilder(boolean useNewConsAsAxiom, boolean inDepthPruning){
		super();
		newConsAsAxiom=useNewConsAsAxiom;
		this.inDepthPruning=inDepthPruning;
	}
	
	public HierarchicalIncConsFindingAgent createAgent(int id, SolProblem pb,
			CanalComm systComm, Network net, ConsFindingAgentStats das) {
		return new HierarchicalIncConsFindingAgent(id,pb,systComm,net,das,newConsAsAxiom,inDepthPruning);
	}
	
	protected boolean newConsAsAxiom=false;
	protected boolean inDepthPruning=false;
}

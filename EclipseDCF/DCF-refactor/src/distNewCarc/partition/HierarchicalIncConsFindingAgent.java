package distNewCarc.partition;

import java.util.ArrayList;
import java.util.Collection;

import solarInterface.SolProblem;
import stats.ConsFindingAgentStats;
import systemStructure.HierarchicalAgent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.Network;

public class HierarchicalIncConsFindingAgent extends IncConsFindingAgent implements HierarchicalAgent {

	public HierarchicalIncConsFindingAgent(int id, SolProblem pb,
			CanalComm systComm, Network net, ConsFindingAgentStats das, boolean useNewConsAsAxiom, boolean inDepthPruning) {
		super(id, pb, systComm, net, das, useNewConsAsAxiom, inDepthPruning);
	}

	public Collection<CanalComm> getUpperAgents() {
		return upperNeighbours;
	}

	public Collection<CanalComm> getLowerAgents() {
		Collection <CanalComm> lowerNeighbours=new ArrayList<CanalComm>();
		for (CanalComm agent:cAg.getNeighbours()){
			if (!upperNeighbours.contains(agent))
				lowerNeighbours.add(agent);
		}
		return lowerNeighbours;
	}
	public void setUpperAgents(Collection<CanalComm> upperAgents) {
		upperNeighbours=upperAgents;
	}

	
	protected Collection <CanalComm> upperNeighbours=new ArrayList<CanalComm>();

}

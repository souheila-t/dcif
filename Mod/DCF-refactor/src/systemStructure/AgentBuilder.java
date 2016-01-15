package systemStructure;

import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Network;
import solarInterface.SolProblem;
import stats.ConsFindingAgentStats;

public interface AgentBuilder<AgentType> {
	public AgentType createAgent(int id, SolProblem pb, CanalComm systComm, Network net, ConsFindingAgentStats das);
	
}

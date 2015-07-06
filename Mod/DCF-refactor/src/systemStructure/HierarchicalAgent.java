package systemStructure;

import java.util.Collection;

import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;

public interface HierarchicalAgent extends Agent {
	public Collection<CanalComm> getUpperAgents();
	public Collection<CanalComm> getLowerAgents();
	public void setUpperAgents(Collection<CanalComm> upperAgents);
}

package distNewCarc.partition.tokenbased;

import agLib.agentCommunicationSystem.CanalComm;
import systemStructure.HierarchicalAgent;

public class CircuitAgent {

	public CircuitAgent(HierarchicalAgent baseAgent){
		ag=baseAgent;
	}
	
	public CanalComm nextAg(int direction){
		CanalComm[] res=new CanalComm[1];
		if (direction > 0)
			res=ag.getLowerAgents().toArray(res);
		else
			res=ag.getUpperAgents().toArray(res);
		return res[0];
	}
	
	protected HierarchicalAgent ag;
}

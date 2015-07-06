package agLib.agentCommunicationSystem.protocols.consensus;

import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;

public interface ConsensusAgent<CssValue> extends Agent{
	public CssValue getOwnCssValue(CanalComm otherAgent);
	public boolean equalOwnCssValue(CssValue hyp, CanalComm otherAgent);
}

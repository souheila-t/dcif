package agLib.agentCommunicationSystem.protocols.consensus;

import agLib.agentCommunicationSystem.CanalComm;

public interface ValTracker<Value> {
	
	public void newConfirmationReceived(Value Hyp, CanalComm sender);
	public void newValReceived(Value Hyp, CanalComm sender);
	public Value getLastValConfirmedBy(CanalComm target);
	public Value getLastValOf(CanalComm target);
	public void resetAllConfirmations();
}

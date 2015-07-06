package agLib.agentCommunicationSystem;

public interface Message<Argument> {

	public Argument getArgument();
	public int getCode();
	public void setCode(int code);
	public CanalComm getSender();
	public void setSender(CanalComm ag);
	public int getSize();
	public String toString();
}

package agLib.agentCommunicationSystem;


public abstract class MessageBase <Arg> implements Message<Arg>{
	
	protected int code;
	protected CanalComm sender;
	protected Arg argument;
	
	public MessageBase(int code, Arg argument, CanalComm sender){
		this.code=code;
		this.argument=argument;
		this.sender=sender;
	}
	
	public MessageBase(int code, Arg argument){
		this.code=code;
		this.argument=argument;
	}
	public MessageBase(){
		argument=null;
	}
	
	public Arg getArgument() {
		return argument;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code=code;
	}
	
	public CanalComm getSender() {
		return sender;
	}
	
	public void setSender(CanalComm ag) {
		sender=ag;
	}

	public abstract int getSize();
	
	public abstract String toString();
		

}

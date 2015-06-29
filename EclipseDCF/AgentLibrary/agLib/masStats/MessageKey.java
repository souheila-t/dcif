package agLib.masStats;

import agLib.agentCommunicationSystem.Message;

public class MessageKey {

	public MessageKey(Message<?> m){
		clKey=m.getClass();
		code=m.getCode();
	}
	@SuppressWarnings("rawtypes")
	public MessageKey(Class<? extends Message> cl, int type){
		clKey=cl;
		code=type;
	}
	
	public boolean equals(Object obj){
		if (obj==this) return true;
		if (obj==null) return false;
		if (obj instanceof MessageKey){
			MessageKey mk=(MessageKey)obj;
			return (code==mk.code) && clKey.equals(mk.clKey);
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public Class<? extends Message> clKey;
	public int code;
}

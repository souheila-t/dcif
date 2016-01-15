package agLib.masStats;

import java.util.ArrayList;
import java.util.List;

import agLib.agentCommunicationSystem.CommStatsUpdater;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.SystemMessage;

import genLib.tools.Pair;

public class BasicAgentCommStats implements CommStatsUpdater, AgentLevelStats {
	//Summary Measures Codes
	public static final int KEY_SUMMARY=-100;
	public static final int SMC_NBSENTMESSAGES=1;
	public static final int SMC_NBRECEIVEDMESSAGES=2;
	public static final int SMC_TOTSIZESENT=3;
	public static final int SMC_TOTSIZERECEIVED=4;
	
	
	//constant codeCtr (not accessible if overridden)
	public static final int CTR_SENT=3001;
	public static final int CTR_RECEIVED=3002;	
	public static final int CTR_NBCONVINITIATED=3003;
	public static final int CTR_NBCONVACCEPTED=3004;
	
	//automatic codeCtr : 3 parameters : 
	//		b- sent or received 1,2
	//		c- class : classcode : max ina given appli 10
	//		d- type of message : max for a given class 100
	// proposition : b*1000+c*100+d
	// a- type of measure (nb,totsize,maxsize,minsize) 1,2,3,4		
	// ex : nb of ...
	// as a result Codes from 1000 to 3002 are for Communications Stats
	public final int KEY_SENT=1;
	public final int KEY_RECEIVED=2;
	// to redefine according to the application if needed
	public final int CLASSKEY_SYSTEMMESSAGE=0;
	
	
		
	
	private int getIndex(List<StatCounter<MessageKey>> list,Message<?> m){
		for (int i=0;i<list.size();i++){
			StatCounter<MessageKey> c=list.get(i);
			if (c.equalKey(new MessageKey(m)))
				return i;
		}
		return -1;
	}
	
	private int getIndex(List<StatCounter<MessageKey>> list,MessageKey cl){
		for (int i=0;i<list.size();i++){
			StatCounter<MessageKey> c=list.get(i);
			if (c.equalKey(cl))
				return i;
		}
		return -1;
	}
	
	public int getReceivedIndex(Message<?> m){
		return getIndex(receivedData,m);
	}
	
	public int getSentIndex(Message<?> m){
		return getIndex(sentData,m);
	}

	public StatCounter<MessageKey> getReceived(Message<?> m){
		int ind=getReceivedIndex(m);
		if (ind==-1) return null;
		return receivedData.get(ind);
	}
	
	public StatCounter<MessageKey> getSent(Message<?> m){
		int ind=getSentIndex(m);
		if (ind==-1) return null;
		return sentData.get(ind);
	}
	
	protected StatCounter<MessageKey> getMC(List<StatCounter<MessageKey>> l,@SuppressWarnings("rawtypes") Class<? extends Message> cl,int type){
		int ind=getIndex(l,new MessageKey(cl,type));
		if (ind==-1) return null;
		return l.get(ind);
	}

	
	//CommunicationStat interface
	
	public void receivedMessages(Message<?> m) {
		StatCounter<MessageKey> mc=getReceived(m);
		if (mc==null){
			mc=new StatCounter<MessageKey>(new MessageKey(m));
			receivedData.add(mc);
		}
		mc.inc(m.getSize());
		receivedMessages.inc(m.getSize());
	}

	public void sentMessages(Message<?> m, int nbTarget) {
	  	StatCounter<MessageKey> mc=getSent(m);
		if (mc==null){
			mc=new StatCounter<MessageKey>(new MessageKey(m));
			sentData.add(mc);
		}
		int s=m.getSize();
		if (!trueBC && nbTarget>1){
			s=s*nbTarget;
    	}    	
		mc.inc(s);
		sentMessages.inc(m.getSize());
	}
	

	public boolean isMessageMeasureCode(int code){
		return (code>=1000) && (code<3005);
	}

	//to override for applications
	@SuppressWarnings("rawtypes")
	public Class<? extends Message> getClass(int classKey) throws ClassNotFoundException{
		switch(classKey){
		case CLASSKEY_SYSTEMMESSAGE:
			return SystemMessage.class;
		}
		return  Message.class;
	}
	
	@SuppressWarnings("rawtypes")
	public int getClassKey(Class<? extends Message> cl) {
		if (cl.equals(SystemMessage.class))
				//cl.newInstance() instanceof SystemMessage)
			return CLASSKEY_SYSTEMMESSAGE;
		return -1;
	}

	public int getCodeCtr(int sent, int classKey, int typeMessage){
		return sent*1000 + classKey*100 + typeMessage;
	}
	
	public StatCounter<?> getCounter(int codeCtr){
		if (!isMessageMeasureCode(codeCtr)) return null;
		switch(codeCtr){
		case CTR_SENT: return sentMessages;
		case CTR_RECEIVED: return receivedMessages;
		case CTR_NBCONVINITIATED: return initLocalConv;
		case CTR_NBCONVACCEPTED: return acceptLocalConv;
	
		}
		//trasnlate code
		int sent=codeCtr/1000;
		int classkey=(codeCtr%1000)/100;
		int typeMessage=(codeCtr%100);
		// get correct MessageCounter
		List<StatCounter<MessageKey>> data;
		if (sent==KEY_SENT)
			data=sentData;
		else
			data=receivedData;
		try {
			StatCounter<MessageKey> mc=getMC(data, getClass(classkey), typeMessage);
			if (mc!=null)
				return mc;
		} catch (ClassNotFoundException e) {e.printStackTrace();}
		return null;		
	}
	
	public Number getMeasure(int codeCtr, int typeMeasure){
		if (typeMeasure==KEY_SUMMARY)
			return get(codeCtr);
		StatCounter<?> mc=getCounter(codeCtr);
		if (mc==null) return -1;
		return mc.get(typeMeasure);		
	}
	



	public List<Integer> getSummaryCodes() {
		ArrayList<Integer> res=new ArrayList<Integer>();
		res.add(SMC_NBSENTMESSAGES);
		res.add(SMC_NBRECEIVEDMESSAGES);
		res.add(SMC_TOTSIZESENT);
		res.add(SMC_TOTSIZERECEIVED);
		return res;
	}

	public Number get(int summaryMeasureCode) {
		switch (summaryMeasureCode){
		case SMC_NBSENTMESSAGES:
			return getMeasure(CTR_SENT,StatCounter.KEY_NB);
		case SMC_NBRECEIVEDMESSAGES:
			return getMeasure(CTR_RECEIVED,StatCounter.KEY_NB);
		case SMC_TOTSIZESENT:
			return getMeasure(CTR_SENT,StatCounter.KEY_TOTSIZE);
		case SMC_TOTSIZERECEIVED:
			return getMeasure(CTR_RECEIVED,StatCounter.KEY_TOTSIZE);
		}
		return null;
	}

	public List<Integer> getAllActiveCtrCodes() {
		ArrayList<Integer> res=new ArrayList<Integer>();
		res.add(CTR_SENT);
		res.add(CTR_RECEIVED);
		res.add(CTR_NBCONVINITIATED);
		res.add(CTR_NBCONVACCEPTED);
		for (StatCounter<MessageKey> ctr:sentData){
			MessageKey k=ctr.getKey();
			int classKey=getClassKey(k.clKey);
			int type=k.code;
			res.add(getCodeCtr(KEY_SENT, classKey, type));
		}
		for (StatCounter<MessageKey> ctr:receivedData){
			MessageKey k=ctr.getKey();
			int classKey=getClassKey(k.clKey);
			int type=k.code;
			res.add(getCodeCtr(KEY_RECEIVED, classKey, type));
		}
		return res;
	}

	@SuppressWarnings("rawtypes")
	public String getFullLabel(int codeCtr, int measureType) {
		if (measureType==KEY_SUMMARY)
			return getSummaryLabel(codeCtr);
		String res="";
		res+=StatCounter.label(measureType);
		// get messageKey name
		switch(codeCtr){
		case CTR_SENT: return res+" sent messages";
		case CTR_RECEIVED: return res+" received messages";		
		case CTR_NBCONVINITIATED: return res+" initLocalConv";
		case CTR_NBCONVACCEPTED: return res+" acceptLocalConv";
		}
		int classkey=(codeCtr%1000)/100;
		int typeMessage=(codeCtr%100);
		try {
			Class<? extends Message> cl=getClass(classkey);
			Message<?> m=(Message<?>)cl.newInstance();
			m.setCode(typeMessage);
			res+=m.toString();
		} catch (Exception e) {e.printStackTrace();}
		
		int sent=codeCtr/1000;
		if (sent==KEY_SENT) res+=" sent";
		else res+=" received";
		return res;
	}

	public String getSummaryLabel(int summaryMeasureCode) {
		switch (summaryMeasureCode){
		case SMC_NBSENTMESSAGES:
			return "Nb of sent messages";
		case SMC_NBRECEIVEDMESSAGES:
			return "Nb of received messages";
		case SMC_TOTSIZESENT:
			return "Size of sent data";
		case SMC_TOTSIZERECEIVED:
			return "Size of received data";
		}
		return null;
	}

	public void acceptLocalProtocol() {
		acceptLocalConv.inc(1);
	}

	public void initLocalProtocol() {
		initLocalConv.inc(1);
	}
	
	public List<Pair<String,Number>>getAllResults(){
		List<Pair<String,Number>> res=new ArrayList<Pair<String,Number>>();
		for (Integer ctrCode:getAllActiveCtrCodes()){
			StatCounter<?> ctr=this.getCounter(ctrCode);
			if (ctr.hasValue)
				res.add(Pair.create(getFullLabel(ctrCode, StatCounter.KEY_VALUE), 
									ctr.get(StatCounter.KEY_VALUE)));
			if (ctr.hasNb)
				res.add(Pair.create(getFullLabel(ctrCode, StatCounter.KEY_NB), 
									ctr.get(StatCounter.KEY_NB)));
			if (ctr.hasSize){
				res.add(Pair.create(getFullLabel(ctrCode, StatCounter.KEY_TOTSIZE), 
						ctr.get(StatCounter.KEY_TOTSIZE)));
				res.add(Pair.create(getFullLabel(ctrCode, StatCounter.KEY_MAXSIZE), 
						ctr.get(StatCounter.KEY_MAXSIZE)));
				res.add(Pair.create(getFullLabel(ctrCode, StatCounter.KEY_MINSIZE), 
						ctr.get(StatCounter.KEY_MINSIZE)));				
			}	
		}
		return res;
	}
	
	public List<Pair<String,Number>>getSummaryResults(){
		List<Pair<String,Number>> res=new ArrayList<Pair<String,Number>>();
		for (Integer summCode:this.getSummaryCodes()){
			res.add(Pair.create(getSummaryLabel(summCode),
								get(summCode)));					
		}
		return res;
	}
	
	
	public boolean trueBC=true;
	public List<StatCounter<MessageKey>> sentData=new ArrayList<StatCounter<MessageKey>>();
	public List<StatCounter<MessageKey>> receivedData=new ArrayList<StatCounter<MessageKey>>();
	public StatCounter<Integer> sentMessages=new StatCounter<Integer>(CTR_SENT);
	public StatCounter<Integer> receivedMessages=new StatCounter<Integer>(CTR_RECEIVED);
	public StatCounter<Integer> initLocalConv=new StatCounter<Integer>(CTR_NBCONVINITIATED);
	public StatCounter<Integer> acceptLocalConv=new StatCounter<Integer>(CTR_NBCONVACCEPTED);


	// 1. definir une structure de donnée pour stocker le nombre et la taille (totale, min et max) 
	// des messages par classe et par type
	// vector de (CoutnerMessage == nb, tailletot, taillemin, taillemax)
	// indexé par (MessageKey == (Classe, type)
	// 2. stocker correctement les données par les deux setters de l'interface
	// TODO
	// 3. definir des getters utiles avec eventuel aggregation sur un groupe de clés
	// comment definir un groupe de cles de facon pratique
	// autoriser le all(Classes and type), et le (Class,allTypes) et le (alclasses, type)
	// utiliser MessageKey, avec superClasses ? et -1 pour allTypes
}



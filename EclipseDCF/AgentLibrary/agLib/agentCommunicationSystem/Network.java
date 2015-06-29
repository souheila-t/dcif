package agLib.agentCommunicationSystem;

import java.util.List;
import java.util.Vector;

import agLib.linkingGraph.LinkingGraph;


public class Network implements SystemMessageTypes {
	
	//private volatile Thread BCThread = null;
	
	
	//public CanalComm commBC;
	public CanalComm commSys;
	//public Vector <CanalComm> agentsInscrits;
	public LinkingGraph graphe;
	public LinkingGraph current, cumul;
	
	public int evolution;
	//probas evolution
	public static final double probaInitialLink=0.5;
	public static final double probaFirstConnectLink=0.02;
	public static final double probaDeconnectLink=0.2;
	public static final double probaReconnectLink=0.02;
	
	public static final int NET_STATIC=0;
	public static final int NET_VARLINKS=1;
	
	
	public Network(LinkingGraph g, CanalComm syst, int evolv) {
		//commBC= new CanalComm(this);
		graphe= g;
		evolution=evolv;
	//	if (evolv==NET_STATIC){
	//		current= g.copy();
	//		cumul = g.copy();			
	//	}
	//	else {
	//	}
		commSys=syst;
	}
	
	
	
/*	public void start() {
		if (BCThread == null) {
			BCThread = new Thread(this,"BC");
			BCThread.start();
		}
	} */
	public void finalize(){
//		commBC=null;
		graphe=null;
	}
/*	public CanalComm getComm() {
		return commBC;
	}*/

//	public void addAgent(CanalComm c) {
//		graphe.addAgent(c);
//	}
	
	public List <CanalComm> getNeighbours(CanalComm ag){
		// for agent : neighbours with whom it can communicate
		// for system : agents that can receive examples
		if (ag==commSys) 
			return current.getAgents();
		return current.getVoisins(ag);
	}

	public List <CanalComm> getNeighboursAmong(CanalComm ag, List <CanalComm> filter){
		Vector <CanalComm> res=new Vector <CanalComm>();
		int i;
		CanalComm tcc;
		List <CanalComm> neighbours=getNeighbours(ag);
		for (i=0;i<neighbours.size();i++) {
			tcc=neighbours.get(i);
			if (filter.contains(tcc)) {
				res.add(tcc);
			}			
		}		
		return res;
	}
		
	public List <CanalComm> getNeighboursExcluding(CanalComm ag, List <CanalComm> filter){
		Vector <CanalComm> res=new Vector <CanalComm>();
		int i;
		CanalComm tcc;
		List <CanalComm> neighbours=getNeighbours(ag);
		for (i=0;i<neighbours.size();i++) {
			tcc=neighbours.get(i);
			if (!filter.contains(tcc)) {
				res.add(tcc);
			}			
		}		
		return res;
	}
	
	public List <CanalComm> getAllAgents(){
		//for system : all agents whose thread is running (start / finish)
		return cumul.Agents;
	}
	
	public void Init(){
		current= graphe.blankCopy();
		cumul = graphe.blankCopy();
		switch(evolution){
		case NET_STATIC :
			InitStatic();
			break;
		case NET_VARLINKS :
			InitVariableLinks();
			break;
		}
	}
	
	public void Evolve(){
		switch(evolution){
		case NET_STATIC :
			break;
		case NET_VARLINKS :
			EvolveVariableLinks();
			break;
		}
		
	}
	
	protected void InitStatic(){
		int i,j;
		int n=graphe.nbAgents();
		for (i=0;i<n;i++){
			for (j=i+1;j<n;j++){
				if (graphe.existeLien(i, j)) firstConnectLink(i,j);					
			}
		}
	}

	protected void InitVariableLinks(){
		int i,j;
		double p;
		int n=graphe.nbAgents();
		for (i=0;i<n;i++){
			for (j=i+1;j<n;j++){
				if (graphe.existeLien(i, j)){
					p=Math.random();
					if (p<=probaInitialLink) {
						//current.addLien(i,j);
						//cumul.addLien(i,j);
						firstConnectLink(i,j);
					}
				}
			}
		}
	}
	
	protected void EvolveVariableLinks(){
		int i,j;
		double p;
		int n=graphe.nbAgents();
		for (i=0;i<n;i++){
		  for (j=i+1;j<n;j++){
		    if (graphe.existeLien(i, j)){
			  p=Math.random();
			  if (current.existeLien(i, j)){
				  if (p<=probaDeconnectLink) {
				    deconnectLink(i,j);
				  }
			  } else if (cumul.existeLien(i, j)){
				  if (p<=probaReconnectLink) {
					reconnectLink(i,j);
				  }
			  } else if (p<=probaFirstConnectLink) {
					firstConnectLink(i,j);
			  }
			}
		  }
		}
	}
	
	public void offlineDeconnectLink(int i,int j){	    
		graphe.removeLien(i,j);
	}
	
	protected void deconnectLink(int i,int j){
	    current.removeLien(i,j);
	    CanalComm a1=current.getAgents().get(i);
	    CanalComm a2=current.getAgents().get(j);
	    a1.enqueue(commSys, new SystemMessage(SYS_DISCONNECT,a2));
	    a2.enqueue(commSys, new SystemMessage(SYS_DISCONNECT,a1));
	}
	protected void reconnectLink(int i,int j){
	    current.addLien(i,j);
	    CanalComm a1=current.getAgents().get(i);
	    CanalComm a2=current.getAgents().get(j);
	    a1.enqueue(commSys, new SystemMessage(SYS_RECONNECT,a2));
	    a2.enqueue(commSys, new SystemMessage(SYS_RECONNECT,a1));
	}
	protected void firstConnectLink(int i,int j){
		current.addLien(i,j);
		cumul.addLien(i,j);
		CanalComm a1=current.getAgents().get(i);
		CanalComm a2=current.getAgents().get(j);
		a1.enqueue(commSys, new SystemMessage(SYS_NEW_NEIGHBOUR,a2));
		a2.enqueue(commSys, new SystemMessage(SYS_NEW_NEIGHBOUR,a1));
	}
	
	

	public String toString(){
		return (current.toString());
	}
	
}
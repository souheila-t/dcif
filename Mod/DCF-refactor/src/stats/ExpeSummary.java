/**
 *
 */
package stats;

import java.util.ArrayList;
import java.util.List;

import agLib.masStats.BasicAgentCommStats;


/**
 * @author Gauvain Bourgne
 *
 */
public class ExpeSummary {

	
	public ExpeSummary(String problem, String distrib, int root, String method, long time, int nbCons, List<ConsFindingAgentStats> agStats){
		this.problem=problem;
		this.distrib=distrib;
		this.method=method;
		this.nbAg=agStats.size();
		this.root=root;
		this.time=time;
		nbConsequencesFound=nbCons;
		agentStats=agStats;
		convertStats();
	}
	
	public static ExpeSummary timeOut(String problem, String distrib, String method, long time){
		List<ConsFindingAgentStats> ag=new ArrayList<ConsFindingAgentStats>();
		ag.add(new ConsFindingAgentStats());
		ExpeSummary result=new ExpeSummary(problem, distrib, 0, method, time, 0, ag);
		result.timeOut=true;
		return result;
	}


	public void convertStats(){
		int i,j;
		data=new long[nbAg+4][NB_ID];
		
		for (i=0;i<nbAg+4;i++)
		   for (j=0;j<NB_ID;j++)
			   data[i][j]=0;
		
		
		for (i=0;i<nbAg;i++){
			ConsFindingAgentStats agStat=agentStats.get(i);
			for (j=0;j<NB_ID;j++){
				data[i+4][j]=(Long) agStat.get(getCorrespondingSummaryCode(j));
				data[0][j]+=data[i+4][j];
				if (data[1][j]<data[i+4][j])
					data[1][j]=data[i+4][j];
				if (data[2][j]>data[i+4][j] || i==0)
					data[2][j]=data[i+4][j];
				if (i==root)
					data[3][j]=data[i+4][j];				
			}
		}
		
	}
	
	public static int getCorrespondingSummaryCode(int index){
		switch(index){
		case ID_SOLTIME: return ConsFindingAgentStats.DCF_SOL_CPU_TIME;
		case ID_EXT: return ConsFindingAgentStats.DCF_NB_EXTENSIONS;
		case ID_INF: return ConsFindingAgentStats.DCF_INFERENCESTEPS;
		case ID_CONS_SENT: return ConsFindingAgentStats.DCF_NBCONSEQSENT;
		case ID_CL_SENT: return ConsFindingAgentStats.DCF_NBCLAUSESSENT;
		case ID_NBMESS: return BasicAgentCommStats.SMC_NBSENTMESSAGES;
		case ID_TOTSIZE: return BasicAgentCommStats.SMC_TOTSIZESENT;
		}
		return -1;
	}
	
	public static String getCorrespondingLabel(int index){
		switch(index){
		case ID_SOLTIME: return "CPU time by SOLAR";
		case ID_EXT: return "extensions";
		case ID_INF: return "inferences";
		case ID_CONS_SENT: return "consequences sent";
		case ID_CL_SENT: return "clauses sents";
		case ID_NBMESS: return "nb Messages";
		case ID_TOTSIZE: return "total size messages";
		}
		return "???";
	}
	
	public String toLine(){
		String line="";
		if (!timeOut)
			line+=problem+";"+distrib+";"+nbAg+";"+method+";"+root+";"+nbConsequencesFound+";"+time;
		else
			line+=problem+";"+distrib+";"+nbAg+";"+method+";"+root+";T.O.;"+time;
		for(int i=0;i<4;i++)
			for (int j=0;j<7;j++)
				line+=";"+data[i][j];
		return line;
	}

	public static String labels(){
		String line="";
		line+="problem;distrib;nbAg+;method;root;nbConsequencesFound;total time";
		for (int i=0;i<4;i++){
			String pref="";
			if (i==1)
				pref="max ";
			else if (i==2)
				pref="min ";
			else if (i==3)
				pref="root-";
			for (int j=0;j<7;j++)
				line+=";"+pref+getCorrespondingLabel(j);
		}
		return line;
	}

	
	public List<ConsFindingAgentStats> agentStats;
	public int nbAg;
	public int root;
	
	
	public String problem;
	public String distrib;
	public String method;
	
	public long time;
	public int nbConsequencesFound;
	
	public boolean timeOut=false;
	public long[][] data;
	public static final int ID_SOLTIME=0;
	public static final int ID_EXT=1;
	public static final int ID_INF=2;
	public static final int ID_CONS_SENT=3;
	public static final int ID_CL_SENT=4;
	public static final int ID_NBMESS=5;
	public static final int ID_TOTSIZE=6;
	public static final int NB_ID=7;
	//int[] :
	// 0 : total
	// 1 : Max
	// 2 : Min
	// 3 : Root
	// 4 - 4+n : individual agents
	
	
}

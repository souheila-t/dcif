package agLib.masStats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import genLib.tools.Aggregators;
import genLib.tools.Pair;



public class ExpResult {
	
	/**
	 * 
	 */
	public int getInd(String label){
		for (int i=0;i<labels.size();i++){
			if (labels.get(i).equals(label))
				return i;
		}
		return -1;
	}
	
	
	public List<Number> getValues(String label){
		int ind=getInd(label);
		if (ind>-1) return getValues(ind);
		return null;
	}
	
	public List<Number> getValues(int ind){
		ArrayList<Number> res=new ArrayList<Number>();
		for (List<Number> list:results){
			res.add(list.get(ind));
		}
		return res;
	}
	
	public int addLabel(String label){
		int ind=getInd(label);
		if (ind==-1){
			ind=labels.size();
			labels.add(label);
			for (List<Number> list:results)
				list.add(0);
		}
		return ind;			
	}
	
	public void addResult(List<Pair<String,Number>> expRes){
		// memory init
		ArrayList<Number> newRes=new ArrayList<Number>();
		for (int i=0;i<labels.size();i++) newRes.add(0);
		results.add(newRes);
		// add values, and new labels
		for (Pair<String,Number> p:expRes){
			int ind=addLabel(p.getLeft());
			newRes.set(ind, p.getRight());
		}
	}
	
	public Number getAgg(int ind, int codeAgg){
		Number res=Aggregators.aggregate(this.getValues(ind), codeAgg);
		return res;
	}
	
	public String toString(){
		String res=new String();
		for (String s:labels){
			res+=s+";";
			for (Number n:getValues(s)) res+=n+";";
			res+="\n";
		}		
		return res;
	}
	
	public void stocker(String filename,
			boolean avg, boolean stdDev, boolean max, boolean min) throws IOException {
		PrintWriter fwrite;
		File fichier=new File(filename.concat(".csv"));
		File old=null;
		int i;
		String filename2=new String(filename);
		if (fichier.exists()) {
			boolean found=false;
			i=1;
			while (!(found)) {
				filename2=filename.concat("_old")
				     .concat(Integer.toString(i));
				old=new File(filename2.concat(".csv"));
				if (!(old.exists())) found=true;
				i++;
			}
			fichier.renameTo(old);
		}
		
		fwrite= new PrintWriter(
				  new BufferedWriter(new FileWriter(filename.concat(".csv"))));
		
		fwrite.println(";".concat(filename));
		for (i=0;i<labels.size();i++){
			if (avg) fwrite.println(labels.get(i)+" (avg) ;"+
					getAgg(i, Aggregators.AGG_AVERAGE)+";");
			if (stdDev) fwrite.println(labels.get(i)+" (stddev) ;"+
					getAgg(i, Aggregators.AGG_STDDEV)+";");
			if (min) fwrite.println(labels.get(i)+" (min) ;"+
					getAgg(i, Aggregators.AGG_MIN)+";");
			if (max) fwrite.println(labels.get(i)+" (max) ;"+
					getAgg(i, Aggregators.AGG_MAX)+";");
			}
		fwrite.close();
	}
	
	
	//each run give a list of <label,value> results
	public List<String> labels=new ArrayList<String>();
	public List<List<Number>> results=new ArrayList<List<Number>>();
}

package cnfPb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import logicLanguage.IndepClause;
import logicLanguage.IndepLiteral;

import solarInterface.IndepPField;
import solarInterface.SolProblem;

public class PFieldGenerator {

	public PFieldGenerator(SolProblem source){
		this.source=source;
	}
	
	protected void setVocabulary(){
		List<IndepLiteral> tempVocab=source.getAllClauses().getPredicates();
		int i=0;
		for (IndepLiteral lit:tempVocab){
			vocabulary.add(lit);
			reordering.add(i);i++;
			frequency.add(0);
		}
		setVocab=true;
	}
	
	protected int getIndexLiteral(IndepLiteral lit){
		IndepLiteral seek=lit.getFreedLiteral().getPositiveVersion();
		for (int i=0;i<vocabulary.size();i++){
			IndepLiteral vocab=vocabulary.get(i);
			if (seek.equals(vocab))
				return i;
		}
		return -1;
	}
	
	protected void setFrequency(){
		//assume vocabulary is set.
		for (IndepClause cl:source.getAllClauses())
			for (IndepLiteral lit:cl.getLiterals()){
				int ind=getIndexLiteral(lit);
				if (ind>=0)
					frequency.set(ind, frequency.get(ind)+1);					
			}
		setFreq=true;
	}
	
	protected void setOrdering(boolean increasing, int nbElt){
		int ind, i, extremum, indext;
		if (nbElt==-1)
			nbElt=reordering.size();
		for (ind=0;ind<nbElt;ind++){
			indext=ind;
			extremum=frequency.get(reordering.get(indext));
			for (i=ind;i<reordering.size();i++){
				int weight=frequency.get(reordering.get(i));
				if ((increasing && weight<extremum) || (!increasing && weight>extremum)){
					indext=i;
					extremum=weight;
				}	
			}
			//exchange ind and indext to put extrema at ind
			Integer newExtremum=reordering.remove(indext);
			reordering.add(ind, newExtremum);
		}
	}
	
	public IndepPField setFreqPField(boolean mostRare, double proportion){
		
		if (!setVocab) setVocabulary();
		if (!setFreq) setFrequency();
		
		int nbElt=(int)(proportion*vocabulary.size());
		
		setOrdering(mostRare, nbElt);
		
		List<IndepLiteral> pfLit=new ArrayList<IndepLiteral>();
		for (int i=0;i<nbElt;i++){
			IndepLiteral l=vocabulary.get(reordering.get(i));
			pfLit.add(l);
			pfLit.add(l.negate(false));
		}
		
		output=new IndepPField(pfLit);
		return output;
	}
	
	public IndepPField setGlobalPField(){
		List<IndepLiteral> vocab=source.getAllClauses().getVocabulary();
		output=new IndepPField(vocab);
		return output;
	}
	
	
	public IndepPField output;
	protected boolean setVocab=false;
	protected boolean setFreq=false;
	protected SolProblem source;
	protected List<IndepLiteral> vocabulary=new ArrayList<IndepLiteral>();
	protected List<Integer> frequency=new ArrayList<Integer>();
	protected List<Integer> reordering=new LinkedList<Integer>();
	
}

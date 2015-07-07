package old_logicLanguage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import old_solarInterface.IndepPField;

import org.nabelab.solar.Clause;
import org.nabelab.solar.parser.ParseException;

import genLib.tools.Arguments;


//aim of this class : maintain a list of (indep) clause while guaranteeing
// 1. subsumption minimality
// 2. that all functions of basic class are based on logical equality

public class CNF extends ArrayList<IndepClause>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2365593454984516096L;

	public CNF(){
		super();
	}
	
	public CNF(List<? extends Clause> rules){
		super();
		for (Clause cl:rules)
			add(new IndepClause(cl));
	}
	
	public CNF(IndepClause clause){
		super();
		add(clause);
	}
	
	public CNF(Arguments arg){
		super();
		if (arg==null) return;
		for (String cl:arg)
			add(new IndepClause(cl));
	}
	
	public static CNF copy(CNF toCopy){
		CNF res=new CNF();
		res.addAll(toCopy);
		return res;
	}	
	
	public static CNF singleCNF(IndepClause cl){
		CNF res=new CNF();
		res.add(cl);
		return res;
	}	
	
//	public boolean directAddAll(CNF c){
//		return super.addAll(c);
//	}
//	public boolean directAdd(IndepClause c){
//		return super.add(c);
//	}
	
/*	public boolean addAndReduce(IndepClause cl){
		//do not add tautological clauses (ndcheck for [])
		if (cl==null) return false;
		if (cl.isEmpty()) System.out.println("BEWARE : adding [] to "+this);
		boolean toAdd=true;		
		//	if (cl.isSimpleTautology()) return false;  // isSimpleTautology does not work : it eliminate for example [+a(X),-a(f(X))]
		//test subsumptions between cl and existing clause to preserve subsumption-minimality
			List<IndepClause> toRemove=new ArrayList<IndepClause>();
			for (IndepClause prev:this){
				if (prev.subsumes(cl)){
					toAdd=false;
					break;
				}
			    if (IndepClause.subsumes(cl,prev)) 
				    toRemove.add(prev);
			}
		if (toAdd) super.add(cl);
		if (!toRemove.isEmpty())
			super.removeAll(toRemove);
		return toAdd;	
	}
	
	public boolean addAllAndReduce(Collection <? extends IndepClause> clauses){
		if (clauses instanceof CNF && isEmpty())
			return super.addAll((CNF)clauses);
		boolean added=false;		
		for (IndepClause cl:clauses)
			added=add(cl) || added;
		return added;
	}
*/	

	//TODO COSTLY, to avoid as much as possible
	private boolean addIfNot(IndepClause toAdd, Collection <? extends IndepClause> toExclude, boolean direct){
		boolean shouldAdd=true;
		for (IndepClause e: toExclude)
			if (e.subsumes(toAdd)){
				shouldAdd=false;
				break;
		}
		if (shouldAdd){
			if (direct) return super.add(toAdd);
			return add(toAdd);
		}
		return false;
	}
	
	public boolean addAllExcept(Collection <? extends IndepClause> clausesToAdd, 
			Collection <? extends IndepClause> toExclude){
		boolean direct=(clausesToAdd instanceof CNF && isEmpty());
		boolean added=false;		
		for (IndepClause cl:clausesToAdd)
			added=addIfNot(cl,toExclude,direct) || added;
		return added;
	}
	
	public boolean addAndReduce(IndepClause cl){
		boolean modified=removeAllSubsumedby(cl);
		modified=addIfNot(cl,this,true);
		return modified;
	}
	
	public boolean removeAllSubsumedby(IndepClause toExclude) {
		List<IndepClause> toRemove=new ArrayList<IndepClause>();
		for (IndepClause cl:this)
			if (IndepClause.subsumes(toExclude, cl))
				toRemove.add(cl);
		if (!toRemove.isEmpty()){
			super.removeAll(toRemove);
			return true;
		}
		return false;
	}
	
	public boolean removeAllSubsumedbyAny(List<? extends IndepClause> toExclude) throws ParseException{
		boolean modified=false;
		for (IndepClause cl:toExclude)
			modified=removeAllSubsumedby(cl)|| modified;
		return modified;
	}	
	
	public void supprLiterals(List<IndepLiteral> lits) throws ParseException{
		CNF res=new CNF();
		for (IndepClause cl:this){
			IndepClause ncl=cl.supprLiterals(lits);
			if (!ncl.isEmpty())
				res.add(ncl);
		}
		clear();
		addAll(res);		
	}
	
	public void supprRoot() throws ParseException{
		List<IndepLiteral> lits=new ArrayList<IndepLiteral>();
		lits.add(IndepLiteral.parse("r_root"));
		lits.add(IndepLiteral.parse("-r_root"));		
		supprLiterals(lits);
	}
	
	public boolean retainAll(Collection<? extends Object> c){
		List<IndepClause> toRemove=new ArrayList<IndepClause>();
		for (IndepClause cl:this){
			boolean keep=false;
			for (Object o:c){
				if (cl.isEquiv(o)){
					keep=true;
					break;
				}
			}
			if (!keep) toRemove.add(cl);	
		}
		super.removeAll(toRemove);
		return (!toRemove.isEmpty());
	}
	
	public void removePrefix(String prefix) {
		for (int i=0;i<size();i++) {
			set(i,get(i).removePrefix(prefix));
		}
	}
	
	
	public void restrictToClausesPartlyInPField(IndepPField pf) throws ParseException{
		CNF toRemove=new CNF();
		for (IndepClause cl:this)
			if (!pf.partlyBelongsTo(cl))
				toRemove.add(cl);
		super.removeAll(toRemove);
	}
	
	public UnitClauseCNF retainUnitClauses(){
		UnitClauseCNF res=new UnitClauseCNF();
		res.addAll(this); // non-unit clauses are not added
		return res;
	}
	
	public List<IndepLiteral> getVocabulary(boolean includePos, boolean includeNeg, boolean removeSign){
		List<IndepLiteral> lits=new ArrayList<IndepLiteral>();
		for (IndepClause c:this)
			for (IndepLiteral lit:c.getVocabulary(includePos,includeNeg,removeSign))
				if (!lits.contains(lit))
					lits.add(lit);
		return lits;		
	}
	
	public List<IndepLiteral> getNegatedVocabulary(){
		return getVocabulary(false,true,false);
	}
	public List<IndepLiteral> getVocabulary(){
		return getVocabulary(true,false,false);
	}
	public List<IndepLiteral> getFullVocabulary(){
		return getVocabulary(true,true,false);
	}
	public List<IndepLiteral> getPredicates(){
		return getVocabulary(false,false,true);
	}
	
	
/*	public void restrictToPField(IndepPField pf) throws ParseException{
		CNF toRemove=new CNF();
		for (IndepClause cl:this)
			if (!pf.belongsTo(cl))
				toRemove.add(cl);
		super.removeAll(toRemove);
	}
*/
	
	public static boolean isEquiv(CNF l1, CNF l2){
		if (l1==l2) return true;
		if ((l1==null) || (l2==null)) return false;
		if (l1.size()!=l2.size()) return false;
		for (IndepClause cl1:l1){
			boolean test=false;
			for (IndepClause cl2:l2) 
				if (IndepClause.isEquiv(cl1,cl2)) {
					test=true;
					break;
			}
			if (!test) return false;
		}
		return true;
	}
	
	public boolean isEquiv(Object c){
		if (c==null || c instanceof CNF) 
			return isEquiv(this,(CNF)c);
		return false;
	}
	

}

package logicLanguage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.pfield.PField;

import solarInterface.IndepPField;
import genLib.tools.Arguments;


//aim of this class : maintain a list of (indep) clause while guaranteeing
// 1. subsumption minimality
// 2. that all functions of basic class are based on logical equality

public class CNF extends ArrayList<Clause>{

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
			add(cl);
	}
	
	public CNF(Clause clause){
		super();
		add(clause);
	}
	
	public CNF(Env env, Options opt, Arguments arg) throws ParseException{
		super();
		if (arg==null) return;
		for (String cl:arg)
			add(Clause.parse(env, opt, cl));
	}
	
	public static CNF copy(CNF toCopy){
		CNF res=new CNF();
		res.addAll(toCopy);
		return res;
	}	
	
	public static CNF copy(List<Clause> toCopy){
		CNF res=new CNF();
		res.addAll(toCopy);
		return res;
	}	
	
	public static CNF singleCNF(Clause cl){
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
	private boolean addIfNot(Clause toAdd, Collection <? extends Clause> toExclude, boolean direct){
		boolean shouldAdd=true;
		for (Clause e: toExclude)
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
	
	public boolean addAllExcept(Collection <? extends Clause> clausesToAdd, 
			Collection <? extends Clause> toExclude){
		boolean direct=(clausesToAdd instanceof CNF && isEmpty());
		boolean added=false;		
		for (Clause cl:clausesToAdd)
			added=addIfNot(cl,toExclude,direct) || added;
		return added;
	}
	
	public boolean addAndReduce(Clause cl){
		boolean modified=removeAllSubsumedby(cl);
		modified=addIfNot(cl,this,true);
		return modified;
	}
	
	public boolean removeAllSubsumedby(Clause toExclude) {
		List<Clause> toRemove=new ArrayList<Clause>();
		for (Clause cl:this)
			if (IndepClause.subsumes(toExclude, cl))
				toRemove.add(cl);
		if (!toRemove.isEmpty()){
			super.removeAll(toRemove);
			return true;
		}
		return false;
	}
	
	public boolean removeAllSubsumedbyAny(List<? extends Clause> toExclude) throws ParseException{
		boolean modified=false;
		for (Clause cl:toExclude)
			modified=removeAllSubsumedby(cl)|| modified;
		return modified;
	}	
	
	public void supprLiterals(Env env, List<Literal> lits) throws ParseException{
		CNF res=new CNF();
		for (Clause cl:this){
			Clause ncl=IndepClause.supprLiterals(env, cl, lits);
			if (!ncl.isEmpty())
				res.add(ncl);
		}
		clear();
		addAll(res);		
	}
	
	public void supprRoot(Env env, Options opt) throws ParseException{
		List<Literal> lits=new ArrayList<Literal>();
		lits.add(Literal.parse(env, opt, "r_root"));
		lits.add(Literal.parse(env, opt, "-r_root"));		
		supprLiterals(env, lits);
	}
	
	public boolean retainAll(Collection<? extends Object> c){
		List<Clause> toRemove=new ArrayList<Clause>();
		for (Clause cl:this){
			boolean keep=false;
			for (Object o:c){
				if (cl.equals(o)){
					keep=true;
					break;
				}
			}
			if (!keep) toRemove.add(cl);	
		}
		super.removeAll(toRemove);
		return (!toRemove.isEmpty());
	}
	
	public void removePrefix(Env env, String prefix) throws ParseException {
		for (int i=0;i<size();i++) {
			set(i,IndepClause.removePrefix(env, get(i), prefix));
		}
	}
	
	
	public void restrictToClausesPartlyInPField(Env env, PField pf) throws ParseException{
		CNF toRemove=new CNF();
		for (Clause cl:this)
			if (!IndepPField.partlyBelongsTo(env, pf, cl))
				toRemove.add(cl);
		super.removeAll(toRemove);
	}
	
	public UnitClauseCNF retainUnitClauses(){
		UnitClauseCNF res=new UnitClauseCNF();
		res.addAll(this); // non-unit clauses are not added
		return res;
	}
	
	public static List<Literal> getVocabulary(Env env, List<Clause> clauses, boolean includePos, boolean includeNeg, boolean removeSign) throws ParseException{
		List<Literal> lits=new ArrayList<Literal>();
		for (Clause c:clauses)
			for (Literal lit:IndepClause.getVocabulary(env, c, includePos,includeNeg,removeSign))
				if (!lits.contains(lit))
					lits.add(lit);
		return lits;		
	}
	
	public static List<Literal> getNegatedVocabulary(Env env, List<Clause> clauses) throws ParseException{
		return getVocabulary(env, clauses, false,true,false);
	}
	public static List<Literal> getVocabulary(Env env, List<Clause> clauses) throws ParseException{
		return getVocabulary(env, clauses, true,false,false);
	}
	public static List<Literal> getFullVocabulary(Env env, List<Clause> clauses) throws ParseException{
		return getVocabulary(env, clauses, true,true,false);
	}
	public static List<Literal> getPredicates(Env env, List<Clause> clauses) throws ParseException{
		return getVocabulary(env, clauses, false,false,true);
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
		for (Clause cl1:l1){
			boolean test=false;
			for (Clause cl2:l2) 
				if (cl1.equals(cl2)) {
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

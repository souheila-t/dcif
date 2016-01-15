package old_logicLanguage;

import java.util.ArrayList;
import java.util.List;


import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;

import agLib.masStats.StatCounter;

import genLib.tools.Arguments;



public final class IndepClause {
	
	public static StatCounter<?> counter;
	
	public IndepClause(String name,List<IndepLiteral> lits){
		this.name=name;
		literals.addAll(lits);
	//	reduce();
	}
	
	public IndepClause(Clause cl){
		name=cl.getName();
		for(Literal lit:cl.getLiterals()){
			IndepLiteral ilit=IndepLiteral.parse(lit.toString());
			literals.add(ilit);
		}
	//	reduce();
	}
	
	public IndepClause(String clauseStrRepr){
		this("unnamed_clause",clauseStrRepr);
	}
	
	public IndepClause(String name, String clauseStrRepr){
		this.name=name;
		Arguments arg=Arguments.parse(clauseStrRepr);
		for (String strLit:arg){
			IndepLiteral ilit=IndepLiteral.parse(strLit);
			literals.add(ilit);			
		}
	//	reduce();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public IndepClause rename(String newName){
		return new IndepClause(newName,getLiterals());
	}
	
	public static boolean subsumes(IndepClause c1, IndepClause c2){
		Env env=new Env();
		Clause c1b=c1.toClause(env);
		Clause c2b=c2.toClause(env);
		c2b.setOffset(c1b.getNumVars()+1);
		boolean res=c1b.subsumes(c2b);
		if (counter!=null)
			counter.inc(c1b.getLiterals().size()+c2b.getLiterals().size());
		return res;
	}
	
	public boolean subsumes(IndepClause c2){
		return subsumes(this, c2);
	}
	
	public static boolean isEquiv(IndepClause c1, IndepClause c2){
		if (c1==c2) return true;
		if (c1==null || c2==null) return false;//cannot be both null given previous test
		return c1.subsumes(c2) && c2.subsumes(c1);
	}
	public boolean isEquiv(Object cl){
		if (cl==null || cl instanceof IndepClause) 
			return isEquiv(this,(IndepClause)cl);
		return false;
	}
	
	// reduction by subsumption with independant variable
	// means that [a(X,Y), -g(X), -g(Y)] would become [a(X,Y), -g(X)] 
	// as -g(Y) would be subsumed by -g(X) when considering independant variables
	public IndepClause reduce(){
		if (literals.size()<2) return this;
		List<IndepLiteral> newLits=new ArrayList<IndepLiteral>();
		List<IndepLiteral> toRemove=new ArrayList<IndepLiteral>();
		for (IndepLiteral add:literals){
			boolean toAdd=true;
			for (IndepLiteral prev:newLits){
				if (prev.subsumes(add)){
					toAdd=false;
					break;
				}
			    if (add.subsumes(prev)) 
				    toRemove.add(prev);
			}
			newLits.removeAll(toRemove);
			if (toAdd) newLits.add(add);
		}
		return new IndepClause(name,newLits);
	}

	
	public Clause toClause(Env env) {
		Options opt=new Options(env);
		if (literals.isEmpty())
			return new Clause(env,new ArrayList<Literal>());
		try {
			return Clause.parse(env,opt, toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Clause toClause(Env env,int type){
		Clause cl=toClause(env);
		cl.setType(type);
		return cl;
	}
	
	public String toSolFileLine(String type){
		return "cnf("+name+", "+type+", "+literals+").";
	}
	public List<IndepLiteral> getLiterals() {
		return literals;
	}
	
	public IndepClause supprLiteral(IndepLiteral lit) throws ParseException{
		List<IndepLiteral> newLits=new ArrayList<IndepLiteral>();
		for (IndepLiteral prevLit:getLiterals()){
			if (!lit.subsumes(prevLit))
				newLits.add(prevLit);
		}
		return new IndepClause(name, newLits);
	}
	
	public IndepClause supprLiterals(List<IndepLiteral> lits) throws ParseException{
		IndepClause temp=this;
		for (IndepLiteral lit:lits){
			temp=temp.supprLiteral(lit);
		}
		return temp;
	}
	
	public int countOccurences(IndepLiteral freeLit){
		int occ=0;
		for (IndepLiteral lit:getLiterals()){
			if (freeLit.toString().equals(lit.getFreedLiteral().toString()))
				occ++;
		}
		return occ;
	}

	public int countOccurences(List<IndepLiteral> freeLits){
		int occ=0;
		for (IndepLiteral lit:getLiterals()){
			if (freeLits.contains(lit.getFreedLiteral()))
				occ++;
		}
		return occ;
	}

	
	//only compare Strings
	public boolean equals(Object o){
		if (o!=null && o instanceof IndepClause){
			if (o==this || toString().equals(o.toString()))
				return true;
		}
		return false;
	}
	
	public boolean equiv(Object o){
		if (o!=null && o instanceof IndepClause){
			if (o==this || toString().equals(o.toString()))
				return true;
			Env env=new Env();
			IndepClause cl=(IndepClause)o;
			if (toClause(env).toString().equals(cl.toClause(env).toString()))
				return true;
			if (subsumes(cl))
				return cl.subsumes(this);
		}
		return false;
	}
	
	private UnitClauseCNF getNegation(){
		UnitClauseCNF res=new UnitClauseCNF();
		for (IndepLiteral lit:getLiterals()){
			IndepClause cl=new IndepClause("["+lit.negate(false)+"]");
			res.add(cl);
		}
		return res;
	}

	public UnitClauseCNF getNegation(boolean skolemize){
		if (!skolemize)
			return getNegation();
		IndepClause sk=skolemize();
		return sk.getNegation();
	}

	private IndepClause skolemize(){
		Env env=new Env();
		Clause cl=toClause(env);
		String s=cl.toString();
		int nbVar=cl.getNumVars();
		for (int i=nbVar-1;i>=0;i--){
			int value=IndepLiteral.skolemNumber+i;
			s=s.replaceAll("_"+i, "skolem"+value);
		}
		IndepLiteral.skolemNumber+=nbVar;
		return new IndepClause(s);
	}	

	
	//check if Clause contains +lit and -lit
	// can happen with conversion clause [-pred[(X,a,Y)](X,Y),+pred(X,a,Y)]
	// who are cleaned by builder to become [-pred(X,a,Y),+pred(X,a,Y)]
	public boolean isSimpleTautology() throws ParseException{
		//Principle: if clause contains +lit and -lit, it is subsumed by -lit
		// ie it is subsumed by the negation of one of its literals
		if (getLiterals().size()<2) return false;
		UnitClauseCNF negatedLiterals=this.getNegation(false);
		for (IndepClause negLit:negatedLiterals){
			if (negLit.subsumes(this))
				return true;
		}
		return false;
	}
	
	public boolean isEmpty(){
		return literals.isEmpty();
	}
	/**
	 * Return a clause premise -> consequent
	 * @param premise a string which can be of 3 kinds
	 *  1. an empty String "", in which case the rule -> consequent is created
	 *  2. a literal/Pliteral (in which case, it does not begin by "[")
	 *  3. a conjonction, ie a UnitClauseCNF (form [[lit0],[lit1],...) where lits are Literals 
	 *  		and not PLiterals
	 * @param consequent a string representing one of 3 things
	 *   1. an empty String "" (created the rule "premises ->", or emptyClause if premise also empty 
	 *   2. a literal or Pliteral (in which case it does not being by"[")
	 *   3. a clause (form [lit0,lit1,lit2,...]) where lits are Literals and not PLiterals
	 * @return a list of Indep clause that represent the rule premise->consequent
	 * @throws ParseException 
	 * @throws ParseException 
	 */
	public static IndepClause newEntailmentRule(String name, UnitClauseCNF premise, IndepClause consequent){
		List<IndepLiteral> newLiterals=new ArrayList<IndepLiteral>();
		IndepClause negatedPremise;
		try {
			negatedPremise = premise.getNegation(false); //TODO check skolemization
			newLiterals.addAll(negatedPremise.getLiterals());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		newLiterals.addAll(consequent.getLiterals());
		return new IndepClause(name, newLiterals);
	}


	
	//	public static List<IndepClause> newEntailmentRule(String premise, String consequent) throws ParseException{
//		Arguments developedNegatedPremises=computePremise(premise);
//		Arguments developedConsequent=computeConsequent(consequent);
//		//int size=developedNegatedPremises.size()*developedConsequent.size();
//		List<IndepClause> result=new ArrayList<IndepClause>();
//		for (String prem:developedNegatedPremises)
//			for (String cons:developedConsequent){
//				String repr="[";
//				if (prem.length()>0) {
//					repr+=prem;
//					if (cons.length()>0) repr+=", ";
//				}
//				repr+=cons+"]";
//			//	System.out.println(" entailmentRule ["+prem+", "+cons+"]");
//				result.add(new IndepClause("["+prem+", "+cons+"]"));
//		}
//		return result;
//	}	
//	private static Arguments computeConsequent(String consequent){
//		if (consequent.length()==0) {
//			Arguments res=new Arguments();
//			res.add("");
//			return res;
//		}
//		if (consequent.startsWith("["))
//			// note: return string "lit0,lit1,...litn" (should not return it as separate list)
//			return new Arguments(consequent.substring(1,consequent.length()-1));
//		//otherwise, it is a Literal or a PLiteral
//		return IndepPLiteral.developPLiteral(consequent);
//	}	
//	private static Arguments computePremise(String premise) throws ParseException{
//		if (premise.length()==0) {
//			Arguments res=new Arguments();
//			res.add("");
//			return res;
//		}
//		if (premise.startsWith("[[")) 
//			return computeConjonctivePremise(premise);
//		// otherwise, it is a Literal or a PLiteral : negate, translate and develop
//		Arguments result=IndepPLiteral.developPLiteral(
//							IndepPLiteral.toQuasiLiteral(
//								IndepPLiteral.negate(premise)));
//		return result;
//	}	
//	private static Arguments computeConjonctivePremise(String premise) throws ParseException{
//		//form should be [[lit0],[lit1],...]
//		//get list of unitClauses
//		Arguments negation=new Arguments();
//		Arguments unitClauses=Arguments.parse(premise, true);
//		for (String cl:unitClauses){
//			String lit=cl.substring(1,cl.length()-1);
//			String neglit=IndepPLiteral.negate(lit);
//			negation.add(neglit);
//		}
//		Arguments result=new Arguments();
//		result.add(negation.toString().substring(1,negation.toString().length()-1));
//		return result;
//	}
	
	
	public IndepClause removePrefix(String prefix) {
		List<IndepLiteral> newLits=new ArrayList<IndepLiteral>();
		for(IndepLiteral lit:getLiterals()){
			newLits.add(lit.removePrefix(prefix));
		}
		return new IndepClause(name,newLits); 
	}

	
	protected List<IndepLiteral> getVocabulary(boolean includeOriginal, boolean includeNegated, boolean removeSign){
		List<IndepLiteral> newLits=new ArrayList<IndepLiteral>();
		for(IndepLiteral lit:getLiterals()){
			IndepLiteral flit=lit.getFreedLiteral();
			if ((includeOriginal||(removeSign&&flit.isPositive()))    && !newLits.contains(flit))
				newLits.add(flit);
			if (includeNegated || (removeSign&&!flit.isPositive())){
				flit=flit.negate(false);
				if (!newLits.contains(flit))
					newLits.add(flit);
			}	
		}
		return newLits;
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

	protected List<IndepLiteral> getPredicates(){
		return getVocabulary(false,false,true);
	}

	
	public IndepClause addLiteral(IndepLiteral lit){
		List<IndepLiteral> newLits=new ArrayList<IndepLiteral>();
		newLits.addAll(literals);
		newLits.add(lit);
		return new IndepClause(name,newLits);
	}
	
	public IndepClause addLiterals(List<IndepLiteral> lit){
		List<IndepLiteral> newLits=new ArrayList<IndepLiteral>();
		newLits.addAll(literals);
		newLits.addAll(lit);
		return new IndepClause(name,newLits);
	}
	
	public String toString(){
		return literals.toString();
	}
	
	
	String name;
	
	List<IndepLiteral> literals= new ArrayList<IndepLiteral>();
	
	
	
	
	
	public static void main(String[] args) throws ParseException{
		//test of every function
		IndepClause[] p=new IndepClause[10];
		p[0]=new IndepClause("[]");
		p[1]=new IndepClause("[f(1,_,t(_,_))]");
		p[2]=new IndepClause("[f(1,_,t(4,4)),-ef(h)]");
		p[3]=new IndepClause("[-f(_,_,_),+f(_,_,_)]");
		p[4]=new IndepClause("[-f(1,h(_),t(d4,d5))]");
		p[5]=new IndepClause("[f(1,_,_),f(1,5,5),-ef(h)]");
		p[6]=new IndepClause("[f[1,_0,_1](_0,_1),-ef(h)]");
		p[7]=new IndepClause("[$]");
		p[8]=new IndepClause("[ar,f(1,_,_),-ef(h),-f(1,5,5)]");
		
		//to Test :
		// Builder (Clause)
		// subsumes
		// isEquiv
		// toClause
		// getLiterals
		//supprLiteral
		//supprLiterals
		//getNegation
		//isEmpty
		//isSimpleTautology
		//newEntailmentRule
		for (int i=0;i<10;i++){
			System.out.println("p["+i+"] : "+p[i]);
			if (p[i]!=null){
				Env env=new Env();
				Clause c1=p[i].toClause(env);
				System.out.println("toClause :"+c1);
				p[i].reduce();
				System.out.println("reduce() :"+p[i]);
				List<IndepLiteral> plits=p[i].getLiterals();
				System.out.println("getLiterals() :"+plits);				
				System.out.println("isEmpty : "+p[i].isEmpty());
				UnitClauseCNF neg=p[i].getNegation(false);
				System.out.println("getNegation : "+neg);				
				System.out.println("isSimpleTautology : "+p[i].isSimpleTautology());
				System.out.println("supprLiteral(f(_,_,_)) : "+
						p[i].supprLiteral(IndepLiteral.parse("f(_,_,_)")));
				System.out.println("supprLiteral : "+p[i].supprLiterals(plits));
				for (int j=0;j<9;j++){
					//String t=p[j].toString();
					System.out.println("subsumes("+p[i]+", "+p[j]+") :"+subsumes(p[i],p[j]));
				}
				for (int j=0;j<9;j++){
					//String t=p[j].toString();
					System.out.println("isEquiv("+p[i]+", "+p[j]+") :"+isEquiv(p[i],p[j]));
				}
				System.out.println();
			}
			
		}
		
	}
	

	
	
}

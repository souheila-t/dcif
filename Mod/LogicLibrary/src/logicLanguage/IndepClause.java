package logicLanguage;

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.ClauseTypes;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;

import agLib.masStats.StatCounter;
import genLib.tools.Arguments;



public final class IndepClause {
	
	public static StatCounter<?> counter;
	
	/*
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
	}*/

	/*
	 * @return the name
	 */
	/*public String getName() {
		return name;
	}*/

	public static Clause rename(Env env, Clause clause, String newName){
		return new Clause(env, newName, clause.getType(), clause.getLiterals());
	}
	
	public static boolean subsumes(Clause c1, Clause c2){;
		boolean res=c1.subsumes(c2);
		if (counter!=null)
			counter.inc(c1.getLiterals().size()+c2.getLiterals().size());
		return res;
	}
	
	/*
	public boolean subsumes(Clause c2){
		return subsumes(this, c2);
	}*/
	/*
	public static boolean isEquiv(Clause c1, Clause c2){
		return Clause.equals(c1, c2);
	}
	
	
	public boolean isEquiv(Object cl){
		if (cl==null || cl instanceof IndepClause) 
			return isEquiv(this,(IndepClause)cl);
		return false;
	}*/
	
	// reduction by subsumption with independant variable
	// means that [a(X,Y), -g(X), -g(Y)] would become [a(X,Y), -g(X)] 
	// as -g(Y) would be subsumed by -g(X) when considering independant variables
	public static Clause reduce(Env env, Clause clause) throws ParseException{
		if (clause.getLiterals().size()<2) return clause;
		List<Literal> newLits=new ArrayList<Literal>();
		List<Literal> toRemove=new ArrayList<Literal>();
		for (Literal add:clause.getLiterals()){
			boolean toAdd = true;
			for (Literal prev:newLits){
				if (IndepLiteral.subsumes(prev, add)){
					toAdd=false;
					break;
				}
			    if (IndepLiteral.subsumes(add, prev)) 
				    toRemove.add(prev);
			}
			newLits.removeAll(toRemove);
			if (toAdd) newLits.add(add);
		}
		return new Clause(env, clause.getName(), clause.getType(), newLits);
	}

	/*
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
	}*/
	
	public static String toSolFileLine(Clause clause, String type){
		return "cnf("+clause.getName()+", "+type+", "+clause.getLiterals()+").";
	}
	/*
	public List<IndepLiteral> getLiterals() {
		return literals;
	}*/
	
	public static Clause supprLiteral(Env env, Clause clause, Literal lit) throws ParseException{
		List<Literal> newLits=new ArrayList<Literal>();
		for (Literal prevLit:clause.getLiterals()){
			if (lit.subsumes(prevLit) != null)
				newLits.add(prevLit);
		}
		return new Clause(env, clause.getName(), clause.getType(), newLits);
	}
	
	public static Clause supprLiterals(Env env, Clause clause, List<Literal> lits) throws ParseException {
		Clause temp = clause;
		for (Literal lit:lits){
			temp = supprLiteral(env, clause, lit);
		}
		return temp;
	}
	
	public static int countOccurences(Env env, Clause clause, Literal freeLit) throws ParseException{
		int occ=0;
		for (Literal lit:clause.getLiterals()){
			if (freeLit.toString().equals(IndepLiteral.getFreedLiteral(env, lit).toString()))
				occ++;
		}
		return occ;
	}

	public static int countOccurences(Env env, Clause clause, List<Literal> freeLits) throws ParseException{
		int occ=0;
		for (Literal lit:clause.getLiterals()){
			if (freeLits.contains(IndepLiteral.getFreedLiteral(env, lit)))
				occ++;
		}
		return occ;
	}

	
	//only compare Strings
	/*
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
	}*/
	
	private static UnitClauseCNF getNegation(Env env, Clause clause) throws ParseException{
		UnitClauseCNF res = new UnitClauseCNF();
		for (Literal lit:clause.getLiterals()){
			Clause cl = Clause.parse(env, new Options(env), "unnamed_clause", ClauseTypes.AXIOM, "["+IndepLiteral.negate(lit)+"]");
			res.add(cl);
		}
		return res;
	}

	public static UnitClauseCNF getNegation(Env env, Clause clause, boolean skolemize) throws ParseException{
		if (!skolemize)
			return getNegation(env, clause);
		Clause sk=skolemize(env, clause);
		return getNegation(env, sk);
	}

	private static Clause skolemize(Env env, Clause clause) throws ParseException{
		String s=clause.toString();
		int nbVar=clause.getNumVars();
		for (int i=nbVar-1;i>=0;i--){
			int value=IndepLiteral.skolemNumber+i;
			s=s.replaceAll("_"+i, "skolem"+value);
		}
		IndepLiteral.skolemNumber+=nbVar;
		return Clause.parse(env, new Options(env), s);
	}	

	
	//check if Clause contains +lit and -lit
	// can happen with conversion clause [-pred[(X,a,Y)](X,Y),+pred(X,a,Y)]
	// who are cleaned by builder to become [-pred(X,a,Y),+pred(X,a,Y)]
	public static boolean isSimpleTautology(Env env, Clause clause) throws ParseException{
		//Principle: if clause contains +lit and -lit, it is subsumed by -lit
		// ie it is subsumed by the negation of one of its literals
		if (clause.getLiterals().size()<2) return false;
		UnitClauseCNF negatedLiterals = getNegation(env, clause, false);
		for (Clause negLit:negatedLiterals){
			if (negLit.subsumes(clause))
				return true;
		}
		return false;
	}
	/*
	public boolean isEmpty(){
		return literals.isEmpty();
	}*/
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
	public static Clause newEntailmentRule(Env env, String name, UnitClauseCNF premise, Clause consequent){
		List<Literal> newLiterals=new ArrayList<Literal>();
		Clause negatedPremise;
		try {
			negatedPremise = premise.getNegation(env, false); //TODO check skolemization
			newLiterals.addAll(negatedPremise.getLiterals());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		newLiterals.addAll(consequent.getLiterals());
		return new Clause(env, name, ClauseTypes.AXIOM, newLiterals);
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
	
	
	/**
	 * @throws ParseException */
	public static Clause removePrefix(Env env, Clause clause, String prefix) throws ParseException {
		List<Literal> newLits=new ArrayList<Literal>();
		for(Literal lit:clause.getLiterals()){
			newLits.add(IndepLiteral.removePrefix(env, prefix, lit));
		}
		return new Clause(env, clause.getName(), clause.getType(), newLits); 
	}

	/**
	 * @throws ParseException */
	protected static List<Literal> getVocabulary(Env env, Clause clause, boolean includeOriginal, boolean includeNegated, boolean removeSign) throws ParseException{
		List<Literal> newLits=new ArrayList<Literal>();
		for(Literal lit:clause.getLiterals()){
			Literal flit=IndepLiteral.getFreedLiteral(env, lit);
			if ((includeOriginal||(removeSign&&flit.isPositive()))    && !newLits.contains(flit))
				newLits.add(flit);
			if (includeNegated || (removeSign&&!flit.isPositive())){
				flit.negate();
				if (!newLits.contains(flit))
					newLits.add(flit);
			}	
		}
		return newLits;
	}
	
	
	public static List<Literal> getNegatedVocabulary(Env env, Clause clause) throws ParseException{
		return getVocabulary(env, clause, false,true,false);
	}
	public static List<Literal> getVocabulary(Env env, Clause clause) throws ParseException{
		return getVocabulary(env, clause, true,false,false);
	}
	public static List<Literal> getFullVocabulary(Env env, Clause clause) throws ParseException{
		return getVocabulary(env, clause, true,true,false);
	}

	protected static List<Literal> getPredicates(Env env, Clause clause) throws ParseException{
		return getVocabulary(env, clause, false,false,true);
	}


	public static Clause addLiteral(Env env, Clause clause, Literal lit){
		List<Literal> newLits=new ArrayList<Literal>();
		newLits.addAll(clause.getLiterals());
		newLits.add(lit);
		return new Clause(env, clause.getName(), clause.getType(),newLits);
	}
	
	public static Clause addLiterals(Env env, Clause clause, List<Literal> lit){
		List<Literal> newLits=new ArrayList<Literal>();
		newLits.addAll(clause.getLiterals());
		newLits.addAll(lit);
		return new Clause(env, clause.getName(), clause.getType(),newLits);
	}
	
	/*
	public String toString(){
		return literals.toString();
	}
	
	
	String name;
	
	List<IndepLiteral> literals= new ArrayList<IndepLiteral>();
	*/
	
	
	
	
	public static void main(String[] args) throws ParseException{
		//test of every function
		IndepClause[] p=new IndepClause[10];
		/*
		p[0]=new IndepClause("[]");
		p[1]=new IndepClause("[f(1,_,t(_,_))]");
		p[2]=new IndepClause("[f(1,_,t(4,4)),-ef(h)]");
		p[3]=new IndepClause("[-f(_,_,_),+f(_,_,_)]");
		p[4]=new IndepClause("[-f(1,h(_),t(d4,d5))]");
		p[5]=new IndepClause("[f(1,_,_),f(1,5,5),-ef(h)]");
		p[6]=new IndepClause("[f[1,_0,_1](_0,_1),-ef(h)]");
		p[7]=new IndepClause("[$]");
		p[8]=new IndepClause("[ar,f(1,_,_),-ef(h),-f(1,5,5)]");*/
		
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
		/**
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
			
		}*/
		
	}
	

	
	
}

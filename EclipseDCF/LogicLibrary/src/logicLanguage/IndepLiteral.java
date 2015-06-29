package logicLanguage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.ClauseTypes;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.parser.ParseException;

import solarInterface.IndepPLiteral;
import solarInterface.Tools;
import genLib.tools.Arguments;

public final class IndepLiteral {
	
	
	/*
	public IndepLiteral(boolean sign, String predicate, List<String> arguments) {
		this.arguments = Collections.unmodifiableList(new ArrayList<String>(arguments));
		this.predicate = predicate;
		this.sign = sign;
	}

	public static IndepLiteral parse(String literal){
		String pred;
		Boolean sign;
		if (literal==null || literal.equals("")){
			pred="u_void";
			sign=true;
			return new IndepLiteral(sign,pred,new ArrayList<String>());
		}			
		String lit=Tools.cleanText(literal.trim(),false);
		//extract sign
		if (lit.charAt(0)=='-') 
			sign=false;
		else sign=true;
		//extract predicate and arguments
		int startPred=1;
		if (lit.charAt(0)!='-' && lit.charAt(0)!='+')
			startPred=0;
		pred=lit.substring(startPred);
		int p_arg=pred.indexOf('(');
		if (p_arg<0) return new IndepLiteral(sign,pred,new ArrayList<String>());
		String args=pred.substring(p_arg+1, pred.lastIndexOf(')'));
		pred=pred.substring(0, p_arg);
		Arguments arg=Arguments.parse("["+args+"]");
		return new IndepLiteral(sign,pred,arg);
	}*/
		
	/**Gets the predicate of the literal.
	 * @param env The environment.
	 * @param literal The literal.*/
	public static String getPredicate(Env env, Literal literal){
		return env.getSymTable().get(literal.getName(), literal.getTerm().getStartType());
	}
	
	/**Gets the arguments of the literal.
	 * @param literal The literal.*/
	public static List<String> getArguments(Literal literal){
		List<String> s = new ArrayList<String>();
		for(int i = 0; i < literal.getArity(); i++)
			s.add(literal.getArg(i).toString());
		return s;
	}
	
	/***/
	public static String getPredicateSignature(Env env, Literal literal){
		return getPredicate(env, literal)+"\\"+literal.getArity();
	}
	/*
	public int getArity(){
		return arguments.size();
	}
	
	public String getPredicateSignature(){
		return predicate+"\\"+getArity();
	}*/
	
	public static Literal getFreedLiteral(Env env, Literal literal) throws ParseException{
		return getFreedLiteral( env, new Options(env), literal);
	}
	
	/**Returns this literal without bindings.
	 * @param env The environment.
	 * @param literal Literal.
	 * @throws ParseException */
	public static Literal getFreedLiteral(Env env, Options opt, Literal literal) throws ParseException{
		String free = getPredicate(env, literal);
		if(literal.getArity() > 0) {
			free += "(";
			for(int i = 0; i < literal.getArity(); i++)
				free += "_" + i + ",";
			free = free.substring(0,free.length()-1)+")";
		}
		Literal l = Literal.parse(env, opt, free);
		IndepLiteral.setSign(l, literal.getSign());
		return l;
	}
	
	/**Returns a positive version of the literal.
	 * @param literal The literal.
	 * @throws ParseException */
	public static Literal getPositiveVersion(Literal literal) throws ParseException{
		Literal l = new Literal(literal);
		if (literal.isPositive()) 
			return l;
		l.negate();
		return l;
	}
	
	/**Returns the literal's term.
	 * @param literal The literal.*/
	public static String getTerm(Literal literal) {
		return literal.getTerm().toString();
	}

	/**Adds a prefix to a literal's predicate.
	 * @param env The environment.
	 * @param prefix Prefix to be added.
	 * @param cumul Prefix will be added on top of previous ones if true.
	 * @param literal The literal.
	 * @throws ParseException*/
	public static Literal addPrefix(Env env, String prefix, boolean cumul, Literal literal) throws ParseException{
		String pred=getPredicate(env, literal);
		if (cumul || !pred.startsWith(prefix))
				pred=prefix+pred;
		if(!literal.getSign())
			pred = "-"+pred;
		return Literal.parse(env, new Options(env), pred);
	}
	
	/**Removes a prefix from a literal's predicate.
	 * @param env The environment.
	 * @param prefix Prefix to be removed.
	 * @param literal The literal.
	 * @throws ParseException*/
	public static Literal removePrefix(Env env, String prefix, Literal literal) throws ParseException{
		String newPred;
		String pred=getPredicate(env, literal);
		if (pred.startsWith(prefix))
			newPred=pred.substring(prefix.length());
		else
			newPred=pred;
		return Literal.parse(env, new Options(env), newPred);
	}
		
	
	/**Returns a Literal that is the negation of the argument.
	 * @param literal The literal.
	 * @throws ParseException */
	public static Literal negate (Literal literal)  {
		Literal l = null;
		try {
			l = negate(null, false, literal);
		} catch (ParseException e) {
			// Do nothing, this never happens
		}
		return l;
	}
	
	/**Returns a Literal that is the negation of a literal.
	 * @param env The environment.
	 * @param skolemize If true returns a skolemized version of that.
	 * @param literal The literal.
	 * @throws ParseException */
	public static Literal negate(Env env, boolean skolemize, Literal literal) throws ParseException{
		Literal lit;
		if (skolemize){
			lit = skolemize(env, literal);
			lit = Literal.parse(env, new Options(env), getTerm(lit));
		}else
			lit = new Literal(literal);
		lit.negate();
		return lit;
	}
	
	/**Returns a new Literal that's the Skolemized version of a literal.
	 * @param env The environment
	 * @param literal The literal
	 * @throws ParseException*/
	private static Literal skolemize(Env env, Literal literal) throws ParseException{
		String s = literal.toString();
		int nbVar = literal.getNumVars();
		for (int i=nbVar-1;i>=0;i--){
			int value=skolemNumber+i;
			s=s.replaceAll("_"+i, "skolem"+value);
		}
		skolemNumber+=nbVar;
		return Literal.parse(env, new Options(env), s);
	}	
/*		
		int pointer=0;
		int minPointer=0;
		String num;
		String res=s;
		int nextVarBegin, value;
		int max=skolemNumber;
		while (s.indexOf('_', pointer)>=0){
			nextVarBegin=s.indexOf('_', pointer);
			if (Character.isDigit(s.charAt(nextVarBegin)+1)){
				pointer=nextVarBegin+1;
				while (pointer<s.length() && Character.isDigit(s.charAt(pointer)))
					pointer++;
				num=s.substring(nextVarBegin+1,pointer);
				value=Integer.parseInt(num)+skolemNumber;
				if (value>max) max=value;
				s=s.replaceAll("_"+num, "skolem"+value);
				pointer=minPointer;
			}
			else {
				pointer=nextVarBegin+1;
				minPointer=pointer;
			}
		}*/
		
		
		/*
	public Literal toLiteral(Env env) {
		Options opt=new Options(env);
		Literal lit;
		try {
			lit = Literal.parse(env, opt, Tools.cleanText(getTerm(),false));
			if (!isPositive())
				lit.negate();
			return lit;
		} catch (ParseException e) {e.printStackTrace();}
		//sign
		return null;
	}		*/
	
	/**Creates Clause with this Literal.
	 * @param env The environment.
	 * @param literal The literal.
	 * @throws ParseException*/
	public static Clause toClause(Env env, Literal literal) throws ParseException{
		List<Literal> lits=new ArrayList<Literal>();
		lits.add(literal);
		return new Clause(env, "unitLit", ClauseTypes.AXIOM, lits);
	}
	
	public static UnitClauseCNF toUnitClauseCNF(Env env, Literal literal) throws ParseException{
		UnitClauseCNF res=new UnitClauseCNF();
		res.add(toClause(env, literal));
		return res;
	}
	
	/**Sets this literal sign to sign.
	 * @param literal The Literal.
	 * @param sign The new sign of this literal.*/
	public static void setSign(Literal literal, boolean sign) {
		if(sign != literal.getSign())
			literal.negate();
	}
		
	/**Returns true if lit1 subsumes lit2.
	 * @param lit1 The literal to verify.
	 * @param lit2 The literal to be verified.*/
	public static boolean subsumes(Literal lit1, Literal lit2) {
		return (lit1.subsumes(lit2) != null);
	}
	
	/**
	 * @throws ParseException */
	public static boolean isFreeLiteral(Env env, String plit) throws ParseException {
		Literal pl = IndepPLiteral.toLiteral(plit, env, false);
		int arity = env.getSymTable().getArity(pl.getName(), TermTypes.PREDICATE);
		List <String> seenVar = new ArrayList<String>();
		for (int i=0;i<arity;i++){
			String v=pl.getArg(i).toString();
			if (!v.startsWith("_") || seenVar.contains(v))
				return false;
			seenVar.add(v);
		}
		return true;
	}
		/*
	public boolean isPositive(){
		return sign;
	}*/
		
		/*
		
	public String toString(){
		if (sign)
			return getTerm();
		else
			return "-"+getTerm();
	}*/

	/**Compares the strings of this literal and another.
	 * @param o Object against which this is compared.*/
	public static boolean equals(Object l1, Object l2){
		if (l1 == null || l2==null) return false;
		if (l1 instanceof Literal && l2 instanceof Literal){
			Literal l1b = (Literal)l1;
			Literal l2b = (Literal) l2;
			if (l1b.isPositive() != l2b.isPositive())
				return false;
			if (l1b.getArity()!= l2b.getArity())
				return false;
			if (l1b.toString().equals(l2b.toString()))
				return true;
		}
		return false;
	}

	/*public boolean equiv(Object o){
		if (o==null) return false;
		if (o instanceof IndepLiteral){
			IndepLiteral l=(IndepLiteral)o;
			if (l.sign!=sign)
				return false;
			if (l.getArity()!=getArity())
				return false;
			if (!l.predicate.equals(predicate)) 
				return false;
			if (getArity()==0 || l.toString().equals(toString()))
				return true;
			return subsumes(l) && l.subsumes(this);
		}
		return false;
	}*/

	
		public static void main(String[] args) throws ParseException{
			//test of every function
			/*
			IndepPLiteral[] p=new IndepPLiteral[10];
			p[0]=new IndepPLiteral("");
			p[1]=new IndepPLiteral("f(1,_,t(_,_))");
			p[2]=new IndepPLiteral("f(1,_,t(4,4)):3");
			p[3]=new IndepPLiteral("+-f(_,_,_):2 <=1");
			p[4]=new IndepPLiteral("-f(1,h(_),t(d4,d5)):1");
			p[5]=new IndepPLiteral("+-f(X,Y,Z)<4");
			p[7]=new IndepPLiteral("f(X,Y,X)");
			p[6]=new IndepPLiteral("f[1,_0,_1](_0,_1)");
			p[8]=new IndepPLiteral(null);*/
			Env env = new Env();
			Options opt = new Options(env);
			Literal l = Literal.parse(env, opt, "f(X, Y, Z)");
			System.out.println(IndepLiteral.getPredicate(env, l));
			System.out.println(IndepLiteral.isFreeLiteral(env, "f(X, Y)"));
			System.out.println(IndepLiteral.isFreeLiteral(env, "f(X, X)"));
			System.out.println(IndepLiteral.isFreeLiteral(env, "f(a, Y)"));
			Literal l2 = skolemize(env, l);
			Literal l3 = skolemize(env, l);
			System.out.println(l3);
			IndepLiteral.setSign(l2, false);
			System.out.println(l2);
			
			//to Test :
			//get Sign
			//get Term
			//getMaxTermDepth, getMaxLength
			//toQuasiLiteral
			//isDouble
			//DevelopPLiteral	
			//negate
			//subsumes
/*			for (int i=0;i<10;i++){
				System.out.println("p["+i+"] : "+p[i]);
				if (p[i]!=null){
					String s=p[i].toString();
					Env env=new Env();
					PLiteral l1=toPLiteral(s,env);
					System.out.println("PLiteral :"+l1);
					Literal l2=toLiteral(s,env,true);
					System.out.println("Literal(true) :"+l2);
					Literal l3=toLiteral(s,env,false);
					System.out.println("Literal(false) :"+l3);
					
					Arguments plits=developPLiteral(s);
					System.out.println("developPLiterals() :"+plits);
					System.out.println("getSign : "+getSign(s)+" / getTerm : "+getTerm(s));
					System.out.println("getMaxLength : "+getMaxLength(s)+" / getMaxTermDepth : "+getMaxTermDepth(s));
					System.out.println("toQuasiLiteral : "+toQuasiLiteral(s)+" / isDouble : "+isDouble(s));
					System.out.println("toLiteral(true) : "+getLiteral(s,true)+" / isStrictNegative : "+isStrictNegative(s));
					System.out.println("toLiteral(false) : "+getLiteral(s,false));
					System.out.println("negate : "+negate(s));
					System.out.println("isFreeLiteral : "+isFreeLiteral(s));
					for (int j=0;j<7;j++){
						String t=p[j].toString();
						System.out.println("subsumes("+s+", "+t+",true) :"+subsumes(s,t,true));
						System.out.println("subsumes("+s+", "+t+",false) :"+subsumes(s,t,false));
					}
					System.out.println();
				}
				
			}
			*/
			
		}
		
		
		
		public static int skolemNumber=0;
//		private String plitRepr;
		/*
		private final String predicate;
		private final List<String> arguments;
		private final boolean sign;*/
	}


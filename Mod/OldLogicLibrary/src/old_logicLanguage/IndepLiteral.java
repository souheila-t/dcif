package old_logicLanguage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import old_solarInterface.IndepPLiteral;
import old_solarInterface.Tools;

import org.nabelab.solar.Clause;
import org.nabelab.solar.ClauseTypes;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.parser.ParseException;

import genLib.tools.Arguments;

public final class IndepLiteral {
	
	
	
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
	}
		
	public String getPredicate(){
		return predicate;
	}
	
	public List<String> getArguments(){
		return arguments;
	}
	
	public int getArity(){
		return arguments.size();
	}
	
	public String getPredicateSignature(){
		return predicate+"\\"+getArity();
	}
	
	public IndepLiteral getFreedLiteral(){
		List<String> newArg=new ArrayList<String>();
		for (int i=0;i<getArity();i++)
			newArg.add("_"+i);
		return new IndepLiteral(sign, predicate, newArg);
	}
	
	public IndepLiteral getPositiveVersion(){
		if (isPositive()) 
			return new IndepLiteral(sign, predicate, arguments);
		return negate(false);
	}
	
	public String getTerm(){
		String term=predicate;
		if (arguments.size()>0){
			term+="(";
			for (String arg:arguments)
				term+=arg+",";
			term=term.substring(0,term.length()-1)+")";
		}
		return term;
	}

	public IndepLiteral addPrefix(String prefix, boolean cumul){
		String pred=getPredicate();
		if (!pred.startsWith(prefix) || cumul)
				pred=prefix+pred;
		
		return new IndepLiteral(sign, pred, arguments);
	}
		
	public IndepLiteral removePrefix(String prefix){
		String newPred;
		String pred=getPredicate();
		if (pred.startsWith(prefix))
			newPred=pred.substring(prefix.length());
		else
			newPred=pred;
		return new IndepLiteral(sign, newPred, arguments);
	}
		
	
	//rudimentary skolemization for now
	public IndepLiteral negate(boolean skolemize){
		if (skolemize){
			IndepLiteral sk=skolemize();
			return new IndepLiteral(!sign, predicate, sk.arguments);
		}
		return new IndepLiteral(!sign, predicate, arguments);
	}
	
	private IndepLiteral skolemize(){
		Env env=new Env();
		Literal lit=toLiteral(env);
		String s=lit.toString();
		int nbVar=lit.getNumVars();
		for (int i=nbVar-1;i>=0;i--){
			int value=skolemNumber+i;
			s=s.replaceAll("_"+i, "skolem"+value);
		}
		skolemNumber+=nbVar;
		return IndepLiteral.parse(s);
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
	}		
	
	public IndepClause toIndClause(){
		List<IndepLiteral> lits=new ArrayList<IndepLiteral>();
		lits.add(this);
		return new IndepClause("unitLit",lits);
	}
	
	public UnitClauseCNF toUnitClauseCNF(){
		UnitClauseCNF res=new UnitClauseCNF();
		res.add(toIndClause());
		return res;
	}
		
	public boolean isPositive(){
		return sign;
	}
		
	public boolean subsumes(IndepLiteral lit2) {
		Env env=new Env();
		if (lit2==null) return false; // or true ? maybe throws exception
		Clause c1=new Clause(env, "a1", ClauseTypes.AXIOM, toLiteral(env));
		Clause c2=new Clause(env, "a2", ClauseTypes.AXIOM, lit2.toLiteral(env));
		c2.setOffset(c1.getNumVars()+1);
		
		return(c1.subsumes(c2));
	}
		
		public static boolean isFreeLiteral(String plit) throws ParseException{
			Env env=new Env();
			Literal pl=IndepPLiteral.toLiteral(plit, env,false);
			int arity=env.getSymTable().getArity(pl.getName(), TermTypes.PREDICATE);
			List <String> seenVar=new ArrayList<String>();
			for (int i=0;i<arity;i++){
				String v=pl.getArg(i).toString();
				if (!v.startsWith("_") || seenVar.contains(v))
					return false;
				seenVar.add(v);
			}
			return true;
		}
		
		
	public String toString(){
		if (sign)
			return getTerm();
		else
			return "-"+getTerm();
	}

	//compare only String
	public boolean equals(Object o){
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
		}
		return false;
	}

	public boolean equiv(Object o){
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
	}

	
		public static void main(String[] args) throws ParseException{
			//test of every function
			IndepPLiteral[] p=new IndepPLiteral[10];
			p[0]=new IndepPLiteral("");
			p[1]=new IndepPLiteral("f(1,_,t(_,_))");
			p[2]=new IndepPLiteral("f(1,_,t(4,4)):3");
			p[3]=new IndepPLiteral("+-f(_,_,_):2 <=1");
			p[4]=new IndepPLiteral("-f(1,h(_),t(d4,d5)):1");
			p[5]=new IndepPLiteral("+-f(X,Y,Z)<4");
			p[7]=new IndepPLiteral("f(X,Y,X)");
			p[6]=new IndepPLiteral("f[1,_0,_1](_0,_1)");
			p[8]=new IndepPLiteral(null);
			
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
		
		private final String predicate;
		private final List<String> arguments;
		private final boolean sign;
	}


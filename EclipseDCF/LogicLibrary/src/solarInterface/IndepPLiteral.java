package solarInterface;

import java.util.ArrayList;
import java.util.List;

import logicLanguage.IndepClause;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.PLiteral;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.parser.ParseException;

import genLib.tools.Arguments;
import genLib.tools.ToolIndex;


public class IndepPLiteral {
	public IndepPLiteral(String plit){
		if (plit==null || plit.equals("")) 
			plitRepr="u_voidLit";
		else
			plitRepr=plit.trim();
	}
	
	
	public static String getSign(String pliteral){
		String plit=pliteral.trim();
		//extract sign
		String sign="";
		int pointer=0;
		while (plit.charAt(pointer)=='+' || plit.charAt(pointer)=='-'){
			sign+=plit.charAt(pointer);
			pointer++;
		}
		return sign;
	}

	public static String getTerm(String pliteral){
		String plit=pliteral.trim();
		int indStart=getSign(plit).length();
		int indEnd=plit.indexOf(':');
		if (indEnd<0)
			indEnd=plit.indexOf('<');
		if (indEnd>=0)
			return plit.substring(indStart, indEnd).trim();
		return plit.substring(indStart);
	}

	public static int getMaxTermDepth(String pliteral){
		String plit=pliteral.trim();
		int indStart=plit.indexOf(':');
		if (indStart<0)
			return -1;
		int indEnd=plit.indexOf('<');
		if (indEnd>=0)
			return Integer.parseInt(plit.substring(indStart+1, indEnd).trim());
		return Integer.parseInt(plit.substring(indStart+1).trim());
	}

	public static int getMaxLength(String pliteral){
		String plit=pliteral.trim();
		int indStart=plit.indexOf('<');
		if (indStart<0)
			return -1;
		if (plit.charAt(indStart+1)=='=')
			return Integer.parseInt(plit.substring(indStart+2).trim());
		return Integer.parseInt(plit.substring(indStart+1).trim())-1;
	}

	public static String addPrefixTo(String prefix, String pliteral, boolean cumul){
		String plit=pliteral.trim();
		//extract sign
		String sign=getSign(plit);
		//extract term and add prefix to it if needed
		String term=plit.substring(sign.length());
		if (!term.startsWith(prefix) || cumul)
			term=prefix+term;
		return sign+term;
	}
	
	public static String removePrefixFrom(String prefix, String pliteral){
		String plit=pliteral.trim();
		//extract sign
		String sign=getSign(plit);
		//extract term and remove its prefix if needed
		String term=plit.substring(sign.length());
		if (term.startsWith(prefix))
			term=term.substring(prefix.length());
		return sign+term;
	}
	
	public static Arguments developPLiteral(String pliteral){
		String plit=pliteral.trim();
		String sign=IndepPLiteral.getSign(plit);
		String rest=plit.substring(sign.length());
		Arguments result=new Arguments();
		if (isDouble(plit)) {
			result.add("+"+rest);
			result.add("-"+rest);			
		}
		else {
			result.add(sign+rest);
		}
		return result;
	}	

	public static String negate(String pliteral){
		String plit=pliteral.trim();
		String sign=IndepPLiteral.getSign(plit);
		String rest=plit.substring(sign.length());
		if (isDouble(plit)) {
			return plit;			
		}
		if (sign.equals("-"))
			return ("+"+rest);
		else //positive
			return ("-"+rest);
	}
	
	//do not deal with development
	public static String toQuasiLiteral(String plit){
		String sign=IndepPLiteral.getSign(plit);
		String term=IndepPLiteral.getTerm(plit);
		return sign+term;
	}
	
	
	public static PLiteral toPLiteral(String plit, Env env) throws ParseException{
		//String cleanPLit=Tools.cleanText(plit,false);
		Options opt=new Options(env);
		PLiteral pl=PLiteral.parse(env, opt, Tools.cleanText(plit,false));
		//the current parsing does not parse termDepth and maxLength
		pl.setMaxLength(getMaxLength(plit));
		pl.setMaxTermDepth(getMaxTermDepth(plit));
		return pl;
	}
	
	public static Literal toLiteral(String plit, Env env, boolean doubleIsPos) throws ParseException{
		Options opt=new Options(env);
		Literal lit=Literal.parse(env, opt, getTerm(Tools.cleanText(plit,false)));
		//sign
		if (isStrictNegative(plit) || (!doubleIsPos&&isDouble(plit)))
				lit.negate();
		return lit;
	}
	public static String getLiteral(String plit, boolean doubleIsPos) throws ParseException{
		String lit=getTerm(plit);
		//sign
		if (isStrictNegative(plit) || (!doubleIsPos&&isDouble(plit)))
				lit="-"+lit;
		else lit="+"+lit;
		return lit;
	}
	
	
	public static boolean isDouble(String plit1){
		return getSign(plit1).equals("+-");
	}
	public static boolean isStrictNegative(String plit1){
		return getSign(plit1).equals("-");
	}
	
	//pli1 and plit2 must not be Doubles
	//if they are, plit1 would be converted to sign(plit2) (should subsumes if possible) 
	// and plit2 would be converted to sign of
	public static boolean subsumes(String plit1, String plit2, boolean testMaxima) throws ParseException {
		if (isDouble(plit2)){
			if (!isDouble(plit2)) return false;
		}
		boolean sign1=true;
		if (isDouble(plit1) && isStrictNegative(plit2))
			sign1=false;
		IndepClause lit1=new IndepClause("["+getLiteral(plit1,sign1)+"]");
		
		IndepClause lit2=new IndepClause("["+getLiteral(plit2,true)+"]");
		boolean subsuming=lit1.subsumes(lit2);
		
		if (testMaxima && subsuming){
			//maxTermDepth if plit2 more constrained, not subsumed
			if (ToolIndex.isBefore(getMaxTermDepth(plit2), getMaxTermDepth(plit1)))
				subsuming=false;
			//maxLength if plit2 more constrained, not subsumed
			if (ToolIndex.isBefore(getMaxLength(plit2), getMaxLength(plit1)))
				subsuming=false;	
		}
		return subsuming;
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
		return plitRepr;
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
		for (int i=0;i<10;i++){
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
		
	}
	
	
	
	
	private String plitRepr;
}

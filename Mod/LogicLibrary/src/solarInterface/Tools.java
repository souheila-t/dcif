package solarInterface;
import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.CFP;
import org.nabelab.solar.Clause;
import org.nabelab.solar.ClauseTypes;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.pfield.PField;





public class Tools implements ClauseTypes{


	public static List<Clause> dataCopyCl(List<? extends Clause> clauses, Env env){
		Options opt=new Options(env);
		ArrayList <Clause> result=new ArrayList<Clause>();
		for (Clause cl : clauses) {
			try {
				String s=cleanText(cl.toString(),true);
				if (!cl.getLiterals().isEmpty()) result.add(Clause.parse(env, opt,s));
			} catch (ParseException e) {e.printStackTrace();}
		}
		return result;
	}

	public static Clause negateUnitClause(Clause unit, Env env) throws ParseException{
		Options opt=new Options(env);
		Literal lit=Literal.parse(env, opt, unit.getLiterals().get(0).toString());
		lit.negate();
		return Clause.parse(env, opt, "["+lit+"]");
	}

	public static boolean isEqualCl(List<Clause> l1, List<Clause> l2){
		if (l1==null) return (l2==null);
		if (l2==null) return (l1==null);		
		if (l1.size()!=l2.size()) return false;
		for (Clause cl1:l1){
			boolean test=false;
			for (Clause cl2:l2) if (isEqual(cl1,cl2)) {
					test=true;
					break;
			}
			if (!test) return false;
		}
		return true;
	}
	
	public static boolean isEqual(Clause c1, Clause c2){
		return subsumes(c1, c2) && subsumes(c2,c1);
	}	
	
	// do not work if a literal is present more than once in l1
	public static boolean isEqual(List<Literal> l1, List <Literal> l2){
		if (l1.size()!=l2.size()) return false;
		for (Literal lit1:l1){
			boolean test=false;
			for (Literal lit2:l2) if (isEqual(lit1,lit2)) {
					test=true;
					break;
			}
			if (!test) return false;
		}
		return true;
	}
	
	public static boolean isEqual(Literal l1,Literal l2){
		if (l1==null) return l2==null;
		return l1.toString().equals(l2.toString());
	}

	public static boolean removeFrom(List<Clause> list, Clause element){
		for (int i=0;i<list.size();i++){
			if (isEqual(list.get(i),element)) {
				list.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public static CFP defineProblem(Env env, Options opt, List<Clause> axioms, List<Clause> topClauses, PField pf) throws ParseException{
		CFP problem= new CFP(env, opt);
		for (Clause cl : axioms)
			problem.addClause(Clause.parse(env, opt, cl.toString()));
		for (Clause cl : topClauses)
			problem.addClause(Clause.parse(env,opt,"tc",Clause.TOP_CLAUSE,cl.toString()));
		if (pf!=null) problem.setPField(pf);
		return problem;
	}
	
	private static boolean isBefore(int i, int j){
		return ((i<j)||(j<0))&&(i>0);
	}
	private static int getFirst(int i, int j, int k){
		int max=Math.abs(i)+Math.abs(j)+Math.abs(k);
		if (i<0) i=max;
		if (j<0) j=max;
		if (k<0) k=max;
		return Math.min(i, Math.min(j,k));
		
	}
	
	//can take clause or literal as argument
	public static String cleanText(String c, boolean supprRoot){
		String prune=c;
		boolean list=false;
		if (c!=null && c.length()>1&&c.charAt(0)=='['){
			prune=c.substring(1, c.toString().length()-1);
			list=true;
		}
				
		int endSq=prune.indexOf("](");
		while (endSq>0){
			int nextPo,nextPf,parToClose, pointer;
		//	int end;
			int begSq=prune.lastIndexOf('[', endSq);
			int bVal,eVal,nextComma;			
			int bVar,eVar,varPointer;
			//Get the Values for the variables (to replace them)
			parToClose=0; bVal=endSq+2; pointer=bVal;
			varPointer=begSq+1;
			while(true){
			//	String debug=prune.substring(pointer);
				nextPo=prune.indexOf('(', pointer);
				nextPf=prune.indexOf(')', pointer);
				nextComma=prune.indexOf(',', pointer);
				//new opening '(' before end of term 
				if ( nextPo==getFirst(nextComma,nextPo,nextPf)
						||(parToClose>0 && isBefore(nextPo,nextPf)) ){
					parToClose++;
					pointer=nextPo+1;
					continue;
				}
				// closing previously opened ')'
				if (parToClose>0 && isBefore(nextPf,nextPo)){
						parToClose--;
						pointer=nextPf+1;
						continue;
				}
				//getting to the end of val
				if (parToClose==0){
					// before of continue in previous step (for min=='('), min is either ')' or ','
					eVal=getFirst(nextComma,nextPo,nextPf)-1;
					//extract val 
					String val=prune.substring(bVal, eVal+1);
					//search current variable
			//		String debug2=prune.substring(varPointer);
					bVar=prune.indexOf('_', varPointer);
					while(!Character.isDigit(prune.charAt(bVar+1))){
						varPointer=bVar+1;
						bVar=prune.indexOf('_', varPointer);
					}
					eVar=getFirst(prune.indexOf(',', bVar),prune.indexOf(')', bVar),endSq)-1;
					//replace
					prune=prune.substring(0, bVar)+val+prune.substring(eVar+1);
					//adjsut offset
					int offset=val.length()-(eVar+1-bVar);
					varPointer=bVar+val.length();
					endSq+=offset;nextPf+=offset;eVal+=offset;
					//adjust pointers and continue or stop if it was the closing )
					if (eVal+1==nextPf) break;	
					varPointer=bVar+val.length();
					bVal=eVal+2; pointer=bVal;
				}
			}
			// delete the collected values
			prune=prune.substring(0,endSq+1)+prune.substring(nextPf+1);
			endSq=prune.indexOf("](");
		}
		prune=prune.replace('[', '(').replace(']',')');
		//TODO et les $?
		if (supprRoot){
			prune=prune.replace("+$,", "").replace(", +$","");
			prune=prune.replace("-$,", "").replace(", -$","");
		}
		prune=prune.replace("$", "r_root");
		
	//	if (c.toString().length()<1) return "";
		if (list) 
			return "["+prune+"]";
		else
			return prune;
	}
	
	public static List<Clause> pruneClause(List<Clause> entry, List<Clause> toRemove){
		ArrayList <Clause> result=new ArrayList<Clause>();
		for (Clause clause : entry) {
    		boolean test=true;
    		for (Clause c2: toRemove) {
    			if (subsumes(c2,clause)) {
    					test=false;
    					break;
    			}
    		}
    		if (test)
    			result.add(clause);
    	}
		return result;
	}
	
	public static boolean subsumes(Clause c1, Clause c2){
		Env env=new Env();
		Options opt=new Options(env);
		Clause c1b,c2b;
		String s1,s2;
		try {
			s1 = cleanText(c1.toString(), false);
			s2 = cleanText(c2.toString(),false);
			c1b = Clause.parse(env, opt,  s1);
			c2b =Clause.parse(env, opt, s2);
			c2b.setOffset(c1b.getNumVars()+1);
			boolean res=c1b.subsumes(c2b);
			return res;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void subsumptionMinimalAddTo(List<Clause> originalList, List<Clause> toAdd){
		for (Clause newCl:toAdd){
			boolean willAdd=true;
			List<Clause> toRemove=new ArrayList<Clause>();
			for (Clause oldCl:originalList){				
				if (subsumes(oldCl,newCl)){
					willAdd=false;
					break;
				}
				if (subsumes(newCl,oldCl)){
					toRemove.add(oldCl);
				}
			}
			if (willAdd) originalList.add(newCl);
			if (!toRemove.isEmpty()) originalList.removeAll(toRemove);
		}
	}
	
	public static List<Literal> getSubsumptionMinimalLiterals(List<Clause> input){
		List<Literal> litList=new ArrayList<Literal>();
		for (Clause cl:input)
			for (Literal lit:cl.getLiterals()){
				boolean test=true;
				for (Literal lit2:litList){
					if (lit.isSubsuming(lit2)!=null){
						litList.set(litList.indexOf(lit2), lit);
						test=false;
						break;
					}
					if (lit2.isSubsuming(lit)!=null){
						test=false;
						break;
					}	
				}
				if (test)
					litList.add(lit);				
		}
		return litList;
	}
	
	public static void main(String[] args){
		
		String example="[grou,-pika,+zur_blu[_0,ca_ss,_1,_2](1,tav(c,2),_0)," +
				"-zurblu[f(_0),grou,_12,_13](_1,tab(_2),s(3,s(s(5))))," +
				"zurblu[mur,hh(_0,_1),ll](tab(s(2),f(e,m)),h))," +
				"+$, marzuk[troi,boon], -$]";
		System.out.println("Ex : "+example+"\n Cleared : "+Tools.cleanText(example,true));
		System.out.println("Ex : "+example+"\n Cleared : "+Tools.cleanText(example,false));
		
		String exampl2="[+$]";
		System.out.println("Ex : "+exampl2+"\n Cleared : "+Tools.cleanText(exampl2,true));
		String exampl3="[]";
		System.out.println("Ex : "+exampl3+"\n Cleared : "+Tools.cleanText(exampl3,false));

	}
}

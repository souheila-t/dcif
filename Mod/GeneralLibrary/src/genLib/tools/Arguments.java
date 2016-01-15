package genLib.tools;

import java.util.ArrayList ;
import java.util.Collection;
import java.util.Stack;



public class Arguments extends ArrayList<String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3528216503270139215L;

	public Arguments(){
		super();
	}
	
	public Arguments(String s){
		super();
		add(s);
	}
	
	public Arguments(Collection<? extends Object> list){
		super();
		for (Object o:list){
			super.add(o.toString());
		}
	}
	
	//return false if both are negative
	private static boolean isBefore(int i, int j){
		return ((i<j)||(j<0))&&(i>=0);
	}
	private static int getFirst(int i, int j, int k){
		int max=Math.abs(i)+Math.abs(j)+Math.abs(k);
		if (i<0) i=max;
		if (j<0) j=max;
		if (k<0) k=max;
		int min=Math.min(i, Math.min(j,k));
		if (min==max) return -1;
		return min;		
	}
	//return true if i is the First element (and is not = -1)
	private static boolean isFirst(int testedInt, int j, int k){
		int min=getFirst(testedInt, j,k);
		return (testedInt>-1) && (testedInt==min);
		
	}

	//transform a string to a list
	public static Arguments parse(String string){
		if (string.trim().charAt(0)=='[')
			return parse(string,true);
		return parse(string,false);
	}
		
	public static Arguments parse(String string, boolean isAList){
		if (!isAList) return new Arguments(string);
		//parsing
		Arguments result=new Arguments();
		// do not suppress last character as it will be used for closing
		String prune=string.substring(1, string.toString().length());
		int nextEndSep,nextBegSep, pointer;
		int nextBegSq, nextBegPar;
		int bElem,eElem,nextComma;
		pointer=0;bElem=0;
		Stack<String> sep=new Stack<String>();
		sep.push("]");
		String nextSep="]";
		while (!sep.isEmpty()){
			@SuppressWarnings("unused")
			String debug=prune.substring(pointer);
			//next opening
			nextBegSq=prune.indexOf('[', pointer);
			nextBegPar=prune.indexOf('(', pointer);
			if (isBefore(nextBegSq,nextBegPar)){
				nextBegSep=nextBegSq;
				nextSep="]";
			}
			else {
				nextBegSep=nextBegPar;
				nextSep=")";
			}
			//next closing of interest
			nextEndSep=prune.indexOf(sep.peek(), pointer);
			nextComma=prune.indexOf(',', pointer);
			//new opening Sep before end of element 
			if ( isFirst(nextBegSep,nextComma,nextEndSep)
						||(sep.size()>1 && isBefore(nextBegSep,nextEndSep)) ){
					sep.push(nextSep);
					pointer=nextBegSep+1;
					continue;
				}
			// closing previously opened Sep
			if (sep.size()>1 && isBefore(nextEndSep,nextBegSep)){
						sep.pop();
						pointer=nextEndSep+1;
						continue;
			}
			//getting to the end of Elem
			if (sep.size()<=1){
				// because of continue in previous steps (for min=='(')
				//  min is either ']' or ','
				if (isBefore(nextEndSep,nextComma)){
					eElem=nextEndSep;
					sep.pop();
				}
				else
					eElem=nextComma;
		//		else eElem=prune.length();
				//extract Elem 
				String elem=prune.substring(bElem, eElem);
				if (eElem>bElem) result.add(elem.trim());
				//if (eElem==prune.length()) break;	
				bElem=eElem+1; pointer=bElem;
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		String s="[e(ssd,ef(h,g,e,e)),(#84), ( ( op(g), k ) | (fee) )]";
		s="cnf(element_1,axiom,\n    ( group_element(e_1) )).";
		s=s.replace('\n', ' ');
		s=s.substring(s.indexOf("(")+1,s.lastIndexOf(")."));
		s="["+s+"]";
		Arguments res=Arguments.parse(s);
		System.out.println("element_1,axiom,\n    ( group_element(e_1) )");
		System.out.println(res);
	}	
}

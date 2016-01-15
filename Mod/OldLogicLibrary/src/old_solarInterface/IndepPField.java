package old_solarInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import old_logicLanguage.CNF;
import old_logicLanguage.IndepClause;
import old_logicLanguage.IndepLiteral;

import org.nabelab.solar.Env;
import org.nabelab.solar.Options;
import org.nabelab.solar.PLiteral;
import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.pfield.PField;

import genLib.tools.ToolIndex;


public class IndepPField {
	
	public static class PFCond{
		public PFCond(){
			super();
		}
		
		public PFCond(int maxDepth, int maxLength){
			this.maxDepth=maxDepth;
			this.maxLength=maxLength;
		}
		// if constraint, then favor more constraining value
		public static PFCond merge(PFCond c1, PFCond c2, boolean constrain){
			int maxD=c2.maxDepth;
			if (ToolIndex.isBefore(c1.maxDepth, c2.maxDepth) && constrain)
				maxD=c1.maxDepth;
			else if (ToolIndex.isBefore(c2.maxDepth, c1.maxDepth) && !constrain)
				maxD=c2.maxDepth;
			int maxL=c2.maxLength;
			if (ToolIndex.isBefore(c1.maxLength, c2.maxLength) && constrain)
				maxL=c1.maxLength;
			else if (ToolIndex.isBefore(c2.maxLength, c1.maxLength) && !constrain)
				maxD=c2.maxLength;
			return new PFCond(maxD,maxL);			
		}
		
		public String toString(){
			String res="";
			if (maxDepth>-1)
				res+=":"+maxDepth;
			if (maxLength>-1)
				res+="<="+maxLength;
			return res;	
		}
		protected int maxDepth=-1;
		protected int maxLength=-1;
	}
	
	public IndepPField(){
		super();
	}
	
	public IndepPField(Collection<IndepLiteral> literals){
		super();
		for (IndepLiteral lit:literals)
			addLiteral(lit);
	}
	
	public IndepPField(List<IndepLiteral> literals, List<PFCond> localCond){
		super();
		for (int i=0;i<literals.size();i++) {
			IndepLiteral lit=literals.get(i);
			if (localCond!=null && i<localCond.size())
				addLiteral(lit,localCond.get(i));
			else 
				addLiteral(lit);
		}
	}
	
	public IndepPField(List<IndepLiteral> literals, List<PFCond> localCond, PFCond gbCond){
		this(literals, localCond);
		globalConditions=gbCond;
	}
	
	public IndepPField(List<IndepLiteral> literals, PFCond cond){
		this(literals);
		globalConditions=cond;
	}
	
	public IndepPField(List<IndepLiteral> literals, int maxDepth, int maxLength){
		this(literals);
		globalConditions=new PFCond(maxDepth,maxLength);
	}
	
	public static IndepPField parse(String repr){
		if (repr==null || repr.equals("")) {
			return new IndepPField();
		}
		if (repr.startsWith("pf("))
			repr=repr.substring(3,repr.lastIndexOf(')'));
		IndepPField res=new IndepPField();
		try {
			Env env=new Env();
			Options opt=new Options(env);
			PField pf=PField.parse(env, opt, "pf("+repr+").");
			for (PLiteral plit:pf.getPLiterals()){
				int maxLength=plit.getMaxLength();
				int maxDepth=plit.getMaxTermDepth();
				IndepLiteral lit=IndepLiteral.parse(plit.getTerm().toString()); 
				if (plit.getSign()==PLiteral.BOTH || plit.getSign()==PLiteral.POS)
					res.addLiteral(lit,maxDepth,maxLength);
				if (plit.getSign()==PLiteral.BOTH || plit.getSign()==PLiteral.NEG)
					res.addLiteral(lit.negate(false),maxDepth,maxLength);
			}
			res.globalConditions=new PFCond(pf.getMaxTermDepth(),pf.getMaxLength());			
		} catch (ParseException e) {
			System.out.println("ParseException during parsing of IndepPField : "+repr);
			e.printStackTrace();
		}
		return res;
	}
	
	// no access to modifications -> read only
	public List<IndepLiteral> getLiterals(){
		List<IndepLiteral> lits=new ArrayList<IndepLiteral>(literals);
		return lits;
	}
	
	public int getMaxTermDepth(){
		return globalConditions.maxDepth;
	}
	public int getMaxLength(){
		return globalConditions.maxLength;
	}
	
	public PFCond getGbConditions(){
		return globalConditions;
	}
	//subsumtion check ???
	protected void addLiteral(IndepLiteral lit, int maxDepth, int maxLength){
		literals.add(lit);
		localConditions.add(new PFCond(maxDepth, maxLength));
	}
	
	protected void addLiteral(IndepLiteral lit, PFCond cond){
		literals.add(lit);
		localConditions.add(new PFCond(cond.maxDepth, cond.maxLength));
	}
	
	protected void addLiteral(IndepLiteral lit){
		addLiteral(lit,new PFCond());
	}
	
	
	// newPLiterals must begin by "[" and end by"]"
//	public IndepPField replacePLiterals(String newPLiterals) throws ParseException{
//		if (newPLiterals==null || newPLiterals.startsWith("[[") || !newPLiterals.startsWith("["))
//			throw new ParseException("Incorrect argument in replacePLiterals("+newPLiterals+") : must be of the form [plit0,plit1,...]");
//		String suffix=strRepr.substring(strRepr.lastIndexOf(']')+1);
//		return new IndepPField(newPLiterals+suffix);
//	}
	
	public IndepPField addPrefix(String prefix, boolean cumul){
		List<IndepLiteral> newLits=new ArrayList<IndepLiteral>();
		for (IndepLiteral lit:getLiterals()){
			//add the new literal (possibily unchanged)
			newLits.add(lit.addPrefix(prefix, cumul));
		}
		return new IndepPField(newLits,localConditions,globalConditions);
	}
	
	public IndepPField removePrefix(String prefix){
		List<IndepLiteral> newLits=new ArrayList<IndepLiteral>();
		for (IndepLiteral lit:getLiterals()){
			newLits.add(lit.removePrefix(prefix));
		}
		return new IndepPField(newLits,localConditions,globalConditions);
	}
		
	// add Literals
	// if literal already present (same string), do not add (without comparing localCond)
	// otherwise add
	public IndepPField addToLiterals(List<IndepLiteral> newLiterals, List<PFCond> localCondNewLits){
		List<IndepLiteral> lits=getLiterals();
		List<PFCond> localConds=new ArrayList<PFCond>(localConditions);
		int i;
		for (i=0;i<newLiterals.size();i++) {
			IndepLiteral toAddLit = newLiterals.get(i);
			if (!lits.contains(toAddLit)){
				lits.add(toAddLit);
				localConds.add(localCondNewLits.get(i));
			}
		}
		return new IndepPField(lits,localConds,globalConditions);
	}

	// add Literals
	// if literal already present, do not add (without comparing localCond)
	// otherwise add
	public IndepPField addToLiteralsFullCheck(List<IndepLiteral> newLiterals, List<PFCond> localCondNewLits){
		List<Integer> toRemove=new LinkedList<Integer>();
		List<IndepLiteral> lits=getLiterals();
		List<PFCond> localConds=new ArrayList<PFCond>(localConditions);
		int i,j;
		for (i=0;i<newLiterals.size();i++) {
			IndepLiteral toAddLit = newLiterals.get(i);
			boolean add=true;
			for (j=0;j<lits.size();j++){
				IndepLiteral initLit=lits.get(j);
					if (initLit.subsumes(toAddLit)){
						add=false;
						break;
					}
					if (toAddLit.subsumes(initLit))
						toRemove.add(0, j); //add first the larger number
			}
			if (add) {
				lits.add(toAddLit);
				localConds.add(localCondNewLits.get(i));
			}
			if (!toRemove.isEmpty())
				for (Integer index:toRemove){
					// this starts from larger indexes so
					// indexes that remains to remove are not affected
					lits.remove(index);
					localConds.remove(index);
				}
		}
		return new IndepPField(lits,localConds,globalConditions);
	}

	
	public IndepPField addToLiterals(List<IndepLiteral> newLiterals){
		List<PFCond> localCond=new ArrayList<PFCond>();
		for (int i=0;i<newLiterals.size();i++)
			localCond.add(new PFCond());
		return addToLiterals(newLiterals, localCond);
	}
		
	public IndepPField addToLiterals(IndepLiteral toAddLit){
		List<IndepLiteral> lits=new ArrayList<IndepLiteral>();
		lits.add(toAddLit);
		List<PFCond> localCond=new ArrayList<PFCond>();
		localCond.add(new PFCond());
		return addToLiterals(lits,localCond);
	}

	public IndepPField setLocalCond(IndepLiteral lit, int maxDepth, int maxLength){
		List<PFCond>localCond=new ArrayList<PFCond>(localConditions);
		int index=literals.indexOf(lit);
		if (index>=0)
			localCond.set(index, new PFCond(maxDepth,maxLength));
		return new IndepPField(literals,localCond,globalConditions);
	}
	
	public IndepPField setMaxLength(int maxLength){
		PFCond gb=new PFCond(globalConditions.maxDepth,maxLength);
		return new IndepPField(literals,localConditions,gb);
	}
	
	// TODO other merge.
	// and symmetrical addLiteral(constrain)
	public IndepPField mergeWith(IndepPField other, int mergeType){
//		System.out.println("Parsing for mergeWith : "+this+" and "+other);
	//	IndepPField res=new IndepPField(literals,localConditions, 
	//			PFCond.merge(globalConditions, other.globalConditions));
		IndepPField res=new IndepPField(literals,localConditions, globalConditions);
		return res.addToLiterals(other.getLiterals(),
				other.constrainAllLocalWith(other.globalConditions).localConditions);
	}
	
	public IndepPField constrainAllLocalWith(PFCond constraint){
		List<PFCond> localCond=new ArrayList<PFCond>();
		for (PFCond initialCond: localConditions){
			localCond.add(PFCond.merge(initialCond, constraint, true));
		}
		return new IndepPField(literals, localCond, globalConditions);
	}

	
	public static IndepPField fitToCNF(CNF clauses, boolean negated){
		IndepPField pf;
		if (negated)
			pf=new IndepPField(clauses.getNegatedVocabulary());
		else
			pf=new IndepPField(clauses.getVocabulary());
		
		for (IndepClause cl:clauses)
			pf=pf.fitConditionsToClause(cl, negated);
		return pf;
	}
	
	public IndepPField fitConditionsToClause(IndepClause cl, boolean negated){
		List<PFCond> localConds=new ArrayList<PFCond>();
		int maxSize=globalConditions.maxLength;
		for (int i=0;i<literals.size();i++){
			IndepLiteral plit=literals.get(i);
			if (negated) plit=plit.negate(false);
			int occ=cl.countOccurences(plit);
			PFCond litCond=localConditions.get(i);
			if (occ>0 && occ>litCond.maxLength)
				localConds.add(new PFCond(litCond.maxDepth, occ));
			else 
				localConds.add(litCond);
		}
		if (cl.getLiterals().size()>maxSize)
			return new IndepPField(literals,localConds,
					new PFCond(globalConditions.maxDepth,maxSize));
		return new IndepPField(literals,localConds, globalConditions);
	}

	//Note : IndepPField only contains freed literals.
	// No depth check
	public boolean belongsTo(IndepClause cl){
		if (globalConditions.maxLength>=0 && cl.getLiterals().size()>globalConditions.maxLength) 
			return false;
		int[] occurences=new int[literals.size()];
		int i;
		for (i=0;i<occurences.length;i++)
			occurences[i]=0;
		for (IndepLiteral lit:cl.getLiterals()){
			int ind=literals.indexOf(lit.getFreedLiteral());
			if (ind>=0){
				occurences[ind]++;
				if (localConditions.get(ind).maxLength>0 
						&& occurences[ind] > localConditions.get(ind).maxLength )
					return false;
			}
			else
				return false;
		}
		return true;
	}
	
	
	//Note : IndepPField should not contains literals that subsumes each others.
	// No depth check
	public boolean belongsToFullCheck(IndepClause cl){
		if (globalConditions.maxLength>=0 && cl.getLiterals().size()>globalConditions.maxLength) 
			return false;
		int[] occurences=new int[literals.size()];
		int i;
		for (i=0;i<occurences.length;i++)
			occurences[i]=0;
		for (IndepLiteral lit:cl.getLiterals()){
			boolean belongs=false;
			for (i=0;i<literals.size();i++){
				IndepLiteral fieldLit=literals.get(i);
				if (fieldLit.subsumes(lit)){
					occurences[i]++;
					if (localConditions.get(i).maxLength>0 
							&& occurences[i] > localConditions.get(i).maxLength )
						return false;
					belongs=true;
					break;
				}
			}
			if (!belongs)
				return false;
		}
		return true;
	}

	public List<IndepLiteral> getLinkedLiterals() throws ParseException{
		List<IndepLiteral> res=new ArrayList<IndepLiteral>();
		Env env=new Env();
		for (IndepLiteral lit:getLiterals())
			if (!lit.toLiteral(env).isMaxGeneral()){
				res.add(lit);
		}
		return res;
	}

	
	//return true if a non empty-subclause of cl belongs to the PF
	// assumes only freed literals in PF
	public boolean partlyBelongsTo(IndepClause cl) {
		for (IndepLiteral lit:cl.getLiterals())
			if (literals.contains(lit.getFreedLiteral()))
				return true;
		return false;
	}

	//return true if a non empty-subclause of cl belongs to the PF
	// and the rest of the clause is in PF2
	// assumes only freed literals in PF
	// does not take into account the length restrictions of PF (only take into account those of PF2)
	public boolean partlyBelongsTo(IndepClause cl, IndepPField pf2) {
		List<IndepLiteral> remaining=new ArrayList<IndepLiteral>();
		int[] occurences=new int[literals.size()];
		int i;
		boolean check=false;
		for (i=0;i<occurences.length;i++)
			occurences[i]=0;
		for (IndepLiteral lit:cl.getLiterals()){
			boolean belongs=false;
			int ind=literals.indexOf(lit.getFreedLiteral());
			if (ind>=0){
				occurences[ind]++;
				if (!(localConditions.get(ind).maxLength>0 
						&& occurences[ind] > localConditions.get(ind).maxLength)){
					belongs=true; check=true;
				}
			}
			if (!belongs){
				remaining.add(lit);
			}
		}
		return (check && pf2.belongsTo(new IndepClause("test",remaining)) );
	}

	
	
	//return true if a non empty-subclause of cl belongs to the PF
	public boolean partlyBelongsToFC(IndepClause cl) {
		for (IndepLiteral lit:cl.getLiterals()){
			boolean belongs=false;
			for (IndepLiteral fieldLit:getLiterals()){
				if (fieldLit.subsumes(lit)){
					// check local constraints : no need
					belongs=true;
					break;
				}
			}
			if (belongs)
				return true;
		}
		return false;
	}

	//return true if a non empty-subclause of cl belongs to the PF
	// and the rest of the clause is in PF2
	public boolean partlyBelongsToFC(IndepClause cl, IndepPField pf2) {
		List<IndepLiteral> remaining=new ArrayList<IndepLiteral>();
		int[] occurences=new int[literals.size()];
		int i;
		boolean check=false;
		for (i=0;i<occurences.length;i++)
			occurences[i]=0;
		for (IndepLiteral lit:cl.getLiterals()){
			boolean belongs=false;
			for (i=0;i<literals.size();i++){
				IndepLiteral fieldLit=literals.get(i);
				if (fieldLit.subsumes(lit)){
					occurences[i]++;
					if (localConditions.get(i).maxLength>0 
							&& occurences[i] > localConditions.get(i).maxLength)
						break;
					belongs=true; check=true;
					break;
				}
			}
			if (!belongs){
				remaining.add(lit);
			}
		}
		return (check && pf2.belongsTo(new IndepClause("test",remaining)) );
	}

	
	
	public PField toPField(Env env, Options opt) throws ParseException{
		//Options opt=new Options(env);
		PField pf=PField.parse(env, opt, toString());
		return pf;
	}	
	
	public boolean sameString(IndepPField comp){
		if (comp==this) {
			System.out.print("[ d-comp ]");
			return true;
		}
		if (comp==null) return (false);
		return toString().equals(comp.toString());
	}
	
	public static String toStringPLit(IndepLiteral lit, PFCond cond){
		return ""+lit+cond; 
	}
	
	public String toString(){
		String strRepr="pf(";
		List<String> lits=new ArrayList<String>();
		for (int i=0;i<literals.size();i++){
			lits.add(toStringPLit(literals.get(i), localConditions.get(i)));
		}
		strRepr+=lits.toString()+globalConditions+").";
		// NOTE : need to add literals one by one if local constraints
		return strRepr;
	}

	// a priori, this is the same as toString, but in case of future change to toString...
	public String toSolFileLine(){
		String strRepr="pf(";
		List<String> lits=new ArrayList<String>();
		for (int i=0;i<literals.size();i++){
			lits.add(toStringPLit(literals.get(i), localConditions.get(i)));
		}
		strRepr+=lits.toString()+globalConditions+").";
		return strRepr;
	}

	//protected String strRepr="[]";
	
	protected List<IndepLiteral> literals=new ArrayList<IndepLiteral>();
	
	protected List<PFCond> localConditions=new ArrayList<PFCond>();
	
	protected PFCond globalConditions=new PFCond();
	
	public static final int MRG_UNION_INITFIT=1;
	
	
	
	
	
	public static void main(String[] args) throws ParseException{
		//test of every function
		
		
		
		IndepPField[] p=new IndepPField[20]; 
		IndepPField temp;
		p[0]=IndepPField.parse("[]");
		p[1]=IndepPField.parse("[+-p_p_lit<3]");
		p[2]=IndepPField.parse(" [p_f(1,_,t), +-e(gfun(h,k),hfun(e,f)) :3,   +-muf <= 5]:3<8");
		p[3]=IndepPField.parse("[+-e(_,_):2  <=1]");
		p[4]=IndepPField.parse("[-e(_,hfun(e,_)):4]:5<= 3");
		p[5]=IndepPField.parse("[muf<3]<4");
		p[6]=IndepPField.parse("");
		p[7]=IndepPField.parse(null);
		p[8]=IndepPField.parse("[a,b,c]");
		p[9]=IndepPField.parse("[b,c,e,f]<=1");
		
//		for (int i=0;i<10;i++){
//			System.out.println("p["+i+"] : "+p[i]);
//			if (p[i]!=null){
//				Env env=new Env();
//				PField pf=p[i].toPField(env);
//				System.out.println("toPField : "+pf);
//				Arguments plits=p[i].getPLiterals();
//				System.out.println("getPLiterals() :"+plits);
//				temp=p[i].addPrefix("p_", false);
//				System.out.println("p["+i+"].addPrefix(p_,false) :"+temp);
//				temp=p[i].addPrefix("p_", true);
//				System.out.println("p["+i+"].addPrefix(p_,true) :"+temp);
//				temp=p[i].removePrefix("p_");
//				System.out.println("p["+i+"].removePrefix(p_) :"+temp);
//				temp=p[i].developPLiterals();
//				System.out.println("p["+i+"].developLiterals() :"+temp);
//				
//				for (int j=i;j<7;j++){
//					temp=p[i].replacePLiterals(p[j].getPLiterals().toString());
//					System.out.println("p["+i+"].replacePLiterals(p["+j+"].getLiterals().toString()) :"+temp);
//					temp=p[i].addToPLiterals(p[j].getPLiterals().toString());
//					System.out.println("p["+i+"].addToPLiterals(p["+j+"].getLiterals().toString()) :"+temp);
//					temp=p[i].mergeWith(p[j]);
//					System.out.println("p["+i+"].mergeWith(p["+j+"]) :"+temp);
//				}
//				System.out.println();
//			}
//			
//		}
//		//TODO test belongsTo
		IndepClause[] c=new IndepClause[20]; 
		c[1]=new IndepClause("[b,c,e]");
		c[0]=new IndepClause("[a,b]");
		c[2]=new IndepClause("[a,e,f]");
		c[3]=new IndepClause("[e]");
		for (int i=0;i<4;i++){
			IndepClause cl=c[i];
			System.out.println();
			System.out.println();
			System.out.println(" Clause : "+cl);
			System.out.println(p[8]);
			System.out.println(" belongs to :"+ p[8].belongsTo(cl));
			System.out.println(" FC belongs to :"+ p[8].belongsToFullCheck(cl));
			System.out.println(" partly belongs to :"+ p[8].partlyBelongsTo(cl));
			System.out.println(" FC partly belongs to :"+ p[8].partlyBelongsToFC(cl));
			System.out.println(" partly belongs to wrt "+p[9]+" : "+ p[8].partlyBelongsTo(cl,p[9]));
			System.out.println(" FC partly belongs to wrt "+p[9]+" : "+ p[8].partlyBelongsToFC(cl,p[9]));
			System.out.println();
			System.out.println(p[9]);
			System.out.println(" belongs to "+ p[9].belongsTo(cl));
			System.out.println(" FC belongs to "+ p[9].belongsToFullCheck(cl));
			System.out.println(" partly belongs to "+ p[9].partlyBelongsTo(cl));
			System.out.println(" FC partly belongs to "+ p[9].partlyBelongsToFC(cl));
			System.out.println(" partly belongs to wrt "+p[8]+" : "+ p[9].partlyBelongsTo(cl,p[8]));
			System.out.println(" FC partly belongs to wrt "+p[8]+" : "+ p[9].partlyBelongsToFC(cl,p[8]));
				
		}
		
	}
	
}

package old_logicLanguage;

import java.util.ArrayList;
import java.util.List;


import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.parser.ParseException;

import genLib.tools.Arguments;


//a CNF with only Unit Clauses
public class UnitClauseCNF extends CNF{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4253144104642346454L;

	public UnitClauseCNF(){
		super();
	}
	public UnitClauseCNF(UnitClauseCNF uccnf){
		this();
		addAll(uccnf);
	}
	public UnitClauseCNF(Arguments arg){
		super(arg);
	}
	public UnitClauseCNF(List<? extends Clause> listCl){
		super(listCl);
	}
	
	// this ENSURES
	public boolean add(IndepClause cl){
		if (cl==null) return false;
		//do not add tautological clauses (ndcheck for [])
		if (cl.getLiterals().size()==1)
			return super.add(cl);
//		System.out.println("Attempt to add non-unit Clause "+cl+" to a UnitClauseCNF !!!!");
		return false;
	}
	
	public List<Literal> getLiterals(Env env) throws ParseException{
		List<Literal> result=new ArrayList<Literal>();
		for (IndepClause cl:this){
			result.add(cl.toClause(env).getLiterals().get(0));
		}		
		return result;
	}
	
	public List<IndepLiteral> getLiterals() throws ParseException{
		List<IndepLiteral> result=new ArrayList<IndepLiteral>();
		for (IndepClause cl:this){
			result.add(cl.getLiterals().get(0));
		}		
		return result;
	}
	
	public IndepClause getNegation(boolean skolemize) throws ParseException{
		IndepClause posDnf=new IndepClause(getLiterals().toString());
		UnitClauseCNF negDnf=posDnf.getNegation(skolemize);
		IndepClause negClause=new IndepClause(negDnf.getLiterals().toString());
		return negClause;		
	}
}

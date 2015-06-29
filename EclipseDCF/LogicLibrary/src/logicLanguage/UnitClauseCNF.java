package logicLanguage;

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
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
	public UnitClauseCNF(Env env, Options opt, Arguments arg) throws ParseException{
		super(env, opt, arg);
	}
	public UnitClauseCNF(List<? extends Clause> listCl){
		super(listCl);
	}
	
	// this ENSURES
	public boolean add(Clause cl){
		if (cl==null) return false;
		//do not add tautological clauses (ndcheck for [])
		if (cl.getLiterals().size()==1)
			return super.add(cl);
//		System.out.println("Attempt to add non-unit Clause "+cl+" to a UnitClauseCNF !!!!");
		return false;
	}
	
	public List<Literal> getLiterals() throws ParseException{
		List<Literal> result=new ArrayList<Literal>();
		for (Clause cl:this){
			result.add(cl.getLiterals().get(0));
		}		
		return result;
	}
	/*
	public List<IndepLiteral> getLiterals() throws ParseException{
		List<IndepLiteral> result=new ArrayList<IndepLiteral>();
		for (IndepClause cl:this){
			result.add(cl.getLiterals().get(0));
		}		
		return result;
	}*/
	
	public Clause getNegation(Env env, boolean skolemize) throws ParseException{
		Clause posDnf= new Clause(env, getLiterals());
		UnitClauseCNF negDnf=IndepClause.getNegation(env, posDnf, skolemize);
		Clause negClause = new Clause(env, negDnf.getLiterals());
		return negClause;		
	}
}

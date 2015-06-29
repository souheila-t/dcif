package mars.reasoning;

import java.util.ArrayList;
import java.util.List;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import logicLanguage.UnitClauseCNF;

import org.nabelab.solar.parser.ParseException;

import genLib.tools.Arguments;



//Should be : 
//	context : CNF
//  hypothesis: UnitClauseCNF

public class FullHypothesis {

	public FullHypothesis(){
		hypothesis=new UnitClauseCNF();
		context=new CNF();
	}
	
	public FullHypothesis(UnitClauseCNF hyp){
		this();
		hypothesis.addAll(hyp);
	}
	
	public FullHypothesis(FullHypothesis fullHyp){
		this();
		hypothesis.addAll(fullHyp.getBase());
		addCtx(fullHyp.getContext());
	}
	
	public FullHypothesis(UnitClauseCNF hyp, CNF ctx){
		this(hyp);
		addCtx(ctx);
	}

	public FullHypothesis(Arguments arg) throws ParseException{
		this();
		if (arg.size()!=2)
			throw new ParseException("Wrong number of strings, should be [hyp, ctx]");
		String strHyp=arg.get(0);
		String strCtx=arg.get(1);
		UnitClauseCNF hyp=new UnitClauseCNF(Arguments.parse(strHyp, true));
		CNF ctx=new CNF(Arguments.parse(strCtx, true));
		hypothesis.addAll(hyp);
		addCtx(ctx);
	}
	
	public UnitClauseCNF getBase(){
		return hypothesis;
	}
	
	public CNF getContext(){
		return context;
	}
	
	public List<IndepClause> getAll(){
		List<IndepClause> result=new ArrayList<IndepClause>();
		result.addAll(getBase());
		result.addAll(getContext());
		return result;
	}
	
	public int sizeHyp(){
		return hypothesis.size();
	}
	
	public int sizeCtx(){
		return context.size();
	}
	
	public void setHypothesis(UnitClauseCNF hyp){
		hypothesis.clear();
		hypothesis.addAll(hyp);
		removeCtx();
	}
	
	public void setHypothesis(UnitClauseCNF hyp, CNF ctx){
		hypothesis.clear();
		removeCtx();
		hypothesis.addAll(hyp);
		addCtx(ctx);
	}
	
	public void setContext(CNF ctx){
		removeCtx();
		addCtx(ctx);
	}
	
	public void removeCtx(){
		context.clear();
	}
	
	//ensures that context never contains hypothesis
	public boolean addCtx(CNF ctx){
		boolean change=context.addAllExcept(ctx,hypothesis);
		return change;
	}
	
	public static boolean isEquiv(FullHypothesis h1,FullHypothesis h2){
		if (h1==h2) return true;
		if ((h1==null) || (h2==null)) return false;
		return h1.getBase().isEquiv(h2.getBase()) && h1.getContext().isEquiv(h2.getContext());
	}
	
	public boolean isEquiv(FullHypothesis h){
		return isEquiv(this,h);
	}
	
	//representation to ease reading
	public String toString(){
		return getBase()+"  /  "+getContext();
	}

	//representation to ease parsing
	public Arguments toArgument(){
		Arguments res=new Arguments();
		res.add(getBase().toString());
		res.add(getContext().toString());
		return res;
	}

	protected UnitClauseCNF hypothesis;
	protected CNF context;
	
	
}

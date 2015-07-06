package logicLanguage;

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.parser.ParseException;

import genLib.tools.Pair;


public class ClauseConnectionNetwork {

	public class Edge{
		public Edge(int clause, int symbol, String sign){
			this.clause=clause;
			term=symbol;
			this.sign=sign;
		}
		
		public int clause;
		public int term;
		public String sign;
	}
	
	public ClauseConnectionNetwork(Env env, CNF clausalTheory) throws ParseException{
		theory=clausalTheory;
		this.env = env;
		buildLanguages();
		buildNetwork();
	}
	
	public void buildLanguages() throws ParseException{
		languageAtoms=new ArrayList<Literal>();
		commonLanguageAtoms=new ArrayList<Literal>();
		
		for (Clause c:theory){
			List<Literal> atoms=IndepClause.getVocabulary(env, c);
			List<Literal> newSymbols=new ArrayList<Literal>();
			for (Literal atom:atoms){
				Literal pred= IndepLiteral.getPositiveVersion(atom);
				Literal neg= IndepLiteral.negate(atom);
				if (usePolarity){
					if (!languageAtoms.contains(atom)){
						if (!newSymbols.contains(atom)) newSymbols.add(atom);
						if (languageAtoms.contains(neg) && !commonLanguageAtoms.contains(pred))
							commonLanguageAtoms.add(pred);
					}
				}
				else{
					if (!languageAtoms.contains(pred)){
						if (!newSymbols.contains(pred)) newSymbols.add(pred);
					}
					else if (!commonLanguageAtoms.contains(pred))
						commonLanguageAtoms.add(pred);
				}	
			}
			languageAtoms.addAll(newSymbols);
		}
	}
	
	public void buildNetwork() throws ParseException{
		clauses=new ArrayList<String>();
		hasSymbol=new ArrayList<Edge>();
		for (int clInd=0;clInd<theory.size();clInd++){
			Clause c=theory.get(clInd);
			clauses.add(c.getName());
			List<Literal> atoms=IndepClause.getVocabulary(env, c);
			for (Literal atom:atoms){
				Literal pred=IndepLiteral.getPositiveVersion(atom);
				int symbInd=commonLanguageAtoms.indexOf(pred);
				if (symbInd>=0){
					String sign="pos";
					if (!atom.isPositive()) sign="neg";
					hasSymbol.add(new Edge(clInd,symbInd,sign));
				}
			}
		}
	}

	public String convertToAsp(){
		String res="";
		int i;
		res+="#const maxEdges="+hasSymbol.size()+".\n\n";
		res+="sign(pos). \n sign(neg). \n";
		res+="clause(0.."+(clauses.size()-1)+"). \n";
		res+="clause(0.."+(clauses.size())+"). \n";
		res+="clauseNumber(0.."+(clauses.size()-1)+"). \n";
		res+="languageAtom(0.."+(commonLanguageAtoms.size()-1)+"). \n";
		
		for (Edge edge:hasSymbol)
			res+="hasAtom("+edge.clause+","+edge.term+","+edge.sign+"). \n";
		
		return res;
	}
	
	public WeightedGraph<String> convertToGraph(){
		List<List<Pair<Integer, Integer>>> graph=new ArrayList<List<Pair<Integer,Integer>>>();
		int i,j;
		for (i=0;i<clauses.size();i++)
			graph.add(new ArrayList<Pair<Integer,Integer>>());
		
		int nbEdges=0;
		for (i=0;i<hasSymbol.size();i++){
			Edge e1=hasSymbol.get(i);
			for (j=i+1;j<hasSymbol.size();j++){
				Edge e2=hasSymbol.get(j);
				if (e1.term==e2.term && !e1.sign.equals(e2.sign) && e1.clause!=e2.clause){
					boolean add=addTo(graph.get(e1.clause),e2.clause,e1.term);
					add=addTo(graph.get(e2.clause),e1.clause,e1.term) || add;
					if (add) nbEdges++;
				}
			}
		}
		return new WeightedGraph(graph,clauses,nbEdges);
	}
	
	private static boolean addTo(List<Pair<Integer,Integer>> list, int clause, int term){
		for (int i=0;i<list.size();i++){
			Pair<Integer,Integer> p=list.get(i);
			if (p.getLeft()==clause){
				if (p.getRight()!=term)
					list.set(i, new Pair(clause,p.getRight()+1));
				return false;
			}
		}
		list.add(new Pair(clause,1));
		return true;
	}
	
	protected CNF theory;
	private Env env = null;
	protected List<String> clauses;
	protected List<Literal> languageAtoms;
	protected List<Literal> commonLanguageAtoms;
	//first is the clause, second the symbol
	protected List<Edge> hasSymbol;
	
	public boolean usePolarity;
}

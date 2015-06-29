package distrib;

import io.LoaderTool;

import java.util.List;

import logicLanguage.IndepClause;
import logicLanguage.PbFormula;

import problemDistribution.DCFProblem;
import solarInterface.IndepPField;
import solarInterface.SolProblem;

public class BioDistributedProblem extends DCFProblem {

	public void loadFromDistributor(String filename) throws Exception{
		SolBioNetParsingDistributor source=new SolBioNetParsingDistributor();
		LoaderTool.load(filename, ".sol", source);
		System.out.println(source);
		System.out.println("Analysis finished, now distributing...");
		for (int i=0;i<source.groups.size();i++){
			List<Integer> elements=source.groups.get(i);
			agents.add("ag"+i);
			localProblems.add(new SolProblem());
			for (Integer elt:elements){
				for (PbFormula f:source.formulas.get(elt.intValue())){
					IndepClause cl=f.toIndepClause();
					localProblems.get(i).addClause(cl, f.getRole().equalsIgnoreCase("top_clause"));
				}
			}
		}
		pf=IndepPField.parse(source.pfLine);
	}

}

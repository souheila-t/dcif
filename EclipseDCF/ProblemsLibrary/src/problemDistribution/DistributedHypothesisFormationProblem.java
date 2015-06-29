package problemDistribution;

import java.util.List;

import solarInterface.IndepPField;

public interface DistributedHypothesisFormationProblem<Theory> {

	public int getNbAgents();
	// Theory should contain individual hypField, current hypothesis, clauses and observations 
	public List<Theory> getDistTheory();
	public IndepPField getGbPField();
	public void setMaxLength(int maxLength);

}

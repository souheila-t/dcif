/**
 * 
 */
package old_problemDistribution;

import java.util.List;

import old_solarInterface.IndepPField;

/**
 * @author Gauvain Bourgne
 * @param <Theory> the format of the individual theory that should be given as output
 *
 */
public interface DistributedConsequenceFindingProblem<Theory> {
	
	public int getNbAgents();
	public List<Theory> getDistTheory();
	public IndepPField getGbPField();
	public void setMaxLength(int maxLength);
	public void setGbPField(IndepPField pf);
	public int getMaxDepth();
	public void setMaxDepth(int maxDepth);
	
}

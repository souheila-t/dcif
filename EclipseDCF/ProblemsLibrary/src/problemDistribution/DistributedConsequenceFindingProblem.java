/**
 * 
 */
package problemDistribution;

import java.util.List;

import org.nabelab.solar.pfield.PField;

import solarInterface.IndepPField;

/**
 * @author Gauvain Bourgne
 * @param <Theory> the format of the individual theory that should be given as output
 *
 */
public interface DistributedConsequenceFindingProblem<Theory> {
	
	public int getNbAgents();
	public List<Theory> getDistTheory();
	public PField getGbPField();
	public void setMaxLength(int maxLength);
	public void setGbPField(PField pf);
	public int getMaxDepth();
	public void setMaxDepth(int maxDepth);
	
}

package problemDistribution;

public class DistributionParameters {


	public DistributionParameters(int n,
			double knownRuleProportion,
			double knownObsProportion,
			double initialRuleRedundancy,
			double initialObsRedundancy) {
		this.initialObsRedundancy = initialObsRedundancy;
		this.initialRuleRedundancy = initialRuleRedundancy;
		this.knownObsProportion = knownObsProportion;
		this.knownRuleProportion = knownRuleProportion;
		this.n = n;
	}
	
	public DistributionParameters(int n, double[] params) {
		this.initialObsRedundancy = params[3];
		this.initialRuleRedundancy = params[2];
		this.knownObsProportion = params[1];
		this.knownRuleProportion = params[0];
		this.n = n;
	}
	/**
	 * @return the number of agents
	 */
	public int getNbAgent() {
		return n;
	}
	/**
	 * @return the knownRuleProportion
	 */
	public double getKnownRuleProportion() {
		return knownRuleProportion;
	}
	/**
	 * @return the knownObsProportion
	 */
	public double getKnownObsProportion() {
		return knownObsProportion;
	}
	/**
	 * @return the initialRuleRedundancy
	 */
	public double getInitialRuleRedundancy() {
		return initialRuleRedundancy;
	}
	/**
	 * @return the initialObsRedundancy
	 */
	public double getInitialObsRedundancy() {
		return initialObsRedundancy;
	}

	@Override
	public String toString(){
		return Integer.toString(n).concat("_")
				.concat(Double.toString(this.knownRuleProportion)).concat("-")
				.concat(Double.toString(this.knownObsProportion)).concat("-")
				.concat(Double.toString(this.initialRuleRedundancy)).concat("-")
				.concat(Double.toString(this.initialObsRedundancy));
	}

	/** number of agents */
	private int n=4;
	/** percentage of rules in the system */
	private double knownRuleProportion=0.8;
	/** percentage of observations in the system */
	private double knownObsProportion=0.7;
	/** initial rule redundancy */
	private double initialRuleRedundancy=1.5;
	/** initial observations redundancy */
	private double initialObsRedundancy=1.5;
}

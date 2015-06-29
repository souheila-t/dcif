package mars;

import agentCommunicationSystem.protocols.consensus.ConsensusAgent;

public interface ConsensusDiagnoser<CssValue,Hypothesis,Context,Rules,Manifestations> 
		extends ConsensusAgent<CssValue>,
				Diagnoser<Hypothesis,Context,Rules,Manifestations> {
}

package mars;

import agentCommunicationSystem.ThreadAgent;

public interface ConsensusDiagnoserAgent<CssValue, Hypothesis, Context, Rules, Manifestations>
		extends
		ConsensusDiagnoser<CssValue, Hypothesis, Context, Rules, Manifestations>,
		ThreadAgent {

}

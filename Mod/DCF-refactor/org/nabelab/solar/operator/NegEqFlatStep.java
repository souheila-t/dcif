package org.nabelab.solar.operator;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Node;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.proof.ProofStep;

public class NegEqFlatStep extends ProofStep {

  /**
   * Constructs a symmetrical splitting step.
   * @param env    the environment.
   * @param clause the tableau clause.
   */
  public NegEqFlatStep(Env env, Literal lit1, Literal lit2) {
    super(env);
    this.lit1 = lit1;
    this.lit2 = lit2;
  }

  /**
   * Converts this proof step to the corresponding operator.
   * @param tableau the tableau. 
   * @param node    the node to which this operator is applied.
   * @return the corresponding operator.
   */
  public Operator convert(Tableau tableau, Node node) {
    return null;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return "negative equality flattening";
  }
  
  /** The first literal which is used to extend this node. */
  @SuppressWarnings("unused")
  private Literal lit1 = null;
  /** The second literal which is used to extend this node. */
  @SuppressWarnings("unused")
  private Literal lit2 = null;  

}

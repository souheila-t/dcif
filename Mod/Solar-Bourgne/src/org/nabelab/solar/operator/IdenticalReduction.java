/************************************************************************
 Copyright 2003-2009, University of Yamanashi. All rights reserved. 
 By using this software the USER indicates that he or she has read,
 understood and will comply with the following:

 --- University of Yamanashi hereby grants USER non-exclusive permission
 to use, copy and/or modify this software for internal, non-commercial,
 research purposes only. Any distribution, including commercial sale or
 license, of this software, copies of the software, its associated
 documentation and/or modifications of either is strictly prohibited
 without the prior consent of University of Yamanashi. Title to
 copyright to this software and its associated documentation shall at
 all times remain with University of Yamanashi.  Appropriate copyright
 notice shall be placed on all software copies, and a complete copy of
 this notice shall be included in all copies of the associated
 documentation. No right is granted to use in advertising, publicity or
 otherwise any trademark, service mark, or the name of University of
 Yamanashi.

 --- This software and any associated documentation is provided "as is"

 UNIVERSITY OF YAMANASHI MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS
 OR IMPLIED, INCLUDING THOSE OF MERCHANTABILITY OR FITNESS FOR A
 PARTICULAR PURPOSE, OR THAT USE OF THE SOFTWARE, MODIFICATIONS, OR
 ASSOCIATED DOCUMENTATION WILL NOT INFRINGE ANY PATENTS, COPYRIGHTS,
 TRADEMARKS OR OTHER INTELLECTUAL PROPERTY RIGHTS OF A THIRD PARTY.

 University of Yamanashi shall not be liable under any circumstances for
 any direct, indirect, special, incidental, or consequential damages
 with respect to any claim by USER or any third party on account of or
 arising from the use, or inability to use, this software or its
 associated documentation, even if University of Yamanashi has been
 advised of the possibility of those damages.
************************************************************************/

package org.nabelab.solar.operator;

import org.nabelab.solar.Env;
import org.nabelab.solar.Node;
import org.nabelab.solar.Stats;
import org.nabelab.solar.proof.IdenticalReductionStep;
import org.nabelab.solar.proof.ProofStep;

/**
 * @author nabesima
 *
 */
public class IdenticalReduction extends Operator {

  /**
   * Constructs a reduction operator which is applied to the specified node.
   * @param env    the environment.
   * @param node   the specified node.
   * @param target the parent node that is a reduction target.
   * @param subst  the unification for the extension. 
   */
  public IdenticalReduction(Env env, Node node, Node target) {
    super(env, node);
    this.target    = target;
    this.mandatory = true;
    tableau.stats().incProds(Stats.IDENTICAL_REDUCTION);
  }
  
  /**
   * Applies this operator.
   * @return true if the application of this operator succeeds.
   */
  public boolean apply() {
    super.apply();
    node.setReductionTarget(target);
    node.addTag(IDENTICAL_REDUCED);
    tableau.stats().incSuccs(Stats.IDENTICAL_REDUCTION);
    return true;
  }

  /**
   * Cancels this operator.
   */
  public void cancel() {
    node.clearReductionTarget();
    node.removeTag(IDENTICAL_REDUCED);
    super.cancel();
  }

  /**
   * Returns the target node of the reduction.
   * @return the target node of the reduction.
   */
  public Node getTargetNode() {
    return target;
  }
  
  /**
   * Converts this operator to the proof step.
   * @return the proof step.
   */
  public ProofStep convert() {
    return new IdenticalReductionStep(env, node.getDepth() - target.getDepth());
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return "IRD " + target + "(" + getNumSyms() + "s)";
  }
  
  /**
   * Returns a simple string representation of this object.
   * @return a simple string representation of this object.
   */
  public String toSimpleString() {
    return "[IRD]";
  }
  
  /** The parent node that is a reduction target. */
  private Node target = null;
  
}

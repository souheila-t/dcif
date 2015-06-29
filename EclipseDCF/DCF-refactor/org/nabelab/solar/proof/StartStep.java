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

package org.nabelab.solar.proof;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Node;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.operator.Operator;
import org.nabelab.solar.operator.RootExtension;

/**
 * @author nabesima
 *
 */
public class StartStep extends ProofStep {

  /**
   * Constructs a start proof step.
   * @param env    the environment.
   * @param clause the start clause.
   */
  public StartStep(Env env, Clause clause) {
    super(env);
    this.clause = clause;
  }

  /**
   * Converts this proof step to the corresponding operator.
   * @param tableau the tableau. 
   * @param node    the node to which this operator is applied.
   * @return the corresponding operator.
   */
  public Operator convert(Tableau tableau, Node node) {
    return new RootExtension(env, node, clause);
  }
  
  /**
   * Returns the used clause in this proof step.
   * @return the used clause in this proof step.
   */
  public Clause getClause() {
    return clause;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return clause.toString();
  }
  
  /** The top clause. */
  private Clause clause = null;
}

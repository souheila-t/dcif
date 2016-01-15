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

package org.nabelab.solar.constraint;

import org.nabelab.solar.Env;
import org.nabelab.solar.Node;
import org.nabelab.solar.OptionTypes;
import org.nabelab.solar.Stats;
import org.nabelab.solar.VarTable;

/**
 * @author nabesima
 *
 */
public abstract class Constraint implements OptionTypes {
  
  /**
   * Construct a constraint.
   * @param env     the environment.
   * @param node    the node which contains this constraint.
   * @param type    the type of this constraint.
   */
  public Constraint(Env env, Node node, int type) {
    this.env      = env;
    this.node     = node;
    this.varTable = env.getVarTable();
    this.type     = type;
    this.time     = env.getTimeStep();
  }
  
  /**
   * Checks the status of this constraint and returns it.
   * @return the status of this constraint.
   */
  public int check() {
    env.getStats().incTests(type);
    int state = isSatisfiable();
    if (state == UNSAT)  
      env.getStats().incSuccs(type);
    return state;
  }

  /**
   * Returns the satisfiability of this constraint.
   * @return the satisfiability of this constraint.
   */
  protected abstract int isSatisfiable();
  
  /**
   * Returns the size of this constraint.
   * @return the size of this constraint.
   */
  public abstract int size();
  
  /**
   * Returns the hash code value of this object.
   * @return the hash code value of this object.
   */
  public abstract int hashCode();
  
  /**
   * Compares the specified object with this object for equality.
   * @param obj the reference object with which to compare.  
   */
  public abstract boolean equals(Object obj);
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    switch (type) {
    case Stats.REGULARITY_CHK:
      return "REGU";
    case Stats.COMPLEMENT_FREE_CHK:
      return "COMP";
    case Stats.TAUTOLOGY_FREE_CHK:
      return "TAUT";
    case Stats.UNIT_SUBSUMPTION_CHK:
      return "UNIS";
    case Stats.SKIP_REGULARITY_CHK:
      return "SKRG";
    case Stats.EQ_CONSTRAINT_CHK:
      return "EQCN";
    default:
      return "?(" + type + ")";
    }
  }
  
  /** The satisfiability of the constraint is not decidable. */
  public final static int UNKNOWN = -1;
  /** The constraint is not satisfiable. */
  public final static int UNSAT = 0;
  /** The constraint is satisfiable. */
  public final static int SAT = 1;
  
  /** The environment. */
  protected Env env = null;
  /** The node which contains this constraint. */
  protected Node node = null;

  /** The variable table. */
  protected VarTable varTable = null;
  /** The type of this constraint. */
  protected int type = 0;
  /** The time step generated at this constraint. */
  protected long time = 0;

}

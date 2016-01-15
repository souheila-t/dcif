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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.nabelab.solar.DebugTypes;
import org.nabelab.solar.Env;
import org.nabelab.solar.OptionTypes;
import org.nabelab.solar.Stats;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.operator.Operator;

/**
 * @author nabesima
 *
 */
public class Constraints implements Iterable<Constraint>, OptionTypes, DebugTypes {
  
  /**
   * Construct a set of constraints.
   * @param env     the environment.
   * @param tableau the tableau to which this constraints belong.
   */
  public Constraints(Env env, Tableau tableau) {
    this.env     = env;
    this.tableau = tableau;
    this.stats   = env.getStats();
  }
  
  /**
   * Adds the specified constraint to this set if it is not already present.
   * @param c a constraint to be added.
   * @return true if this set did not already contain the specified constraint.
   */
  public boolean add(Constraint c) {
    boolean added = elements.add(c);
    if (added) {
      curNumConstraints += c.size();
      if (stats.getProds(Stats.MAX_CONSTRAINTS) < curNumConstraints) {
        stats.setProds(Stats.MAX_CONSTRAINTS, curNumConstraints);
//        System.out.println(env.getTimeStep() + ", " + curNumConstraints);
//        System.out.println(tableau);
      }
    }
    return added;
  }
  
  /**
   * Adds the specified set of constraints.
   * @param cs the specified set of constraints.
   */
  public void addAll(List<Constraint> cs) {
    for (Constraint c : cs) {
      curNumConstraints += c.size();
      elements.add(c);
    }
    if (stats.getProds(Stats.MAX_CONSTRAINTS) < curNumConstraints)
      stats.setProds(Stats.MAX_CONSTRAINTS, curNumConstraints);
  }

  /**
   * Removes the specified set of constraints.
   * @param cs the specified set of constraints.
   */
  public void removeAll(List<Constraint> cs) {
    for (Constraint c : cs) {
      curNumConstraints -= c.size();
      elements.remove(c);
    }
  }
  
  /**
   * Returns true if all the constraints are satisfiable.
   * @return true if all the constraints are satisfiable.
   */
  public boolean backtrackUntilSatisfiable() {

//    if (tableau.getOptions().use(USE_TEST1)) {
//      if (tableau.inf() % 8 != 0)
//        return true;
//    }
    
    ArrayList<Constraint> satisfied = env.getSATConstraintList();
    
    OuterLoop:
    while (true) {
      satisfied.clear();
      for (Constraint c : elements) {
        int status = c.check();
        if (status == Constraint.UNSAT) {
          if (env.dbgNow(DBG_TABLEAUX)) {
            System.out.println();
            System.out.println("FAILED by " + c + ".");
          }
          if (!tableau.cancel()) {
            assert(false);
            return false;
          }
          continue OuterLoop;    // Because some constraints may be removed in the cancellation.       
        }
        else if (status == Constraint.SAT) 
          satisfied.add(c);
      }
      if (!satisfied.isEmpty()) {
        Operator last = tableau.getLastOperator();
        if (last != null) {
          last.addSatisfied(satisfied);
          removeAll(satisfied);
        }
        if (env.dbgNow(DBG_CONSTRAINT)) {
          System.out.println();
          for (Constraint c : satisfied)
            System.out.println("Satisfied " + c);
        }
      }
      break;
    }
    
    return true;
  }

  /**
   * Returns an iterator over the elements in this set. The elements are returned in no particular order.
   * @return an iterator over the elements in this set. The elements are returned in no particular order.
   */
  public Iterator<Constraint> iterator() {
    return elements.iterator();
  }
  
  /**
   * Returns true if there is no constraints.
   * @return true if there is no constraints.
   */
  public boolean isEmpty() {
    return elements.isEmpty();
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return elements.toString();
  }
  
  /** The environment. */
  private Env env = null;
  /** The tableau to which this constraints belong. */
  private Tableau tableau = null;  
  /** The set of constraints. */
  private HashSet<Constraint> elements = new HashSet<Constraint>();

  /** The statistics information. */
  private Stats stats = null;
  /** The current number of constraints. */
  private int curNumConstraints = 0;
}

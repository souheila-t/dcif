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

package org.nabelab.solar;

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.operator.Operator;
import org.nabelab.solar.proof.Proof;

/**
 * @author nabesima
 *
 */
public class AppOps {
  
  /**
   * Constructs an empty list of applied operators.
   * @param env the environment.
   * @param cfp the consequence finding problem.
   */
  public AppOps(Env env, CFP cfp) {
    this.env = env;
    this.cfp = cfp;
    this.ops = new ArrayList<Operator>();
  }
  
  /**
   * Adds the specified operator to the end of this list.
   * @param op  the operator to add.
   */
  public void push(Operator op) {
    ops.add(op);
  }
  
  /**
   * Removes the last operator of this list.
   * @return the last operator.
   */
  public Operator pop() {
    return ops.remove(ops.size() - 1);
  }

  /**
   * Returns the last operator.
   * @return the last operator.
   */
  public Operator last() {
    return ops.get(ops.size() - 1);
  }
  
  /**
   * Returns true if this list is empty.
   * @return true if this list is empty.
   */
  public boolean isEmpty() {
    return ops.isEmpty();
  }
  
  /**
   * Returns the proof of the specified node.
   * @param target the target clause of the proof.
   * @param node the node to prove.
   * @return the proof of the specified node.
   */
  public Proof getProof(Conseq target, Node node) {
    ArrayList<Operator> used = new ArrayList<Operator>();
    if (node == null) 
      used.addAll(ops);
    else {
      // Finds the applied operator of the node.
      int index = -1;
      for (int i=0; i < ops.size(); i++) {  
        if (ops.get(i).getNode() == node) { 
          index = i;
          break;
        }
      }
      // Adds applied operators from the index.
      if (index == -1)
        return null;
      for (int i=index; i < ops.size(); i++)
        used.add(ops.get(i));
    }

    return new Proof(env, cfp, target, used);    
  }
  
  /**
   * Returns the set of used clauses in this proof.
   * @return the set of used clauses in this proof.
   */
  public List<Clause> getUsedClauses() { 
    ArrayList<Clause> clauses = new ArrayList<Clause>();
    for (Operator op : ops) 
      if (op.getClause() != null)
        clauses.add(op.getClause());
    return clauses;
  }
  
  /**
   * Returns a simple string representation of this object.
   * @return a simple string representation of this object.
   */
  public String toSimpleString() {
    StringBuilder str = new StringBuilder();
    for (Operator op : ops) 
      str.append(op.toSimpleString());
    return str.toString();
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    for (Operator op : ops) 
      str.append(op.toString() + "\n");
    return str.toString();
  }

  /** The environment. */
  private Env env = null;
  /** The consequence finding problem. */
  private CFP cfp = null;
  /** The list of applied operators. */
  private ArrayList<Operator> ops = null;
}

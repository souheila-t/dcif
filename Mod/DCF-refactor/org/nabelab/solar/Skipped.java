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

import org.nabelab.solar.indexing.DiscTree;

/**
 * @author nabesima
 *
 */
public class Skipped {

  /**
   * Constructs a empty set of skipped nodes. 
   * @param env the environment.
   */
  public Skipped(Env env) {
    this.env  = env;
    this.tree = new DiscTree<Node>(env, false);
  }
  
  /**
   * Adds the specified node to the skipped nodes.
   * @param node the specified node to add.
   */
  public void add(Node node){
    Literal lit = node.getLiteral();
    nodes.add(node);
    tree.add(lit, node);
  }
  
  /**
   * Removes the specified node from the skipped nodes.
   * @param lit the specified node to be removed.
   */
  public void remove(Node node) {
    Node removed = nodes.remove(nodes.size() - 1);
    Literal  lit = node.getLiteral();
    assert(lit == removed.getLiteral());
    tree.remove(lit, node);
  }

  /**
   * Clears the skipped nodes.
   */
  public void clear() {
    nodes.clear();
    tree = new DiscTree<Node>(env, false);
  }
  
  /**
   * Returns the unifiable literals with the specified literal.
   * @param lit  the specified literal to check.
   * @return the unifiable literals with the specified literal.
   */
  public List<Unifiable<Node>> findUnifiable(Literal lit) {
    return tree.findUnifiable(lit);
  }
  
  /**
   * Returns the complementary unifiable literals with the specified literal.
   * @param lit  the specified literal to check.
   * @return the complementary unifiable literals with the specified literal.
   */
  public List<Unifiable<Node>> findCompUnifiable(Literal lit) {
    return tree.findCompUnifiable(lit);
  }
  
  /**
   * Converts the skipped literals to the consequence.
   * @return the consequence.
   */
  public Conseq convertToConseq() {
    Literal[] lits = new Literal[nodes.size()];
    for (int i=0; i < nodes.size(); i++)
      lits[i] = nodes.get(i).getLiteral().instantiate();
    Conseq c = new Conseq(env, "consequence", ClauseTypes.CONSEQUENCE, lits);
    //c.rename(env.getNegVarRenameMap());
    return c; 
  }
  
  /**
   * Converts the skipped literals to the clause.
   * @return the clause.
   */
  public Clause convertToClause() {
    Literal[] lits = new Literal[nodes.size()];
    for (int i=0; i < nodes.size(); i++)
      lits[i] = nodes.get(i).getLiteral();
    return new Clause(env, "clause", ClauseTypes.AXIOM, lits); 
  }

  /**
   * Converts the skipped literals to the clause.
   * @return the clause.
   */
  public Clause convertToInstantiatedClause() {
    Literal[] lits = new Literal[nodes.size()];
    for (int i=0; i < nodes.size(); i++)
      lits[i] = nodes.get(i).getLiteral().instantiate();
    return new Clause(env, "clause", ClauseTypes.AXIOM, lits); 
  }

  /**
   * Returns the literal at the specified index.
   * @param index the specified position.
   * @return Returns the literal at the specified index.
   */
  public Literal getLiteral(int index) {
    return nodes.get(index).getLiteral();
  }
  
  /**
   * Returns the node at the specified index.
   * @param index the specified position.
   * @return Returns the node at the specified index.
   */
  public Node getNode(int index) {
    return nodes.get(index);
  }
  
  /**
   * Returns the number of skipped nodes.
   * @return the number of skipped nodes.
   */
  public int size() {
    return nodes.size();
  }
  
  /**
   * Returns true if there is no skipped nodes.
   * @return true if there is no skipped nodes.
   */
  public boolean isEmpty() {
    return nodes.isEmpty();
  }
  
  /** The environment. */
  private Env env = null;
  /** The discrimination tree. */
  private DiscTree<Node> tree = null;
  /** The set of nodes. */
  private ArrayList<Node> nodes = new ArrayList<Node>(); 
  
}

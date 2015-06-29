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

package org.nabelab.solar.indexing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.VarTable;
import org.nabelab.solar.util.ArrayStack;


/**
 * @author nabesima
 *
 */
public class FVecTrie {
  
  /**
   * Constructs a feature vector tries from the specified clauses.
   * @param env  the environment.
   * @param fvec the set of feature vectors used for deciding node order.
   * @param newNameSpace if true, then all variables in this tree belong to another name space.
   * @return the feature vector trie.
   */
  public FVecTrie(Env env, boolean newNameSpace) {
    this.env           = env;
    this.varTable      = env.getVarTable();
    this.root          = new FVecNode(env, this);
    this.clauseNodeMap = new HashMap<Clause, FVecNode>();
    this.newNameSpace  = newNameSpace;
  }
  
  /**
   * Registers the specified clauses with the feature vector to this trie.
   * @param fvec   the feature vector of the clause.
   * @param clause the clause to add.
   * @deprecated
   */
  public void old_add(FVec fvec, Clause clause) {
    FVecNode node = root;
    for (int i=0; i < fvec.size(); i++) {
      FVecNode child = node.findChild(fvec.get(i));
      if (child == null) 
        child = node.addChild(fvec.get(i));
      node = child;
    }
    node.addClause(clause);
    clauseNodeMap.put(clause, node);
    if (numVars < fvec.getMaxVarName() + 1)
      numVars = fvec.getMaxVarName() + 1;
  }
  
  /**
   * Registers the specified clauses with the feature vector to this trie.
   * @param fvec   the feature vector of the clause.
   * @param clause the clause to add.
   */
  public void add(FVec fvec, Clause clause) {

    if (!root.hasChildren()) {
      FVecNode child = root.addChild(fvec.get(0));
      child.addClause(clause);
      clauseNodeMap.put(clause, child);
      if (numVars < fvec.getMaxVarName() + 1)
        numVars = fvec.getMaxVarName() + 1;
      return;
    }

    FVecNode node = root;
    int index = 0;
    while (node.hasChildren()) {
    
      FVecNode child = node.findChild(fvec.get(index));
      if (child == null) {
        child = node.addChild(fvec.get(index));
        child.addClause(clause);
        clauseNodeMap.put(clause, child);
        if (numVars < fvec.getMaxVarName() + 1)
          numVars = fvec.getMaxVarName() + 1;
        return;
      }
      
      node = child;
      index++;
      continue;
    } 
      
    assert(node.hasClauses());
      
    FVec otherFVec = node.getClauses().get(0).getFVec(false);

    // If two feature vectors are same, then adds clause to the child.
    boolean same = true;
    for (int i=index; i < fvec.size(); i++) {
      if (fvec.get(i) != otherFVec.get(i)) {
        same = false;
        break;
      }
    }
    if (same) {
      node.addClause(clause);
      clauseNodeMap.put(clause, node);
      if (numVars < fvec.getMaxVarName() + 1)
        numVars = fvec.getMaxVarName() + 1;
      return;
    }
      
    // Removes registered clauses in the node.
    List<Clause> otherClauses = node.removeAllClauses();
    for (Clause c : otherClauses)
      clauseNodeMap.remove(c);
    
    // Add nodes as long as they have the same feature.
    while (fvec.get(index) == otherFVec.get(index)) {
      node = node.addChild(fvec.get(index));
      index++;
    }
      
    FVecNode leaf      = node.addChild(fvec.get(index));
    FVecNode otherLeaf = node.addChild(otherFVec.get(index));

    leaf.addClause(clause);
    clauseNodeMap.put(clause, leaf);
    if (numVars < fvec.getMaxVarName() + 1)
      numVars = fvec.getMaxVarName() + 1;
      
    for (Clause c : otherClauses) {
      otherLeaf.addClause(c);
      clauseNodeMap.put(c, otherLeaf);
    }
  }
  
  /**
   * Removes the specified set of clauses.
   * @param clause the set of clauses to be removed.
   */
  public void remove(Iterable<Clause> clauses) {
    for (Clause clause : clauses)
      remove(clause);
  }
  
  /**
   * Removes the specified clause.
   * @param clause the clause to be removed.
   */
  public void remove(Clause clause) {
    FVecNode node = clauseNodeMap.get(clause);
    remove(node, clause);
  }
  
  /**
   * Removes the specified clause from the specified leaf node.
   * @param node   the leaf node.
   * @param clause the clause to be removed.
   */
  public void remove(FVecNode node, Clause clause) {
    node.removeClause(clause);
    clauseNodeMap.remove(clause);
    if (node.hasClauses())
      return;
    while (!node.isRoot()) {
      FVecNode parent = node.getParent();
      parent.removeChild(node);
      if (parent.hasChildren())
        break;
      node = parent;
    }
  }
  
  /**
   * Returns the set of clauses registered in this trie.
   * @return the set of clauses registered in this trie.
   */
  public Set<Clause> getClauses() {
    return clauseNodeMap.keySet();
  }
  
  /**
   * Returns a subsuming clause if the specified clause is subsumed by the clause in this trie (forward subsumption checking).
   * @param fvec   the feature vector of the clause.
   * @param clause the clause to be checked.
   * @return the subsuming clause.
   */
  public Clause findSubsuming(FVec fvec, Clause clause) {
    FVecNode child = root.getFirstChild();
    if (child == null || child.getValue() > fvec.get(0))
      return null;
    if (newNameSpace) {
      trieOffset = varTable.getNumVars();
      varTable.addVars(numVars);      
    }
    else 
      trieOffset = 0;
    Clause ret = findSubsuming(child, fvec, clause);
    if (newNameSpace)
      varTable.removeVars(numVars);
    return ret;
  }
  
  /**
   * Returns a subsuming clause if the specified clause is subsumed by the clause in this trie (forward subsumption checking).
   * @param node   the node from which the checking starts.
   * @param fvec   the feature vector of the clause.
   * @param clause the clause to be checked.
   * @return the subsuming clause.
   */
  private Clause findSubsuming(FVecNode node, FVec fvec, Clause clause) {
    numSubsumpChecksWithoutFiltering += size();
    ArrayStack<FVecCand> candStack = env.getFVecCandStack();
    int cur = 0;
    while (true) {
      Inner:
      while (node.getValue() <= fvec.get(cur)) {
        // Finds the next candidate.
        FVecNode right = node.getRight();
        if (right != null && right.getValue() <= fvec.get(cur))
          candStack.push(new FVecCand(right, cur));
        if (!node.hasChildren()) {
          FVec otherFVec = node.getClauses().get(0).getFVec(false);
          while (cur < fvec.size()) {
            if (otherFVec.get(cur) > fvec.get(cur)) 
              break Inner;
            cur++;
          }          
          for (Clause c : node.getClauses()) {
            if (newNameSpace) 
              c.setOffset(trieOffset);
            numSubsumpChecks++;
            if (c.subsumes(clause)) {
              if (newNameSpace) 
                c.setOffset(0);
              return c;
            }
            if (newNameSpace) 
              c.setOffset(0);
          }
          break;
        }
        cur++;
        node = node.getFirstChild();
      }
      // Try the next candidates.
      if (candStack.isEmpty())
        return null;
      FVecCand cand = candStack.pop();
      node = cand.getNode();
      cur  = cand.getCur();
    }
  }
  
  /**
   * Returns the set of clauses subsumed by the specified clause (backward subsumption checking).
   * @param fvec   the feature vector of the clause.
   * @param clause the clause to be checked.
   * @return the set of clauses subsumed by the specified clause.
   */
  public List<Clause> findSubsumed(FVec fvec, Clause clause) {
    FVecNode child = root.getLastChild();
    List<Clause> out = new LinkedList<Clause>(); 
    if (child == null || child.getValue() < fvec.get(0))
      return out;
    if (newNameSpace) {
      trieOffset = varTable.getNumVars();
      varTable.addVars(numVars);      
    }
    else 
      trieOffset = 0;
    findSubsumed(child, fvec, clause, out);
    if (newNameSpace)
      varTable.removeVars(numVars);
    return out;
  }
  
  /**
   * Returns the set of clauses subsumed by the specified clause (backward subsumption checking).
   * @param node   the node from which the checking starts.
   * @param fvec   the feature vector of the clause.
   * @param clause the clause to be checked.
   * @param out    the set of subsumed clauses.
   */
  private void findSubsumed(FVecNode node, FVec fvec, Clause clause, List<Clause> out) {
    numSubsumpChecksWithoutFiltering += size();
    ArrayStack<FVecCand> candStack = env.getFVecCandStack();
    int cur = 0;
    while (true) {
      Inner:
      while (node.getValue() >= fvec.get(cur)) {
        // Finds the next candidate.
        FVecNode left = node.getLeft();
        if (left != null && left.getValue() >= fvec.get(cur))
          candStack.push(new FVecCand(left, cur));
        if (!node.hasChildren()) {
          
          FVec otherFVec = node.getClauses().get(0).getFVec(false);
          while (cur < fvec.size()) {
            if (otherFVec.get(cur) < fvec.get(cur)) 
              break Inner;
            cur++;
          }          
          
          for (Clause c : node.getClauses()) {
            if (newNameSpace) 
              c.setOffset(trieOffset);
            numSubsumpChecks++;
            if (clause.subsumes(c))
              out.add(c);
            if (newNameSpace) 
              c.setOffset(0);
          }
          break;
        }
        cur++;
        node = node.getLastChild();
      }
      // Try the next candidates.
      if (candStack.isEmpty())
        return;
      FVecCand cand = candStack.pop();
      node = cand.getNode();
      cur  = cand.getCur();
    }
  }  

  /**
   * Removes the set of clauses subsumed by the specified clause (backward subsumption checking).
   * @param fvec   the feature vector of the clause.
   * @param clause the clause to be checked.
   * @return the set of clauses subsumed by the specified clause.
   */
  public List<Clause> removeSubsumed(FVec fvec, Clause clause) {
    List<Clause> subsumed = findSubsumed(fvec, clause);
    remove(subsumed);
    return subsumed;
  }

  /**
   * Returns the number of registered clauses.
   * @return the number of registered clauses.
   */
  public int size() {
    return clauseNodeMap.size();
  }
  
  /**
   * Returns the number of subsumption-checking.
   * @return the number of subsumption-checking.
   */
  public long getNumSubsumChecks() {
    return numSubsumpChecks;
  }
  
  /**
   * Returns the number of subsumption-checking without filtering.
   * @return the number of subsumption-checking without filtering.
   */
  public long getNumSubsumChecksWithoutFiltering() {
    return numSubsumpChecksWithoutFiltering;
  }
  
  /**
   * Returns a string representation of this object.
   * @param root the root node.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder("root\n");
    if (root.hasClauses()) {
      str.append(root.getClauses());
      str.append("\n");
    }
    FVecNode node = root.getFirstChild();
    while (node != null) {

      // Make indents in proportion to the number of ancestors.
      StringBuilder indent = new StringBuilder();
      FVecNode n = node;
      while (!n.getParent().isRoot()) {
        n = n.getParent();
        if (n.getRight() != null)
          indent.insert(0, "| ");
        else
          indent.insert(0, "  ");
      }
      indent.insert(0, " ");
      
      // Print the node.
      str.append(indent);
      str.append("+ ");
      str.append(node.toString());
      str.append("\n");
      
      // If the node has leaves, then print it.
      if (node.hasClauses()) {
        if (node.getRight() != null)
          str.append(indent + "| ");
        else
          str.append(indent + "  ");
        str.append(node.getClauses());
        str.append("\n");
      }

      node = node.getNext();
    }
    return str.toString();
  }
  
  /** The environment. */
  private Env env = null;
  /** The variable table. */
  private VarTable varTable = null;  
  /** The root node of this trie. */
  private FVecNode root = null;
  /** The mapping from a clause to the node that contains the clause. */
  private HashMap<Clause,FVecNode> clauseNodeMap = null;
  /** The number of variables in this trie. */
  private int numVars = 0;
  /** Whether all variables in this tree belong to another name space or not. */
  private boolean newNameSpace = false;
  /** The variable offset. */
  private int trieOffset = 0;
  /** The number of subsumption-checking. */
  private long numSubsumpChecks = 0;
  /** The number of subsumption-checking without filtering. */
  private long numSubsumpChecksWithoutFiltering = 0;

}

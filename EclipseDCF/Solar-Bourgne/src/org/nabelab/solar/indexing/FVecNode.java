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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;


/**
 * @author nabesima
 *
 */
public class FVecNode {

  /**
   * Constructs a root node.
   * @param env  the environment.
   * @param trie the trie to which this node belongs.
   */
  public FVecNode(Env env, FVecTrie trie) {
    this.env  = env;
    this.trie = trie;
  }
  
  /**
   * Constructs a node which has the specified value.
   * @param value  the specified value.
   * @param parent the parent node.
   */  
  public FVecNode(int value, FVecNode parent) {
    this.env    = parent.env;
    this.trie   = parent.trie;
    this.parent = parent;
    this.value  = value;
  }

  /**
   * Returns the value of this node.
   * @return the value of this node.
   */
  public int getValue() {
    return value;
  }
  
  /**
   * Returns the parent node.
   * @return the parent node.
   */
  public FVecNode getParent() {
    return parent;
  }
  
  /**
   * Returns the right node.
   * @return the right node.
   */
  public FVecNode getRight() {
    return right;
  }

  /**
   * Returns the left node.
   * @return the left node.
   */
  public FVecNode getLeft() {
    return left;
  }

  /** 
   * Returns the first child.
   * @return the first child.
   */
  public FVecNode getFirstChild() {
    return firstChild;
  }
  
  /** 
   * Returns the last child.
   * @return the last child.
   */
  public FVecNode getLastChild() {
    return lastChild;
  }
  
  /**
   * Return the next node.
   * @param the next node.
   */
  public FVecNode getNext() {
    // If there is a child, then it is the next node.
    if (firstChild != null)
      return firstChild;
      
    // If there is a right brother, then returns it.
    if (right != null)
      return right;
      
    // Otherwise
    FVecNode p = parent;
    while (p.right == null) 
      if ((p = p.parent) == null)
        return null;
      
    return p.right;
  }

  /**
   * Returns true if this is the root node.
   * @return true if this is the root node.
   */
  public boolean isRoot() {
    return parent == null;
  }
  
  /**
   * Returns the child which has the specified value.
   * @param value the specified value.
   * @return the child which has the specified value.
   */
  public FVecNode findChild(int value) {
    if (childMap == null) return null;
    return childMap.get(value);
  }

  /** 
   * Adds a new child with the specified value.
   * @param value the specified value.
   * @return the new child.
   */
  public FVecNode addChild(int value) {
    FVecNode c = new FVecNode(value, this);
    if (firstChild == null) {
      firstChild = c;
      lastChild  = c;
    }
    else {
      if (c.compareTo(firstChild) < 0) {
        c.right = firstChild;
        firstChild.left = c;
        firstChild = c;
      }
      else {
        FVecNode prev = firstChild;
        while (true) {
          if (prev.right == null) {
            prev.right = c;
            c.left = prev;
            lastChild = c;
            break;
          }
          else if (c.compareTo(prev.right) < 0) {
            c.left = prev;
            c.right = prev.right;
            prev.right.left = c;
            prev.right = c;
            break;
          }
          prev = prev.right;
        }
      }
    }
    if (childMap == null) 
      childMap = new ChildMap();
    childMap.put(c);
    return c;
  }
  
  /**
   * Removes the specified child node.
   * @param node the child node to be removed.
   */
  public void removeChild(FVecNode node) {
    if (firstChild == node) {
      if (node.right != null) {
        node.right.left = null;
        firstChild = node.right;
      }
      else
        firstChild = lastChild = null;
    }
    else {
      FVecNode prev = firstChild;
      while (true) {
        if (prev.right == node) {
          prev.right = prev.right.right;
          if (prev.right != null) 
            prev.right.left = prev;
          else
            lastChild = prev;
          break;
        }
        prev = prev.right;
      }
    }
    childMap.remove(node);
  }
  
  /**
   * Returns true if this node has a child.
   * @return true if this node has a child.
   */
  public boolean hasChildren() {
    return firstChild != null;    
  }
  
  /**
   * Adds the specified clause to the this leaf node.
   * @param clause the clause to add.
   */
  public void addClause(Clause clause) {
    assert(firstChild == null);
    if (clauses == null)
      clauses = new LinkedList<Clause>();
    clauses.add(clause);
  }
  
  /**
   * Removes the specified clause from the this leaf node.
   * @param clause the clause to be removed.
   */  
  public void removeClause(Clause clause) {
    assert(clauses != null);
    clauses.remove(clause);
  }
  
  /**
   * Removes all clauses in the this leaf node.
   * @return all clauses to be removed.
   */  
  public List<Clause> removeAllClauses() {
    assert(clauses != null);
    List<Clause> ret = clauses;
    clauses = null;
    return ret;
  }

  /**
   * Returns true if this node has clauses.
   * @return true if this node has clauses.
   */
  public boolean hasClauses() {
    if (clauses == null)
      return false;
    return clauses.size() > 0;
  }
  
  /**
   * Returns the set of clauses from this leaf node.
   * @return the set of clauses from this leaf node.
   */
  public List<Clause> getClauses() {
    return clauses;
  }
  
  /**
   * Compares this object with the specified object for order.
   * @param obj the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *         less than, equal to, or greater than the specified object.
   */
  public int compareTo(FVecNode obj) {
    return value - obj.value;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (isRoot()) return "root";
    return "" + value;
  }
  
  /**
   * Returns the feature vector string representation.
   * @return the feature vector string representation.
   */
  public String getFVecStr() {
    assert(firstChild == null);
    StringBuilder str = new StringBuilder();
    FVecNode node = this;
    while (node != null) {
      str.insert(0, node.toString() + " ");
      node = node.parent;
    }
    return str.toString();
  }
  
  /** The environment. */
  private Env env = null;
  /** The feature vector trie to which this node belongs. */
  private FVecTrie trie = null;
  /** The value of this node. */
  private int value = 0;
  /** The parent node of this node. */
  private FVecNode parent = null;
  /** The left node. */
  private FVecNode left = null;
  /** The right node. */
  private FVecNode right = null;
  /** The most left child node. */
  private FVecNode firstChild = null;
  /** The most right child node. */
  private FVecNode lastChild = null;
  /** The mapping of children with its value. */
  private ChildMap childMap = null;
  /** The set of clauses associated with the leaf node. */
  private List<Clause> clauses = null;
  
  private final static class ChildMap {
    public FVecNode get(int value) {
      if (size == 0)
        return null;
      int index = hash(value);
      if (table[index] == null)
        return null;
      for (FVecNode node : table[index])
        if (value == node.value)
          return node;
      return null;
    }
    public void put(FVecNode node) {
      if (size + 1 > capacity / 2)
        rehash();
      insert(node);
      size++;
    }
    public void remove(FVecNode node) {
      int index = hash(node.value);
      if (table[index] == null)
        return;
      ListIterator<FVecNode> i = table[index].listIterator();
      while (i.hasNext()) {
        FVecNode n = i.next();
        if (node == n) {
          i.remove();
          size--;
          return;
        }
      }
    }
    
    private int hash(int value) {
      return value % capacity;
    }
    private void insert(FVecNode node) {
      int index = hash(node.value);
      if (table[index] == null)
        table[index] = new LinkedList<FVecNode>();
      table[index].add(node);
    }
    @SuppressWarnings("unchecked")
    private void rehash() {
      LinkedList<FVecNode>[] old = table;
      if (capacity == 0)
        capacity = 7;
      else 
        capacity <<= 1;
      table = new LinkedList[capacity];
      if (old != null)
        for (int i=0; i < old.length; i++)
          if (old[i] != null)
            for (FVecNode node : old[i])
              insert(node);
    }    
    
    private LinkedList<FVecNode>[] table = null;
    private int capacity = 0;
    private int size = 0;
  }  
}

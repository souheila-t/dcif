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

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Env;
import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;
import org.nabelab.util.LightArrayList;


/**
 * @author nabesima
 *
 */
public class DiscNode<E> implements TermTypes {

  /**
   * Constructs a root node.
   * @param env  the environment.
   * @param tree the tree to which this node belongs.
   */
  public DiscNode(Env env, DiscTree<E> tree) {
    this.env  = env;
    this.tree = tree;
  }
  
 /**
   * Constructs a node which has the specified name and type.
   * @param name   the specified name.
   * @param type   the specified type.
   * @param parent the parent node.
   */  
  public DiscNode(int name, int type, DiscNode<E> parent) {
    this.env  = parent.env;
    this.tree = parent.tree;
    this.name = name;
    this.type = type;
    this.arity  = env.getSymTable().getArity(name, type); 
    this.parent = parent;
    // Constructs a term with a single element.
    int[] nameArray = { name };
    int[] typeArray = { type };
    int[] nextArray = { 1    };
    this.term = new Term(env, nameArray, typeArray, nextArray); 
  }
  
  /**
   * Returns the name of this node.
   * @return the name of this node.
   */
  public int getName() {
    return name;
  }

  /**
   * Returns the type of this node.
   * @return the type of this node.
   */
  public int getType() {
    return type;
  }
  
  /**
   * Returns the arity of this node.
   * @return the arity of this node.
   */
  public int getArity() {
    return arity;
  }
  
  /**
   * Returns the term of this node.
   * @return the term of this node.
   */
  public Term getTerm() {
    return term;
  }
  
  /**
   * Returns the parent node.
   * @return the parent node.
   */
  public DiscNode<E> getParent() {
    return parent;
  }

  /**
   * Returns the left node.
   * @return the left node.
   */
  public DiscNode<E> getLeft() {
    return left;
  }

  /**
   * Returns the right node.
   * @return the right node.
   */
  public DiscNode<E> getRight() {
    return right;
  }

  /** 
   * Returns the first child.
   * @return the first child.
   */
  public DiscNode<E> getFirstChild() {
    return child;
  }
  
  /**
   * Return the next node.
   * @param root the root node.
   * @return the next node.
   */
  public DiscNode<E> getNext(DiscNode<E> root) {
    // If there is a child, then it is the next node.
    if (child != null)
      return child;
      
    // If there is a right brother, then returns it.
    if (right != null)
      return right;
      
    // Otherwise
    DiscNode<E> p = parent;
    while (p != root) {
      if (p.right != null) 
        return p.right;
      p = p.parent;
    }
    
    return null;
  }

  /**
   * Returns true if this is the root node.
   * @return true if this is the root node.
   */
  public boolean isRoot() {
    return parent == null;
  }
  
  /**
   * Returns true if this node has a child.
   * @return true if this node has a child.
   */
  public boolean hasChildren() {
    return child != null;
  }
  
  /**
   * Returns true if this node has a variable child.
   * @return true if this node has a variable child.
   */
  public boolean hasVarChildren() {
    return child.type == VARIABLE;
  }

  /**
   * Returns the child which has the specified name and type. 
   * @param name the specified name.
   * @param type the specified type.
   * @return the child which has the specified name and type.
   */
  public DiscNode<E> findChild(int name, int type) {
    if (childMap == null) return null;
    return childMap.get(name, type);
  }

  /** 
   * Adds a new child with the specified name and type.
   * @return the new child.
   */
  public DiscNode<E> addChild(int name, int type) {
    DiscNode<E> c = new DiscNode<E>(name, type, this);
    if (child == null)
      child = c;
    else {
      if (c.compareTo(child) < 0) {
        c.right = child;
        child.left = c;
        child = c;
      }
      else {
        DiscNode<E> prev = child;
        while (true) {
          if (prev.right == null) {
            prev.right = c;
            c.left = prev;
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
      childMap = new ChildMap<E>();
    childMap.put(c);
    return c;
  }
  
  /**
   * Removes the specified child node.
   * @param node the child node to be removed.
   */
  public void removeChild(DiscNode<E> node) {
    if (child == node) {
      if (node.right != null) {
        node.right.left = null;
        child = node.right;
      }
      else
        child = null;
    }
    else {
      DiscNode<E> prev = child;
      while (true) {
        if (prev.right == node) {
          prev.right = prev.right.right;
          if (prev.right != null) 
            prev.right.left = prev;
          break;
        }
        prev = prev.right;
      }
    }
    childMap.remove(node);
    if (node.revJumpLinks != null)
      for (JumpLink<E> link : node.revJumpLinks) 
        link.getSrc().jumpLinks.remove(link);
  }

  /**
   * Adds a jump link from this node to the specified destination with the specified label.
   * @param dest  the destination of the jump link.
   * @param label the label term of the jump link.
   */
  public void addJumpLink(DiscNode<E> dest, Term label) {
    assert(leaves == null);
    if (jumpLinks == null)
      jumpLinks = new ArrayList<JumpLink<E>>();
    JumpLink<E> link = new JumpLink<E>(this, label, dest);
    jumpLinks.add(link);
    if (dest.revJumpLinks == null)
      dest.revJumpLinks = new ArrayList<JumpLink<E>>();
    dest.revJumpLinks.add(link);
  }
  
  /**
   * Returns the jump link at the specified index.
   * @param index the index.
   * @return the jump link at the specified index.
   */
  public JumpLink<E> getJumpLink(int index) {
    return jumpLinks.get(index);
  }
  
  /**
   * Returns the set of jump links from this node.
   * @return the set of jump links from this node.
   */
  public List<JumpLink<E>> getJumpLinks() {
    return jumpLinks;
  }

  /**
   * Returns the number of jump links.
   * @return the number of jump links.
   */
  public int getNumJumpLinks() {
    if (jumpLinks == null)
      return 0;
    return jumpLinks.size();    
  }
  
  /**
   * Returns true if this node has jump links.
   * @return true if this node has jump links.
   */
  public boolean hasJumpLinks() {
    if (jumpLinks == null)
      return false;
    //assert(jumpLinks.size() > 0);
    return true;
  }
  
  /**
   * Adds the specified leaf object to this node.
   * @param object the leaf object.
   */
  public void addLeaf(E object) {
    assert(child == null);
    if (leaves == null)
      leaves = new LightArrayList<E>();
    leaves.add(object);
  }
  
  /**
   * Removes the specified leaf object from this node.
   * @param object the leaf object.
   * @return true if the leaf object is removed.
   */
  public boolean removeLeaf(E object) {
    assert(leaves != null);
    return leaves.remove(object);
  }
  
  /**
   * Returns the set of leaves.
   * @return the set of leaves.
   */
  public LightArrayList<E> getLeaves() {
    assert(leaves != null);
    return leaves;
  }
  
  /**
   * Returns true if this node has leaves.
   * @return true if this node has leaves.
   */
  public boolean hasLeaves() {
    if (leaves == null)
      return false;
    return leaves.size() > 0;
  }
  
  /**
   * Compares this object with the specified object for order.
   * @param obj the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *         less than, equal to, or greater than the specified object.
   */
  public int compareTo(DiscNode<E> obj) {
    if (type == obj.type)
      return name - obj.name;
    else
      return type - obj.type;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (isRoot()) return "root";
    String str = env.getSymTable().get(name, type);
    if (arity != 0)
      str += "/" + arity;
    return str;
  }
  
  /**
   * Returns a tree representation from this object.
   * @return a tree representation from this object.
   */
  public String toTreeString() {
    return this.toString() + "\n" + tree.toString(this);
  }
  
  /** The environment. */
  private Env env = null;
  /** The tree to which this node belongs. */ 
  private DiscTree<E> tree = null;
  /** The name of this node. */
  private int name = 0;
  /** The type of this node. */
  private int type = 0;
  /** The arity of this node. */
  private int arity = 0;
  /** The term of this node. */
  private Term term = null;
  /** The parent node of this node. */
  private DiscNode<E> parent = null;
  /** The left node. */
  private DiscNode<E> left = null;
  /** The right node. */
  private DiscNode<E> right = null;
  /** The most left child node. */
  private DiscNode<E> child = null;
  /** The mapping of children with its name and type. */
  private ChildMap<E> childMap = null;
  /** The set of jump links from this node. */
  private ArrayList<JumpLink<E>> jumpLinks = null;
  /** The set of jump links to this node. */
  private ArrayList<JumpLink<E>> revJumpLinks = null;
  /** The set of leaf objects. */
  private LightArrayList<E> leaves = null;
  
  private final static class ChildMap<E> {
    public DiscNode<E> get(int name, int type) {
      if (size == 0)
        return null;
      int index = hash(name, type);
      LightArrayList<DiscNode<E>> nodes = table[index];
      if (nodes == null)
        return null;
      for (int i=0; i < nodes.size(); i++) {
        DiscNode<E> node = nodes.get(i);
        if (name == node.getName() && type == node.getType()) 
          return node;
      }
      return null;
    }
    public void put(DiscNode<E> node) {
      if (size + 1 > capacity / 2)
        rehash();
      insert(node);
      size++;
    }
    public void remove(DiscNode<E> node) {
      int index = hash(node.getName(), node.getType());
      LightArrayList<DiscNode<E>> nodes = table[index];
      if (nodes == null)
        return;
      for (int i=0; i < nodes.size(); i++) {
        DiscNode<E> n = nodes.get(i);
        if (node == n) {
          nodes.remove(i);
          size--;
          return;
        }
      }
    }
    
    private int hash(int name, int type) {
      return Math.abs((31 * (31 + name) + type)) % capacity;
    }
    private void insert(DiscNode<E> node) {
      int index = hash(node.getName(), node.getType());
      if (table[index] == null)
        table[index] = new LightArrayList<DiscNode<E>>();
      table[index].add(node);
    }
    @SuppressWarnings("unchecked")
    private void rehash() {
      LightArrayList<DiscNode<E>>[] old = table;
      if (capacity == 0)
        capacity = 7;
      else 
        capacity <<= 1;
      table = new LightArrayList[capacity];
      if (old != null)
        for (int i=0; i < old.length; i++) 
          if (old[i] != null) 
            for (int j=0; j < old[i].size(); j++) 
              insert(old[i].get(j));
    }    
    
    private LightArrayList<DiscNode<E>>[] table = null;
    private int capacity = 0;
    private int size = 0;
  }
}

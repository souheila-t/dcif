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

import java.util.Arrays;

/**
 * @author nabesima
 *
 */
public class Subst {
  
  /**
   * Constructs a empty substitution.
   */
  public Subst() {
    vars = new int[INITIAL_SIZE];
    vals = new Term[INITIAL_SIZE];
  }

  /**
   * Constructs a copy of the specified substitution.
   */
  public Subst(Subst g) {
    vars = new int[g.vars.length];
    vals = new Term[g.vals.length];
    size = g.size;
    System.arraycopy(g.vars, 0, vars, 0, size);
    System.arraycopy(g.vals, 0, vals, 0, size);
  }

  /**
   * Adds the specified substitution.
   * @param var the variable name.
   * @param val the value of the variable.
   * @deprecated
   */
  public void add(int var, Term val) {
    ensure(size + 1);
    vars[size] = var;
    vals[size] = val;
    size++;
    // numSyms += val.size(false); // estimation // TODO Need this code? 
  }
  
  /**
   * Adds the specified substitution.
   * @param var  the variable name.
   * @param val  the value of the variable.
   * @param syms the number of symbols in the value.
   */
  public void add(int var, Term val, int syms) {
    ensure(size + 1);
    vars[size] = var;
    vals[size] = val;
    size++;
    numSyms += syms;
  }
   
  /**
   * Adds the specified substitution.
   * @param g the substitution.
   */
  public void add(Subst g) {
    ensure(size + g.size);
    for (int i=0,j=size; i < g.size; i++,j++) {
      vars[j] = g.vars[i];
      vals[j] = g.vals[i];
      size++;
    }
    numSyms += g.numSyms;
  }
  
  /**
   * Returns the variable at the specified position.
   * @param index the specified position.
   * @return the variable at the specified position.
   */
  public int getVar(int index) {
    return vars[index];
  }
  
  /**
   * Returns the value at the specified position.
   * @param index the specified position.
   * @return the value at the specified position.
   */
  public Term getVal(int index) {
    return vals[index];
  }
  
  /**
   * Instantiates values in this substitution. 
   */
  public void instantiate() {
    for (int i=0; i < size; i++)
      vals[i] = vals[i].instantiate();
  }
  
  /**
   * Renames the variables using the specified rename-mapping. No variable in this success must have a value.
   * @param renameMap  the rename-mapping.
   * @param minVarName the smallest variable name to be renamed.
   * @param minVarName the largest variable name to be renamed.
   */
  public void subrename(VarRenameMap renameMap, int minVarName, int maxVarName) {
    // Updates the specified variables in values.
    for (int i=0; i < size; i++) {
      assert(!(minVarName <= vars[i] && vars[i] <= maxVarName));
      vals[i].subrename(renameMap, minVarName, maxVarName);
    }
  }
  
  /**
   * Returns true if this substitution is empty.
   * @return true if this substitution is empty.
   */
  public boolean isEmpty() {
    return size == 0;
  }
  
  /**
   * Returns the number of elements in this substitution.
   * @return the number of elements in this substitution.
   */
  public int size() {
    return size;
  }
  
  /**
   * Returns the number of symbols in this substitution.
   * @return the number of symbols in this substitution.
   */
  public int getNumSyms() {
    return numSyms;
  }
  
  /**
   * Ensures the capacity of arrays.
   * @param cap the capacity.
   */
  private void ensure(int cap) {
    if (vars.length < cap) {
      // MEMO for J2SE1.6
      //vars = Arrays.copyOf(vars, cap << 1);
      //vals = Arrays.copyOf(vals, cap << 1);
      int[]  oldvars = vars;
      Term[] oldvals = vals;
      vars = new int[cap << 1];
      vals = new Term[cap << 1];
      System.arraycopy(oldvars, 0, vars, 0, oldvars.length);
      System.arraycopy(oldvals, 0, vals, 0, oldvals.length);
    }
  }

  /**
   * Clears this substitution.
   */
  public void clear() {
    size = 0;
    numSyms = 0;
  }
  
  /**
   * Returns the hash code value of this object.
   * @return the hash code value of this object.
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + size;
    result = prime * result + Arrays.hashCode(vals);
    result = prime * result + Arrays.hashCode(vars);
    return result;
  }

  /**
   * Compares the specified object with this object for equality.
   * @param obj the reference object with which to compare.  
   */
  public boolean equals(Subst obj) {
    if (size != obj.size)
      return false;
    if (!Arrays.equals(vals, obj.vals))
      return false;
    if (!Arrays.equals(vars, obj.vars))
      return false;
    return true;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (size == 0) 
      return "[]";
    StringBuilder str = new StringBuilder("[");
    for (int i=0; i < size-1; i++) 
      str.append("_" + vars[i] + "/" + vals[i] + ", ");
    str.append("_" + vars[size-1] + "/" + vals[size-1]);
    str.append("]");
    return str.toString();
  }
    
  /** The initial size of this substitution. */
  private final static int INITIAL_SIZE = 4;

  /** The list of variables */
  protected int[] vars = null;
  /** The list of values. */
  protected Term[] vals = null;
  /** The number of elements. */
  protected int size = 0;
  /** The number of symbols in this substitution. */
  protected int numSyms = 0;
}

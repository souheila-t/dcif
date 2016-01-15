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

package org.nabelab.solar.equality;

import org.nabelab.solar.Env;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.util.IntStack;

/**
 * @author nabesima
 *
 */
public class TermWeight implements TermTypes {
  
  /**
   * Constructs a term weight.
   * @param wmap the weight assignment of symbols.
   */
  public TermWeight(WeightMap wmap, PriorityMap pmap) {
    this.wmap = wmap;
    this.pmap = pmap;
  }

  /**
   * Counts the weight of the specified symbol.
   * @param name the name of the symbol.
   * @param type the type of the symbol.
   */
  public void count(int name, int type) {
    if (type == VARIABLE) {
      ensureVarFreqSize(name);
      varFreq[name]++;
      if (name < minVar)
        minVar = name;
      if (maxVar < name)
        maxVar = name;
      priList.push(-name);
    }
    else {
      assert(type != PREDICATE);
      weight += wmap.get(name, type);
      priList.push(pmap.get(name, type));
    }
  }

  /**
   * Ensures the capacity of the variable frequency array.
   * @param name the variable name.
   */
  private void ensureVarFreqSize(int name) {
    if (varFreq.length <= name) {
      int[] newVarFreq = new int[name << 1];
      System.arraycopy(varFreq, 0, newVarFreq, 0, varFreq.length);
      varFreq = newVarFreq;
    }
  }
  
  /**
   * Returns true if this term weight is greater than the other term weight.
   * @param b the other term weight to be compared.
   * @return true if this term weight is greater than the other term weight.
   */
  public int isGreaterThan(TermWeight b) {
    TermWeight a = this;
    
    int min = Math.min(a.minVar, b.minVar);
    int max = Math.max(a.maxVar, b.maxVar);
    a.ensureVarFreqSize(max);
    b.ensureVarFreqSize(max);

    boolean geq = true;
    int alldiff = 0;

    //
    // (1) Checks |a| > |b|
    //
    if (a.weight >= b.weight) {
    
      // Compares the weight of non-variable terms.
      boolean hasGreaterFreq = (a.weight > b.weight);

      // Compares frequencies of variables.      
      for (int i=min; i <= max; i++) {
        int diff = a.varFreq[i] - b.varFreq[i];
        if (diff < 0)
          geq = false;
        if (diff > 0)
          hasGreaterFreq = true;
        alldiff += diff;
      }

      if (geq && hasGreaterFreq)
        return TRUE;
    }
    
    //
    // (2) Checks |a| >= |b| and a |> b 
    //
    
    // (2a) Firstly, checks a |> b (compares the priority of each flat term)
    int end = Math.min(a.priList.size(), b.priList.size());
    int i   = 0;
    while (i < end) {
      int pri1 = a.priList.get(i);
      int pri2 = b.priList.get(i);
      // If one is a variable, then it is undecidable. 
      if (pri1 < 0 || pri2 < 0) {
        if (pri1 != pri2)
          return UNDECIDABLE;
      }
      else if (pri1 < pri2)
        return UNDECIDABLE;
      else if (pri1 > pri2)
        break;        
      i++;
    }
    if (i == end) {
      assert(priList.size() == b.priList.size());
      // 'a' is equal to 'b'.
      return SAME;
    }
        
    // (2b) Secondly, checks |a| >= |b|
    if (geq) {
      if (alldiff * wmap.getMinWeight() >= -(a.weight - b.weight))
        return TRUE;
      return UNDECIDABLE;
    }

    return UNDECIDABLE;
  }
  
  /**
   * Returns TRUE if this term weight is greater than or equal to the other term weight.
   * @param b the other term weight to be compared.
   * @return TRUE if this term weight is greater than or equal to the other term weight.
   */
//  public int isGreaterThanOrEqualTo(TermWeight b) {
//    TermWeight a = this;
//    
//    // (1) Compares the priority of each flat term.
//    int end = Math.min(a.priList.size(), b.priList.size());
//    int i   = 0;
//    while (i < end) {
//      int pri1 = a.priList.get(i);
//      int pri2 = b.priList.get(i);
//      // If one is a variable, then it is undecidable. 
//      if (pri1 < 0 || pri2 < 0) {
//        if (pri1 != pri2)
//          return UNDECIDABLE;
//      }
//      else if (pri1 < pri2)
//        return FALSE;
//      else if (pri1 > pri2)
//        break;        
//      i++;
//    }
//    if (i == end) {
//      assert(priList.size() == b.priList.size());
//      // 'a' is equal to 'b'.
//      return SAME;
//    }
//    
//    // (2) Checks this term weight is greater than or equal to the other. 
//    // The following is a sufficient condition (not a necessary condition)
//    int min = Math.min(a.minVar, b.minVar);
//    int max = Math.max(a.maxVar, b.maxVar);
//    a.ensureVarFreqSize(max);
//    b.ensureVarFreqSize(max);
//    int total = 0;
//    for (i=min; i <= max; i++) {
//      if (a.varFreq[i] < b.varFreq[i])
//        return UNDECIDABLE;
//      total += a.varFreq[i] - b.varFreq[i];
//    }
//    if (total * wmap.getMinWeight() >= -(a.weight - b.weight))
//      return TRUE;
//    return UNDECIDABLE; 
//  }

  /**
   * Clears the term weight.
   */
  public void clear() {
    weight = 0;
    if (minVar != Integer.MAX_VALUE) {
      for (int i=minVar; i <= maxVar; i++) 
        varFreq[i] = 0;
    }
    minVar = Integer.MAX_VALUE;
    maxVar = -1;
    priList.clear();
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append(weight);
    for (int i=minVar; i <= maxVar; i++)
      if (varFreq[i] != 0)
        str.append(" + " + varFreq[i] + "*_" + i);
    
    return str.toString();
  }

  /** A constant that denotes true. */
  public final static int  TRUE = +1;
  /** A constant that denotes undecidable. */
  public final static int  UNDECIDABLE  = 0;
  /** A constant that denotes two terms are same. */
  public final static int  SAME = +2;
  
  /** The initial size of the variable-frequency array. */
  private final static int INITIAL_SIZE = 512;
  
  /** The environment. */
  @SuppressWarnings("unused")
  private Env env = null;
  /** The weight assignment of symbols. */
  private WeightMap wmap = null;
  /** The priority assignment of symbols. */
  private PriorityMap pmap = null;

  /** The weight of functions or constants. */
  private int weight = 0;
  /** The frequency of variables. */
  private int[] varFreq = new int[INITIAL_SIZE];
  /** The minimum variable name. */
  private int minVar = Integer.MAX_VALUE;
  /** The maximum variable name. */
  private int maxVar = -1;
  
  /** The priority list of a flat term. */
  private IntStack priList = new IntStack();
  
}

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
package org.nabelab.mhs;

import org.nabelab.util.IntArrayList;
import org.nabelab.util.LightArrayList;

public class HitSetCand {

  /**
   * Constructs a truth value table.
   * @param numVars  the number of variables.
   */
  public HitSetCand(int numVars) {
   values  = new int[numVars + 1];
   levels  = new int[numVars + 1];
   reason  = new Clause[numVars + 1];
   implied = new boolean[numVars + 1];
   assigned = new IntArrayList(numVars);
   impLits = new IntArrayList(numVars);
   trueLits = new IntArrayList();
   criticals = new LightArrayList<CriticalSets>(numVars + 1);
   for (int i=0; i <= numVars; i++)
     criticals.add(new CriticalSets());
  }
  
  /**
   * Makes the specified literal as true.
   * @param lit  the specified literal.
   */
  public void addTrueLit(int lit) {
    if (lit > 0) {
      int var = lit;
      assert(values[var] != FALSE);
      values[var] = TRUE;
      levels[var] = -1;
      trueLits.add(lit);
    }
    else {
      int var = -lit;
      assert(values[var] != TRUE);
      values[var] = FALSE;
      levels[var] = -1;
      trueLits.add(lit);
    }
  }
  
  /**
   * Makes the specified literal as true.
   * @param lit       the specified literal.
   * @param from      the clause that implies the specified literal.
   * @param claIndex  an index of a new hit clause.
   */
  public void addImpliedLit(int lit, Clause from, int claIndex) {
    if (lit > 0) {
      int var = lit;
      assert(values[var] == UNDEF);
      values[var] = TRUE;
      levels[var] = claIndex;
      reason[var] = from;
      implied[var] = true;
      impLits.add(lit);
    }
    else {
      int var = -lit;
      assert(values[var] == UNDEF);
      values[var] = FALSE;
      levels[var] = claIndex;
      reason[var] = from;
      implied[var] = true;
      impLits.add(lit);
    }
  }
//  /**
//   * Makes the specified literal as true.
//   * @param lit       the specified literal.
//   * @param claIndex  the assigned level of the literal.
//   */
//  public void add(int lit, int claIndex) {
//    if (lit > 0) {
//      assert(values[+lit] == UNDEF);
//      values[+lit] = TRUE;
//      levels[+lit] = claIndex;
//      trueLits.add(lit);
//    }
//    else {
//      assert(values[-lit] == UNDEF);
//      values[-lit] = FALSE;
//      levels[-lit] = claIndex;
//      trueLits.add(lit);
//    }
//  }
  
  /**
   * Makes the specified literal as true.
   * @param lit       the specified literal.
   * @param clause    the clause that contains the literal.
   * @param claIndex  the index of the clause.
   */
  public void add(int lit, Clause clause, int claIndex) {
    if (lit > 0) {
      int var = lit;
      assert(values[var] == UNDEF || (values[var] == TRUE && implied[var]));
      values[var] = TRUE;
      levels[var] = claIndex;
      implied[var] = false;
      assigned.add(lit);
      criticals.get(var).add(clause, claIndex);        
    }
    else {
      int var = -lit;
      assert(values[var] == UNDEF || (values[var] == FALSE && implied[var]));
      values[var] = FALSE;
      levels[var] = claIndex;
      implied[var] = false;
      assigned.add(lit);
      criticals.get(var).add(clause, claIndex);        
    }
  }

//  /**
//   * Removes the last assignment.
//   */
//  public void removeLast() {
//    int lit = assigned.removeLast();
//    int var = Math.abs(lit);
//    
//    criticals.get(var).init();
//    values[var] = UNDEF;
//  }

  /**
   * Returns true if this hits the specified clause.
   * @param clause   the specified clause.
   * @param claIndex the index of the clause.
   * @return true if this hits the specified clause.
   */
  public boolean isHitting(Clause clause, int claIndex) {
    
    // Checks this hits the specified clause. 
    int lit = 0;
    boolean single = true;    
    for (int i=0; i < clause.size(); i++) {
      int l = clause.getAt(i);
      if (contains(l)) {
        if (!single) 
          return true;   // no-need to update the critical sets.
        lit = l;
        single = false;
      }
      
    }
    
    // If this hits the clause by only one element, then updates the critical sets.
    if (lit != 0) {
      criticals.get(Math.abs(lit)).add(clause, claIndex);
      return true;
    }
    
    return false;
  }
  
  /**
   * Hits the specified clause and returns the index of the literal to be added.
   * @param clause   the specified clause.
   * @param claIndex the index of the clause.
   * @param litIndex the index of the literal to be checked.
   * @return  the index of the literal to be added.
   */  
  public int hit(Clause clause, int claIndex, int litIndex) {
  
    // TODO
//    for (int i=0; i < clause.size(); i++) {
//      int lit = clause.getAt(i);
//      if (getValue(lit) == TRUE) {
//        assert(isImplied(lit));
//        return SKIP;
//      }
//    }
    
    
//    boolean hasImplied = false;
//    for (int i=0; i < clause.size(); i++) {
//      int lit = clause.getAt(i);
//      if (implied[Math.abs(lit)] && getValue(lit) == HitSetCand.TRUE) {
//        hasImplied = true;
//        break;
//      }
//    }
    
    NEXT_ELEMENT: 
    while (litIndex < clause.size()) {
      int newHLit = clause.getAt(litIndex); // newHLit will be added to this hitting set.

      // Tautology checking.
      if (getValue(newHLit) == HitSetCand.FALSE) {
        litIndex++;
        continue;
      }
      // TODO
//      if (hasImplied && !implied[Math.abs(newHLit)]) {
//        litIndex++;
//        continue;
//      }
      
        
      for (int i=0; i < assigned.size(); i++) {
        int curHLit = assigned.getAt(i);
        CriticalSets csets = criticals.get(Math.abs(curHLit));
        
        if (!csets.update(newHLit, claIndex)) {
          
          // Restore to the original state.
          for (int j=0; j < i; j++) 
            criticals.get(Math.abs(assigned.getAt(j))).backtrackTo(claIndex);
          
          litIndex++;
          continue NEXT_ELEMENT;
        }
      }
      
//      CriticalSets newCSets = criticals.get(Math.abs(newHLit));
//      newCSets.add(clause, claIndex);
//      add(newHLit, clause, claIndex);
      
      return litIndex;
    }
        
    return NON_MINIMAL;
  }

  /**
   * Backtracks to the specified clause index.
   * @param claIndex  the specified clause index.
   */
  public void backtrackTo(int claIndex) {
    
    while (!assigned.isEmpty() && claIndex <= getLevel(assigned.getLast())) {
      //removeLast();
      int lit = assigned.removeLast();
      int var = Math.abs(lit);
      criticals.get(var).init();
      values[var] = UNDEF;
    }
    while (!impLits.isEmpty() && claIndex <= getLevel(impLits.getLast())) {
      int lit = impLits.removeLast();
      int var = Math.abs(lit);
      values[var] = UNDEF;
      reason[var] = null;
      implied[var] = false;
    }
      
    for (int i=0; i < assigned.size(); i++) {
      int lit = assigned.getAt(i);
      criticals.get(Math.abs(lit)).backtrackTo(claIndex);
    }
  }  

  /**
   * Returns a hitting set.
   * @return a hitting set.
   */
  public HitSet getHitSet() {
    HitSet hs = new HitSet(assigned);
    hs.addAll(trueLits);
    return hs;
  }

  /**
   * Returns the value of the specified literal.
   * @param lit  the specified literal. 
   * @return the value of the specified literal.
   */
  public int getValue(int lit) {
    if (lit > 0) {
      assert(+lit < values.length);
      return +values[+lit];
    }
    else {
      assert(-lit < values.length);
      return -values[-lit];
    }
  }
  
  /**
   * Returns the assigned level of the specified literal.
   * @param lit  the specified literal. 
   * @return the assigned level of the specified literal.
   */
  public int getLevel(int lit) {
    if (lit > 0) {
      assert(+lit < levels.length);
      return levels[+lit];
    }
    else {
      assert(-lit < levels.length);
      return levels[-lit];
    }
  }
  
  /**
   * Returns the assigned level of the specified literal.
   * @param lit  the specified literal. 
   * @return the assigned level of the specified literal.
   */
  public boolean isImplied(int lit) {
    if (lit > 0) {
      assert(+lit < levels.length);
      return implied[+lit];
    }
    else {
      assert(-lit < levels.length);
      return implied[-lit];
    }
  }
  
//  /**
//   * Returns the assigned level of the last element.
//   * @return the assigned level of the last element.
//   */
//  public int getLastAssignedLitLevel() {
//    return getLevel(assigned.getLast());
//  }
  
  /**
   * Returns the number of variables.
   * @return the number of variables.
   */
  public int getNumVars() {
    return values.length;
  }
  
  /**
   * Returns the true if the specified literal is true.
   * @param lit  the specified literal.
   * @return the true if the specified literal is true.
   */
  private boolean contains(int lit) {
    if (lit > 0) 
      return values[+lit] == TRUE  && !implied[+lit];
    else 
      return values[-lit] == FALSE && !implied[-lit];
  }
  
  /**
   * Returns the assigned literal at the specified index.
   * @param index  the specified index.
   * @return the assigned literal at the specified index.
   */
  public int getAt(int index) {
    return assigned.getAt(index);
  }
  
  /**
   * Returns the number of assigned literals.
   * @return the number of assigned literals.
   */
  public int size() {
    return assigned.size() + trueLits.size(); 
  }

//  /**
//   * Returns true if this set is empty.
//   * @return true if this set is empty.
//   */
//  public boolean isEmpty() {
//    return size() == 0;
//  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return assigned.toString();
  }

  public final static int TRUE     = 1;
  public final static int UNDEF    = 0;
  public final static int FALSE    = -1;
  
  public final static int NON_MINIMAL = -1;
  public final static int SKIP = -2;

  /** The truth values of variables. */
  private int values[] = null;
  /** The assigned levels of variables. */
  private int levels[] = null;
  /** The reasons of the assignment. */
  private Clause reason[] = null;
  /** True if a variable is implied. */
  private boolean implied[] = null;
  
  /** The list of assigned literals. */
  private IntArrayList assigned = null;
  /** The list of implied literals by unit propagations. */
  private IntArrayList impLits = null;
  /** The list of true literals. */
  private IntArrayList trueLits = null;
  /** The list of critical sets. */
  private LightArrayList<CriticalSets> criticals = null;
}

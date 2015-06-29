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

public class LitWatcher {
  
  /**
   * Constructs a literal watcher.
   * @param numVars  the number of variables.
   */
  @SuppressWarnings("unchecked")
  public LitWatcher(HitSetCand hitSetCand) {
    this.hitSetCand = hitSetCand;

    int numVars = hitSetCand.getNumVars();
    pos = new LightArrayList[numVars];
    neg = new LightArrayList[numVars];
    for (int i=0; i < numVars; i++) {
      pos[i] = new LightArrayList<Clause>();
      neg[i] = new LightArrayList<Clause>();
    }      
  }
  
  /**
   * Attaches the specified clause to the watcher.
   * @param clause  the specified clause.
   */
  public void attach(Clause clause) {
    int lit1 = clause.get1stWLit();
    int lit2 = clause.get2ndWLit();
    if (lit1 > 0) neg[+lit1].add(clause); else pos[-lit1].add(clause);
    if (lit2 > 0) neg[+lit2].add(clause); else pos[-lit2].add(clause);
  }
  
  /**
   * Updates the watched lists.
   * @param lit       a new assigned literal.
   * @param claIndex  the index of a new hit clause.
   * @return true if there is no conflict.
   */
  public boolean update(int lit, int claIndex) {
    assert(unitLits.isEmpty());
    
    unitLits.add(lit);
    
    while (!unitLits.isEmpty()) {
      int plit = unitLits.removeLast();
      int nlit = -plit;
    
      LightArrayList<Clause> list = null;
      if (plit > 0)
        list = pos[plit];
      else
        list = neg[nlit];
      
      NEXT_CLAUSE:
      for (int i=list.size()-1; i >= 0; i--) {
        Clause c = list.get(i);
        //c.update(-lit, hitSetCand, this);
        
        int idx1st = c.get1stWLitIdx();
        int idx2nd = c.get2ndWLitIdx();
        
        // Make sure the false literal is the second:
        if (c.getAt(idx1st) == nlit) {
          int tmp = idx1st;
          idx1st= idx2nd;
          idx2nd= tmp;
        }
    
        // If 1st watch is true, then clause is already satisfied.
        int firstLit = c.getAt(idx1st);
        int firstVal = hitSetCand.getValue(firstLit); 
        if (firstVal == HitSetCand.TRUE)
          continue NEXT_CLAUSE;
    
        // Look for new watch for the second:
        for (int j=0; j < c.size(); j++) {
          if (j == idx1st || j == idx2nd)
            continue;
          lit = c.getAt(j);
          if (hitSetCand.getValue(lit) != HitSetCand.FALSE) {
            
            // Changes watched literals.
            list.remove(i);
            if (lit > 0)
              neg[+lit].add(c);
            else
              pos[-lit].add(c);
            c.setWLitIdx(idx1st, j);
            
            continue NEXT_CLAUSE;
          }
        }
        //c.setWLitIdx(idx1st, idx2nd);
        
        // Did not find watch -- clause is unit under assignment:
        if (firstVal == HitSetCand.FALSE) {
          // TODO: conflict!
          ///System.out.println("conflict!");
          unitLits.clear();
          return false;
        }
        else {
          // TODO: unit!
          ///System.out.println("unit! " + firstLit);
          hitSetCand.addImpliedLit(firstLit, c, claIndex);
          unitLits.add(firstLit);
        }
      }
    }
    
    return true;
  }
  
//  /**
//   * Returns the literals by unit propagations. 
//   * @return the literals by unit propagations. 
//   */
//  public IntArrayList getUnitLits() {
//    return unitLits;
//  }
  
  /** A hitting set candidate. */
  private HitSetCand hitSetCand = null;
  /** The list of unit literals. */
  private IntArrayList unitLits = new IntArrayList();
  
  /** Watched clauses lists. */
  private LightArrayList<Clause> pos[] = null;
  private LightArrayList<Clause> neg[] = null;
  
}

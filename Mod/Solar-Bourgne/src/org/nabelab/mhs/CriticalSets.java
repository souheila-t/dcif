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

import org.nabelab.util.LightArrayList;

public class CriticalSets {

  /**
   * Initializes this critical sets.
   */
  public void init() {
    added.clear();
    removed.clear();
    numAddedSets = 0;
  }

  /**
   * Adds the specified clause to this set.
   * @param clause  the clause to add.
   * @param level   the index when the clause is added.
   */
  public void add(Clause clause, int level) {
    if (added.isEmpty() || added.getLast().getLevel() != level) {
      LvList<Clause> lvlist = lvListClausePool.get();
      lvlist.init(level);
      lvlist.add(clause);
      added.add(lvlist);
    } else {
      added.getLast().add(clause);
    }
    numAddedSets++;
  }

  /**
   * Removes the specified set.
   * @param idx1st  the first index of the set.
   * @param idx2nd  the second index of the set.
   * @param level   the level at the removing.
   */
  public void remove(int idx1st, int idx2nd, int level) {
    LvList<Clause> src = added.get(idx1st);
    Clause set = src.remove(idx2nd);
    numAddedSets--;
    if (removed.isEmpty() || removed.getLast().getLevel() != level) {
      LvList<RemovedCSet> lvlist = lvListRemovedCSetPool.get();
      lvlist.init(level);
      RemovedCSet lvset = new RemovedCSet(set, src);
      lvlist.add(lvset);
      removed.add(lvlist);
    } else {
      removed.getLast().add(new RemovedCSet(set, src));
    }
  }

  /**
   * Returns true if this critical set is not empty after the addition of the specified literal.
   * @param lit       the specified literal.
   * @param claIndex  the index of the clause that contains the literal.
   * @return true if this critical set is not empty after the addition of the specified literal.
   */
  public boolean update(int lit, int claIndex) {
    
    for (int i=0; i < added.size(); i++) {
      LvList<Clause> list = added.get(i);
      for (int j=0; j < list.size(); j++) {
        Clause clause = list.get(j);
        if (clause.contains(lit)) {    // If the clause contains lit, then it becomes no-critical.
          
          remove(i, j, claIndex);
          
          if (isEmpty()) {
            backtrackTo(claIndex);
            return false;
          }
        }  
      }
    }

    return true;
  }

//  /**
//   * Returns the critical sets at the specified index.
//   * @param index  the specified index.
//   * @return the critical sets at the specified index.
//   */
//  public LvList<IntSet> getLvCSets(int index) {
//    return added.get(index);
//  }
//
//  /**
//   * Returns the number of critical sets with level.
//   * @return the number of critical sets with level.
//   */
//  public int getNumLvCSets() {
//    return added.size();
//  }

  /**
   * Returns true if the critical sets is empty.
   */
  public boolean isEmpty() {
    return numAddedSets == 0;
  }

  /**
   * Backtracks to the specified level.
   * @param claIndex  the specified level.
   */
  public void backtrackTo(int claIndex) {
    while (claIndex <= added.getLast().getLevel()) {
      numAddedSets -= added.getLast().size();
      lvListClausePool.put(added.removeLast());
    }

    while (!removed.isEmpty() && claIndex <= removed.getLast().getLevel()) {
      LvList<RemovedCSet> list = removed.removeLast();
      for (int i = 0; i < list.size(); i++) {
        RemovedCSet lvset = list.get(i);
        LvList<Clause> src = lvset.getSrc();
        if (src.getLevel() < claIndex) {
          src.add(lvset.getSet());
          numAddedSets++;
        }
      }
      lvListRemovedCSetPool.put(list);
    }
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return "add=" + added + "/rem=" + removed;
  }

  /** The list of added critical sets. */
  private LightArrayList<LvList<Clause>> added = new LightArrayList<LvList<Clause>>();

  /** The list of removed critical sets. */
  private LightArrayList<LvList<RemovedCSet>> removed = new LightArrayList<LvList<RemovedCSet>>();

  /** The number of added critical sets. */
  private int numAddedSets = 0;

  /** A critical set with the removed level. */
  private final static class RemovedCSet {
    public RemovedCSet(Clause clause, LvList<Clause> source) {
      this.clause = clause;
      this.source = source;
    }

    public Clause getSet() {
      return clause;
    }

    public LvList<Clause> getSrc() {
      return source;
    }

    public String toString() {
      return "Lv" + source.getLevel() + ":" + clause;
    }

    private Clause         clause = null;
    private LvList<Clause> source = null;
  }

  /** A list with the level. */
  private final static class LvList<E> extends LightArrayList<E> {
    public LvList(int level) {
      this.level = level;
    }

    public void init(int level) {
      this.level = level;
      super.clear();
    }

    public int getLevel() {
      return level;
    }

    public String toString() {
      return "Lv" + level + ":" + super.toString();
    }

    private int level = 0;
  }

  /** The pool of LvList objects. */
  private final static class LvListPool<E> {

    public LvList<E> get() {
      if (lists.isEmpty())
        return new LvList<E>(0);
      return lists.removeLast();
    }

    public void put(LvList<E> list) {
      lists.add(list);
    }

    private LightArrayList<LvList<E>> lists = new LightArrayList<LvList<E>>();
  }

  /** The pool of the LvList<Clause> and LvList<RemovedCSet> objects. */
  private static LvListPool<Clause>      lvListClausePool      = new LvListPool<Clause>();
  private static LvListPool<RemovedCSet> lvListRemovedCSetPool = new LvListPool<RemovedCSet>();
}

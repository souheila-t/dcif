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
import java.util.Collections;
import java.util.Comparator;

/**
 * @author nabesima
 *
 */
public class LSuccCache implements OptionTypes, DebugTypes {
  
  /**
   * Constructs a empty local success cache.
   * @param env        the environment.
   * @param opt        the options.
   * @param node       a node has this cache.
   * @param negOffset  a offset value for negative variables.
   * @param maxSize    the maximum number of local successes to be stored.
   */
  protected LSuccCache(Env env, Options opt, Node node, int negOffset, int maxSize) {
    this.env         = env;
    this.opt         = opt;
    this.varTable    = env.getVarTable();
    this.node        = node;
    this.orgNumVars  = node.getOrgNumVars();
    this.orgVarState = node.getOrgVarState();
    this.negOffset   = negOffset;
    this.minNegVar   = negOffset;
    this.maxSize     = maxSize;
  }
  
  /**
   * Constructs a empty local success cache.
   * @param env  the environment.
   * @param opt        the options.
   * @param node a node has this cache.
   * @param maxSize    the maximum number of local successes to be stored.
   */
  public LSuccCache(Env env, Options opt, Node node, int maxSize) {
    this(env, opt, node, 0, maxSize);
  }
  
  /**
   * Adds a local success to this cache.
   * @param skiiped  the skipped nodes if added
   */
  public LSucc add(Skipped skippedNodes) {
    
    Conseq conq = skippedNodes.convertToConseq();

    LSucc general = hasMoreGeneralSuccess(conq);
    if (general != null)
      return null;
    
    NegVarRenameMap map = env.getNegVarRenameMap();
    map.setOffset(negOffset);

    // Updates the negative variables in the consequence.
    conq.subrename(map, orgNumVars, Integer.MAX_VALUE);
    
    LSucc g = new LSucc(env, conq);
    for (int i=orgVarState; i < varTable.state(); i++) {
      int var = varTable.getSubstitutedVar(i);
      if (var < orgNumVars) {
        Term val = varTable.getTailVar(var).instantiate();
//        if (val.isVar() && orgNumVars <= val.getName())    // ignore such as { _0 / _-1 } -> can not IGNORE!!
//          continue;
        val.subrename(map, orgNumVars, Integer.MAX_VALUE);
        g.add(var, val, val.size(false));
      }
    }    

    // Updates the minimum variable name.
    if (map.getMinVar() < minNegVar)
      minNegVar = map.getMinVar();
    
    // Records the information for unit lemma generation.
    if (opt.use(USE_UNIT_LEMMA_MATCHING)) {
      int depth = node.getShallowestTargetDepth();
      // If node.getDepth() is equal to depth, then generates an unit lemma in tableau.getNextSubgoal().
      assert(depth != -1);
      g.setShallowestTargetDepth(depth);
    }    
    
    if (g.isEmpty()) {
      cache.clear();
      cache.add(g);
      hasEmptySucc = true;
    }
    else {
      cache.add(g);
    }
    
    return g;
  }

  /**
   * Adds a local success to this cache.
   * @param conqs  a set of consequence.
   * @return a local success if added.
   */
  private LSucc add(ConseqSet conqs) {
    
    LSucc general = hasMoreGeneralSuccess(conqs);
    if (general != null)
      return null;

    NegVarRenameMap map = env.getNegVarRenameMap();
    map.setOffset(negOffset);    

    // Updates the negative variables in consequences.
    ConseqSet newConqs = new ConseqSet(env);
    for (Clause conq : conqs) {
      conq.subrename(map, Integer.MIN_VALUE, -1);
      conq.subrename(map, orgNumVars, Integer.MAX_VALUE);
      newConqs.add(conq);
    }
    conqs = newConqs;

    LSucc g = new LSucc(env, conqs);
    for (int i=orgVarState; i < varTable.state(); i++) {
      int var = varTable.getSubstitutedVar(i);
      if (0 <= var && var < orgNumVars) {                   // Ignores negative variables.
        Term val = varTable.getTailVar(var).instantiate();
        val.subrename(map, Integer.MIN_VALUE, -1);          // Updates the negative variables in values.
        val.subrename(map, orgNumVars, Integer.MAX_VALUE);
        g.add(var, val, val.size(false));
      }
    }    

    // Updates the minimum variable name.
    if (map.getMinVar() < minNegVar)
      minNegVar = map.getMinVar();

    if (g.isEmpty()) {
      cache.clear();
      cache.add(g);
      hasEmptySucc = true;
    }
    else {
      cache.add(g);
    }
    
    return g;
  }

  /**
   * Adds a local success cache of a child node to this cache.
   * @param child  a local success cache of a child node.
   */
  public void add(LSuccCache child) {

    // Renames the negative variables.
    child.renameNegVars(0);
    renameNegVars(child.minNegVar);

    // Copies local successes of the child node to this cache.
    int state = varTable.state();
    for (LSucc succ : child.cache) {
      varTable.substitute(succ);
      LSucc g = add(succ.getConseqs());
      if (g != null) {
        int depth = Math.min(node.getDepth(), succ.getShallowestTargetDepth());
        g.setShallowestTargetDepth(depth);
        if (opt.use(USE_UNIT_LEMMA_MATCHING) && node.getDepth() == depth) {
          ClauseDB clauseDB = node.getTableau().getClauseDB();
          Literal ulemma = clauseDB.addUnitLemma(node);
          if (ulemma != null) {
            env.getStats().setProds(Stats.UNIT_LEMMA_MATCHING, clauseDB.getNumUnitLemmas());
            if (env.dbgNow(DBG_UNIT_LEMMA_DETAIL)) {
              System.out.println();
              System.out.println(env.getTimeStep() + " " + this);
              System.out.println("UNIT LEMMA:");
              System.out.println(" " + ulemma.toSimpString());          
            }
            else if (env.dbgNow(DBG_UNIT_LEMMA)) 
              System.out.println(env.getTimeStep() + " UNIT LEMMA: " + ulemma.toSimpString());
          }
        }
      }
      varTable.backtrackTo(state);
    }
    
    // Renames negative variables.
    renameNegVars(0);    
  }
  
  /**
   * Returns true if this cache contains a general success.
   * @param conqs  a set of consequences.
   * @return a general local success if exists.
   */
  public LSucc hasMoreGeneralSuccess(ConseqSet conqs) {
    if (hasEmptySucc)
      return cache.get(0);
    
    for (int i=0; i < cache.size(); i++) {
      LSucc succ = cache.get(i);
      if (succ.isMoreGeneral(orgNumVars, conqs)) {
        return succ;
      }
    }
    return null;
  }
  
  /**
   * Returns true if this cache contains a general success.
   * @param conq  the skipped nodes.
   * @return a general local success if exists.
   */
  public LSucc hasMoreGeneralSuccess(Clause conq) {
    if (hasEmptySucc)
      return cache.get(0);
    
    for (int i=0; i < cache.size(); i++) {
      LSucc succ = cache.get(i);
      if (succ.isMoreGeneral(orgNumVars, conq)) {
        return succ;
      }
    }
    return null;
  }
  
  /**
   * Rename negative variables in this cache.
   * @param negOffset  a offset for negative variables.
   */
  private void renameNegVars(int negOffset) {
    // If the specified offset is different from the current offset, then do renaming.
    if (this.negOffset == negOffset)
      return;

    // Records the new offset.
    this.negOffset = negOffset;
    this.minNegVar = 0;
    
    // Renaming.
    NegVarRenameMap map = env.getNegVarRenameMap();
    for (LSucc succ : cache) { 
      map.clear();
      map.setOffset(negOffset);
      succ.subrename(map, Integer.MIN_VALUE, -1);
      if (map.getMinVar() < minNegVar)
        minNegVar = map.getMinVar();        
    }    
  }
  
  /**
   * Combines with the specified local success cache.
   */
  public LSuccCache combine(LSuccCache other) {
  
//    // TEST CODE
//    assert(orgVarState == other.orgVarState);
//    assert(orgNumVars  == other.orgNumVars );
//    varTable.backtrackTo(orgVarState);
//    if (orgNumVars < varTable.getNumVars())
//      varTable.removeVars(varTable.getNumVars() - orgNumVars);

    // Renames the negative variables.
    this.renameNegVars(0);
    other.renameNegVars(minNegVar);
    
    if (maxSize > 0) {
      Collections.sort(cache, comparator);
      Collections.sort(other.cache, comparator);
    }
    
    LSuccCache newCache = new LSuccCache(env, opt, node, other.minNegVar, maxSize);    
    OuterLoop:
    for (int i=0; i < cache.size(); i++) {
      LSucc s = cache.get(i);
      varTable.substitute(s);
      int state = varTable.state();
      InnerLoop:
      for (int j=0; j < other.cache.size(); j++) {
        LSucc t = other.cache.get(j);
        for (int k=0; k < t.size(); k++) {
          int  var = t.getVar(k);
          Term val = t.getVal(k); 
          // Allow a substitution that equals to own variable: { X/X }
          if (val.isVar()) {
            Term v = varTable.getTailVar(val.getName() + val.getOffset());
            if (v.getName() + v.getOffset() == var) 
              continue;
          }
          // Forbid a substitution that contains own variable: { X/f(X) }
          if (val.containsVar(var) == -1) {
            varTable.backtrackTo(state);            
            continue InnerLoop;
          }
          // Forbid a substitution that contains a loop: { X/f(Y), Y/Z, Z/X }
          // If we apply { X/f(Y) } and { Y/Z }, then we can detect a loop in { Z/X } (={ Z/f(Z) }) at the above code.
          
          Term cur = varTable.getValue(var);
          // The variable has no value.
          if (cur == null) 
            varTable.substitute(var, val);
          // If the value of the variable in s is NOT unifiable with the value in t, then the composition fails. 
          else if (val.unify(cur) == null) {
            varTable.backtrackTo(state);
            continue InnerLoop;
          }
        }
               
        // Synthesize sub-consequences.
        ConseqSet sconqs = s.getConseqs().instantiate();
        ConseqSet tconqs = t.getConseqs().instantiate();
        // TODO: NOT IMPLEMENTED
        ConseqSet uconqs = new ConseqSet(env);
        uconqs.addAll(sconqs);    // TODO: Pseudo code
        uconqs.addAll(tconqs);    // TODO: Pseudo code
        
        LSucc u = newCache.add(uconqs);
        
        if (u != null) {

          // Updates the shallowest reduction target depth.
          if (opt.use(USE_UNIT_LEMMA_MATCHING)) {
            int sdepth = s.getShallowestTargetDepth();
            int tdepth = t.getShallowestTargetDepth();
            u.setShallowestTargetDepth(Math.min(sdepth, tdepth));
          }

          if (newCache.isFull()) {
            node.getTableau().getSearchParam().setExhaustiveness(false);
            node.markAsNotExhausted();
            varTable.backtrackTo(orgVarState);
            break OuterLoop;
          }

          if (env.dbg(DBG_DIV_CONQ_DETAIL) && u != null) {
            System.out.println(" L:" + s);
            System.out.println(" R:" + t);
            System.out.println(" C:" + u);
          }
          
        }
        varTable.backtrackTo(state);
      }
      varTable.backtrackTo(orgVarState);
    }
    
    // Renames negative variables.
    newCache.renameNegVars(0);
    
    return newCache;
  }
  
  /**
   * Returns the set of consequences in this cache.
   * @return the set of consequences in this cache.
   */
  public ConseqSet getConseqs() {
    ConseqSet conqs = new ConseqSet(env);
    for (LSucc succ : cache) {
      for (Conseq c : succ.getConseqs()) {
        Conseq conq = c.instantiate();
        conq.rename(env.getNegVarRenameMap());
        conqs.add(conq);        
      }
    }    
    return conqs;
  }
  
  /**
   * Returns the number of local successes.
   * @return the number of local successes.
   */
  public int size() {
    return cache.size();
  }
  
  /**
   * Clears all local success.
   */
  public void clear() {
    cache.clear();
    hasEmptySucc = false;
  }
  
  /**
   * Returns true if this cache is full.
   * @return true if this cache is full.
   */
  public boolean isFull() {
    return maxSize != 0 && maxSize <= cache.size();    
  }
  
  /**
   * Returns the maximum size of this cache.
   * @return the maximum size of this cache.
   */
  public int getMaxSize() {
    return maxSize;
  }
  
  /**
   * Returns true if this cache is empty.
   * @return true if this cache is empty.
   */
  public boolean isEmpty() {
    return cache.size() == 0;
  }
  
  /**
   * Returns true if this cache has an empty local success.
   * @return true if this cache has an empty local success.
   */
  public boolean hasEmptySucc() {
    return hasEmptySucc;
  }
  
  /**
   * Returns the local success at the specified position.
   * @param index  an index of a local success.
   * @return the local success at the specified position.
   */
  public LSucc get(int index) {
    return cache.get(index);
  }
  
  /**
   * Sets an inference step beginning to record local successes.
   * @param infStep  an inferece step.
   */
  public void setStartInfStep(long infStep) {
    startInfStep = infStep;    
  }
  
  /**
   * @return an inferece step beginning to record local successes.
   */
  public long getStartInfStep() {
    return startInfStep;
  }

  /**
   * Sets an end inference step of recording local successes.
   * @param infStep  an inferece step.
   */
  public void setEndInfStep(long infStep) {
    endInfStep = infStep;    
  }
  
  /**
   * @return an end inference step of recording local successes.
   */
  public long getEndInfStep() {
    return endInfStep;
  }

  /**
   * Returns the number of inference steps to create this local successes.
   * @return
   */
  public long getInfSteps() {
    if (startInfStep == 0 || endInfStep == 0)
      return 0;
    return endInfStep - startInfStep;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    for (LSucc succ : cache)
      str.append(" " + succ + "\n");
    return str.toString();
  }

  /** The environment. */
  private Env env = null;
  /** The options. */
  private Options opt = null;
  /** The variable table. */
  private VarTable varTable = null;
  /** The node has this cache. */
  private Node node = null;
  /** The maximum variable name at the first solving the node. */
  private int orgNumVars = 0;
  /** The variable table state at the first solving the node. */
  private int orgVarState = 0;
  /** The set of substitutions. */
  private ArrayList<LSucc> cache = new ArrayList<LSucc>();
  /** An empty failure if found. */
  private boolean hasEmptySucc = false;
  /** The offset for negative variables. */
  private int negOffset = 0;
  /** The minimum variable name in this cache. */
  private int minNegVar = 0;
  /** The maximum number of local successes to be stored. */
  private int maxSize = 0;
  /** The start inference step beginning to record local successes. */
  private long startInfStep = 0;
  /** The end inference step of recording local successes. */
  private long endInfStep = 0;
  
  private static final Comparator<LSucc> comparator = new Comparator<LSucc>() {
    public int compare(LSucc s, LSucc t) {
      return s.getNumSyms() - t.getNumSyms();
    } 
  };
}
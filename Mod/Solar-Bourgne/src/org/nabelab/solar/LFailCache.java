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

/**
 * @author nabesima
 *
 */
public class LFailCache implements DebugTypes {
  
  /**
   * Constructs a empty local failure cache.
   * @param env the environment.
   */
  public LFailCache(Env env) {
    this.env = env;
    this.varTable = env.getVarTable();
  }
  
  /**
   * Adds the local failure to this cache.
   * @param orgVarState  the original variable state.
   * @param orgNumVars   the original number of variables.
   * @param skiiped      the skipped nodes.
   */
  public LFail add(int orgVarState, int orgNumVars, Skipped skippedNodes) {
    
//    Clause skipped = skippedNodes.convertToClause());  // NOTE: BUG in LCL168-1.p    
//    Clause skipped = skippedNodes.convertToConseq());  // OK but strict
    Clause skipped = skippedNodes.convertToInstantiatedClause();

    NegVarRenameMap map = env.getNegVarRenameMap();
    //skipped.subrename(map, orgNumVars);
    
    LFail g = new LFail(env, skipped);
    for (int i=orgVarState; i < varTable.state(); i++) {
      int var = varTable.getSubstitutedVar(i);
      if (var < orgNumVars) {
        Term val = varTable.getTailVar(var).instantiate();
        val.subrename(map, orgNumVars, Integer.MAX_VALUE);
        g.add(var, val, val.size(false));
      }
    }    

    if (g.isEmpty()) {
      cache.clear();
      cache.add(g);
      hasEmptyFail = true;
    }
    else if (!hasEmptyFail) 
      cache.add(g);
    
    return g;
  }

  /**
   * Returns true if this cache contains a general failure.
   * @param orgNumVars   the original number of variables.
   * @param curSkipped the skipped nodes.
   * @return a general local failure if exists.
   */
  public LFail hasMoreGeneralFailure(int orgNumVars, Clause curSkipped) {
    if (hasEmptyFail)
      return cache.get(0);
    
    //System.out.println(varTable);
    for (int i=0; i < cache.size(); i++) {
      LFail fail = cache.get(i);
      if (fail.isMoreGeneral(orgNumVars, curSkipped)) {
        //System.out.println(fail + " is more general.");
        return fail;
      }
      //System.out.println(fail + " is NOT more general.");
    }
    return null;
  }
  
  /**
   * Clears all local failures.
   */
  public void clear() {
    cache.clear();
    hasEmptyFail = false;
  }
  
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return cache.toString();
  }

  /** The environment. */
  private Env env = null;
  /** The variable table. */
  private VarTable varTable = null;
  /** The set of substitutions. */
  private ArrayList<LFail> cache = new ArrayList<LFail>();
  /** An empty failure if found. */
  private boolean hasEmptyFail = false;
}

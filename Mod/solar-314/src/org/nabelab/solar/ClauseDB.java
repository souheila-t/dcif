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
import java.util.List;

import org.nabelab.solar.indexing.DiscNode;
import org.nabelab.solar.indexing.DiscTree;
import org.nabelab.solar.proof.Proof;
import org.nabelab.solar.util.Pair;


/**
 * @author nabesima
 *
 */
public class ClauseDB implements ClauseTypes, OptionTypes, DebugTypes {

  /**
   * Constructs a clause database.
   * @param env     the environment.
   * @param opt     the options.
   * @param clauses the set of clauses.
   */
  public ClauseDB(Env env, Options opt, List<Clause> clauses) {
    this.env = env;
    this.opt = opt;
    
    // Makes a discrimination tree.
    discTree = new DiscTree<PClause>(env, true);
    for (Clause c : clauses) {
      for (int i=0; i < c.size(); i++) {
        Literal lit = c.get(i);
        if (opt.use(USE_NEGATION_AS_FAILURE) && lit.hasNAF()) 
          continue;
        PClause pc  = new PClause(c, i);
        discTree.addInstantiated(lit, pc);
      }
      if (c.isNegative())
        negClauses.add(c);
    }
    
    // Makes a discrimination tree for unit axioms.
    unitAxiomTree = new DiscTree<Clause>(env, true);
    for (Clause c : clauses) {
      if (c.size() == 1) 
        unitAxiomTree.addInstantiated(c.get(0), c);
    }
    
    if (env.dbg(DBG_UNIT_AXIOM)) {
      System.out.println("[Unit axioms]");
      System.out.println(unitAxiomTree);
    }
    
    // Makes a discrimination tree for unit axioms.
    unitLemmaTree = new DiscTree<Conseq>(env, true);
  }
  
  /**
   * Adds the unit axioms.
   * @param units  the set of unit axioms.
   */
  public void addUnitAxiomsForMatching(List<Clause> units) {
    for (Clause c : units) {
      assert(c.isUnit()); 
      unitAxiomTree.addInstantiated(c.get(0), c);
    }
  }
  
  /**
   * Returns the clauses whose have a complementary unifiable literal with specified literal.
   * @param lit the specified literal.
   * @return the clauses whose have a complementary unifiable literal with specified literal.
   */
  public List<Unifiable<PClause>> getCompUnifiable(Literal lit) {
    return discTree.findCompUnifiable(lit);
  }  

  /**
   * Returns the clauses whose have a unifiable literal with specified literal.
   * @param lit the specified literal.
   * @return the clauses whose have a unifiable literal with specified literal.
   */
  public List<Unifiable<PClause>> getUnifiable(Literal lit) {
    return discTree.findUnifiable(lit);
  }  

  /**
   * Returns a unit axiom which subsumes the specified literal.
   * @param lit the literal to check.
   * @return a unit axiom which subsumes the specified literal.
   */
  public Clause hasSubsumingUnitAxiom(Literal lit) {
    Clause clause = unitAxiomTree.isSubsumed(lit);
    if (clause == null) 
      return null;
    assert(clause.isUnit());
    return clause;
  }
  
  /**
   * Returns a unit lemma which subsumes the specified literal.
   * @param lit the literal to check.
   * @return a unit lemma which subsumes the specified literal.
   */
  public Conseq hasSubsumingUnitLemma(Literal lit) {
    Conseq conseq = unitLemmaTree.isSubsumed(lit);
    if (conseq == null)
      return null;
    assert(conseq.isUnit());
    return conseq;
  }
  
  /**
   * Returns a unit axiom which complementary subsumes the specified literal.
   * @param lit the literal to check.
   * @return a unit axiom which complementary subsumes the specified literal.
   */
  public Clause hasCompSubsumingUnitAxiom(Literal lit) {
    Clause clause = unitAxiomTree.isCompSubsumed(lit);
    if (clause == null) 
      return null;
    assert(clause.isUnit());
    return clause;
  }
  
  /**
   * Returns a unit lemma which complementary subsumes the specified literal.
   * @param lit the literal to check.
   * @return a unit lemma which complementary subsumes the specified literal.
   */
  public Conseq hasCompSubsumingUnitLemma(Literal lit) {
    Conseq conseq = unitLemmaTree.isCompSubsumed(lit);
    if (conseq == null)
      return null;
    assert(conseq.isUnit());
    return conseq;
  }
  
  /**
   * Returns the clauses whose have a complementary unifiable literal with specified literal.
   * @param lit the specified literal.
   * @return the clauses whose have a complementary unifiable literal with specified literal.
   */
  public List<Unifiable<Conseq>> getCompUnifiableUnitLemma(Literal lit) {
    return unitLemmaTree.findCompUnifiable(lit);
  }  

  /**
   * Adds the specified node as a unit lemma.
   * @param node the specified node.
   * @return the literal which is added to the database.
   */
  public Literal addUnitLemma(Node node) {
    Literal lit = node.getLiteral();
    // Forward subsumption checking.
    if (unitLemmaTree.isCompSubsumed(lit) != null || unitAxiomTree.isCompSubsumed(lit) != null)
      return null;

    // Constructs a copy of the literal.
    lit = lit.instantiate();
    lit.rename();
    lit.negate();
    
    // TEST!
    if (opt.use(USE_TEST3)) {
      List<Pair<DiscNode<PClause>,PClause>> subsumeds = discTree.findSubsumed(lit);
      if (!subsumeds.isEmpty()) {
        System.out.println(env.getTimeStep());
        System.out.println("node lit  = " + node.getLiteral());
        System.out.println("new lemma = " + lit);
        System.out.println("subsumeds = " + subsumeds);
        int delClauses  = 0;
        int delLiterals = 0;
        for (Pair<DiscNode<PClause>,PClause> subsumed : subsumeds) {
          Clause c = subsumed.get2nd().getClause();
          delClauses  += 1;
          delLiterals += c.size();
          System.out.println(" removed = " + c);
          for (int i=0; i < c.size(); i++) {
            Literal l  = c.get(i);
            PClause pc = new PClause(c, i);
            System.out.println("  pc = " + pc);
            discTree.removeInstantiated(l, pc);
          }
        }
        Clause  c  = new Clause(env, "a lemma", AXIOM, lit);
        PClause pc = new PClause(c, 0);
        c.getNumExts(true, this);      
        discTree.addInstantiated(lit, pc);
        env.getStats().incProds(Stats.CLAUSES,  1 - delClauses);
        env.getStats().incProds(Stats.LITERALS, 1 - delLiterals);
        return Literal.restart;
      }
    }    
    
    // Backward subsumption checking.
    unitLemmaTree.removeSubsumed(lit);
    
    Conseq conseq = new Conseq(env, "unit_lemma", AXIOM, lit);
    if (opt.hasVerifyOp()) { 
      Proof proof = node.getTableau().getProof(conseq, node);
      conseq.setProof(proof);
    }
    unitLemmaTree.addInstantiated(lit, conseq);
    
//    if (opt.use(USE_STRONG_CONTRACTION)) {
//      Clause ua = hasCompSubsumingUnitAxiom(lit);
//      if (ua != null) { 
//        compSubsumingUnitClause = ua;
//        if (env.dbgNow(DBG_STRONG_CONTRACTION)) 
//          System.out.println(env.getTimeStep() + " STRONG CONTRACTION by the unit axiom " + ua);
//      }
//      else {
//        Clause ul = hasCompSubsumingUnitLemma(lit);
//        if (ul != null) {
//          compSubsumingUnitClause = ul;
//          if (env.dbgNow(DBG_STRONG_CONTRACTION))
//            System.out.println(env.getTimeStep() + " STRONG CONTRACTION by the unit lemma " + ul);
//        }
//      }
//    }    
//    
    return lit;
  }
    
  //  /**
//   * Returns a unit clause that complementary subsumes a unit lemma.
//   * @return a unit clause that complementary subsumes a unit lemma.
//   */
//  public Clause hasCompSubsumedUnitLemma() {
//    return compSubsumingUnitClause;
//  }
//  
  /**
   * Returns the number of unit axioms.
   * @return the number of unit axioms.
   */
  public int getNumUnitAxioms() {
    return unitAxiomTree.getNumClauses();
  }
  
  /**
   * Returns the number of unit lemmas.
   * @return the number of unit lemmas.
   */
  public int getNumUnitLemmas() {
    return unitLemmaTree.getNumClauses();
  }
  
  /**
   * Returns the number of negative clauses.
   * @return the number of negative clauses.
   */
  public List<Clause> getNegClauses() {
    return negClauses;
  }
  
  /**
   * Returns the number of negative clauses.
   * @return the number of negative clauses.
   */
  public int getNumNegClauses() {
    return negClauses.size();
  }
  
//  /**
//   * Returns the type of tableau calculus.
//   * @return the type of tableau calculus.
//   */
//  public int getCalcType() {
//    return opt.getCalcType();
//  }

  /** The environment. */
  private Env env = null;
  /** The options. */
  private Options opt = null;
  /** The discrimination tree. */
  private DiscTree<PClause> discTree = null;
  /** The discrimination tree for unit axioms. */
  private DiscTree<Clause> unitAxiomTree = null;
  /** The discrimination tree for unit lemmas. */
  private DiscTree<Conseq> unitLemmaTree = null;
  /** The set of negative clauses. */
  private List<Clause> negClauses = new ArrayList<Clause>();

}

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

package org.nabelab.solar.operator;

import java.util.List;

import org.nabelab.solar.CFP;
import org.nabelab.solar.Clause;
import org.nabelab.solar.Conseq;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Node;
import org.nabelab.solar.PClause;
import org.nabelab.solar.Stats;
import org.nabelab.solar.Subst;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;
import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.Unifiable;

/**
 * @author nabesima
 *
 */
public class ExtensionChecker extends OpChecker implements TermTypes, Tags {
  
  /**
   * Constructs a extension operation checker.
   * @param env     the environment.
   * @param tableau the tableau.
   */
  public ExtensionChecker(Env env, Tableau tableau) {
    super(env, tableau);
  }

  /**
   * Returns the applicable operators to the specified node.
   * @param node the node to check.
   * @param ops  the applicable operators.
   * @return true if the tableau is not redundant.
   */
  public boolean check(Node node, Operators ops) {
    
    Literal lit = node.getLiteral();
    
    if (opt.use(USE_UNIT_AXIOM_MATCHING)) {
      tableau.stats().incTests(Stats.UNIT_AXIOM_MATCHING);
      Clause uaxiom = tableau.getClauseDB().hasCompSubsumingUnitAxiom(lit);
      if (uaxiom != null) {  
        ops.add(new UnitAxiomMatching(env, node, uaxiom));
        return true;
      }
    }
    
    if (opt.use(USE_UNIT_LEMMA_MATCHING)) {
      tableau.stats().incTests(Stats.UNIT_LEMMA_MATCHING);
      Conseq ulemma = tableau.getClauseDB().hasCompSubsumingUnitLemma(lit);
      if (ulemma != null) {
        ops.add(new UnitLemmaMatching(env, node, ulemma));
        return true;
      }
    }
    
    if (opt.use(USE_UNIT_LEMMA_EXTENSION)) {
      tableau.stats().incTests(Stats.UNIT_LEMMA_EXTENSION);
      List<Unifiable<Conseq>> unifs = tableau.getClauseDB().getCompUnifiableUnitLemma(lit);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<Conseq> unif = unifs.get(i);  

          // Do not allow to extend the positive equality node with a node whose first argument is variable.
          if (opt.getEqType() >= CFP.EQ_SMT) {
            if (lit.isPosEqualPred()) {
              Term term = unif.getObject().get(0).getTerm();
              int arg1stPos = term.getStart() + 1;
              if (term.getType(arg1stPos) == VARIABLE) 
                continue;
            }
          }     

          ops.add(new UnitLemmaExtension(env, node, unif));
        }
      }      
    }
    
    if (opt.getEqType() >= CFP.EQ_SMT) {
      // Do not allow to extend the RAW equal predicate.
      if (node.hasTag(EQ_RAW))
        return true;        
      // Do not allow to extend the negative equal predicate whose first argument is variable.
      if (lit.isEqualPred()) {
        lit = lit.instantiate();
        if (lit.isNegative()) {
          Term term = lit.getTerm();
          int arg1stPos = term.getStart() + 1;
          if (term.getType(arg1stPos) == VARIABLE) 
            return true;
        }
      }
    }

    boolean exhaustivenessChecking = true;
    if (param.getDepthLimit() == 0 || node.getExtDepth() < param.getDepthLimit()) 
      exhaustivenessChecking = false;
    else if (!param.getExhaustiveness()) {
      node.markAsNotExhausted();
      return true;
    }

    //
    // SnMTn or SnM: Extension rules for negative equality.
    //
    if (opt.getEqType() >= CFP.EQ_SNMTN && lit.isNegEqualPred()) {
      
      Term term = lit.getTerm();
      Term arg1 = term.getArg(0);
      Term arg2 = term.getArg(1);
      Term newVar = Term.createVar(env, -1);
      Term eq1 = Term.createPredicate(env, EQUAL_PRED, arg1, newVar);
      Term eq2 = Term.createPredicate(env, EQUAL_PRED, newVar, arg1);
      Term eq3 = Term.createPredicate(env, EQUAL_PRED, arg2, arg1);
      Literal neq1 = new Literal(env, false, eq1);
      Literal neq2 = new Literal(env, false, eq2);
      Literal neq3 = new Literal(env, false, eq3);      

      // (NE1) Finds clauses which contains a complementary unifiable literal with neq1 (-equal(arg1, newVar)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      List<Unifiable<PClause>> unifs = tableau.getClauseDB().getCompUnifiable(neq1);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  

          // Do not allow to extend the negative equality node with a node whose second argument is a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          Term exarg1 = exterm.getArg(0);
          Term exarg2 = exterm.getArg(1);
          if (exarg2.getStartType() == VARIABLE) 
            continue;

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }
          
          exarg1 = Term.newOffset(exarg1, unif.getOffset());
          exarg2 = Term.newOffset(exarg2, unif.getOffset());
          Subst g = arg1.isUnifiable(exarg1);
          assert(g != null);
          unif.setSubst(g);

          // Creates the grand child negative equality predicate.
          exarg2 = exarg2.instantiate();
          Term gceq = Term.createPredicate(env, EQUAL_PRED, exarg2, arg2);
          Literal ngceq = new Literal(env, false, gceq);
          
          ops.add(new EqExtension(env, node, unif, arg1, arg2, exarg2, ngceq));
        }
      }
      // (NE2) Finds clauses which contains a complementary unifiable literal with neq2 (-equal(newVar, arg1)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getCompUnifiable(neq2);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the negative equality node with a node whose first argument is variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          Term exarg1 = exterm.getArg(0);
          Term exarg2 = exterm.getArg(1);
          if (exarg1.getStartType() == VARIABLE) 
            continue;

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }

          exarg1 = Term.newOffset(exarg1, unif.getOffset());
          exarg2 = Term.newOffset(exarg2, unif.getOffset());
          Subst g = arg1.isUnifiable(exarg2);
          assert(g != null);
          unif.setSubst(g);

          // Creates the grand child negative equality predicate.
          exarg1 = exarg1.instantiate();
          Term gceq = Term.createPredicate(env, EQUAL_PRED, exarg1, arg2);
          Literal ngceq = new Literal(env, false, gceq);

          ops.add(new EqExtension(env, node, unif, arg1, arg2, exarg1, ngceq));            
        }
      }
      // (NE3) Finds clauses which contains a complementary unifiable literal with lit (-equal(arg1, arg2)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getCompUnifiable(lit);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the negative equality node with a node whose second argument is not a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          int arg2ndPos = exterm.getNext(exterm.getStart() + 1);
          if (exterm.getType(arg2ndPos) != VARIABLE)
            continue;
                       
          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }

          ops.add(new EqExtension(env, node, unif, arg1, arg2));
        }
      }
      // (NE4) Finds clauses which contains a complementary unifiable literal with neq3 (-equal(arg2, arg1)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getCompUnifiable(neq3);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the negative equality node with a node whose first argument is not variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          int arg1stPos = exterm.getStart() + 1;
          if (exterm.getType(arg1stPos) != VARIABLE)
            continue;
                       
          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }

          ops.add(new EqExtension(env, node, unif, arg1, arg2));
        }
      }
    }      
    //
    // SnMTn: Extension rules for positive equality.
    //
    else if (opt.getEqType() == CFP.EQ_SNMTN && lit.isPosEqualPred()) {
      
      Term term = lit.getTerm();
      Term arg1 = term.getArg(0);
      Term arg2 = term.getArg(1);
      Term newVar = Term.createVar(env, -1);
      Term eq1 = Term.createPredicate(env, EQUAL_PRED, arg1, newVar);
      Term eq2 = Term.createPredicate(env, EQUAL_PRED, newVar, arg1);
      Literal peq1 = new Literal(env, true, eq1);
      Literal peq2 = new Literal(env, true, eq2);

      // (SnMTn:PE1) Finds clauses which contains a complementary unifiable literal with lit.
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      List<Unifiable<PClause>> unifs = tableau.getClauseDB().getCompUnifiable(lit);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the negative equality node with a node whose first argument is a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          int arg1stPos = exterm.getStart() + 1;
          if (exterm.getType(arg1stPos) == VARIABLE)
            continue;
                       
          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }

          ops.add(new EqExtension(env, node, unif, arg1, arg2));
        }
      }
      
      // (SnMTn:PE2) Finds clauses which contains a (NOT COMPLEMENTARY) unifiable literal with peq1 (equal(arg1, newVar)). 
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getUnifiable(peq1);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the positive equality node with a node whose first argument is a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          Term exarg1 = exterm.getArg(0);
          Term exarg2 = exterm.getArg(1);
          if (exarg1.getStartType() == VARIABLE)
            continue;

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }
          
          exarg1 = Term.newOffset(exarg1, unif.getOffset());
          exarg2 = Term.newOffset(exarg2, unif.getOffset());
          Subst g = arg1.isUnifiable(exarg1);
          assert(g != null);
          unif.setSubst(g);

          // Creates the grand child negative equality predicate.
          exarg2 = exarg2.instantiate();
          Term gceq = Term.createPredicate(env, EQUAL_PRED, exarg2, arg2);
          Literal pgceq = new Literal(env, true, gceq);
          
          ops.add(new EqExtension(env, node, unif, arg1, arg2, exarg2, pgceq));
        }
      }
      
      // (SnMTn:PE3) Finds clauses which contains a (NOT COMPLEMENTARY) unifiable literal with peq2 (equal(newVar, arg1)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getUnifiable(peq2);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the positive equality node with a node whose second argument is a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          Term exarg1 = exterm.getArg(0);
          Term exarg2 = exterm.getArg(1);
          if (exarg2.getStartType() == VARIABLE) 
            continue;

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }

          exarg1 = Term.newOffset(exarg1, unif.getOffset());
          exarg2 = Term.newOffset(exarg2, unif.getOffset());
          Subst g = arg1.isUnifiable(exarg2);
          assert(g != null);
          unif.setSubst(g);

          // Creates the grand child negative equality predicate.
          exarg1 = exarg1.instantiate();
          Term gceq = Term.createPredicate(env, EQUAL_PRED, exarg1, arg2);
          Literal pgceq = new Literal(env, true, gceq);

          ops.add(new EqExtension(env, node, unif, arg1, arg2, exarg1, pgceq));            
        }
      }
      
    }
    //
    // SnM: Extension rules for positive equality.
    //
    else if (opt.getEqType() == CFP.EQ_SNM && lit.isPosEqualPred()) {

      Term term = lit.getTerm();
      Term arg1 = term.getArg(0);
      Term arg2 = term.getArg(1);
      Term newVar = Term.createVar(env, -1);
      Term eq1 = Term.createPredicate(env, EQUAL_PRED, arg1, newVar);
      Term eq2 = Term.createPredicate(env, EQUAL_PRED, newVar, arg1);
      Literal peq1 = new Literal(env, true, eq1);
      Literal peq2 = new Literal(env, true, eq2);

      // (SnM:PE1) Finds clauses which contains a complementary unifiable literal with peq1 (equal(arg1, newVar)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      List<Unifiable<PClause>> unifs = tableau.getClauseDB().getCompUnifiable(peq1);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  

          // Do not allow to extend the positive equality node with a node whose both arguments are variables.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          Term exarg1 = exterm.getArg(0);
          Term exarg2 = exterm.getArg(1);
          if (exarg1.getStartType() == VARIABLE || exarg2.getStartType() == VARIABLE) 
            continue;

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }
          
          exarg1 = Term.newOffset(exarg1, unif.getOffset());
          exarg2 = Term.newOffset(exarg2, unif.getOffset());
          Subst g = arg1.isUnifiable(exarg1);
          assert(g != null);
          unif.setSubst(g);

          // Creates the grand child negative equality predicate.
          exarg2 = exarg2.instantiate();
          Term gceq = Term.createPredicate(env, EQUAL_PRED, exarg2, arg2);
          Literal ngceq = new Literal(env, false, gceq);
          
          ops.add(new EqExtension(env, node, unif, arg1, arg2, exarg2, ngceq));
        }
      }      
      // (SnM:PE2) Finds clauses which contains a complementary unifiable literal with peq2 (equal(newVar, arg1)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getCompUnifiable(peq2);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the positive equality node with a node whose second argument is a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          Term exarg1 = exterm.getArg(0);
          Term exarg2 = exterm.getArg(1);
          if (exarg2.getStartType() == VARIABLE) 
            continue;

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }

          exarg1 = Term.newOffset(exarg1, unif.getOffset());
          exarg2 = Term.newOffset(exarg2, unif.getOffset());
          Subst g = arg1.isUnifiable(exarg2);
          assert(g != null);
          unif.setSubst(g);

          // Creates the grand child negative equality predicate.
          exarg1 = exarg1.instantiate();
          Term gceq = Term.createPredicate(env, EQUAL_PRED, exarg1, arg2);
          Literal ngceq = new Literal(env, false, gceq);

          ops.add(new EqExtension(env, node, unif, arg1, arg2, exarg1, ngceq));            
        }
      }
      // (SnM:PE3) Finds clauses which contains a complementary unifiable literal with lit.
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getCompUnifiable(lit);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the negative equality node with a node whose first argument is a variable or the second argument is not a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          int arg1stPos = exterm.getStart() + 1;
          int arg2ndPos = exterm.getNext(arg1stPos);
          if (exterm.getType(arg1stPos) == VARIABLE || exterm.getType(arg2ndPos) != VARIABLE)
            continue;
                       
          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }

          ops.add(new EqExtension(env, node, unif, arg1, arg2));
        }
      }
      // (SnM:PE4) Finds clauses which contains a (NOT COMPLEMENTARY) unifiable literal with peq1 (equal(arg1, newVar)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getUnifiable(peq1);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the positive equality node with a node whose first argument is a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          Term exarg1 = exterm.getArg(0);
          Term exarg2 = exterm.getArg(1);
          if (exarg1.getStartType() == VARIABLE)
            continue;

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }
          
          exarg1 = Term.newOffset(exarg1, unif.getOffset());
          exarg2 = Term.newOffset(exarg2, unif.getOffset());
          Subst g = arg1.isUnifiable(exarg1);
          assert(g != null);
          unif.setSubst(g);

          // Creates the grand child negative equality predicate.
          exarg2 = exarg2.instantiate();
          Term gceq = Term.createPredicate(env, EQUAL_PRED, exarg2, arg2);
          Literal pgceq = new Literal(env, true, gceq);
          
          ops.add(new EqExtension(env, node, unif, arg1, arg2, exarg2, pgceq));
        }
      }
      
      // (SnM:PE5) Finds clauses which contains a (NOT COMPLEMENTARY) unifiable literal with peq2 (equal(newVar, arg1)).
      tableau.stats().incTests(Stats.EQ_EXTENSION);
      unifs = tableau.getClauseDB().getUnifiable(peq2);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the positive equality node with a node whose second argument is a variable.
          PClause pclause = unif.getObject();
          Term exterm = pclause.getClause().get(pclause.getPos()).getTerm();
          Term exarg1 = exterm.getArg(0);
          Term exarg2 = exterm.getArg(1);
          if (exarg2.getStartType() == VARIABLE) 
            continue;

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }

          exarg1 = Term.newOffset(exarg1, unif.getOffset());
          exarg2 = Term.newOffset(exarg2, unif.getOffset());
          Subst g = arg1.isUnifiable(exarg2);
          assert(g != null);
          unif.setSubst(g);

          // Creates the grand child negative equality predicate.
          exarg1 = exarg1.instantiate();
          Term gceq = Term.createPredicate(env, EQUAL_PRED, exarg1, arg2);
          Literal pgceq = new Literal(env, true, gceq);

          ops.add(new EqExtension(env, node, unif, arg1, arg2, exarg1, pgceq));            
        }
      }            
    }
    else { 
      tableau.stats().incTests(Stats.EXTENSION);
      List<Unifiable<PClause>> unifs = tableau.getClauseDB().getCompUnifiable(lit);
      if (unifs != null) {
        for (int i=0; i < unifs.size(); i++) {
          Unifiable<PClause> unif = unifs.get(i);  
          
          // Do not allow to extend the positive equality node with a node whose first argument is variable.
          if (opt.getEqType() >= CFP.EQ_SMT) {
            if (lit.isPosEqualPred()) {
              PClause pclause = unif.getObject();
              Term term = pclause.getClause().get(pclause.getPos()).getTerm();
              int arg1stPos = term.getStart() + 1;
              if (term.getType(arg1stPos) == VARIABLE) 
                continue;
            }
          }     

          // Do not allow to extend the node with a clause whose ID is greater than the top clause's.
          if (opt.use(USE_INC_CARC_COMP) && tableau.getCFP().getProblemType() == CFP.CHARACTERISTIC) {
            int topID = tableau.getTopClause().getID();
            int curID = unif.getObject().getClause().getID();
            assert(topID != 0 && curID != 0);
            if (topID < curID)
              continue;
          }

          if (exhaustivenessChecking) {
            param.setExhaustiveness(false);
            node.markAsNotExhausted();
            return true;
          }
//          // Allow unit axioms
//          if (exhaustivenessChecking && !unif.getObject().getClause().isUnit())
//            continue;
            
          ops.add(new Extension(env, node, unif));
        }
      }
    }
    
//    if (exhaustivenessChecking) {
//      param.setExhaustiveness(false);
//      node.markAsNotExhausted();
//    }

    return true;
  }    
  
}

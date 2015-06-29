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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.nabelab.mining.DB;
import org.nabelab.mining.ItemSet;
import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.simp.TermIntMap;
import org.nabelab.solar.util.IntHashSet;
import org.nabelab.util.IntArrayList;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class CFPProfiler implements ExitStatus, TermTypes, OptionTypes {

  public static void main(String[] args) {
    try {
      Env env = new Env();
      Options opt = new Options(env);             // Create the default options.
      opt.parse(args);                            // Analyze the command line arguments.

      CFP cfp = new CFP(env, opt);
      cfp.parse(new File(opt.getProblemFile()), opt.getBaseDir());          // Load the consequence finding problem.
      
//      // Print out positive unit equalities.
//      for (Clause c : cfp.getClauses()) {
//        if (c.isUnit()) {
//          Literal l = c.get(0);
//          if (l.isPosEqualPred())
//            System.out.println(opt.getProblemFile() + ": " + c);
//        }
//      }
//      // Print out negative unit equalities.
//      for (Clause c : cfp.getClauses()) {
//        if (c.isUnit()) {
//          Literal l = c.get(0);
//          if (l.isNegEqualPred())
//            System.out.println(opt.getProblemFile() + ": " + c);
//        }
//      }
      
//      // Print out clauses that have one or more positive equalities.
//      for (Clause c : cfp.getClauses()) {
//        int numPosEq = 0;
//        for (Literal l : c.getLiterals()) 
//          if (l.isPosEqualPred())
//            numPosEq++;
//        if (numPosEq >= 1)
//          System.out.println(opt.getProblemFile() + ": " + c + "," + numPosEq);
//      }
      
//      // Print out top clauses that have one or more positive equalities. 
//      cfp.initTopClauses();
//      for (Clause c : cfp.getTopClauses()) {
//        int numPosEq = 0;
//        for (Literal l : c.getLiterals()) 
//          if (l.isPosEqualPred())
//            numPosEq++;
//        if (numPosEq >= 1)
//          System.out.println(opt.getProblemFile() + ": " + c + "," + numPosEq);
//      }

//      cfp.initTopClauses();
//      for (Clause c : cfp.getTopClauses()) 
//        if (c.isPositive())
//          System.out.println(opt.getProblemFile() + ": pos top clause " + c);
      
//      SymTable symTable = env.getSymTable();
//      System.out.format("%s,%d,clauses,%d,literals,%d,consts,%d,funcs,%d,pred\n", 
//          opt.getProblemFile(),
//          cfp.getNumClauses(),
//          cfp.getNumLiterals(),
//          symTable.getNumSyms(CONSTANT),
//          symTable.getNumSyms(FUNCTION),
//          symTable.getNumSyms(PREDICATE)
//      );
      

      /*
      StringWriter str = new StringWriter();
      PrintWriter  out = new PrintWriter(str);
      
      int orgNumClauses  = cfp.getNumClauses();
      int orgNumLiterals = cfp.getNumLiterals();
      
      cfp.initFVecMap();
      if (opt.use(USE_CLAUSE_SUBSUMP_MINIZING))
        cfp.convertToSubsumpMinimal(out);

      int minNumClauses  = cfp.getNumClauses();
      int minNumLiterals = cfp.getNumLiterals();
      
      int modNumClauses  = 0;
      int modNumLiterals = 0;
      int minModNumClauses  = 0;
      int minModNumLiterals = 0;

      boolean addedEqRef = false;
      if (opt.getEqType() != CFP.EQ_AXIOMS_REQUIRED && cfp.useEquality()) {
        
        addedEqRef = cfp.addEqReflexivity();  // For checking the unit subsumption etc..
        
        cfp.convertToNoEqualityFormat(out);

        modNumClauses  = cfp.getNumClauses();
        modNumLiterals = cfp.getNumLiterals();

        cfp.initFVecMap();
        if (opt.use(USE_CLAUSE_SUBSUMP_MINIZING)) 
          cfp.convertToSubsumpMinimal(out);
        
        minModNumClauses  = cfp.getNumClauses();
        minModNumLiterals = cfp.getNumLiterals();
        
      }
      
      cfp.initTopClauses();
      
      if (addedEqRef)
        cfp.removeEqReflexivity();
      
      System.out.format("%s,%d,org clauses,%d,org literals,%d,min clauses,%d,min literals,%d,mod clauses,%d,mod literals,%d,min mod clauses,%d,min mod literals\n", 
          opt.getProblemFile(),
          orgNumClauses, orgNumLiterals,
          minNumClauses, minNumLiterals,
          modNumClauses, modNumLiterals,
          minModNumClauses, minModNumLiterals);
      */
      
      /*
      ClauseDB clauseDB = new ClauseDB(env, opt, cfp.getClauses());
      VarTable varTable = env.getVarTable();
      for (Clause c : cfp) {
        varTable.addVars(c.getNumVars());
        for (Literal l : c) {
          if (clauseDB.getCompUnifiable(l) == null) {
            System.out.format("%s,%s,%s\n", opt.getProblemFile(), c,l);
            break;
          }
        }
        varTable.removeAllVars();
      }
      */
      
      /*
      for (Clause c : cfp)
        if (c.isPredMonoAxiom())
          System.out.format("PredMono: %s,%s\n", opt.getProblemFile(), c);
      
      for (Clause c : cfp)
        if (c.isFuncMonoAxiom())
          System.out.format("FuncMono: %s,%s\n", opt.getProblemFile(), c);
       */
      
      /*
      SymTable symTable = env.getSymTable();
      boolean pocc[] = new boolean[symTable.getNumSyms(PREDICATE)];
      boolean nocc[] = new boolean[symTable.getNumSyms(PREDICATE)];
      int eqName = symTable.getEqualPredName();
      if (eqName >= 0)
        pocc[eqName] = nocc[eqName] = true;
      for (Clause c : cfp) {
        for (Literal l : c) {
          int name = l.getName();
          if (l.isPositive())
            pocc[name] = true;
          else
            nocc[name] = true;
        }
      }
      IntHashSet pures = new IntHashSet();
      for (int i=0; i < pocc.length; i++)
        if (pocc[i] != nocc[i])
          pures.add(i);
      for (Clause c : cfp) {
        for (Literal l : c) {
          if (pures.contains(l.getName())) {
            System.out.format("%s,%s,%s\n", opt.getProblemFile(), c,l);
            break;
          }
        }      
      }
      */
      
      /*
      // Translates each term into an integer.
      TermIntMap map = new TermIntMap();
      DB db = new DB();
      for (Clause c : cfp) {
        ItemSet items = new ItemSet();
        for (Literal l : c) {
          int id = map.put(l.getTerm());
          if (l.isNegative()) 
            id = -id;
          items.add(id);
        }
        items.sort();
        db.add(items);
      }
      System.out.println(db);
      */
      
      int numUnitPos = 0;
      int numNonUnitPos = 0;
      int numUnitNeg = 0;
      int numNonUnitNeg = 0;
      for (Clause c : cfp) 
        if (c.isPositive()) 
          if (c.isUnit())
            numUnitPos++;
          else
            numNonUnitPos++;
        else if (c.isNegative())
          if (c.isUnit())
            numUnitNeg++;
          else
            numNonUnitNeg++;
      System.out.format("%s,unit-pos,%d,non-unit-pos,%d,unit-neg,%d,non-unit-neg,%d\n", opt.getProblemFile(), numUnitPos, numNonUnitPos, numUnitNeg, numNonUnitNeg);
      
    } catch (IllegalArgumentException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(OPTION_ERROR);
    } catch (FileNotFoundException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(FILE_NOT_FOUND);
    } catch (ParseException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(PARSE_ERROR);
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace(System.err);
      System.exit(UNKNOWN_ERROR);      
    }
  }

}

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

import java.util.Collections;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Options;
import org.nabelab.solar.Signature;
import org.nabelab.solar.SymTable;
import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;

/**
 * @author nabesima
 *
 */
public class WeightMap implements TermTypes {
  
  /**
   * Constructs a weight assignment.
   * @param env the environment.
   * @param opt the options.
   * @param clauses the set of clauses.
   */
  public WeightMap(Env env, Options opt, List<Clause> clauses) {
    this.env = env;
    this.opt = opt;
    this.symTable = env.getSymTable();
    
    // Initializes the weight of constants and functions.
    cweight = new int[symTable.getNumSyms(CONSTANT)];
    fweight  = new int[symTable.getNumSyms(FUNCTION)];
   
    switch (opt.getTermWeightFunc()) {
    case UNIFORM_FUNC:
      {
        for (int i=0; i < cweight.length; i++)
          cweight[i] = 1;
        for (int i=0; i < fweight.length; i++)
          fweight[i] = 1;
      }
      break;
      
    case LEX_ORDER_FUNC: 
      {
        List<Signature> consts = symTable.getConstants();
        List<Signature> funcs = symTable.getFunctions();

        // Sorts the symbols.        
        Collections.sort(consts, new LexOrder());
        Collections.sort(funcs, new LexOrder());

        int weight = 1;
        for (Signature sig : consts) 
          cweight[sig.getID()] = weight++;
        for (Signature sig : funcs) 
          fweight[sig.getID()] = weight++;

//        int weight = 1;
//        for (Signature sig : consts) { 
//          cweight[sig.getID()] = weight * weight;
//          weight++;
//        }
//        for (Signature sig : funcs) { 
//          fweight[sig.getID()] = weight * weight;
//          weight++;
//        }
      }
      break;

    case OCC_ORDER_FUNC: 
    {
      List<Signature> consts = symTable.getConstants();
      List<Signature> funcs = symTable.getFunctions();
      
      // Counts up the frequencies of all symbols.
      TermFreqTable freqTable = new TermFreqTable(symTable);
      freqTable.count(clauses);
      
      // Sorts the symbols.
      Collections.sort(consts, new OccOrder(freqTable));
      Collections.sort(funcs, new OccOrder(freqTable));

      int weight = 1;
      for (Signature sig : consts) 
        cweight[sig.getID()] = weight++;
      for (Signature sig : funcs) 
        fweight[sig.getID()] = weight++;
    }
    break;

    case ROCC_ORDER_FUNC: 
    {
      List<Signature> consts = symTable.getConstants();
      List<Signature> funcs = symTable.getFunctions();
      
      // Counts up the frequencies of all symbols.
      TermFreqTable freqTable = new TermFreqTable(symTable);
      freqTable.count(clauses);
      
      // Sorts the symbols.
      Collections.sort(consts, new ROccOrder(freqTable));
      Collections.sort(funcs, new ROccOrder(freqTable));

      int weight = 1;
      for (Signature sig : consts) 
        cweight[sig.getID()] = weight++;
      for (Signature sig : funcs) 
        fweight[sig.getID()] = weight++;
    }
    break;
    
    default:
      assert(false);
    }
    
    // Finds the symbol of the minimum order.
    minWeight = Integer.MAX_VALUE;
    minSignature = null;
    for (Signature sig : symTable.getConstants()) {
      if (cweight[sig.getID()] < minWeight) {
        minWeight = cweight[sig.getID()];
        minSignature = sig;
      }
    }
    for (Signature sig : symTable.getFunctions()) {
      if (fweight[sig.getID()] < minWeight) {
        minWeight = fweight[sig.getID()];
        minSignature = sig;
      }
    }
  }

  /**
   * Returns the weight of the specified symbol.
   * @param name the name of the symbol.
   * @param type the type of the symbol.
   * @return the weight of the symbol.
   */
  public int get(int name, int type) {
    switch (type) {
    case VARIABLE:
      assert(false);
      
    case CONSTANT:
      return cweight[name];
      
    case INTEGER:
      return Math.abs(name);
      
    case FUNCTION:
      return fweight[name];      
      
    default:
      assert(false);
    }
    
    return 0;
  }
  
  /**
   * Returns the minimum weight.
   * @return
   */
  public int getMinWeight() {
    return minWeight;
  }
  
  /**
   * Returns true if the specified term is minimum in the symbol weight ordering.
   * @param term  a term to be checked.
   * @return true if the specified term is minimum in the symbol weight ordering.
   */
  public boolean isMin(Term term) {
    int name  = term.getStartName();
    int type  = term.getStartType();
    return minSignature.getID() == name && minSignature.getType() == type;
  }
  
  /**
   * Returns true if the specified term is minimum in the symbol weight ordering.
   * @param term  a term to be checked.
   * @return true if the specified term is minimum in the symbol weight ordering.
   */
  public boolean isMin(int name, int type) {
    return minSignature.getID() == name && minSignature.getType() == type;
  }
  
  // An indicator that denotes an element is not initialized.
  private static final int NOT_INITIALIZED = -1;
  
  /** Uniform term weight. */
  public final static int UNIFORM_FUNC = 0;
  /** Lexicographic weight ordering of symbol names. */
  public final static int LEX_ORDER_FUNC = 1;
  /** Occurrences based ordering for symbols. */
  public final static int OCC_ORDER_FUNC = 2;
  /** Occurrences based reverse ordering for symbols. */
  public final static int ROCC_ORDER_FUNC = 3;
  
  /** The environment. */
  @SuppressWarnings("unused")
  private Env env= null;
  /** The options. */
  @SuppressWarnings("unused")
  private Options opt = null;
  /** The symbol table. */
  private SymTable symTable = null;
  
  /** The weight of constant symbols. */
  private int[] cweight = null;
  /** The weight of functional symbols. */
  private int[] fweight = null;
  /** The minimum weight. */
  private int minWeight = NOT_INITIALIZED;
  /** The signature which has the minimum weight. */
  private Signature minSignature = null;
  
}

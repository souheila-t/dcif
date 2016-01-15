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

package org.nabelab.solar.indexing;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.DebugTypes;
import org.nabelab.solar.Env;
import org.nabelab.solar.PLiteral;
import org.nabelab.solar.SymTable;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.pfield.PField;

/**
 * @author nabesima
 *
 */
public class FVecMap implements TermTypes, DebugTypes {

  /**
   * Constructs a mapping from the features to the indexes.
   * @param env      the environment.
   * @param symTable the symbol table.
   * @param samples  the sample clauses.
   * @param pfield   the production field (a part of samples).
   */
  public FVecMap(Env env, SymTable symTable, List<Clause> samples, PField pfield) {
    this.env = env;
    
    // Initializes the offsets of types. 
    offset = new int[symTable.getNumTypes()];
    offset[VARIABLE ] = NUM_SYMS_OCC;
    offset[CONSTANT ] = offset[VARIABLE];
    offset[INTEGER  ] = offset[CONSTANT] + symTable.getNumSyms(CONSTANT) * 2;
    offset[FUNCTION ] = offset[INTEGER ] + 2 * 2;  // classifies odd and even numbers
    offset[PREDICATE] = offset[FUNCTION] + symTable.getNumSyms(FUNCTION) * 2; 
    numRawFeatures = offset[PREDICATE] + symTable.getNumSyms(PREDICATE) * 2;
    
    // Initializes the mapping from a raw index to the string representation.
    symbols = new String[numRawFeatures];
    symbols[POS_SYMS_OCC] = "pos";
    symbols[NEG_SYMS_OCC] = "neg";
    for (int i=0; i < symTable.getNumSyms(CONSTANT); i++) {
      int idx = offset[CONSTANT] + (i << 1);
      String str = symTable.get(i, CONSTANT);
      symbols[idx + 0] = "+" + str;
      symbols[idx + 1] = "-" + str;
    }
    symbols[offset[INTEGER] + 0] = "+even";
    symbols[offset[INTEGER] + 1] = "-even";
    symbols[offset[INTEGER] + 2] = "+odd";
    symbols[offset[INTEGER] + 3] = "-odd";
    for (int i=0; i < symTable.getNumSyms(FUNCTION); i++) {
      int idx = offset[FUNCTION] + (i << 1);
      String str = symTable.get(i, FUNCTION) + "/" + symTable.getArity(i, FUNCTION);
      symbols[idx + 0] = "+" + str;
      symbols[idx + 1] = "-" + str;
    }
    for (int i=0; i < symTable.getNumSyms(PREDICATE); i++) {
      int idx = offset[PREDICATE] + (i << 1);
      String str = symTable.get(i, PREDICATE) + "/" + symTable.getArity(i, PREDICATE);
      symbols[idx + 0] = "+" + str;
      symbols[idx + 1] = "-" + str;
    }
    
    if (samples == null) 
      return;
    
    // Counts the kinds of occurrences for each feature.
    Usage[] usages = new Usage[numRawFeatures];
    for (int i=0; i < usages.length; i++)
      usages[i] = new Usage(i);
    
    int raw[] = new int[numRawFeatures];
    for (Clause sample : samples) {
      sample.getRawFVec(raw);
      for (int i=0; i < numRawFeatures; i++) {
        usages[i].addOcc(raw[i]);
        raw[i] = 0;  // clear
      }
    }
    // Features which are belongs to a production field, are important.
    if (pfield != null && !pfield.isEmpty()) {
      List<PLiteral> plits = pfield.getPLiterals();
      for (PLiteral plit : plits) {
        if (plit.isSpecial()) {
          switch (plit.getSign()) {
          case PLiteral.POS:
            for (int i=0; i < usages.length; i+=2)
              usages[i].incImp();
            break;        
          case PLiteral.NEG:
            for (int i=1; i < usages.length; i+=2)
              usages[i].incImp();
            break;        
          case PLiteral.BOTH:
            // Do nothing.
            break;
          default:
            assert(false);
          }
        }
        else {
          int name = plit.getTerm().getStartName();
          switch (plit.getSign()) {
          case PLiteral.POS:  
            usages[getRawIdx(name, PREDICATE, true )].incImp();  
            for (int i=0; i < offset[PREDICATE]; i+=2)
              usages[i].incImp();
            break;
          case PLiteral.NEG:  
            usages[getRawIdx(name, PREDICATE, false)].incImp();  
            for (int i=1; i < offset[PREDICATE]; i+=2)
              usages[i].incImp();
            break;
          case PLiteral.BOTH:
            usages[getRawIdx(name, PREDICATE, true )].incImp(); 
            usages[getRawIdx(name, PREDICATE, false)].incImp(); 
            for (int i=0; i < offset[PREDICATE]; i+=1)
              usages[i].incImp();
            break;
          default:
            assert(false);
          }
        }
      }
    }
    
    Arrays.sort(usages);

    if (env.dbg(DBG_FEATURE_VECTOR)) {
      System.out.println("[Feature vector indexing]");
      for (int i=0; i < usages.length; i++) {
        Usage u = usages[i];
        System.out.println(" " + symbols[u.getFeature()] + " : " + u.getImportance() + "/" + u.getCardinality());
      }
    }
    
    // If the task is consequence finding, then use all features.
    if (pfield != null && !pfield.isEmpty()) {
      numFeatures = usages.length;
    }
    else {
      // Counts the features that occur two or more times.
      numFeatures = 0;
      while (numFeatures < usages.length) {
        if (usages[numFeatures].getCardinality() < 2)
          break;
        numFeatures++;
      }
    
      // Ensures at least one feature exists 
      if (numFeatures == 0)
        numFeatures = 1;
    }
    
    // Is there an unchecked predicate?
    hasUncheckedPredOcc = false;
    if (numFeatures > MAX_NUM_FEATURES) {
      for (int i=MAX_NUM_FEATURES; i < numFeatures; i++) {
        if (offset[PREDICATE] <= usages[i].getFeature()) {
          hasUncheckedPredOcc = true;
          break;
        }
      }
      numFeatures = MAX_NUM_FEATURES;
    }
    
    // Chooses the features used in a feature vector trie.
    forder = new int[numRawFeatures];
    Arrays.fill(forder, -1);
    for (int i=0; i < numFeatures; i++) 
      forder[usages[i].getFeature()] = i;
    
    initialized = true;
  }
  
  /**
   * Returns the raw feature index of the specified sign.
   * @param positive the sign.
   * @return the raw feature index of the specified sign.
   */
  public int getRawIdx(boolean positive) {
    return positive ? POS_SYMS_OCC : NEG_SYMS_OCC;
  }
  
  /**
   * Returns the raw feature index of the specified symbol.
   * @param name     the name of the symbol.
   * @param type     the type of the symbol. 
   * @param positive the sign of the symbol.
   * @return the raw feature index of the specified symbol.
   */  
  public int getRawIdx(int name, int type, boolean positive) {
     assert(type != VARIABLE);
     if (type == INTEGER)
       name = name & 0x1;
     return offset[type] + (name << 1) + (positive ? 0 : 1);
  }
  
  /**
   * Returns true if there is an unchecked predicate. 
   * @return true if there is an unchecked predicate.
   */
  public boolean hasUncheckedPredOcc() {
    return hasUncheckedPredOcc;
  }

 /**
   * Returns the feature index of the specified sign.
   * @param positive the sign.
   * @return the feature index of the specified sign.
   */
  public int getIdx(boolean positive) {
    assert(initialized);
    return forder[positive ? POS_SYMS_OCC : NEG_SYMS_OCC];
  }

  /**
   * Returns the feature index of the specified symbol.
   * @param name     the name of the symbol.
   * @param type     the type of the symbol. 
   * @param positive the sign of the symbol.
   * @return the feature index of the specified symbol.
   */
  public int getIdx(int name, int type, boolean positive) {
    assert(initialized);
    assert(type != VARIABLE);
    if (type == INTEGER)
      name = name & 0x1;
    return forder[offset[type] + (name << 1) + (positive ? 0 : 1)];
  }
  
  /**
   * Returns the number of the features.
   * @return the number of the features.
   */
  public int getNumFeatures() {
    assert(initialized);
    return numFeatures;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    String str = "";
    for (int i=0; i < offset.length; i++) 
      str += String.format("offset[%d] = %d\n", i, offset[i]);
    return str;
  }
  
  /** The positive symbol occurrence. */
  private final static int POS_SYMS_OCC = 0;
  /** The negative symbol occurrence. */
  private final static int NEG_SYMS_OCC = 1;
  /** The number of symbol occurrences. */
  private final static int NUM_SYMS_OCC = 2;
  /** The maximum number of features */
  private final static int MAX_NUM_FEATURES = 75;
  
  /** The environment. */
  @SuppressWarnings("unused")
  private Env env = null;
  /** The offsets of symbol-types. */
  private int[] offset = null;
  /** The number of raw features. */
  private int numRawFeatures = 0;
  /** The mapping from the raw index to the string representation. */
  private String[] symbols = null;
  /** Whether the mapping is initialized or not. */
  private boolean initialized = false;
  /** The number of features. */
  private int numFeatures = 0;
  /** Whether there is an unchecked predicate or not. */
  private boolean hasUncheckedPredOcc = true;
  /** The order of features. */
  private int[] forder = null;

  /** A class for counting the kinds of occurrence of this feature. */
  private final class Usage implements Comparable<Usage> {
    public Usage(int feature) {
      this.feature = feature;
      this.occurs  = new BitSet();
    }
    public void addOcc(int value) {
      occurs.set(value);
    }
    public void incImp() {
      importance++;
    }
    public int getFeature() {
      return feature;
    }
    public int getImportance() {
      return importance;
    }
    public int getCardinality() {
      return occurs.cardinality();
    }
    public int compareTo(Usage obj) {
      if (importance != obj.importance)
        return obj.importance - importance;
      if (obj.occurs.cardinality() == occurs.cardinality())
        return obj.feature - feature; 
      return obj.occurs.cardinality() - occurs.cardinality();
    }
    public String toString() {
      return symbols[feature] + " " + occurs + " (" + occurs.cardinality() + ")";
    }
    private int feature = 0;
    private BitSet occurs = null;
    private int importance = 0;
  }

}

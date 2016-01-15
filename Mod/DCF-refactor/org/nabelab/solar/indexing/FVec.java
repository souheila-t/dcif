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

/**
 * Feature vector class. 
 * @author nabesima
  */
public class FVec {

  /**
   * Constructs a empty feature vector. 
   * @param map the mapping from the features to the indexes.
   */
  public FVec(FVecMap map) {
    this.map = map;
    element = new int[map.getNumFeatures()]; 
  }
  
  /**
   * Increases the number of occurrences of the specified symbol.
   * @param name     the name of the symbol.
   * @param type     the type of the symbol.
   * @param positive the sign of the symbol.
   */
  public void inc(int name, int type, boolean positive) {
    int idx = map.getIdx(name, type, positive);
    if (idx != -1)
      element[idx]++;
    numSymbols++;
  }
  
  /**
   * Increases the number of occurrences of the specified sign.
   * @param positive the specified sign.
   */
  public void inc(boolean positive) {
    int idx = map.getIdx(positive);
    if (idx != -1)
      element[idx]++;
  }

  /**
   * Returns the number of occurrences of the specified index.
   * @param index the specified index.
   * @return the number of occurrences of the specified index.
   */
  public int get(int index) {
    return element[index];
  }
  
  /**
   * Returns the total number of occurrences of symbols in this vector.
   * @return the total number of occurrences of symbols in this vector.
   */
  public int getNumSymbols() {
    return numSymbols;
  }
  
  /**
   * Sets the maximum variable name in this vector.
   * @param var the variable name.
   */
  public void setMaxVarName(int var) {
    if (var > maxVarName)
      maxVarName = var;
  }
  
  /**
   * Returns the maximum variable name in this vector.
   * @return the maximum variable name in this vector.
   */
  public int getMaxVarName() {
    return maxVarName;
  }
  
  /**
   * Returns the size of this vector.
   * @return the size of this vector.
   */
  public int size() {
    return element.length;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return Arrays.toString(element);
  }

  /** The mapping from the features to the indexes. */
  private FVecMap map = null;
  /** The elements of this vector. */
  private int[] element = null;
  /** The maximum variable name in this vector. */
  private int maxVarName = 0;
  /** The total number of occurrences of symbols in this vector. */
  private int numSymbols = 0;
  
}

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

/**
 * @author nabesima
 *
 */
public class SearchParam {
  
  /**
   * Constructs a search parameter.
   * @param strategy      the type of search strategy to use.
   * @param lengthening   the type of consequence iterative lengthening search strategy to use.
   * @param maxLenConseqs the maximum length of consequences.
   * @param maxNumConseqs the maximum number of consequences.
   * @param timeLimit     the time limit in milliseconds.
   * @param elapsedTime   the elapsed time in milliseconds.
   * @param maxNumInfs    the maximum number of inferences.
   */
  public SearchParam(int strategy, int lengthening, int depthLimit,
      int maxLenConseqs, int maxNumConseqs, long timeLimit, long elapsedTime, long maxNumInfs) {
    this.strategy      = strategy;
    this.lengthening   = lengthening;
    this.depthLimit    = depthLimit;
    this.maxLenConseqs = maxLenConseqs;
    this.maxNumConseqs = maxNumConseqs;
    this.timeLimit     = timeLimit;
    this.elapsedTime   = elapsedTime;
    this.maxNumInfs    = maxNumInfs;
  }
  
  /**
   * Returns the depth limit.
   * @return the depth limit.
   */
  public int getDepthLimit() {
    return depthLimit;
  }
  
  /**
   * Returns the maximum length of consequences.
   * @return the maximum length of consequences.
   */
  public int getMaxLenConseqs() {
    return maxLenConseqs;
  }

  /**
   * Returns the maximum number of consequences.
   * @return the maximum number of consequences.
   */
  public int getMaxNumConseqs() {
    return maxNumConseqs;
  }
  
  /**
   * Returns the time limit.
   * @return the time limit.
   */
  public long getTimeLimit() {
    return timeLimit;
  }
  
  /**
   * Returns the maximum number of inferences.
   * @return the maximum number of inferences.
   */
  public long getMaxNumInfs() {
    return maxNumInfs;
  }
  
  /**
   * Returns true if the search is exhaustive.
   * @return true if the search is exhaustive.
   */
  public boolean getExhaustiveness() {
    return exhaustive;
  }
  
  /**
   * Sets the exhaustiveness of the search.
   * @param val the exhaustiveness of the search.
   */
  public void setExhaustiveness(boolean val) {
    exhaustive = val;
  }
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object.
   */
  public String toString() {
    // 123: (dep:1 len:1 num:10 sec:12.3)
    String str = "";
    if (depthLimit == 0)
      str += " dep:-";
    else
      str += " dep:" + depthLimit;
    if (maxLenConseqs == -1)
      str += " len:-";
    else if (maxLenConseqs != 0 || lengthening != Strategy.CIL_NONE)
      str += " len:" + maxLenConseqs;
    if (strategy == Strategy.DFIDR) {
      if (maxNumConseqs != 0)
        str += " num:" + maxNumConseqs;
      if (timeLimit != 0) {
        double time = ((timeLimit - elapsedTime) / 100) / 10.0;
        str += " sec:" + time;
      }
    }
    
    return str.trim();
  }  

  /** Type of search strategy. */  
  private int strategy = Strategy.DFID;
  /** Whether uses consequence iterative lengthening search strategy or not. */
  private int lengthening = Strategy.CIL_NONE;
  
  /** The depth limit. */
  private int depthLimit = 0;
  /** The maximum length limit. */
  private int maxLenConseqs = 0;
  /** The maximum number of consequences. */
  private int maxNumConseqs = 0;
  /** The time limit. */
  private long timeLimit = 0;  
  /** The elapsed time. */
  private long elapsedTime = 0;  
  /** The maximum number of inferences. */
  private long maxNumInfs = 0;
  /** Is the search is exhaustive? */
  private boolean exhaustive = true;
}

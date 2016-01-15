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

/**
 * @author nabesima
 *
 */
public class NEFInfo {
  
  /**
   * Constructs a NEF information.
   * @param orgArgIdx the index of the argument of the original negative equality, that has a non-variable term will be replaced.
   * @param subArgIdx the index of the argument of argIdx, that will be replaced.
   */
  public NEFInfo(int orgArgIdx, int subArgIdx) {
    this.orgArgIdx = orgArgIdx;
    this.subArgIdx = subArgIdx;
  }

  /**
   * Returns the index of the argument of the original negative equality, that has a non-variable term will be replaced.
   * @return the index of the argument of the original negative equality, that has a non-variable term will be replaced.
   */
  public int getOrgArgIdx() {
    return orgArgIdx;
  }

  /**
   * Returns the index of the argument of argIdx, that will be replaced.
   * @return the index of the argument of argIdx, that will be replaced.
   */
  public int getSubArgIdx() {
    return subArgIdx;
  }
  
  /** The index of the argument of the original negative equality, that has a non-variable term will be replaced. */
  private int orgArgIdx;
  /** The index of the argument of argIdx, that will be replaced. */
  private int subArgIdx;  
}

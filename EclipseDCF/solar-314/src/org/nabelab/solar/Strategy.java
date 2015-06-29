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

import org.nabelab.solar.pfield.PField;

/**
 * @author nabesima
 *
 */
public class Strategy implements OptionTypes, ExitStatus {

  /**
   * Constructs a empty production field.
   * @param cfp the consequence finding problem to which this belongs.
   */
  public Strategy(CFP cfp) {
    this.cfp = cfp;
    this.opt = cfp.getOptions();
  }
  
  /**
   * Returns the next search parameter.
   * @param stats        the statistics information.
   * @param elapsedTime  the elapsed time in milliseconds from the solving.
   * @param numUsedSkips the maximum number of skipped nodes in the previous stage.
   * @param prev         the previous parameter.
   * @return the next search parameter.
   */  
  public SearchParam getNextSearchParam(Stats stats, long elapsedTime, int numUsedSkips, SearchParam prev) {

    // If solved, then finish.
    if (cfp.getStatus() != UNKNOWN)
      return null;    

    // The first stage.
    if (prev == null) {
      
      // The original search parameters.
      orgDepthLimit    = opt.getDepthLimit();
      orgMaxLenConseqs = cfp.getPField().getMaxLength();
      orgMaxNumConseqs = opt.getMaxNumConseqs();
      orgTimeLimit     = opt.getTimeLimit();
      orgMaxNumInfs    = opt.getMaxNumInfs();

      // Makes the first search parameter. 
      curDepthLimit    = orgDepthLimit;
      curMaxLenConseqs = orgMaxLenConseqs;
      curMaxNumConseqs = orgMaxNumConseqs;
      curTimeLimit     = orgTimeLimit;
      
      if (opt.getStrategy() == DF) {
        // The maximum length limit starts from 0.
        if (opt.getLengthening() != CIL_NONE)
          curMaxLenConseqs = 0;
      }
      else if (opt.getStrategy() == DFID) {
        // The depth limit starts from 1.
        curDepthLimit = 1;
        // The maximum length limit starts from 0.
        if (opt.getLengthening() != CIL_NONE)
          curMaxLenConseqs = 0;
      }
      else if (opt.getStrategy() == DFIDR) {
        // The depth limit starts from 1.
        curDepthLimit = 1;
        // The maximum length limit starts from 0.
        if (opt.getLengthening() != CIL_NONE) 
          curMaxLenConseqs = 0;
        // Calculates the number of stages.
        numStages = orgDepthLimit;
        if (opt.getLengthening() != CIL_NONE)
          numStages *= ((0 < orgMaxLenConseqs) ? orgMaxLenConseqs : 1);
        curMaxNumConseqs = orgMaxNumConseqs / numStages;
        curTimeLimit     = orgTimeLimit     / numStages;
        
        // Ensures that the strategy tries to find out one consequence at least.
        if (orgMaxNumConseqs != 0 && curMaxNumConseqs == 0)
          curMaxNumConseqs = 1;
        // If no time, then halts the process.
        if (orgTimeLimit != 0 && curTimeLimit == 0)
          return null;
      }
        
      return new SearchParam(
          opt.getStrategy(), opt.getLengthening(),
          curDepthLimit, curMaxLenConseqs, curMaxNumConseqs, curTimeLimit, elapsedTime, orgMaxNumInfs);
    }

    if (opt.getLengthening() != CIL_NONE && (prev.getMaxLenConseqs() == 0 || prev.getMaxLenConseqs() == numUsedSkips))
      prev.setExhaustiveness(false);
    
    // If the previous search finished exhaustively, then it is satisfiable.
    if (prev.getExhaustiveness()) { 
      cfp.setStatus(SATISFIABLE);
      return null;
    }
    
    // Checks the running time limit.
    if (orgTimeLimit != 0 && elapsedTime >= orgTimeLimit)
      return null;
    // Checks the limitation of the found consequences.
    if (orgMaxNumConseqs != 0 && cfp.getConseqSet().size() >= orgMaxNumConseqs)
      return null;
    // Checks the inference limit.
    if (orgMaxNumInfs != 0 && stats.inf() >= orgMaxNumInfs)
      return null;
    
    switch (opt.getStrategy()) {
    case DF:
      switch (opt.getLengthening()) {
      case CIL_NONE:
        return null;
      case CIL_SLAVE:
      case CIL_MASTER:
        if (curMaxLenConseqs == orgMaxLenConseqs)
          return null;
        if (numUsedSkips < curMaxLenConseqs) 
          return null;  
        curMaxLenConseqs++;
        break;
      }  
      break;
      
    case DFID:
      switch (opt.getLengthening()) {
      case CIL_NONE:
        if (curDepthLimit == orgDepthLimit)
          return null;
        curDepthLimit++;
        break;
      case CIL_SLAVE:
        if ((orgMaxLenConseqs == PField.UNLIMITED || curMaxLenConseqs < orgMaxLenConseqs) && numUsedSkips == curMaxLenConseqs)
          curMaxLenConseqs++;
        else if (orgDepthLimit == 0 || curDepthLimit < orgDepthLimit) {
          curDepthLimit++;
          curMaxLenConseqs = 0;
        }
        else
          return null;
        break;
      case CIL_MASTER:
        if (orgDepthLimit == 0 || curDepthLimit < orgDepthLimit)
          curDepthLimit++;
        else if ((orgMaxLenConseqs == PField.UNLIMITED || curMaxLenConseqs < orgMaxLenConseqs) && numUsedSkips == curMaxLenConseqs) {
          curMaxLenConseqs++;
          curDepthLimit = 1;
        }
        else
          return null;
        break;
      default:
        assert(false);
      }
      break;
      
    case DFIDR:
      switch (opt.getLengthening()) {
      case CIL_NONE:
        if (curDepthLimit == orgDepthLimit)
          return null;
        curDepthLimit++;
        numStages--;
        break;
      case CIL_SLAVE:
        if ((orgMaxLenConseqs == PField.UNLIMITED || curMaxLenConseqs < orgMaxLenConseqs) && numUsedSkips == curMaxLenConseqs) {
          curMaxLenConseqs++;
          numStages--;
        }
        else if (curDepthLimit < orgDepthLimit) {
          curDepthLimit++;
          curMaxLenConseqs = 0;
          numStages -= orgMaxLenConseqs - curMaxLenConseqs - 1;
        }
        else
          return null;
        break;
      case CIL_MASTER:
        if (curDepthLimit < orgDepthLimit) {
          curDepthLimit++;
          numStages--;
        }
        else if ((orgMaxLenConseqs == PField.UNLIMITED || curMaxLenConseqs < orgMaxLenConseqs) && numUsedSkips == curMaxLenConseqs) {
          curMaxLenConseqs++;
          curDepthLimit = 1;
          numStages--;
        }
        else
          return null;
        break;
      default:
        assert(false);
      }
      if (orgMaxNumConseqs != 0) {
        curMaxNumConseqs = (orgMaxNumConseqs - cfp.getConseqSet().size()) / numStages;
        // Ensures that the strategy tries to find out one consequence at least.
        if (curMaxNumConseqs <= 0) curMaxNumConseqs = 1;
        curMaxNumConseqs += cfp.getConseqSet().size();
      }
      if (orgTimeLimit != 0) {
        curTimeLimit = (orgTimeLimit - elapsedTime) / numStages + elapsedTime;
        // If no time, then halts the process.
        if (curTimeLimit <= elapsedTime) 
          return null;
      }
      break;
      
    default:
      assert(false);
    }  

    return new SearchParam(
        opt.getStrategy(), opt.getLengthening(),
        curDepthLimit, curMaxLenConseqs, curMaxNumConseqs, curTimeLimit, elapsedTime, orgMaxNumInfs);
  }
  
  /** Depth first search strategy */ 
  public final static int DF = 0;
  /** Depth first iterative deepening search strategy */ 
  public final static int DFID = 1;
  /** Depth first iterative deepening with resource redistribution search strategy */ 
  public final static int DFIDR = 2;

  /** Do not use consequence iterative lengthening (CIL) search */
  public final static int CIL_NONE = 0;
  /** Runs CIL at each iteration of DF(ID,IDR) */
  public final static int CIL_SLAVE = 1;
  /** Runs DF(ID,IDR) at each iteration of CIL */
  public final static int CIL_MASTER = 2;
  
  /** The consequence finding problem. */
  private CFP cfp = null;

  /** The original depth limit. */
  private int orgDepthLimit = 0;
  /** The original length limit. */
  private int orgMaxLenConseqs = 0;
  /** The original maximum number of consequences. */
  private int orgMaxNumConseqs = 0;
  /** The original time limit. */
  private long orgTimeLimit = 0;
  /** The original maximum number of inferences. */
  private long orgMaxNumInfs = 0;
  /** The number of stages */
  private int numStages = 0;
  
  /** The current depth limit. */
  private int curDepthLimit = 0;
  /** The current length limit. */
  private int curMaxLenConseqs = 0;
  /** The current maximum number of consequences. */
  private int curMaxNumConseqs = 0;
  /** The current time limit. */
  private long curTimeLimit = 0;
  
  
  
  /** The options */
  private Options opt = null;

}

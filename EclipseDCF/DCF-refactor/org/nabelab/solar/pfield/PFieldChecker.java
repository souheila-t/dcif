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

package org.nabelab.solar.pfield;

import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.OptionTypes;
import org.nabelab.solar.Options;
import org.nabelab.solar.Unifiable;

/**
 * @author nabesima
 *
 */
public abstract class PFieldChecker implements OptionTypes {
  
  /**
   * Constructs a production field checker.
   * @param env a consequence finding problem.
   * @param pfield a production field
   */
  public PFieldChecker(Env env, PField pfield) {
    this.env = env;
    this.pfield = pfield;

    maxLenCounter = new PFieldCounter(pfield.getMaxLength());
    maxTermDepthCounter = new PFieldCounter(pfield.getMaxTermDepth());        
  }
  
  /**
   * Constructs a production field checker.
   * @param env a consequence finding problem.
   * @param opt options.
   * @param pfield a production field
   */
  public static PFieldChecker create(Env env, Options opt, PField pfield) {
    if (opt.use(USE_BRIDGE_FORMULA_TRANSLATION))
      return new PFieldCheckerWithoutSubst(env, pfield);
    else
      return new PFieldCheckerWithSubst(env, pfield);
  }
  /**
   * Sets the maximum length of consequences.
   * @param max the maximum length.
   */
  public void setMaxLength(int max) {
    maxLenCounter = new PFieldCounter(max);
  }
  
  /**
   * Returns true if the specified clause belongs to this production field.
   * @param c  the specified clause.
   * @return true if the specified clause belongs to this production field.
   */
  public boolean belongs(Clause c) {
    // TODO: The following code does not check length conditions.
    for (Literal lit : c.getLiterals()) {
      List<Unifiable<PFieldItem>> unifs = getUnifiableItems(lit);
      if (unifs == null)
        return false;
    }    
    return true;
  }
  
  /**
   * Returns true if the specified literal belongs to this production field.
   * @param lit  the specified literal.
   * @return true if the specified literal belongs to this production field.
   */
  public boolean belongs(Literal lit) {
    return getUnifiableItems(lit) != null;
  }

  /**
   * Returns the list of unifiable production field items.
   * @param lit the specified literal.
   * @return the list of unifiable production field items.
   */
  public abstract List<Unifiable<PFieldItem>> getUnifiableItems(Literal lit);
  
  /** The environment. */
  protected Env env = null;
  /** The production field. */
  protected PField pfield = null;
  /** The counter to check the maximum length. */
  protected PFieldCounter maxLenCounter = null;
  /** The counter to check the maximum term depth. */
  protected PFieldCounter maxTermDepthCounter = null;
  
}

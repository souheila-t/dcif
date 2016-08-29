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

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.PLiteral;
import org.nabelab.solar.SymTable;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.Unifiable;

/**
 * @author nabesima
 *
 */
public class PFieldCheckerWithoutSubst extends PFieldChecker implements TermTypes {
  
  // TODO: The current implementation does not support the term-depth limitation.

  /**
   * Constructs a production field checker.
   * @param env a consequence finding problem.
   */
  public PFieldCheckerWithoutSubst(Env env, PField pfield) {
    super(env, pfield);

    SymTable symTable = env.getSymTable();
    positives = new PFieldItem[symTable.getNumSyms(PREDICATE)];
    negatives = new PFieldItem[symTable.getNumSyms(PREDICATE)];
    
    List<PLiteral> specials = new ArrayList<PLiteral>();
    List<PLiteral> normals = new ArrayList<PLiteral>();
    for (PLiteral plit : pfield.getPLiterals()) {
      // The production field need to be stable.
      assert(plit.isMaxGeneral());
      if (plit.isSpecial())
        specials.add(plit);
      else
        normals.add(plit);
    }
    
    for (PLiteral plit : specials) {
      for (int name=0; name < symTable.getNumSyms(PREDICATE); name++) {
        PFieldItem item = new PFieldItem(plit, maxLenCounter);
        switch (plit.getSign()) {
        case PLiteral.POS:
          positives[name] = item; 
          break;
        case PLiteral.NEG:
          negatives[name] = item; 
          break;
        case PLiteral.BOTH:        // Shares the same item.
          positives[name] = item;
          negatives[name] = item; 
          break;
        default:
          assert(false);
        }
      }      
    }
    
    for (PLiteral plit : normals) {
      int name = plit.getName();
      int sign = plit.getSign();
      PFieldItem item = new PFieldItem(plit, maxLenCounter);
      switch (sign) {
      case PLiteral.POS:
        positives[name] = item; 
        break;
      case PLiteral.NEG:
        negatives[name] = item; 
        break;
      case PLiteral.BOTH:        // Shares the same item.
        positives[name] = item;
        negatives[name] = item; 
        break;
      default:
        assert(false);
      }
    }
  }

  /**
   * Returns the list of unifiable production field items.
   * @param lit the specified literal.
   * @return the list of unifiable production field items.
   */
  public List<Unifiable<PFieldItem>> getUnifiableItems(Literal lit) {
    if (maxLenCounter.get() == 0)
      return null;
    
    PFieldItem item = (lit.isPositive()) ? positives[lit.getName()] : negatives[lit.getName()];
    if (item == null)
      return null;
    if (item.isSkippable()) {
      List<Unifiable<PFieldItem>> unifs = new ArrayList<Unifiable<PFieldItem>>();
      unifs.add(new Unifiable<PFieldItem>(null, item, 0));      
      return unifs;
    }
    
    return null;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    
    str.append("Max length     : " + maxLenCounter    + "\n");
    str.append("Max term depth : " + maxTermDepthCounter + "\n");
    
    for (int i=0; i < positives.length; i++) {
      if (positives[i] != null) 
        str.append(positives[i]);
      if (negatives[i] != null) 
        str.append(negatives[i]);
    }    
    
    return str.toString();
  }

  /** The allowed number of positive literals. */
  private PFieldItem[] positives = null;
  /** The allowed number of negative literals. */
  private PFieldItem[] negatives = null;
  
}

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author nabesima
 *
 */
public class SymTable implements TermTypes {

  /**
   * Constructs a symbol table.
   */
  public SymTable() {
    constants  = new ArrayList<Signature>();
    functions  = new ArrayList<Signature>();
    predicates = new ArrayList<Signature>();
  }

  /**
   * Registers the constant name and returns the identifier.
   * @param name the name of the constant.
   * @return the identifier of the constant.
   */
  public int putConstant(String name) {
    // Checking whether the constant has been registered.
    for (Signature cons : constants)
      if (cons.equals(name, 0))
        return cons.getID();

    // Register the new constant.
    int id = constants.size();
    Signature sig = new Signature(id, CONSTANT, name, 0);
    constants.add(sig);

    // If the constant is a Skolem constant, then marks it.
    if (name.matches("sk(a)?(c)?\\d+"))
      sig.addTag(SKOLEM);

    return id;
  }

  /**
   * Registers the function name and returns the identifier.
   * @param name the name of the function.
   * @param arity the arity of the function.
   * @return the identifier of the function.
   */
  public int putFunction(String name, int arity) {
    // Checking whether the function has been registered.
    for (Signature func : functions)
      if (func.equals(name, arity))
        return func.getID();

    // Register the new function.
    int id = functions.size();
    Signature sig = new Signature(id, FUNCTION, name, arity);
    functions.add(sig);

    // If the function is a Skolem function, then marks it.
    if (name.matches("sk(a)?(f)?\\d+"))
      sig.addTag(SKOLEM);

    return id;
  }


  /**
   * Registers the predicate  name and returns the identifier.
   * @param name the name of the predicate.
   * @param arity the arity of the predicate.
   * @return the identifier of the predicate.
   */
  public int putPredicate(String name, int arity) {
    return putPredicate(name, arity, NONE);
  }

  /**
   * Registers the predicate  name and returns the identifier.
   * @param name   the name of the predicate.
   * @param arity  the arity of the predicate.
   * @param tags   the additional properties of the predicate.
   * @return the identifier of the predicate.
   */
  public int putPredicate(String name, int arity, int tags) {
    // Checking whether the predicate has been registered.
    for (Signature pred : predicates)
      if (pred.equals(name, arity))
        return pred.getID();

    // Register the new predicate.
    int id = predicates.size();
    Signature sig = new Signature(id, PREDICATE, name, arity, tags);
    predicates.add(sig);
    return id;
  }

  /**
   * Returns a new positive source connector predicate.
   * @return the name of the created connector predicate.
   */
  public int createNewPosSrcConnector(int arity) {
    String name = "" + CONN_PRED + numConnectors++;
    return putPredicate(name, arity, POS_SRC_CONN);
  }

  /**
   * Returns a new negative source connector predicate.
   * @return the name of the created connector predicate.
   */
  public int createNewNegSrcConnector(int arity) {
    String name = "" + CONN_PRED + numConnectors++;
    return putPredicate(name, arity, NEG_SRC_CONN);
  }

  /**
   * Return the number of kinds of connector predicates.
   * @return the number of kinds of connector predicates.
   */
  public int getNumConnPreds() {
    return numConnectors;
  }

  /**
   * Returns true if a equality predicate is registered.
   * @return true if a equality predicate is registered.
   */
  public boolean hasEqualPred() {
    for (Signature pred : predicates) {
      if (pred.getName().equals(EQUAL_PRED) && pred.getArity() == 2) {
        equalPredName = pred.getID();
        return true;
      }
    }
    equalPredName = NOT_EXISTS;
    return false;
  }

  /**
   * Returns the equality predicate name if exists. Otherwise, return -1.
   * @return Returns the equality predicate name if exists. Otherwise, return -1.
   */
  public int getEqualPredName() {
    if (equalPredName == NOT_INITIALIZED)
      hasEqualPred();
    return equalPredName;
  }

  /**
   * Returns the string representation which corresponds to the name and the type.
   * @param name the name of the term.
   * @param type the type of the term.
   */
  public String get(int name, int type) {
    switch (type) {
    case CONSTANT:  return constants.get(name).getName();
    case INTEGER:   return "" + name;
    case VARIABLE:  return "_" + name;
    case FUNCTION:  return functions.get(name).getName();
    case PREDICATE: return predicates.get(name).getName();
    default:
      assert(false);
    }
    return null;
  }

  /**
   * Adds the specified tag to the signature.
   * @param name the name of the term.
   * @param type the type of the term.
   * @param tag  the specified tag.
   */
  public void addTag(int name, int type, int tag) {
    switch (type) {
    case CONSTANT:  constants.get(name).addTag(tag);  return;
    case INTEGER:   return;
    case VARIABLE:  return;
    case FUNCTION:  functions.get(name).addTag(tag);  return;
    case PREDICATE: predicates.get(name).addTag(tag); return;
    default:
      assert(false);
    }
    return;
  }

  /**
   * Returns true if the signature has the specified tag.
   * @param name the name of the term.
   * @param type the type of the term.
   * @param tag  the specified tag.
   * @return true if the signature has the specified tag.
   */
  public boolean hasTag(int name, int type, int tag) {
    switch (type) {
    case CONSTANT:  return constants.get(name).hasTag(tag);
    case INTEGER:   return false;
    case VARIABLE:  return false;
    case FUNCTION:  return functions.get(name).hasTag(tag);
    case PREDICATE: return predicates.get(name).hasTag(tag);
    default:
      assert(false);
    }
    return false;
  }

  /**
   * Returns the name of the term.
   * @param name the name of the term.
   * @param type the type of the term.
   * @return the name of the term. If there is no specified term, then return -1.
   */
  public int getName(String name, int type, int arity) {
    ArrayList<Signature> list = null;
    switch (type) {
    case CONSTANT:  list = constants;  break;
    case INTEGER:   throw new UnsupportedOperationException("SymTable.getName() is not supported for INTEGER");
    case VARIABLE:  throw new UnsupportedOperationException("SymTable.getName() is not supported for VARIABLE");
    case FUNCTION:  list = functions;  break;
    case PREDICATE: list = predicates; break;
    default:
      assert(false);
    }
    for (Signature sig : list)
      if (sig.equals(name, arity))
        return sig.getID();
    return -1;
  }

  /**
   * Returns the arity of the term.
   * @param name the name of the term.
   * @param type the type of the term.
   * @return the arity of the term.
   */
  public int getArity(int name, int type) {
    switch (type) {
    case CONSTANT:
    case INTEGER:
    case VARIABLE:  return 0;
    case FUNCTION:  return functions.get(name).getArity();
    case PREDICATE: return predicates.get(name).getArity();
    default:
      assert(false);
    }
    return 0;
  }

  /**
   * Returns the number of symbols.
   * @return the number of symbols.
   */
  public int getNumSyms() {
    return constants.size() + functions.size() + predicates.size();
  }

  /**
   * Returns the number of symbols in the specified type.
   * @param type the specified type.
   * @return the number of symbols in the specified type.
   */
  public int getNumSyms(int type) {
    switch (type) {
    case VARIABLE:  return 0;
    case CONSTANT:  return constants.size();
    case INTEGER:   return 0;
    case FUNCTION:  return functions.size();
    case PREDICATE: return predicates.size();
    default:
      assert(false);
    }
    return 0;
  }

  /**
   * Returns the set of signatures of constants.
   * @return the set of signatures of constants.
   */
  public List<Signature> getConstants() {
    return new ArrayList<Signature>(constants);
  }

  /**
   * Returns the set of signatures of functions.
   * @return the set of signatures of functions.
   */
  public List<Signature> getFunctions() {
    return new ArrayList<Signature>(functions);
  }

  /**
   * Returns the set of signatures of predicates.
   * @return the set of signatures of predicates.
   */
  public List<Signature> getPredicates() {
    return new ArrayList<Signature>(predicates);
  }

  /**
   * Returns the number of types.
   * @return the number of types.
   */
  public int getNumTypes() {
    return NUM_TYPES;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("[Constants]\n");
    for (int i=0; i < constants.size(); i++)
      str.append(String.format(" %-3d : %s\n", i, constants.get(i)));
    str.append("[Functions]\n");
    for (int i=0; i < functions.size(); i++)
      str.append(String.format(" %-3d : %s\n", i, functions.get(i).toString()));
    str.append("[Predicates]\n");
    for (int i=0; i < predicates.size(); i++)
      str.append(String.format(" %-3d : %s\n", i, predicates.get(i).toString()));
    return str.toString();
  }

  // The mapping from constants to identifiers.
  private ArrayList<Signature> constants = null;
  // The mapping from functions to identifiers.
  private ArrayList<Signature> functions = null;
  // The mapping from predicates to identifiers.
  private ArrayList<Signature> predicates = null;

  // The name of equality predicate.
  private int equalPredName = NOT_INITIALIZED;
  // The number of connectors.
  private int numConnectors = 0;

  // An indicator that denotes there is no equality predicate.
  private static final int NOT_EXISTS = -1;
  // An indicator that denotes an element is not initialized.
  private static final int NOT_INITIALIZED = -2;

}

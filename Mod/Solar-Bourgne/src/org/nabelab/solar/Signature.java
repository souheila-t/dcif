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
 * Signature of a symbol.
 * @author nabesima
 */
public class Signature implements TermTypes {

  /**
   * Constructs a signature from the name and arity of a symbol.
   * @param id    the id of the symbol.
   * @param type  the type of the symbol.
   * @param name  the name of the symbol.
   * @param arity the arity of the symbol.
   */
  public Signature(int id, int type, String name, int arity) {
    this(id, type, name, arity, NONE);
  }
  
  /**
   * Constructs a signature from the name and arity of a symbol.
   * @param id    the id of the symbol.
   * @param type  the type of the symbol.
   * @param name  the name of the symbol.
   * @param arity the arity of the symbol.
   * @param tags  the additional properties of the symbol.
   */
  public Signature(int id, int type, String name, int arity, int tags) {
    this.id    = id;
    this.type  = type;
    this.name  = name;
    this.arity = arity;
    this.tags  = tags;
  }
  
  /**
   * Returns the identifier of this signature.
   * @return the identifier.
   */
  public int getID() {
    return id;
  }
  
  /**
   * Returns the type of this signature.
   * @return the type of this signature.
   */
  public int getType() {
    return type;
  }

  /**
   * Returns the name of this signature.
   * @return the name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Returns the arity of this signature.
   * @return the arity
   */
  public int getArity() {
    return arity;
  }
  
  /**
   * Adds the specified tag to this signature.
   * @param tag  the specified tag.
   */
  public void addTag(int tag) {
    tags |= tag;
  }

  /**
   * Returns true if this symbol has the specified tag.
   * @param tag  the specified tag.
   * @return true if this symbol has the specified tag.
   */
  public boolean hasTag(int tag) {
    return (tags & tag) != 0;
  }

  /**
   * Compares the name and arity with this signature.
   * @param name  the name to compare.
   * @param arity the arity to compare.
   */
  public boolean equals(String name, int arity) {
    return this.arity == arity && this.name.equals(name);
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return name + "/" + arity;
  }
  
  /** The identifier of this symbol. */
  private int id = 0;
  /** The type of this symbol. */
  private int type = 0;
  /** The name of this symbol. */
  private String name = null;
  /** The arity of this symbol. */
  private int arity = 0;
  /** The additional properties of this symbol. */
  private int tags = 0;
  
}
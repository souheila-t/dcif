package org.nabelab.solar.constraint;

import java.util.HashSet;

import org.nabelab.solar.Env;
import org.nabelab.solar.Node;

public class Conjunction extends Constraint {

  /**
   * Construct a conjunction of constraints.
   * @param env      the environment.
   * @param node     the node which contains this constraint.
   * @param type     the type of this constraint.
   * @param conjunct the list of constraints.
   */
  public Conjunction(Env env, Node node, int type, HashSet<Constraint> conjunct) {
    super(env, node, type);
    assert(conjunct.size() > 0);
    this.conjunct = conjunct;
  }

  /**
   * Returns true if this constraint is satisfiable.
   * @return true if this constraint is satisfiable.
   */
  protected int isSatisfiable() {
    int s = SAT;
    for (Constraint c : conjunct) {
      int ss = c.isSatisfiable();
      if (ss == UNKNOWN)
        s = UNKNOWN;
      else if (ss == UNSAT)
        return UNSAT;
    }
    return s;
  }

  /**
   * Returns the size of this disjunct.
   * @return the size of this disjunct.
   */
  public int size() {
    return conjunct.size();
  }

  /**
   * Instantiates all variables in this constraint.
   */
	public void instantiate() {
		for (Constraint constraint : conjunct)
			constraint.instantiate();
	}

  /**
   * Returns the hash code value of this object.
   * @return the hash code value of this object.
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((conjunct == null) ? 0 : conjunct.hashCode());
    return result;
  }

  /**
   * Compares the specified object with this object for equality.
   * @param obj the reference object with which to compare.
   */
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Conjunction other = (Conjunction) obj;
    if (conjunct == null) {
      if (other.conjunct != null)
        return false;
    } else if (!conjunct.equals(other.conjunct))
      return false;
    return true;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    int i = 0;
    for (Constraint c : conjunct) {
      str.append(c.toString());
      if (++i < conjunct.size())
        str.append(" v ");
    }

    return str.toString();
  }

  /** The list of constraints. */
  private HashSet<Constraint> conjunct = null;

}

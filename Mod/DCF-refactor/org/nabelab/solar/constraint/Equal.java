package org.nabelab.solar.constraint;

import org.nabelab.solar.Env;
import org.nabelab.solar.Node;
import org.nabelab.solar.Options;
import org.nabelab.solar.Subst;
import org.nabelab.solar.Term;

public class Equal extends Constraint {

	/**
	 * Construct a not-equal constraint.
	 * @param env   the environment.
	 * @param opt   the options.
	 * @param node  the node which contains this constraint.
	 * @param type  the type of this constraint.
	 */
	public Equal(Env env, Options opt, Node node, int type, int var, Term val) {
		super(env, node, type);
		this.var = varTable.getTailVar(var);
		if (opt.use(USE_CONSTRAINT_INSTANTIATION) && val.hasBindedVars())
			val = val.instantiate();
		this.val = val;
	}

	/**
	 * Returns true if this constraint is satisfiable.
	 * @return true if this constraint is satisfiable.
	 */
	protected int isSatisfiable() {
		Subst g = val.isUnifiable(var);
		if (g == null)
			return UNSAT;
		if (g.isEmpty())
			return SAT;
		return UNKNOWN;
	}

	  /**
	   * Returns the size of this constraint.
	   * @return the size of this constraint.
	   */
	  public int size() {
	    return 1;
	  }

	  /**
	   * Instantiates all variables in this constraint.
	   */
		public void instantiate() {
			var = var.instantiate();
			val = val.instantiate();
		}

		/**
	   * Returns the hash code value of this object.
	   * @return the hash code value of this object.
	   */
	  public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((val == null) ? 0 : val.hashCode());
	    result = prime * result + ((var == null) ? 0 : var.hashCode());
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
	    Equal other = (Equal) obj;
	    if (val == null) {
	      if (other.val != null)
	        return false;
	    } else if (!val.equals(other.val))
	      return false;
	    if (var == null) {
	      if (other.var != null)
	        return false;
	    } else if (!var.equals(other.var))
	      return false;
	    return true;
	  }

	  /**
	   * Returns a string representation of this object.
	   * @return a string representation of this object.
	   */
	  public String toString() {
	    return super.toString() + " [" + val + " == " + var + "]"; //  @" + time;
	  }

	  /** The variable number. */
	  private Term var = null;
	  /** The variable value. */
	  private Term val = null;

}

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

import java.util.EventObject;
import java.util.List;

@SuppressWarnings("serial")
public class SOLAREvent extends EventObject {

	/**
	 * Constructs a SOLAR event.
	 * @param source  the object on which the Event initially occurred.
	 * @param type    the type of this event object.
	 */
	public SOLAREvent(Object source, int type) {
		this(source, type, null, null);
	}

	/**
	 * Constructs a SOLAR event.
	 * @param source  the object on which the Event initially occurred.
	 * @param type    the type of this event object.
	 * @param found   a found consequence.
	 * @param removed consequences subsumed by the found consequence.
	 */
	public SOLAREvent(Object source, int type, Clause found, List<Clause> removed) {
		super(source);
		this.type    = type;
		this.found   = found;
		this.removed = removed;
	}

	/**
	 * Constructs a finished event.
	 * @param source  the object on which the Event initially occurred.
	 * @return a finished event.
	 */
	public SOLAREvent createFinishedEvent(Object source) {
		return new SOLAREvent(source, FINISHED);
	}

	/**
	 * Returns the type of this event.
	 * @return the type of this event.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns a found consequence.
	 * @return a found consequence.
	 */
	public Clause getFoundClause() {
		return found;
	}

	/**
	 * Returns clauses which are subsumed by the found consequence.
	 * @return clauses which are subsumed by the found consequence.
	 */
	public List<Clause> getRemovedClauses() {
		return removed;
	}

	/** The type of this event. */
	private int type = 0;
	/** A found consequence. */
	private Clause found = null;
	/** Clauses subsumed by the found consequence. */
	private List<Clause> removed = null;

	/** The SOLAR process is finished. */
	public static final int FINISHED = 1;
	/** A consequence is found. */
	public static final int FOUND = 2;
}

package org.nabelab.solar.pfield;

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.PLiteral;

public class PFCardConstraint {

	public PFCardConstraint() {
		group=new ArrayList<PLiteral>();
	}
	public PFCardConstraint(int maxLength) {
		maxLengthGroup = maxLength;
		group=new ArrayList<PLiteral>();
	}
	public PFCardConstraint(List<PLiteral> group, int maxLength) {
		maxLengthGroup = maxLength;
		this.group=group;
	}
	
	
	public List<PLiteral> getGroup() {
		return group;
	}
	public void add(PLiteral literal) {
		group.add(literal);
	}
	public void addAll(List<PLiteral> literals) {
		group.addAll(literals);
	}
	public int getMaxLengthGroup() {
		return maxLengthGroup;
	}
	public void setMaxLengthGroup(int maxLengthGroup) {
		this.maxLengthGroup = maxLengthGroup;
	}
	private List<PLiteral> group;
	private int maxLengthGroup;
}

package org.mql.java.models;

import org.mql.java.annotations.Relation;

public class RelationshipInfo {
	private String from;
	private String simpleFrom;
	private String to;
	private String simpleTo;
	private String relation;
	private String minOccurs;
	private String maxOccurs;

	public RelationshipInfo(String from, String to, String relation) {
		this.from = from;
		this.to = to;
		this.relation = relation;
		
		String[] fromParts = from.split("\\.");
		int fromLength = fromParts.length;
		String[] toParts = to.split("\\.");
		int toLength = toParts.length;
		this.simpleFrom = fromLength > 0 ? fromParts[fromLength - 1] : "" ;
		this.simpleTo = toLength > 0 ? toParts[toLength - 1] : "" ;
	}
	
	public RelationshipInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getRelation() {
		return relation;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSimpleFrom() {
		return simpleFrom;
	}

	public void setSimpleFrom(String simpleFrom) {
		this.simpleFrom = simpleFrom;
	}

	public String getSimpleTo() {
		return simpleTo;
	}

	public void setSimpleTo(String simpleTo) {
		this.simpleTo = simpleTo;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	public String getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
	}

	public String getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}
	
}

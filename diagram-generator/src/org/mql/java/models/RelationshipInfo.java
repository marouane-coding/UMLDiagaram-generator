package org.mql.java.models;

import org.mql.java.annotations.Relation;

public class RelationshipInfo {
	private String from;
	private String to;
	private String relation;

	public RelationshipInfo(String from, String to, Relation relation) {
		this.from = from;
		this.to = to;
		this.relation = relation.value();
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
	
}

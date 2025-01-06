package org.mql.java.models;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldInfo {
	private Field field;
	private String name;
	private String type;
	private String simpleTypeName;
	private char modifier;
	private boolean isCustomType;
	
	public FieldInfo() {
		// TODO Auto-generated constructor stub
	}

	public FieldInfo(Field field) {
		this.field = field;
		name = field.getName();
		type = field.getType().getName();
		simpleTypeName = field.getType().getSimpleName();
		modifier = decodeModifier(field.getModifiers());
		isCustomType = !field.getType().isPrimitive() && !field.getType().getName().startsWith("java");
	}
	
	private char decodeModifier(int modifier) {
	    String modifiers = Modifier.toString(modifier);
	    
	    if (modifiers.contains("public")) return '+';
	    else if (modifiers.contains("private")) return '-';
	    else if (modifiers.contains("protected")) return '#';
	    else return '~';
	}
	
	public String getRepresentation() {
		return modifier + " " + name + " : " + simpleTypeName;
	}

	public Field getField() {
		return this.field;
	}
	
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public char getModifier() {
		return modifier;
	}
	
	public boolean isCustomType() {
		return isCustomType;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSimpleTypeName(String simpleTypeName) {
		this.simpleTypeName = simpleTypeName;
	}

	public void setModifier(char modifier) {
		this.modifier = modifier;
	}

	public void setCustomType(boolean isCustomType) {
		this.isCustomType = isCustomType;
	}
	
	
}

package org.mql.java.models;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MethodInfo {
	private String name;
	private String returnType;
	private String params;
	private char modifier;
	
	public MethodInfo() {
		// TODO Auto-generated constructor stub
	}
			
	public MethodInfo(Method method) {
		name = method.getName();
		returnType = method.getReturnType().getSimpleName();
		modifier = decodeModifier(method.getModifiers());
		params = "(" + 
	            Arrays.stream(method.getParameterTypes())
	                  .map(Class::getSimpleName)
	                  .collect(Collectors.joining(", ")) +
	            ")";
	}

	private char decodeModifier(int modifier) {
	    String modifiers = Modifier.toString(modifier);
	    
	    if (modifiers.contains("public")) return '+';
	    else if (modifiers.contains("private")) return '-';
	    else if (modifiers.contains("protected")) return '#';
	    else return '~';
	}
	
	public String getRepresentation() {
		return modifier + " " + name + params + " : " + returnType;
	}

	public String getName() {
		return name;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getParams() {
		return params;
	}

	public char getModifier() {
		return modifier;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public void setModifier(char modifier) {
		this.modifier = modifier;
	}
	
	
}

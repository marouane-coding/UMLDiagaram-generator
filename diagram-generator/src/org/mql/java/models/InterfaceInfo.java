package org.mql.java.models;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class InterfaceInfo {
	private String simpleName;
	private String name;
	private String modifiers;
	private String extendedClass;
	private List<FieldInfo> fields;
	private List<MethodInfo> methods;

	public InterfaceInfo(Class<?> cls) {
		simpleName = cls.getSimpleName();
		name = cls.getName();
		getModifiers(cls);
		getExtendedClass(cls);
		getFields(cls);	
		getMethods(cls);
	}

	private void getExtendedClass(Class<?> cls) {
		if (cls.getSuperclass() == null) return;
		extendedClass = cls.getSuperclass().getName();
		
		if ("java.lang.Object".equals(extendedClass)) {
			extendedClass =  null;
		}
	}
	
	private void getModifiers(Class<?> cls) {
		modifiers = Modifier.toString(cls.getModifiers());
	}
	
	private void getFields(Class<?> cls){
		fields = new ArrayList<FieldInfo>();
		
		for (Field field : cls.getDeclaredFields()) {
			fields.add(new FieldInfo(field));
		}
	}
	
	private void getMethods(Class<?> cls){
		methods = new ArrayList<MethodInfo>();
		
		for (Method method : cls.getDeclaredMethods()) {
			methods.add(new MethodInfo(method));
		}
	}

	public String getSimpleName() {
		return simpleName;
	}

	public String getName() {
		return name;
	}

	public String getModifiers() {
		return modifiers;
	}

	public String getExtendedClass() {
		return extendedClass;
	}

	public List<FieldInfo> getFields() {
		return fields;
	}

	public List<MethodInfo> getMethods() {
		return methods;
	}
	
}

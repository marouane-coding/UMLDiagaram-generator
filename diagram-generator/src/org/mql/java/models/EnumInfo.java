package org.mql.java.models;

import java.util.ArrayList;
import java.util.List;

public class EnumInfo {
	private String name;
	private String simpleName;
	private List<String> fields;

	public EnumInfo(String path) {
        try {
	        Class<?> cls = Class.forName(path);
            name = cls.getName();
            simpleName = cls.getSimpleName();
            fields = new ArrayList<>();
                        Object[] constants = cls.getEnumConstants();
            for (Object constant : constants) {
                fields.add(constant.toString());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	

}

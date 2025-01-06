package org.mql.java.models;

import java.util.List;

public class PackageInfo {
	private String name;
	private List<PackageInfo> subPackages;
	private List<ClassInfo> classes;
	private List<InterfaceInfo> interfaces;
	private List<AnnotationInfo> annotations;
	private List<EnumInfo> enums;
	
	public PackageInfo() {
		
	}

	public PackageInfo(String path) {
		String[] pathParts = path.split("\\.");
		name = pathParts[pathParts.length-1];
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PackageInfo> getSubPackages() {
		return subPackages;
	}

	public void setSubPackages(List<PackageInfo> subPackages) {
		this.subPackages = subPackages;
	}

	public List<ClassInfo> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassInfo> classes) {
		this.classes = classes;
	}

	public List<InterfaceInfo> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<InterfaceInfo> interfaces) {
		this.interfaces = interfaces;
	}

	public List<AnnotationInfo> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<AnnotationInfo> annotations) {
		this.annotations = annotations;
	}

	public List<EnumInfo> getEnums() {
		return enums;
	}

	public void setEnums(List<EnumInfo> enums) {
		this.enums = enums;
	}

	@Override
	public String toString() {
        return "PackageInfo{" +
                "name='" + name + '\'' +
                ", subPackages=" + subPackages +
                ", classes=" + classes +
                '}';
    }
}

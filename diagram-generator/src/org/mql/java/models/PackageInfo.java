package org.mql.java.models;

import java.util.List;

public class PackageInfo {
	private String name;
	private List<PackageInfo> subPackages;
	private List<ClassInfo> classes;

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

	@Override
	public String toString() {
        return "PackageInfo{" +
                "name='" + name + '\'' +
                ", subPackages=" + subPackages +
                ", classes=" + classes +
                '}';
    }
}

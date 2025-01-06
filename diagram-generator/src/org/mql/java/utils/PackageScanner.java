package org.mql.java.utils;

import org.mql.java.models.AnnotationInfo;
import org.mql.java.models.ClassInfo;
import org.mql.java.models.EnumInfo;
import org.mql.java.models.InterfaceInfo;
import org.mql.java.models.PackageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageScanner {
    private String packageName;
    
    public PackageScanner(String packageName) {
        this.packageName = packageName;
    }

    public List<ClassInfo> getClasses() {
        List<ClassInfo> classInfos = new ArrayList<>();
        String path = "src/" + packageName.replace('.', '/');
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".java")) {
                    String className = packageName + "." + file.getName().replace(".java", "");
                    classInfos.add(new ClassInfo(className));
                }
            }
        }
        return classInfos;
    }
    
    public List<ClassInfo> getClassesRecursively() {
        List<ClassInfo> classInfos = new ArrayList<>();
        String path = "src/" + packageName.replace('.', '/');
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            scanDirectoryRecursively(directory, packageName, classInfos);
        }

        return classInfos;
    }
    
    private void scanDirectoryRecursively(File directory, String currentPackage, List<ClassInfo> classInfos) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                String subPackage = currentPackage + "." + file.getName();
                scanDirectoryRecursively(file, subPackage, classInfos);
            } else if (file.getName().endsWith(".java")) {
                String className = currentPackage + "." + file.getName().replace(".java", "");
                classInfos.add(new ClassInfo(className));
            }
        }
    }
    
    public Map<String, List<ClassInfo>> mapClassesToPackages() {
        Map<String, List<ClassInfo>> packageMap = new HashMap<>();
        String path = "src/" + packageName.replace('.', '/');
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            mapDirectoryRecursively(directory, packageName, packageMap);
        }

        return packageMap;
    }

    private void mapDirectoryRecursively(File directory, String currentPackage, Map<String, List<ClassInfo>> packageMap) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                String subPackage = currentPackage + "." + file.getName();
                mapDirectoryRecursively(file, subPackage, packageMap);
            } else if (file.getName().endsWith(".java")) {
                String className = currentPackage + "." + file.getName().replace(".java", "");
                packageMap.computeIfAbsent(currentPackage, k -> new ArrayList<>()).add(new ClassInfo(className));
            }
        }
    }
    
    public PackageInfo scanPackage(String packageName) {
        String path = "src/" + packageName.replace('.', '/');
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            PackageInfo packageInfo = new PackageInfo(packageName);
            List<ClassInfo> classes = new ArrayList<>();
            List<InterfaceInfo> interfaces = new ArrayList<>();
            List<AnnotationInfo> annotations = new ArrayList<>();
            List<EnumInfo> enums = new ArrayList<>();
            List<PackageInfo> subPackages = new ArrayList<>();

            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    String subPackageName = packageName + "." + file.getName();
                    subPackages.add(scanPackage(subPackageName));
                } else if (file.getName().endsWith(".java")) {
                    String className = packageName + "." + file.getName().replace(".java", "");
                    if ("class".equals(classType(className))) {
                        classes.add(new ClassInfo(className));
                    } else if ("interface".equals(classType(className))) {
                    	try {
							interfaces.add(new InterfaceInfo(className));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
                    } else if ("enum".equals(classType(className))) {
                    	enums.add(new EnumInfo(className));
                    } else if ("annotation".equals(classType(className))) {
                    	annotations.add(new AnnotationInfo(className));
                    }
                }
            }

            packageInfo.setClasses(classes);
            packageInfo.setInterfaces(interfaces);
            packageInfo.setSubPackages(subPackages);
            packageInfo.setAnnotations(annotations);
            packageInfo.setEnums(enums);

            return packageInfo;
        }

        return null;
    }

    public PackageInfo scan() {
        return scanPackage(packageName);
    }
    
    public String classType(String className) {
    	Class<?> cls;
		try {
			cls = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		if (cls.isEnum()) return "enum";
		else if (cls.isInterface()) return "interface";
		else if (cls.isAnnotation()) return "annotation";
		else return "class";
	}
}

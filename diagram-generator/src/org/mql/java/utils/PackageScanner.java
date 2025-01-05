package org.mql.java.utils;

import org.mql.java.annotations.Relation;
import org.mql.java.models.ClassInfo;
import org.mql.java.models.PackageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageScanner {
    private String packageName;
    private XMLGenerator generator;

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
            List<PackageInfo> subPackages = new ArrayList<>();

            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    String subPackageName = packageName + "." + file.getName();
                    subPackages.add(scanPackage(subPackageName));
                } else if (file.getName().endsWith(".java")) {
                    String className = packageName + "." + file.getName().replace(".java", "");
                    classes.add(new ClassInfo(className));
                }
            }

            packageInfo.setClasses(classes);
            packageInfo.setSubPackages(subPackages);

            return packageInfo;
        }

        return null;
    }

    public PackageInfo scan() {
        return scanPackage(packageName);
    }
}

package org.mql.java.utils;

import org.mql.java.models.AnnotationInfo;
import org.mql.java.models.ClassInfo;
import org.mql.java.models.EnumInfo;
import org.mql.java.models.InterfaceInfo;
import org.mql.java.models.PackageInfo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageScanner {
    private final String packageName;
    
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
    
    // For external projects
    
    private ClassLoader createClassLoaderForProject(String projectPath) throws MalformedURLException {
        File binDirectory = new File(projectPath, "bin"); 
        if (!binDirectory.exists()) {
            throw new IllegalArgumentException("The bin directory does not exist: " + binDirectory.getAbsolutePath());
        }

        URL[] urls = {binDirectory.toURI().toURL()};
        return new URLClassLoader(urls, getClass().getClassLoader());
    }

    
    public Map<String, List<ClassInfo>> mapClassesToPackages() {
        Map<String, List<ClassInfo>> packageMap = new HashMap<>();
        File binDirectory = new File(packageName, "bin"); 

        try {
            // Create ClassLoader for the external project's bin directory
            ClassLoader classLoader = createClassLoaderForProject(packageName);
            mapDirectoryRecursively(binDirectory, "", packageMap, classLoader);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return packageMap;
    }

    private void mapDirectoryRecursively(File directory, String currentPackage, Map<String, List<ClassInfo>> packageMap, ClassLoader classLoader) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                String subPackage = currentPackage.isEmpty() ? file.getName() : currentPackage + "." + file.getName();
                mapDirectoryRecursively(file, subPackage, packageMap, classLoader);
            } else if (file.getName().endsWith(".class")) {
                String className = (currentPackage.isEmpty() ? "" : currentPackage + ".") + file.getName().replace(".class", "");

                try {
                    System.out.println("## Loading class: " + className);
                    Class<?> cls = classLoader.loadClass(className);
                    packageMap.computeIfAbsent(currentPackage, k -> new ArrayList<>()).add(new ClassInfo(cls));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public PackageInfo scanPackage(String packageName, ClassLoader classLoader, File binDirectory) {
        String path = packageName.replace('.', File.separatorChar);
        File directory = new File(binDirectory, path);

        System.out.println("Package path: " + path);
        System.out.println("Directory exists: " + directory.exists() + " " + directory.getAbsolutePath());

        if (directory.exists() && directory.isDirectory()) {
            PackageInfo packageInfo = new PackageInfo(packageName);
            List<ClassInfo> classes = new ArrayList<>();
            List<InterfaceInfo> interfaces = new ArrayList<>();
            List<AnnotationInfo> annotations = new ArrayList<>();
            List<EnumInfo> enums = new ArrayList<>();
            List<PackageInfo> subPackages = new ArrayList<>();

            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    String subPackageName = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                    subPackages.add(scanPackage(subPackageName, classLoader, binDirectory));
                } else if (file.getName().endsWith(".class")) {
                    String className = (packageName.isEmpty() ? "" : packageName + ".") + file.getName().replace(".class", "");
                    try {
                        System.out.println("Loading class: " + className);
                        Class<?> cls = classLoader.loadClass(className);
                        if (cls.isInterface()) {
                            interfaces.add(new InterfaceInfo(cls));
                        } else if (cls.isEnum()) {
                            enums.add(new EnumInfo(cls));
                        } else if (cls.isAnnotation()) {
                            annotations.add(new AnnotationInfo(cls));
                        } else {
                            classes.add(new ClassInfo(cls));
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("Class not found: " + className);
                        e.printStackTrace();
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

        System.out.println("Directory not found for package: " + packageName);
        return null;
    }



    public PackageInfo scan() {
        try {
            File binDirectory = new File(packageName, "bin");
            if (!binDirectory.exists()) {
                System.err.println("Bin directory does not exist: " + binDirectory.getAbsolutePath());
                return null;
            }

            ClassLoader classLoader = createClassLoaderForProject(packageName);
            return scanPackage("", classLoader, binDirectory); 
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }



}
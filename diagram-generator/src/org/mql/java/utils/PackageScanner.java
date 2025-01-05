package org.mql.java.utils;

import org.mql.java.models.ClassInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
}

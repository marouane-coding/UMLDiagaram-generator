package org.mql.java.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Inherited;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationInfo {
    private String name;
    private RetentionPolicy retentionPolicy;
    private boolean isInherited;
    private Map<String, String> attributes;

    public AnnotationInfo(String annotationPath) {
        attributes = new HashMap<>();
        try {
            Class<?> annotationClass = Class.forName(annotationPath);

            name = annotationClass.getSimpleName();

            Retention retention = annotationClass.getAnnotation(Retention.class);
            retentionPolicy = (retention != null) ? retention.value() : RetentionPolicy.CLASS;

            isInherited = annotationClass.isAnnotationPresent(Inherited.class);

            for (Method method : annotationClass.getDeclaredMethods()) {
                attributes.put(method.getName(), method.getReturnType().getSimpleName());
            }
    	}
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public RetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    public boolean isInherited() {
        return isInherited;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "AnnotationInfo{" +
                "name='" + name + '\'' +
                ", retentionPolicy=" + retentionPolicy +
                ", isInherited=" + isInherited +
                ", attributes=" + attributes +
                '}';
    }
}

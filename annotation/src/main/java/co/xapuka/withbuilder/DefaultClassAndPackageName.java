package co.xapuka.withbuilder;

import javax.lang.model.element.TypeElement;

final class DefaultClassAndPackageName implements ClassAndPackageName {

    private String className;

    private String packageName;

    private String suffix;

    private DefaultClassAndPackageName() {
    }

    static ClassAndPackageName from (TypeElement typeElement, String suffix){
        DefaultClassAndPackageName instance = new DefaultClassAndPackageName();
        instance.className = typeElement.getSimpleName().toString();
        instance.suffix = suffix;
        instance.packageName = typeElement.getQualifiedName().toString().replaceFirst("\\."+instance.className+"$", "");
        return instance;
    }

    public String getClassName() {
        return className;
    }

    public String getBuilderClassName() {
        return className + suffix;
    }

    public String getPackageName() {
        return packageName;
    }
}

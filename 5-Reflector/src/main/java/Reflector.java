import java.awt.image.ImageObserver;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class Reflector {
    private static List<Type> filterNotObject(Type[] types) {
        var list = new ArrayList<Type>(Arrays.asList(types));
        list.remove(Object.class);
        return list;
    }

    private static String fullName(Type type, boolean printBounds) {
        if (type instanceof Class<?>) {
            return ((Class) type).getCanonicalName();
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            var builder = new StringBuilder();
            if(parameterizedType.getOwnerType() != null) {
                builder.append(fullName(parameterizedType.getOwnerType(), false));
                builder.append(".");
            }
            builder.append(fullName(parameterizedType.getRawType(), false));
            builder.append(printGenericTypes(parameterizedType.getActualTypeArguments()));
            return builder.toString();
        }
        if (type instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            var builder = new StringBuilder();
            builder.append(typeVariable.getName());
            if (filterNotObject(typeVariable.getBounds()).size() > 0 && printBounds) {
                builder.append(" extends ");
                var innerJoiner = new StringJoiner(" & ");
                for (var bound : filterNotObject(typeVariable.getBounds())) {
                    innerJoiner.add(fullName(bound, false));
                }
                builder.append(innerJoiner.toString());
            }
            return builder.toString();
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            var builder = new StringBuilder();
            builder.append("?");
            if (filterNotObject(wildcardType.getUpperBounds()).size() > 0) {
                builder.append(" extends ");
                var joiner = new StringJoiner(" & ");
                for(var bound : filterNotObject(wildcardType.getUpperBounds())) {
                    joiner.add(fullName(bound, false));
                }
                builder.append(joiner.toString());
            }
            if (filterNotObject(wildcardType.getLowerBounds()).size() > 0) {
                builder.append(" super ");
                var joiner = new StringJoiner(" & ");
                for(var bound : filterNotObject(wildcardType.getLowerBounds())) {
                    joiner.add(fullName(bound, false));
                }
                builder.append(joiner.toString());
            }
            return builder.toString();
        }
        if(type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            var builder = new StringBuilder();
            builder.append(fullName(genericArrayType.getGenericComponentType(), false));
            builder.append("[]");
            return builder.toString();
        }
        return null;
    }

    private static String printField(Field field) {
        var builder = new StringBuilder();
        builder.append(Modifier.toString(field.getModifiers()));
        builder.append(" ");
        builder.append(fullName(field.getGenericType()));
        builder.append(" ");
        builder.append(field.getName());
        if (Modifier.isFinal(field.getModifiers())) {
            builder.append(" = ").append(getDefaultValue(field.getType()));
        }
        builder.append(";");
        return builder.toString();
    }

    private static String printGenericTypes(Type[] variables) {
        if (variables.length == 0) {
            return "";
        }
        var joiner = new StringJoiner(", ", "<", ">");
        for (var type : variables) {
            joiner.add(fullName(type));
        }
        return joiner.toString();
    }

    private static String printArguments(Type[] arguments, int ignored) {
        var joiner = new StringJoiner(", ", "(", ")");
        int number = 0;
        for (int i = ignored; i < arguments.length; i++) {
            joiner.add(fullName(arguments[i]) + " t" + number++);
        }
        return joiner.toString();
    }

    private static String getDefaultValue(Class<?> clazz) {
        if (clazz.equals(boolean.class)) {
            return "false";
        }
        if (clazz.equals(char.class)) {
            return "'0'";
        }
        if (clazz.equals(byte.class)) {
            return "0";
        }
        if (clazz.equals(short.class)) {
            return "0";
        }
        if (clazz.equals(int.class)) {
            return "0";
        }
        if (clazz.equals(long.class)) {
            return "0";
        }
        if (clazz.equals(float.class)) {
            return "0";
        }
        if (clazz.equals(double.class)) {
            return "0";
        }
        if (clazz.equals(void.class)) {
            return "";
        }
        return "null";
    }

    private static String printMethod(Method method) {
        var builder = new StringBuilder()
                .append(Modifier.toString(method.getModifiers()))
                .append(printGenericTypes(method.getTypeParameters()))
                .append(" ")
                .append(fullName(method.getGenericReturnType()))
                .append(" ")
                .append(method.getName())
                .append(printArguments(method.getGenericParameterTypes(), 0));
        if (!Modifier.isAbstract(method.getModifiers())) {
            builder.append(" { ")
                    .append("return ")
                    .append(getDefaultValue(method.getReturnType()))
                    .append("; ")
                    .append("}");
        } else {
            builder.append(";");
        }
        return builder.toString();
    }

    private static String printConstructor(Constructor<?> constructor, String name, boolean isNested) {
        return new StringBuilder()
                .append(Modifier.toString(constructor.getModifiers()))
                .append(printGenericTypes(constructor.getTypeParameters()))
                .append(" ")
                .append(name)
                .append(printArguments(constructor.getGenericParameterTypes(), isNested ? 0 : 1))
                .append(" { }")
                .toString();
    }

    private static String printClassSignature(Class<?> clazz, String name) {
        var builder = new StringBuilder();
        builder.append(Modifier.toString(clazz.getModifiers()));
        builder.append(" ");
        if (!Modifier.isInterface(clazz.getModifiers())) {
            builder.append("class ");
        }
        builder.append(name);
        builder.append(" ");
        builder.append(printGenericTypes(clazz.getTypeParameters()));
        if (clazz.getGenericSuperclass() != null && !clazz.getGenericSuperclass().equals(Object.class)) {
            builder.append(" extends ");
            builder.append(fullName(clazz.getGenericSuperclass()));
        }
        if (clazz.getGenericInterfaces().length != 0) {
            if (Modifier.isInterface(clazz.getModifiers())) {
                builder.append(" extends ");
            } else {
                builder.append(" implements ");
            }
            var joiner = new StringJoiner(", ");
            for (var implementedInterface : clazz.getGenericInterfaces()) {
                joiner.add(fullName(implementedInterface));
            }
            builder.append(joiner.toString());
        }
        return builder.toString();
    }

    private static String printClass(Class<?> clazz, String name, int indent, boolean isNested) {
        var builder = new StringBuilder();
        builder.append(" ".repeat(indent));
        builder.append(printClassSignature(clazz, name));
        builder.append(" {\n");

        for (var constructor : clazz.getDeclaredConstructors()) {
            builder.append(" ".repeat(indent + 4));
            builder.append(printConstructor(constructor, name, isNested)).append("\n");
        }
        for (var field : clazz.getDeclaredFields()) {
            if (!field.isSynthetic()) {
                builder.append(" ".repeat(indent + 4));
                builder.append(printField(field)).append("\n");
            }
        }
        for (var method : clazz.getDeclaredMethods()) {
            if (!method.isSynthetic()) {
                builder.append(" ".repeat(indent + 4));
                builder.append(printMethod(method)).append("\n");
            }
        }
        for (var innerClass : clazz.getDeclaredClasses()) {
            if (!clazz.isSynthetic()) {
                builder.append(printClass(innerClass, innerClass.getSimpleName(), indent + 4, Modifier.isStatic(innerClass.getModifiers()))).append("\n\n");
            }
        }
        builder.append(" ".repeat(indent));
        builder.append("}");
        return builder.toString();
    }

    private static void printStructure(Class<?> clazz) {
        System.out.println(printClass(clazz, "SomeClass", 0, true));
    }


    private static void diffClasses(Class<?> a, Class<?> b) {

    }

    public static void main(String[] args) {
        //printStructure(A.class);
        //printStructure(B.class);
        //printStructure(Function.class);
        printStructure(A.class);
    }

}

abstract class A<T extends Integer> extends B {
    java.lang.Integer ololo;
    int ke;
    List<Integer> l;
    private volatile T kek;

    <U> A(U x) {
    }

    private synchronized <V, U extends Object & Comparable<? super V>> T setKek(T fff, U a, int z, Integer y) {
        kek = fff;
        return null;
    }

    private void foo(A<? extends Object> fu) {
    }

    interface F extends Instrumentation, ImageObserver {

    }

    static class BB {

    }

    class C {
        class D {

        }
    }

    private class AAAA {
        AAAA(Object sss) {
        }
    }
}

class B {
    class D {
    }
}

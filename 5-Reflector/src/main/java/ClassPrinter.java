import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;

/**
 * This class converts Class into string and compares two classes.
 */
class ClassPrinter {
    //Just because not null. It will be changed before each operation
    private static @NotNull Class<?> replaceFrom = Object.class;
    private static @NotNull String replaceTo = "Object";

    private static List<Type> filterNotObject(@NotNull Type[] types) {
        var list = new ArrayList<Type>(Arrays.asList(types));
        list.remove(Object.class);
        return list;
    }

    private static String fullName(@NotNull Type type) {
        return fullName(type, false, true);
    }

    private static String fullName(@NotNull Type type, boolean printBounds) {
        return fullName(type, printBounds, true);
    }

    private static String fullName(@NotNull Type type, boolean printBounds, boolean printCanonical) {
        if (type instanceof Class<?>) {
            var clazz = (Class<?>) type;
            if (clazz.equals(replaceFrom)) {
                return replaceTo;
            }
            return printCanonical ? clazz.getCanonicalName() : ((Class) type).getSimpleName();
        }
        if (type instanceof ParameterizedType) {
            var parameterizedType = (ParameterizedType) type;
            var builder = new StringBuilder();
            if (parameterizedType.getOwnerType() != null) {
                builder.append(fullName(parameterizedType.getOwnerType()));
                builder.append(".");
                builder.append(fullName(parameterizedType.getRawType(), false, false));
            } else {
                builder.append(fullName(parameterizedType.getRawType()));
            }
            builder.append(printGenericTypes(parameterizedType.getActualTypeArguments()));
            return builder.toString();
        }
        if (type instanceof TypeVariable<?>) {
            var typeVariable = (TypeVariable<?>) type;
            var builder = new StringBuilder();
            builder.append(typeVariable.getName());
            if (filterNotObject(typeVariable.getBounds()).size() > 0 && printBounds) {
                builder.append(" extends ");
                var innerJoiner = new StringJoiner(" & ");
                for (var bound : filterNotObject(typeVariable.getBounds())) {
                    innerJoiner.add(fullName(bound));
                }
                builder.append(innerJoiner.toString());
            }
            return builder.toString();
        }
        if (type instanceof WildcardType) {
            var wildcardType = (WildcardType) type;
            var builder = new StringBuilder();
            builder.append("?");
            if (filterNotObject(wildcardType.getUpperBounds()).size() > 0) {
                builder.append(" extends ");
                var joiner = new StringJoiner(" & ");
                for (var bound : filterNotObject(wildcardType.getUpperBounds())) {
                    joiner.add(fullName(bound));
                }
                builder.append(joiner.toString());
            }
            if (filterNotObject(wildcardType.getLowerBounds()).size() > 0) {
                builder.append(" super ");
                var joiner = new StringJoiner(" & ");
                for (var bound : filterNotObject(wildcardType.getLowerBounds())) {
                    joiner.add(fullName(bound));
                }
                builder.append(joiner.toString());
            }
            return builder.toString();
        }
        if (type instanceof GenericArrayType) {
            var genericArrayType = (GenericArrayType) type;
            return fullName(genericArrayType.getGenericComponentType()) +
                    "[]";
        }
        return null;
    }

    private static String printGenericTypes(@NotNull Type[] variables) {
        if (variables.length == 0) {
            return "";
        }
        var joiner = new StringJoiner(", ", "<", ">");
        for (var type : variables) {
            joiner.add(fullName(type, true));
        }
        return joiner.toString().replaceAll("\\s+", " ").trim();
    }

    private static String printField(@NotNull Field field) {
        var builder = new StringBuilder();
        builder.append(Modifier.toString(field.getModifiers()));
        builder.append(" ");
        builder.append(fullName(field.getGenericType()));
        builder.append(" ");
        builder.append(field.getName());
        if (Modifier.isFinal(field.getModifiers())) {
            builder.append("=").append(getDefaultValue(field.getType()));
        }
        builder.append(";");
        return builder.toString().replaceAll("\\s+", " ").trim();
    }

    private static String printArguments(@NotNull Type[] arguments, int ignored) {
        var joiner = new StringJoiner(", ", "(", ")");
        int number = 0;
        for (int i = ignored; i < arguments.length; i++) {
            joiner.add(fullName(arguments[i]) + " t" + number++);
        }
        return joiner.toString().replaceAll("\\s+", " ").trim();
    }

    private static String getDefaultValue(@NotNull Class<?> clazz) {
        if (clazz.equals(boolean.class)) {
            return "false";
        } else if (clazz.equals(byte.class)
                || clazz.equals(short.class)
                || clazz.equals(int.class)
                || clazz.equals(long.class)
                || clazz.equals(float.class)
                || clazz.equals(double.class)) {
            return "0";
        } else if (clazz.equals(void.class)) {
            return "";
        } else if (clazz.equals(char.class)) {
            return "'0'";
        } else {
            return "null";
        }
    }

    private static String printExceptions(@NotNull Type[] exceptions) {
        if (exceptions.length == 0) {
            return "";
        }
        var joiner = new StringJoiner(", ", "throws ", "");
        for (var exception : exceptions) {
            joiner.add(fullName(exception));
        }
        return joiner.toString();
    }

    private static String printMethod(@NotNull Method method) {
        var builder = new StringBuilder()
                .append(Modifier.toString(method.getModifiers()))
                .append(printGenericTypes(method.getTypeParameters()))
                .append(" ")
                .append(fullName(method.getGenericReturnType()))
                .append(" ")
                .append(method.getName())
                .append(printArguments(method.getGenericParameterTypes(), 0))
                .append(" ")
                .append(printExceptions(method.getGenericExceptionTypes()));
        if (!Modifier.isAbstract(method.getModifiers())) {
            builder.append("{")
                    .append("return ")
                    .append(getDefaultValue(method.getReturnType()))
                    .append(";")
                    .append("}");
        } else {
            builder.append(";");
        }
        return builder.toString().replaceAll("\\s+", " ").trim();
    }

    private static String printConstructor(@NotNull Constructor<?> constructor,
                                           @NotNull String name, boolean isNested) {
        return (Modifier.toString(constructor.getModifiers()) +
                printGenericTypes(constructor.getTypeParameters()) +
                " " +
                name +
                printArguments(constructor.getGenericParameterTypes(), isNested ? 0 : 1) +
                " " +
                printExceptions(constructor.getGenericExceptionTypes()) +
                "{}").replaceAll("\\s+", " ").trim();
    }

    private static String printClassSignature(@NotNull Class<?> clazz, @NotNull String name) {
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
        return builder.toString().replaceAll("\\s+", " ").trim();
    }

    private static String printClass(@NotNull Class<?> clazz, @NotNull String name, boolean isNested) {
        var builder = new StringBuilder();
        builder.append(printClassSignature(clazz, name));
        builder.append("{");
        var lines = new ArrayList<String>();
        for (var constructor : clazz.getDeclaredConstructors()) {
            lines.add(printConstructor(constructor, name, isNested));
        }
        for (var field : clazz.getDeclaredFields()) {
            if (!field.isSynthetic()) {
                lines.add(printField(field));
            }
        }
        for (var method : clazz.getDeclaredMethods()) {
            if (!method.isSynthetic()) {
                lines.add(printMethod(method));
            }
        }
        for (var innerClass : clazz.getDeclaredClasses()) {
            if (!clazz.isSynthetic()) {
                lines.add(printClass(innerClass, innerClass.getSimpleName(),
                        Modifier.isStatic(innerClass.getModifiers())));
            }
        }
        Collections.sort(lines);
        for (var line : lines) {
            builder.append(line);
        }
        builder.append("}");
        return builder.toString().replaceAll("\\s+", " ").trim();
    }

    /** Returns the java code of structure of the given class with the same name */
    public static String printStructure(@NotNull Class<?> clazz) {
        replaceFrom = clazz;
        replaceTo = clazz.getSimpleName();
        return printClass(clazz, clazz.getSimpleName(), true);
    }

    /**
     * Returns the java code of structure of the given class with the given name
     *
     * @param name name of the generated class
     */
    static String printStructure(@NotNull Class<?> clazz, @NotNull String name) {
        replaceFrom = clazz;
        replaceTo = name;
        return printClass(clazz, name, true);
    }

    /**
     * Compares two classes and returns unique fields and methods
     *
     * @return list of two elements. Each element contains unique fields and methods for {@code a} and {@code b} respectively.
     */
    static List<Set<String>> diffClasses(@NotNull Class<?> a, @NotNull Class<?> b) {
        var aSet = getFieldsAndMethods(a);
        var bSet = getFieldsAndMethods(b);
        var intersection = new HashSet<String>(aSet);
        intersection.retainAll(bSet);
        aSet.removeAll(intersection);
        bSet.removeAll(intersection);
        return List.of(aSet, bSet);
    }

    private static Set<String> getFieldsAndMethods(@NotNull Class<?> clazz) {
        var set = new HashSet<String>();
        replaceFrom = clazz;
        replaceTo = "SomeClass";
        for (var field : clazz.getDeclaredFields()) {
            set.add(printField(field));
        }
        for (var method : clazz.getDeclaredMethods()) {
            set.add(printMethod(method));
        }
        return set;
    }
}


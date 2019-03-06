package ru.hse.aabukov.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Injector {
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws InjectionCycleException, ImplementationNotFoundException, AmbiguousImplementationException {
        var used = new ArrayList<Boolean>(implementationClassNames.size());
        for(int i = 0; i < used.size(); i++) {
            used.set(i, false);
        }
        Object result = tryInitialize(rootClassName, implementationClassNames, used);
        return result;
    }

    private static List<Integer> getCandidates(String className, List<String> implementationNames) {
        List<Integer> result = new ArrayList<Integer>();
        for(int i = 0; i < implementationNames.size(); i++) {
            var name = implementationNames.get(i);
            try {
                var interfaces = Class.forName(name).getInterfaces();
                var count = Arrays.stream(interfaces).filter(x -> x.getName().equals(className)).count();
                if(count > 0) {
                    result.add(i);
                }
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private static Object tryInitialize(String rootClassName, List<String> implementationClassNames, ArrayList<Boolean> used) throws AmbiguousImplementationException, ImplementationNotFoundException, InjectionCycleException {
        Constructor<?>[] constructors = new Constructor[0];
        try {
            constructors = Class.forName(rootClassName).getDeclaredConstructors();
        } catch (Exception ignored) {
        }
        for(var constructor : constructors) {
            var names = Arrays.asList(constructor.getParameterTypes()).stream().map(x -> x.getName()).collect(Collectors.toList());
            if(constructor.getParameterTypes().length == 0) {
                try {
                    return Class.forName(rootClassName).getDeclaredConstructor().newInstance();
                } catch (Exception ignored) {
                }
            }
            Object[] parameters = new Object[constructor.getParameterTypes().length];
            int i = 0;
            for (var clazz : constructor.getParameterTypes()) {
                var candidates = getCandidates(clazz.getName(), implementationClassNames);
                if(candidates.size() > 1) {
                    throw new AmbiguousImplementationException();
                }
                if(candidates.size() == 0) {
                    throw new ImplementationNotFoundException();
                }
                if(used.get(candidates.get(0))) {
                    throw new InjectionCycleException();
                }
                used.set(candidates.get(0), true);
                parameters[i++] = tryInitialize(clazz.getName(), implementationClassNames, used);
            }
            try {
                return constructor.newInstance(parameters);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}

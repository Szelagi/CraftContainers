package pl.szelagi.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Instances {
    private static Map<Class<?>, Set<Class<?>>> cache = new HashMap<>();

    public static Set<Class<?>> getAllInstanceofTypes(Class<?> cls) {
        return cache.computeIfAbsent(cls, key -> {
            Set<Class<?>> result = new HashSet<>();
            collectTypes(cls, result);
            return result;
        });
    }

    private static void collectTypes(Class<?> cls, Set<Class<?>> result) {
        if (cls == null || cls == Object.class) return;
        result.add(cls);

        for (var iface : cls.getInterfaces()) {
            collectTypes(iface, result);
        }

        collectTypes(cls.getSuperclass(), result);
    }

}

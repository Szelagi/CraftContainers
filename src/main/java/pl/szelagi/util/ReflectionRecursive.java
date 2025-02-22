/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

// Author: Szelagi
// Github: https://github.com/Szelagi
package pl.szelagi.util;

import pl.szelagi.event.sapi.SAPIEventHandler;
import pl.szelagi.event.sapi.SAPIListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class ReflectionRecursive {
    @Deprecated
    public static Method getDeclaredMethod(Object object, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return getDeclaredMethod(object.getClass(), name, parameterTypes);
    }

    public static Method getDeclaredMethod(Class<?> classType, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        Method method;
        Class<?> currentClass = classType;
        do {
            try {
                method = currentClass.getDeclaredMethod(name, parameterTypes);
                return method;
            } catch (
                    NoSuchMethodException ignore) {
            }
        } while ((currentClass = currentClass.getSuperclass()) != null);
        throw new NoSuchMethodException("ReflectionRecursive no such method '" + name + "' in class" + classType.getName());
    }

    @Deprecated
    public static Field getDeclaredField(Object object, String name) throws NoSuchFieldException, SecurityException {
        return getDeclaredField(object.getClass(), name);
    }

    public static Field getDeclaredField(Class<?> classType, String name) throws NoSuchFieldException, SecurityException {
        Field field;
        Class<?> currentClass = classType;
        do {
            try {
                field = currentClass.getDeclaredField(name);
                return field;
            } catch (
                    NoSuchFieldException ignore) {
            }
        } while ((currentClass = currentClass.getSuperclass()) != null);
        throw new NoSuchFieldException("ReflectionRecursive no such field  '" + name + "' in class " + classType.getName());
    }

    public static Collection<Method> getSAPIHandlerMethods(Class<? extends SAPIListener> classType) {
        var methodHashMap = new HashMap<String, Method>();
        Class<?> currentClass = classType;

        while (currentClass != null) {
            var annotatedMethods = Arrays.stream(currentClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(SAPIEventHandler.class))
                    .filter(method -> method.getReturnType().equals(void.class))
                    .filter(method -> method.getParameterCount() == 1)
                    .toList();

            for (Method method : annotatedMethods) {
                methodHashMap.putIfAbsent(method.getName(), method);
            }

            currentClass = currentClass.getSuperclass();
        }

        var methods = methodHashMap.values();
        methods.forEach(method -> method.setAccessible(true));
        return methods;
    }


    public static Collection<Method> getEventMethods(Class<?> classType, Class<?> parameterType) throws SecurityException {
        HashMap<String, Method> methodHashMap = new HashMap<>();

        Class<?> currentClass = classType;
        do {
            Arrays
                    .stream(currentClass.getDeclaredMethods())
                    .filter(m -> m.getReturnType()
                            .equals(void.class))
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> m.getParameterTypes()[0].equals(parameterType))
                    .forEach(method -> {
                        if (methodHashMap.containsKey(method.getName()))
                            return;
                        methodHashMap.put(method.getName(), method);
                    });
        } while ((currentClass = currentClass.getSuperclass()) != null);
        return methodHashMap.values();
    }
}

package ru.spbau.daniil.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Class which has a static method which gets a method from class and makes it accessible
 */
class PrivateMethodGetter {
    @NotNull
    static <T> Method getMethodAndMakeItPublic(
            Class<T> classWithMethod,
            @NotNull String methodName,
            Class<?>... args) throws NoSuchMethodException {
        Method method = classWithMethod.getDeclaredMethod(methodName, args);
        method.setAccessible(true);
        return method;
    }
}

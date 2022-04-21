package de.mclonips.validation;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;

import java.lang.reflect.Constructor;

public class ValidationInterceptor {

    public static <T> void validate(@Origin Constructor<T> constructor, @AllArguments Object[] args) {
        Validator.validate(constructor, args);
    }
}

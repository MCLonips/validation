package de.mclonips.validation;

import jakarta.validation.*;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility to validate {@link jakarta.validation} annotations on objects.
 */
@UtilityClass
public class Validator {

    private final jakarta.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> void validate(Constructor<T> constructor, Object[] args) {
        Set<ConstraintViolation<T>> violations = validator.forExecutables()
                                                          .validateConstructorParameters(constructor, args);
        checkViolations(constructor.getDeclaringClass().getTypeName(), violations);
    }

    public <T> void validate(T instance) {
        Set<ConstraintViolation<T>> violations = validator.validate(instance);
        checkViolations(instance.getClass().getTypeName(), violations);
    }

    private <T> void checkViolations(String className, Set<ConstraintViolation<T>> violations) {
        if (!violations.isEmpty()) {
            String message = violations.stream()
                                       .sorted(Validator::compare)
                                       .map(cv -> getParameterName(cv) + " - " + cv.getMessage())
                                       .collect(Collectors.joining(System.lineSeparator()));

            throw new ConstraintViolationException("Invalid instantiation of type " + className + System.lineSeparator() + message, violations);
        }
    }

    private int compare(ConstraintViolation<?> o1, ConstraintViolation<?> o2) {
        return Integer.compare(getParameterIndex(o1), getParameterIndex(o2));
    }

    private String getParameterName(ConstraintViolation<?> cv) {
        Iterator<Path.Node> path = cv.getPropertyPath().iterator();
        while (path.hasNext()) {
            Path.Node node = path.next();
            if (node.getKind() == ElementKind.PARAMETER) {
                return node.getName();
            }
        }

        return "";
    }

    private int getParameterIndex(ConstraintViolation<?> cv) {
        Iterator<Path.Node> path = cv.getPropertyPath().iterator();
        while (path.hasNext()) {
            Path.Node node = path.next();
            if (node.getKind() == ElementKind.PARAMETER) {
                return node.as(Path.ParameterNode.class).getParameterIndex();
            }
        }

        return -1;
    }
}

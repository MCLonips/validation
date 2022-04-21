package de.mclonips.validation.plugin;

import de.mclonips.validation.ValidationInterceptor;
import jakarta.validation.Constraint;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;

import java.io.IOException;

import static net.bytebuddy.matcher.ElementMatchers.*;

@Slf4j
public class ValidationWeavingPlugin implements Plugin {

    @Override
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        return builder.constructor(this::hasConstrainedParameter)
                      .intercept(SuperMethodCall.INSTANCE.andThen(
                                MethodDelegation.to(ValidationInterceptor.class)));
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean matches(TypeDescription typeDefinitions) {
        var matches = typeDefinitions.getDeclaredMethods()
                                     .stream()
                                     .anyMatch(m -> m.isConstructor() && hasConstrainedParameter(m));
        if (matches) {
            log.info(String.format("Added validator call to class %s", typeDefinitions.getTypeName()));
        }

        return matches;
    }

    private boolean hasConstrainedParameter(MethodDescription method) {
        return method.getParameters()
                     .asDefined()
                     .stream()
                     .anyMatch(this::isConstrained);
    }

    private boolean isConstrained(
              ParameterDescription.InDefinedShape parameter) {

        return !parameter.getDeclaredAnnotations()
                         .asTypeList()
                         .filter(hasAnnotation(annotationType(Constraint.class)))
                         .isEmpty();
    }
}

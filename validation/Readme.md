# Validation

Small library providing some basic jakarta bean validation.

## Usage

### Maven

To use the validation in your application add the following dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>de.mclonips</groupId>
    <artifactId>validation</artifactId>
</dependency>
```

This allows you to use basic validation features of jakarta bean annotation. Java 14 adds `Records` as a standard feature. It is not necessary to create a constructor while using `Records`. To avoid boilerplate code while using `Records`and to use standard implementations this library adds the
possibility to inject a validation call in the `Records` constructor while manipulating the byte code.

To use this add the following plugin-configuration to your `pom.xml`:

```xml

<plugin>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy-maven-plugin</artifactId>
    <version>LATEST</version>
    <executions>
        <execution>
            <goals>
                <goal>transform</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>de.mclonips</groupId>
            <artifactId>validation</artifactId>
            <version>LATEST</version>
        </dependency>
    </dependencies>
</plugin>
```

When executing `maven clean compile` the byte-buddy-plugin will be executed and checks classes on the classpath to see if class is a `Record` and if the constructor contains any jakarta validation annotation a call to validate the class will be added to the bytecode. A log appears in the maven build
log.

```shell
[INFO] Resolving transformer dependency: MavenCoordinate{groupId='de.mclonips', artifactId='validation-tests', version='1.1.0', packaging='jar'}
[INFO] Resolved plugin: de.mclonips.validation.plugin.ValidationWeavingPlugin
[INFO] Resolved entry point: REBASE
[INFO] Processing class files located in in: /Users/tfranken/Desktop/Projekte/java-common/validation-tests/target/classes
[INFO] Added validator call to class de.mclonips.validation.domain.TestRecord
[INFO] Transformed 1 types
```

### Class validation

To apply jakarta bean validation to a class with annotated members add a call to the `Validator` to the constructor of the class.

Use `de.mclonips.validator.Validator.validate(this)` to perform validation after setting the properties inside the constructor:

```java
public class TestClass {

    @NotBlank
    private final String name;
    @Email
    private final String email;
    @Min(20)
    @Max(30)
    private final Integer age;

    public TestClass(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;

        Validator.validate(this);
    }
}
```

### Record validation

While using `Records` you must not provide variables directly like in a classic `Class`. To validate a `Record` the annotations must be provided in the variable decleration section of the `Record`. This is everything to do for you! The rest will be done by black byte code manipulating magic.
A `Record` with working bean validation looks like this:

```java
public record TestRecord(@NotBlank String name,
                         @Email String email,
                         @Min(20) @Max(30) Integer age) {

}
```

When compiling the class and decompiling the result byte code the record looks like this:

```java
public record TestRecord(@NotBlank String name, @Email String email, @Min(20L) @Max(30L) Integer age) {

    public TestRecord(@NotBlank String name, @Email String email, @Min(20L) @Max(30L) Integer age) {
        this(var1, var2, var3, (TestRecord$auxiliary$T7EWuvn7) null);
        ValidationInterceptor.validate(cachedValue$cLPUD2pn$kbpm191, new Object[]{var1, var2, var3});
    }

    // other stuff like getters 
}
```

The validation call looks different like the one in a plain class but has been adde to the `Records` constructor:

`ValidationInterceptor.validate(cachedValue$cLPUD2pn$kbpm191, new Object[]{var1, var2, var3});`

**WATCH OUT:** This will only be applied to the `compile`-goal of the maven lifecycle. Testclasses will be compiled using test-compile, in the standard configuration like above test classes will not be weaved.
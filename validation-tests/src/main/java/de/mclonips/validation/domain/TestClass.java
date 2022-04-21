package de.mclonips.validation.domain;

import de.mclonips.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

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

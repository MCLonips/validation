package de.mclonips.validation.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TestRecord(@NotBlank String name,
                         @Email String email,
                         @Min(20) @Max(30) Integer age) {

}

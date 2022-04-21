package validation;

import de.mclonips.validation.domain.TestClass;

public class ClassValidationTest extends AbstractValidationTest<TestClass> {

    @Override
    public TestClass create(String name, String email, Integer age) {
        return new TestClass(name, email, age);
    }
}

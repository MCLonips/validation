package validation;

import de.mclonips.validation.domain.TestRecord;

public class RecordValidationTest extends AbstractValidationTest<TestRecord> {

    @Override
    public TestRecord create(String name, String email, Integer age) {
        return new TestRecord(name, email, age);
    }
}

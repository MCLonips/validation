package validation;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public abstract class AbstractValidationTest<T> {

    @Test
    void validExample() {
        assertThatCode(() -> create("testclass", "a.b@c.de", 28))
                  .doesNotThrowAnyException();
    }

    @Nested
    class RaWStringTest {

        @Test
        void nameIsNull() {
            assertThatExceptionOfType(ValidationException.class)
                      .isThrownBy(() -> create(null, "a.b@c.de", 28));
        }

        @Test
        void nameIsEmpty() {
            assertThatExceptionOfType(ValidationException.class)
                      .isThrownBy(() -> create("", "a.b@c.de", 28));
        }

        @Test
        void nameIsBlank() {
            assertThatExceptionOfType(ValidationException.class)
                      .isThrownBy(() -> create("         ", "a.b@c.de", 28));
        }
    }

    @Nested
    class EMailTest {

        @Test
        void emailIsNull() {
            assertThatCode(() -> create("test", null, 28))
                      .doesNotThrowAnyException();
        }

        @Test
        void emailIsEmpty() {
            assertThatCode(() -> create("test", "", 28))
                      .doesNotThrowAnyException();
        }

        @Test
        void emailIsBlank() {
            assertThatExceptionOfType(ValidationException.class)
                      .isThrownBy(() -> create("test", "   ", 28));
        }

        @Test
        void emailIsNoValidFormat() {
            assertThatExceptionOfType(ValidationException.class)
                      .isThrownBy(() -> create("test", "testemail", 28));
        }
    }

    @Nested
    class IntegerTest {

        @Test
        void ageIsNull() {
            assertThatCode(() -> create("test", "a.b@c.de", null))
                      .doesNotThrowAnyException();
        }

        @Test
        void ageIsToSmall() {
            assertThatExceptionOfType(ValidationException.class)
                      .isThrownBy(() -> create("test", "a.b@c.de", 18));
        }

        @Test
        void ageIsToHigh() {
            assertThatExceptionOfType(ValidationException.class)
                      .isThrownBy(() -> create("test", "a.b@c.de", 32));
        }

        @Test
        void ageIsMin() {
            assertThatCode(() -> create("test", "a.b@c.de", 20))
                      .doesNotThrowAnyException();
        }

        @Test
        void ageIsMax() {
            assertThatCode(() -> create("test", "a.b@c.de", 30))
                      .doesNotThrowAnyException();
        }
    }

    public abstract T create(String name, String email, Integer age);
}

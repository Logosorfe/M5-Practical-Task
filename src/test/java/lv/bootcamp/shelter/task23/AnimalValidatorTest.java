package lv.bootcamp.shelter.task23;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tasks 2 & 3: Parameterized tests and exception tests
 * <p>
 * Practice:
 * - @ParameterizedTest with @CsvSource
 * - @ValueSource and @NullAndEmptySource
 * - assertThrows with message checks
 * - AssertJ assertThatThrownBy
 * <p>
 * Instructions:
 * Write tests for AnimalValidator. Each TODO describes one test to write.
 */
@DisplayName("AnimalValidator")
class AnimalValidatorTest {

    private AnimalValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AnimalValidator();
    }

    // ==================== Task 2: Parameterized tests ====================

    @Nested
    @DisplayName("validateName")
    class ValidateName {

        @ParameterizedTest
        @ValueSource(strings = {"Buddy", "Luna", "Mr. Whiskers", "X"})
        @DisplayName("accepts valid names")
        void shouldAcceptValidNames(String name) {
            assertDoesNotThrow(() -> validator.validateName(name));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("rejects blank or null names")
        void shouldRejectBlankNames(String name) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.validateName(name));
            assertThat(ex).hasMessageContaining("must not be blank");
        }

        @Test
        @DisplayName("rejects name longer than 100 characters")
        void shouldRejectOverlyLongName() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.validateName("The path of the righteous man is beset on all sides by the " +
                            "inequities of the selfish and the tyranny of evil men. Blessed is he who, in the " +
                            "name of charity and good will, shepherds the weak through the valley of the " +
                            "darkness, for he is truly his brother's keeper and the finder of lost children. " +
                            "And I will strike down upon thee with great vengeance and furious anger those who " +
                            "attempt to poison and destroy My brothers. And you will know I am the Lord when I " +
                            "lay My vengeance upon you."));
            assertThat(ex).hasMessageContaining("100 characters");
        }
    }

    @Nested
    @DisplayName("validateAge")
    class ValidateAge {

        @ParameterizedTest
        @CsvSource({
                "0",
                "1",
                "10",
                "50"
        })
        @DisplayName("accepts valid ages")
        void shouldAcceptValidAges(int age) {
            assertDoesNotThrow(() -> validator.validateAge(age));
        }

        @ParameterizedTest
        @CsvSource({
                "-1, must not be negative",
                "-100, must not be negative",
                "51, seems unrealistic",
                "999, seems unrealistic"
        })
        @DisplayName("rejects invalid ages with correct message")
        void shouldRejectInvalidAges(int age, String expectedMessagePart) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.validateAge(age));
            assertThat(ex).hasMessageContaining(expectedMessagePart);
        }
    }

    // ==================== Task 3: Exception tests ====================

    @Nested
    @DisplayName("validate (full animal)")
    class ValidateFullAnimal {

        @Test
        @DisplayName("throws NullPointerException for null animal")
        void shouldThrowForNullAnimal() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> validator.validate(null));
            assertThat(ex).hasMessageContaining("must not be null");
        }

        @Test
        @DisplayName("throws for animal with blank name")
        void shouldThrowForBlankName() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateName(new Animal("",
                    "Tiger",
                    13,
                    false,
                    LocalDate.now())
                    .getName()));
        }

        @Test
        @DisplayName("throws for animal with blank species")
        void shouldThrowForBlankSpecies() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateSpecies(new Animal("Rex",
                    "",
                    13,
                    false,
                    LocalDate.now())
                    .getSpecies()));
        }

        @Test
        @DisplayName("throws for animal with negative age")
        void shouldThrowForNegativeAge() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateAge(new Animal("Negativo",
                    "",
                    -13,
                    true,
                    LocalDate.now())
                    .getAge()));
        }

        @Test
        @DisplayName("does not throw for fully valid animal")
        void shouldPassForValidAnimal() {
            assertDoesNotThrow(() -> validator.validate(new Animal("Buddy",
                    "Dog",
                    3,
                    true,
                    LocalDate.now())));
        }
    }
}

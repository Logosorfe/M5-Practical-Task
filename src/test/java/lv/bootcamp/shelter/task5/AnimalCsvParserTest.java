package lv.bootcamp.shelter.task5;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Task 5: Nested test classes for CSV parsing
 * <p>
 * Practice:
 * - @Nested to organize by scenario
 * - @DisplayName for readable output
 * - Testing Optional results (isPresent, isEmpty)
 * - Testing file I/O with temp files
 * <p>
 * Instructions:
 * Write tests for AnimalCsvParser. Use @Nested classes to group tests by scenario.
 * For file-based tests, use Files.createTempFile() and Files.writeString() to create test data.
 */
@DisplayName("AnimalCsvParser")
class AnimalCsvParserTest {

    private AnimalCsvParser parser;

    @BeforeEach
    void setUp() {
        parser = new AnimalCsvParser();
    }

    // ==================== parseRow tests ====================

    @Nested
    @DisplayName("When parsing valid rows")
    class ValidRows {

        @Test
        @DisplayName("parses a complete row into an Animal")
        void shouldParseCompleteRow() {
            Optional<Animal> optionalAnimal = parser.parseRow("Buddy,Dog,3,true,2026-01-15");

            Animal animal = null;
            if (optionalAnimal.isPresent()) {
                animal = optionalAnimal.get();
            }

            assertThat(animal.getName()).isEqualTo("Buddy");
            assertThat(animal.getSpecies()).isEqualTo("Dog");
            assertThat(animal.getAge()).isEqualTo(3);
            assertThat(animal.isVaccinated()).isTrue();
            assertThat(animal.getIntakeDate()).isEqualTo(LocalDate.parse("2026-01-15"));
        }

        @Test
        @DisplayName("trims whitespace from fields")
        void shouldTrimWhitespace() {
            Optional<Animal> optionalAnimal = parser.parseRow("  Buddy , Dog , 3 , true , 2026-01-15 ");

            Animal animal = null;
            if (optionalAnimal.isPresent()) {
                animal = optionalAnimal.get();
            }

            assertThat(animal.getName()).isEqualTo("Buddy");
        }

        @Test
        @DisplayName("parses vaccinated=false correctly")
        void shouldParseFalseVaccination() {
            Optional<Animal> optionalAnimal = parser.parseRow("  Buddy , Dog , 3 , false , 2026-01-15 ");

            Animal animal = null;
            if (optionalAnimal.isPresent()) {
                animal = optionalAnimal.get();
            }

            assertThat(animal.isVaccinated()).isFalse();
        }
    }

    @Nested
    @DisplayName("When parsing malformed rows")
    class MalformedRows {

        @Test
        @DisplayName("returns empty for null input")
        void shouldReturnEmptyForNull() {
            assertThat(parser.parseRow(null)).isEmpty();
        }

        @Test
        @DisplayName("returns empty for blank input")
        void shouldReturnEmptyForBlank() {
            assertThat(parser.parseRow("   ")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when row has fewer than 5 fields")
        void shouldReturnEmptyForTooFewFields() {
            assertThat(parser.parseRow("Buddy,Dog,3")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when name is missing")
        void shouldReturnEmptyForMissingName() {
            assertThat(parser.parseRow(",Dog,3,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when age is not a number")
        void shouldReturnEmptyForBadAge() {
            assertThat(parser.parseRow("Buddy,Dog,old,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when age is negative")
        void shouldReturnEmptyForNegativeAge() {
            assertThat(parser.parseRow("Buddy,Dog,-1,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when date is invalid")
        void shouldReturnEmptyForBadDate() {
            assertThat(parser.parseRow("Buddy,Dog,-1,true,not-a-date")).isEmpty();
        }
    }

    @Nested
    @DisplayName("When handling edge cases")
    class EdgeCases {

        @Test
        @DisplayName("handles vaccinated field as any non-true string → false")
        void shouldTreatNonTrueAsFalse() {
            Optional<Animal> optionalAnimal = parser.parseRow("  Buddy , Dog , 3 , false , 2026-01-15 ");

            Animal animal = null;
            if (optionalAnimal.isPresent()) {
                animal = optionalAnimal.get();
            }

            assertThat(animal.isVaccinated()).isFalse();
        }

        @Test
        @DisplayName("handles age 0 as valid")
        void shouldAcceptAgeZero() {
            Optional<Animal> optionalAnimal = parser.parseRow("  Buddy , Dog , 0 , false , 2026-01-15 ");

            Animal animal = null;
            if (optionalAnimal.isPresent()) {
                animal = optionalAnimal.get();
            }

            assertThat(animal.getAge()).isZero();
        }
    }

    // ==================== parseFile tests ====================

    @Nested
    @DisplayName("When parsing a CSV file")
    class ParseFile {

        @Test
        @DisplayName("parses valid rows and counts skipped rows")
        void shouldParseFileAndCountSkipped() throws IOException {
            Path tempFile = Files.createTempFile("test-intake", ".csv");
            Files.write(tempFile, ("INCOMING\n"
                    + "Daffy,Duck,3,false,2026-01-15\n"
                    + "Sylvester,Cat,12,true,2026-03-09\n"
                    + "Bugs,Bunny,9,false,2026-08-24\n"
                    + "\n").getBytes(StandardCharsets.UTF_8));
            AnimalCsvParser.ParseResult result = parser.parseFile(tempFile);

            assertThat(result.animals().size()).isEqualTo(3);
            assertThat(result.skippedRows()).isEqualTo(1);

            Files.deleteIfExists(tempFile);
        }

        @Test
        @DisplayName("returns empty result for file with only a header")
        void shouldReturnEmptyForHeaderOnly() throws IOException {
            Path tempFile = Files.createTempFile("test-intake", ".csv");
            Files.write(tempFile, ("name,species,age,vaccinated,intakeDate").getBytes(StandardCharsets.UTF_8));
            AnimalCsvParser.ParseResult result = parser.parseFile(tempFile);

            assertThat(result.animals()).isEmpty();
            assertThat(result.skippedRows()).isZero();

            Files.deleteIfExists(tempFile);
        }

        @Test
        @DisplayName("throws IOException for non-existent file")
        void shouldThrowForMissingFile() {
            assertThrows(IOException.class, () -> parser.parseFile(Path.of("does-not-exist.csv")));
        }
    }
}

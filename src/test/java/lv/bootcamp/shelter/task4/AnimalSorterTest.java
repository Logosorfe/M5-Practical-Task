package lv.bootcamp.shelter.task4;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Task 4: Collection and sorting tests
 * <p>
 * Practice:
 * - AssertJ list assertions (extracting, containsExactly)
 * - Testing sort order
 * - Testing null/empty input handling
 * <p>
 * Instructions:
 * Write tests for AnimalSorter. Use AssertJ's extracting() and containsExactly()
 * to verify the order of results.
 */
@DisplayName("AnimalSorter")
class AnimalSorterTest {

    private AnimalSorter sorter;

    private Animal buddy;
    private Animal luna;
    private Animal max;
    private Animal bella;

    @BeforeEach
    void setUp() {
        sorter = new AnimalSorter();
        buddy = new Animal("Buddy", "Dog", 3, true, LocalDate.of(2026, 1, 15));
        luna = new Animal("Luna", "Cat", 2, true, LocalDate.of(2026, 1, 10));
        max = new Animal("Max", "Dog", 5, false, LocalDate.of(2026, 1, 20));
        bella = new Animal("Bella", "Cat", 1, true, LocalDate.of(2026, 1, 5));
    }

    // --- sortByAge ---

    @Test
    @DisplayName("sortByAge: returns animals ordered youngest to oldest")
    void shouldSortByAgeAscending() {
        assertThat(sorter.sortByAge(List.of(buddy, luna, max, bella))).extracting(Animal::getName)
                .containsExactly("Bella", "Luna", "Buddy", "Max");
    }

    @Test
    @DisplayName("sortByAge: returns empty list for null input")
    void shouldReturnEmptyForNullInput() {
        assertThat(sorter.sortByAge(null)).isEqualTo(List.of());
    }

    @Test
    @DisplayName("sortByAge: returns empty list for empty input")
    void shouldReturnEmptyForEmptyInput() {
        assertThat(sorter.sortByAge(List.of())).isEmpty();
    }

    @Test
    @DisplayName("sortByAge: does not modify the original list")
    void shouldNotModifyOriginalList() {
        List<Animal> original = new ArrayList<>(List.of(buddy, luna, max, bella));
        sorter.sortByAge(original);
        assertThat(original).isEqualTo(new ArrayList<>(List.of(buddy, luna, max, bella)));
    }

    // --- sortByName ---

    @Test
    @DisplayName("sortByName: returns animals in alphabetical order")
    void shouldSortByNameAlphabetically() {
        assertThat(sorter.sortByName(List.of(buddy, luna, max, bella)))
                .containsExactly(bella, buddy, luna, max);
    }

    @Test
    @DisplayName("sortByName: is case-insensitive")
    void shouldSortNamesCaseInsensitively() {
        Animal zebra = new Animal("zebra", "Zebra", 3, true, LocalDate.of(2026, 1, 15));
        Animal alpha = new Animal("Alpha", "Dog", 2, true, LocalDate.of(2026, 1, 10));
        assertThat(sorter.sortByName(List.of(zebra, alpha))).extracting(Animal::getName)
                .containsExactly("Alpha", "zebra");
    }

    // --- sortByIntakeDate ---

    @Test
    @DisplayName("sortByIntakeDate: returns animals from earliest to latest")
    void shouldSortByIntakeDateAscending() {
        assertThat(sorter.sortByIntakeDate(List.of(buddy, luna, max, bella))).extracting(Animal::getIntakeDate)
                .containsExactly(bella.getIntakeDate(), luna.getIntakeDate(), buddy.getIntakeDate(),
                        max.getIntakeDate());
    }

    // --- sortBySpeciesThenAgeDescending ---

    @Test
    @DisplayName("sortBySpeciesThenAgeDescending: groups by species then orders by age desc")
    void shouldSortBySpeciesThenAgeDesc() {
        assertThat(sorter.sortBySpeciesThenAgeDescending(List.of(buddy, luna, max, bella)))
                .extracting(Animal::getName)
                .containsExactly("Luna", "Bella", "Max", "Buddy");
    }
}

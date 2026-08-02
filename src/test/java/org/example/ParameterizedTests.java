package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ParameterizedTests {

    // -------------------------
    // ValueSource
    // -------------------------
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    void testValueSource(int quantity) {

        MenuItem item = new MenuItem("Burger", 100);
        item.quantity = quantity;

        assertTrue(item.getSubtotal() > 0);
    }

    // -------------------------
    // CsvSource
    // -------------------------
    @ParameterizedTest
    @CsvSource({
            "1,100",
            "2,200",
            "3,300",
            "5,500"
    })
    void testCsvSource(int quantity, int expectedSubtotal) {

        MenuItem item = new MenuItem("Burger", 100);
        item.quantity = quantity;

        assertEquals(expectedSubtotal, item.getSubtotal());
    }

    // -------------------------
    // MethodSource
    // -------------------------
    static Stream<org.junit.jupiter.params.provider.Arguments> dataProvider() {

        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(1,100),
                org.junit.jupiter.params.provider.Arguments.of(2,200),
                org.junit.jupiter.params.provider.Arguments.of(4,400)
        );
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void testMethodSource(int quantity, int expected) {

        MenuItem item = new MenuItem("Pizza",100);
        item.quantity = quantity;

        assertEquals(expected,item.getSubtotal());

    }

    // -------------------------
    // CsvFileSource
    // -------------------------
    @ParameterizedTest
    @CsvFileSource(resources = "/menu.csv", numLinesToSkip = 1)
    void testCsvFileSource(int quantity,int expectedSubtotal) {

        MenuItem item = new MenuItem("Drinks",100);

        item.quantity = quantity;

        assertEquals(expectedSubtotal,item.getSubtotal());

    }

}
package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for OrderValidator - the logic extracted out of
 * MUFoodCornerAdvanced.confirmOrder() so it's testable without a live
 * Swing UI. Includes boundary value analysis on the phone-length rule
 * (valid range: 7-15 characters).
 */
class OrderValidatorTest {

    OrderValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OrderValidator();
    }

    @Test
    @DisplayName("TC26 - required fields: all filled returns true")
    void testRequiredFieldsAllFilled() {
        assertTrue(validator.areRequiredFieldsFilled("Nish", "01711111111", "Sylhet"));
    }

    @Test
    @DisplayName("TC27 - required fields: missing name returns false")
    void testRequiredFieldsMissingName() {
        assertFalse(validator.areRequiredFieldsFilled("", "01711111111", "Sylhet"));
    }

    @Test
    @DisplayName("TC28 - required fields: whitespace-only phone counts as missing")
    void testRequiredFieldsBlankPhone() {
        assertFalse(validator.areRequiredFieldsFilled("Nish", "   ", "Sylhet"));
    }

    @Test
    @DisplayName("TC29 - phone boundary: 6 characters is below the minimum, rejected")
    void testPhoneBelowMinLength() {
        assertFalse(validator.isValidPhone("123456")); // 6 chars
    }

    @Test
    @DisplayName("TC30 - phone boundary: 7 characters is the minimum, accepted")
    void testPhoneAtMinLength() {
        assertTrue(validator.isValidPhone("1234567")); // 7 chars
    }

    @Test
    @DisplayName("TC31 - phone boundary: 15 characters is the maximum, accepted")
    void testPhoneAtMaxLength() {
        assertTrue(validator.isValidPhone("123456789012345")); // 15 chars
    }

    @Test
    @DisplayName("TC32 - phone boundary: 16 characters is above the maximum, rejected")
    void testPhoneAboveMaxLength() {
        assertFalse(validator.isValidPhone("1234567890123456")); // 16 chars
    }

    @Test
    @DisplayName("TC33 - empty cart: no item has quantity > 0, returns false")
    void testHasItemsInCartWhenCartEmpty() {
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem("Burger", 100); // quantity defaults to 0
        items.add(item);

        assertFalse(validator.hasItemsInCart(items));
    }

    @Test
    @DisplayName("TC34 - non-empty cart: at least one item has quantity > 0, returns true")
    void testHasItemsInCartWhenItemSelected() {
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem("Burger", 100);
        item.quantity = 1;
        items.add(item);

        assertTrue(validator.hasItemsInCart(items));
    }
}

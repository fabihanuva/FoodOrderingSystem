package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the REAL DefaultDiscountService (no mocking) - this is the class
 * actually instantiated and used by MUFoodCornerAdvanced in production, so
 * these tests give decision/condition coverage for the three promo branches
 * that OrderServiceMockTest's mocked DiscountService cannot exercise.
 */
class DefaultDiscountServiceTest {

    DefaultDiscountService discountService;

    @BeforeEach
    void setUp() {
        discountService = new DefaultDiscountService();
    }

    @Test
    @DisplayName("TC35 - MU50 branch: flat 50 discount regardless of subtotal")
    void testMu50GivesFlatFiftyDiscount() {
        assertEquals(50, discountService.getDiscount("MU50", 500));
        assertEquals(50, discountService.getDiscount("mu50", 20)); // case-insensitive
    }

    @Test
    @DisplayName("TC36 - OFF10 branch: 10 percent of subtotal")
    void testOff10GivesTenPercentDiscount() {
        assertEquals(20, discountService.getDiscount("OFF10", 200));
        assertEquals(0, discountService.getDiscount("off10", 0));
    }

    @Test
    @DisplayName("TC37 - unrecognized code branch: no discount applied")
    void testUnknownCodeGivesZeroDiscount() {
        assertEquals(0, discountService.getDiscount("NOTAREALCODE", 500));
    }

    @Test
    @DisplayName("TC38 - null/blank code: no discount applied, no exception thrown")
    void testNullOrBlankCodeGivesZeroDiscount() {
        assertEquals(0, discountService.getDiscount(null, 500));
        assertEquals(0, discountService.getDiscount("   ", 500));
    }
}

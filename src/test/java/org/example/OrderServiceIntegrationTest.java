package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test: OrderService wired to the REAL DefaultDiscountService
 * (no mocks), producing a real Order. This is what actually runs in
 * production now that MUFoodCornerAdvanced.confirmOrder() calls
 * orderService.placeOrder() directly - distinct from OrderServiceMockTest,
 * which isolates OrderService with a fake DiscountService for unit-level
 * verification.
 */
class OrderServiceIntegrationTest {

    OrderService orderService;
    ArrayList<MenuItem> items;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(new DefaultDiscountService());

        items = new ArrayList<>();
        MenuItem burger = new MenuItem("Burger", 100);
        burger.quantity = 2; // subtotal = 200
        items.add(burger);
    }

    @Test
    @DisplayName("TC39 - end-to-end: real MU50 promo code reduces the real order total by 50")
    void testRealMu50DiscountAppliedThroughFullStack() {
        Order order = orderService.placeOrder(items, "Nish", "01711111111", "Sylhet", "MU50");

        assertEquals(50, order.discount);
        assertEquals(150, order.total);
    }

    @Test
    @DisplayName("TC40 - end-to-end: no promo code leaves the real order total unchanged")
    void testNoPromoCodeThroughFullStack() {
        Order order = orderService.placeOrder(items, "Nish", "01711111111", "Sylhet", "");

        assertEquals(0, order.discount);
        assertEquals(200, order.total);
    }
}

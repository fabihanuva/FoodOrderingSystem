package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// @ExtendWith(MockitoExtension.class) initializes @Mock/@InjectMocks fields
// automatically before each test - no manual MockitoAnnotations.openMocks() needed.
@ExtendWith(MockitoExtension.class)
class OrderServiceMockTest {

    // A fake DiscountService - we control exactly what it returns, we never
    // run the real MU50/OFF10 logic in these tests.
    @Mock
    DiscountService discountService;

    // Mockito builds an OrderService and injects the @Mock above through its
    // constructor (OrderService(DiscountService)).
    @InjectMocks
    OrderService orderService;

    ArrayList<MenuItem> items;

    @BeforeEach
    void setUp() {
        items = new ArrayList<>();
        MenuItem burger = new MenuItem("Burger", 100);
        burger.quantity = 2; // subtotal = 200
        items.add(burger);
    }

    @Test
    @DisplayName("TC11 - when/thenReturn: mocked discount is applied to the order total")
    void testPlaceOrderAppliesMockedDiscount() {
        when(discountService.getDiscount("MU50", 200)).thenReturn(50);

        Order order = orderService.placeOrder(items, "Nish", "01711111111", "Sylhet", "MU50");

        assertEquals(150, order.total, "200 subtotal - 50 mocked discount = 150");
    }

    @Test
    @DisplayName("TC12 - when/thenReturn: zero discount leaves the total unchanged")
    void testPlaceOrderWithNoDiscount() {
        when(discountService.getDiscount("BADCODE", 200)).thenReturn(0);

        Order order = orderService.placeOrder(items, "Nish", "01711111111", "Sylhet", "BADCODE");

        assertEquals(200, order.total);
    }

    @Test
    @DisplayName("TC13 - verify: discountService is called exactly once with the right arguments")
    void testDiscountServiceIsCalledWithCorrectArgs() {
        when(discountService.getDiscount(anyString(), anyInt())).thenReturn(0);

        orderService.placeOrder(items, "Nish", "01711111111", "Sylhet", "OFF10");

        verify(discountService, times(1)).getDiscount("OFF10", 200);
    }

    @Test
    @DisplayName("TC14 - verify: discountService is never consulted for an empty cart")
    void testDiscountServiceNotCalledForEmptyCart() {
        orderService.placeOrder(new ArrayList<>(), "Nish", "01711111111", "Sylhet", "MU50");

        verify(discountService, never()).getDiscount(anyString(), anyInt());
    }
}

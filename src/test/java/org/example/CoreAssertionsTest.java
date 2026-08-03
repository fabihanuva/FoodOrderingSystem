package org.example;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CoreAssertionsTest {

    // Shared across every test in this class - built once, not per test.
    static ArrayList<MenuItem> sharedMenu;

    @BeforeAll
    static void initSharedMenu() {
        sharedMenu = new ArrayList<>();
        sharedMenu.add(new MenuItem("Khichuri", 40));
        sharedMenu.add(new MenuItem("Shingara", 10));
        System.out.println("@BeforeAll: shared menu built once for the whole class");
    }

    @AfterAll
    static void tearDownSharedMenu() {
        sharedMenu.clear();
        System.out.println("@AfterAll: shared menu cleared once after all tests finish");
    }

    @Test
    @DisplayName("TC05 - assertTrue: menu item price is positive")
    void testAssertTruePriceIsPositive() {
        MenuItem item = new MenuItem("Drinks", 25);
        assertTrue(item.price > 0, "Price should be greater than 0");
    }

    @Test
    @DisplayName("TC06 - assertFalse: freshly created item has not been ordered yet")
    void testAssertFalseFreshItemHasNoQuantity() {
        MenuItem item = new MenuItem("Eggchop", 10);
        assertFalse(item.quantity > 0, "A brand new item should start with 0 quantity");
    }

    @Test
    @DisplayName("TC07 - assertNull: no order exists before one is constructed")
    void testAssertNullBeforeOrderIsCreated() {
        Order order = null;
        assertNull(order, "Order reference should be null before construction");
    }

    @Test
    @DisplayName("TC08 - assertNotNull: constructing an order yields a real instance with a status")
    void testAssertNotNullAfterOrderIsCreated() {
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem("Burger", 100);
        item.quantity = 1;
        items.add(item);

        Order order = new Order(items, "Nish", "01711111111", "Sylhet", 0);

        assertNotNull(order, "Order should not be null after construction");
        assertNotNull(order.status, "A newly placed order should have a status ('Pending')");
    }

    @Test
    @DisplayName("TC09 - assertArrayEquals: menu item names match the expected order")
    void testAssertArrayEqualsMenuNames() {
        String[] expected = {"Khichuri", "Shingara"};
        String[] actual = sharedMenu.stream().map(m -> m.name).toArray(String[]::new);
        assertArrayEquals(expected, actual, "Menu item names should match in name and order");
    }

    @Test
    @DisplayName("TC10 - ArithmeticException: dividing by zero orders throws")
    void testArithmeticExceptionOnDivideByZero() {
        MenuItem item = new MenuItem("Burger", 100);
        item.quantity = 3;
        int subtotal = item.getSubtotal();
        int zeroOrders = 0;

        assertThrows(ArithmeticException.class, () -> {
            int average = subtotal / zeroOrders; // "/ by zero" -> ArithmeticException
        });
    }
}

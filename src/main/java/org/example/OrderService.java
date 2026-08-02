package org.example;

import java.util.ArrayList;

/**
 * Builds an Order from a cart of MenuItems, delegating discount calculation
 * to an injected DiscountService. Constructor injection makes this class
 * easy to unit test with a mocked DiscountService instead of the real one.
 */
public class OrderService {

    private final DiscountService discountService;

    public OrderService(DiscountService discountService) {
        this.discountService = discountService;
    }

    public Order placeOrder(ArrayList<MenuItem> items, String name, String phone, String address, String promoCode) {
        int subtotal = 0;
        for (MenuItem item : items) {
            subtotal += item.getSubtotal();
        }

        int discount = 0;
        if (!items.isEmpty()) {
            discount = discountService.getDiscount(promoCode, subtotal);
        }

        return new Order(items, name, phone, address, discount);
    }
}

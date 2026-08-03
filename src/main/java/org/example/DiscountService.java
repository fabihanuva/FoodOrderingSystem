package org.example;

/**
 * Calculates the discount to apply to an order given a promo code and subtotal.
 * Pulled out of MUFoodCornerAdvanced.getDiscount() so promo logic is testable
 * (and mockable) on its own, separate from the Swing UI.
 */
public interface DiscountService {
    int getDiscount(String promoCode, int subtotal);
}

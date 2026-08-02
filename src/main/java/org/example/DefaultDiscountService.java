package org.example;

/**
 * Default promo rules, matching the ones previously hardcoded inside
 * MUFoodCornerAdvanced.getDiscount(): MU50 = flat TK 50 off, OFF10 = 10% off.
 */
public class DefaultDiscountService implements DiscountService {
    @Override
    public int getDiscount(String promoCode, int subtotal) {
        String p = (promoCode == null) ? "" : promoCode.trim().toUpperCase();
        if (p.equals("MU50")) return 50;
        if (p.equals("OFF10")) return (int) (subtotal * 0.1);
        return 0;
    }
}

package org.example;

import java.util.ArrayList;

/**
 * Validation rules for placing an order, extracted out of
 * MUFoodCornerAdvanced.confirmOrder() so they can be unit tested without a
 * live Swing UI. No Swing dependency - pure logic, same rules as before.
 */
public class OrderValidator {

    private static final String PHONE_PATTERN = "^[0-9+\\-\\s]{7,15}$";

    public boolean areRequiredFieldsFilled(String name, String phone, String address) {
        return isFilled(name) && isFilled(phone) && isFilled(address);
    }

    public boolean isValidPhone(String phone) {
        return phone != null && phone.trim().matches(PHONE_PATTERN);
    }

    public boolean hasItemsInCart(ArrayList<MenuItem> items) {
        if (items == null) return false;
        for (MenuItem item : items) {
            if (item.quantity > 0) return true;
        }
        return false;
    }

    private boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

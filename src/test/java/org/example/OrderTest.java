package org.example;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    void testAssertDoesNotThrow() {

        ArrayList<MenuItem> items = new ArrayList<>();

        MenuItem item = new MenuItem("Burger",100);
        item.quantity = 2;

        items.add(item);

        assertDoesNotThrow(() -> {
            new Order(items,"Nish","01711111111","Sylhet",0);
        });

    }

    @Test
    void testAssertTimeout() {

        ArrayList<MenuItem> items = new ArrayList<>();

        MenuItem item = new MenuItem("Pizza",200);
        item.quantity = 1;

        items.add(item);

        assertTimeout(Duration.ofSeconds(1), () -> {

            Order order = new Order(items,"Nish","01711111111","Sylhet",0);

            order.getSummary();

        });

    }

    @Test
    void testAssertThrows() {

        assertThrows(NullPointerException.class, () -> {

            ArrayList<MenuItem> items = null;

            Order order = new Order(items,"Nish","01711111111","Sylhet",0);

            order.getSummary();

        });

    }

}
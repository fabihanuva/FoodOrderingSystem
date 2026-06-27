import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    @Test
    void testCalculateTotalWithDiscount() {

        ArrayList<MenuItem> items = new ArrayList<>();

        MenuItem burger = new MenuItem("Burger", 200);
        burger.quantity = 2;     //400

        MenuItem pizza = new MenuItem("Pizza", 300);
        pizza.quantity = 1;      //300

        items.add(burger);
        items.add(pizza);

        Order order = new Order(
                items,
                "Nish",
                "01700000000",
                "Sylhet",
                100
        );

        assertEquals(600, order.total);
    }

    @Test
    void testDefaultStatusIsPending() {

        ArrayList<MenuItem> items = new ArrayList<>();

        MenuItem coffee = new MenuItem("Coffee",100);
        coffee.quantity = 1;

        items.add(coffee);

        Order order = new Order(
                items,
                "Nish",
                "01700000000",
                "Sylhet",
                0
        );

        assertEquals("Pending", order.status);
    }



}





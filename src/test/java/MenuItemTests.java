import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MenuItemTests {

    MenuItem item;

    @BeforeEach
    void setUp() {
        item = new MenuItem("Burger", 100);
        System.out.println("Before Each Test");
    }
    @AfterEach
    void tearDown() {
        System.out.println("After Each Test");
    }

    @Test
    void testAssertEquals() {

        item.quantity = 2;

        assertEquals(200, item.getSubtotal());
    }

    @Test
    void testAssertNotEquals() {

        item.quantity = 2;

        assertNotEquals(100, item.getSubtotal());
    }

    @Test
    void testAssertSame() {

        MenuItem sameItem = item;

        assertSame(item, sameItem);
    }

    @Test
    void testAssertNotSame() {

        MenuItem anotherItem = new MenuItem("Burger",100);

        assertNotSame(item, anotherItem);
    }




}
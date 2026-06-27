import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    @DisplayName("TC01 - Constructor sets name correctly")
    void testConstructorSetsName() {
        MenuItem item = new MenuItem("Khichuri", 40);
        assertEquals("Khichuri", item.name);
    }

    @Test
    @DisplayName("TC02 - Constructor sets price correctly")
    void testConstructorSetsPrice() {
        MenuItem item = new MenuItem("Shingara", 10);
        assertEquals(10, item.price);
    }

    @Test
    @DisplayName("TC03 - Constructor always initializes quantity to 0")
    void testConstructorSetsQuantityToZero() {
        MenuItem item = new MenuItem("Drinks", 25);
        assertEquals(0, item.quantity);
    }

    @Test
    @DisplayName("TC04 - Subtotal is 0 when quantity is 0 (default)")
    void testSubtotalIsZeroByDefault() {
        MenuItem item = new MenuItem("Eggchop", 10);
        assertEquals(0, item.getSubtotal());
    }
}

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
}

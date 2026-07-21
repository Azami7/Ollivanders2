package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.DE_MEDICINA_PRAECEPTA;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link DE_MEDICINA_PRAECEPTA}.
 */
public class DeMedicinaPraeceptaTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new DE_MEDICINA_PRAECEPTA(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
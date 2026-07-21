package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.MODERN_MAGICAL_TRANSPORTATION;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link MODERN_MAGICAL_TRANSPORTATION}.
 */
public class ModernMagicalTransportationTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new MODERN_MAGICAL_TRANSPORTATION(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

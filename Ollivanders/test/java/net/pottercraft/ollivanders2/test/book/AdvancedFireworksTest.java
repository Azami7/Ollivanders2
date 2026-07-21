package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.ADVANCED_FIREWORKS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link ADVANCED_FIREWORKS}.
 */
public class AdvancedFireworksTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new ADVANCED_FIREWORKS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
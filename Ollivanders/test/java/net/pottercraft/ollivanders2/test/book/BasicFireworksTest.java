package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.BASIC_FIREWORKS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link BASIC_FIREWORKS}.
 */
public class BasicFireworksTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new BASIC_FIREWORKS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
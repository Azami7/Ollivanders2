package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.CHARMING_COLORS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link CHARMING_COLORS}.
 */
public class CharmingColorsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new CHARMING_COLORS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
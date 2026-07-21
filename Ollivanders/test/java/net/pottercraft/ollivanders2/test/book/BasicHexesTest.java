package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.BASIC_HEXES;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link BASIC_HEXES}.
 */
public class BasicHexesTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new BASIC_HEXES(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
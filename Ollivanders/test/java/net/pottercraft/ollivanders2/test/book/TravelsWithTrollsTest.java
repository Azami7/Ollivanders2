package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.TRAVELS_WITH_TROLLS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link TRAVELS_WITH_TROLLS}.
 */
public class TravelsWithTrollsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new TRAVELS_WITH_TROLLS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.HOLIDAYS_WITH_HAGS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link HOLIDAYS_WITH_HAGS}.
 */
public class HolidaysWithHagsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new HOLIDAYS_WITH_HAGS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
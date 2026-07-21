package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.YEAR_WITH_A_YETI;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link YEAR_WITH_A_YETI}.
 */
public class YearWithAYetiTest extends  BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new YEAR_WITH_A_YETI(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
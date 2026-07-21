package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.THE_HEALERS_HELPMATE;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link THE_HEALERS_HELPMATE}.
 */
public class TheHealersHelpmateTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new THE_HEALERS_HELPMATE(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

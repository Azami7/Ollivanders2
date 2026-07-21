package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.BREAK_WITH_A_BANSHEE;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link BREAK_WITH_A_BANSHEE}.
 */
public class BreakWithABansheeTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new BREAK_WITH_A_BANSHEE(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.PRACTICAL_DEFENSIVE_MAGIC;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link PRACTICAL_DEFENSIVE_MAGIC}.
 */
public class PracticalDefensiveMagicTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new PRACTICAL_DEFENSIVE_MAGIC(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
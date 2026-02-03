package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.PRACTICAL_DEFENSIVE_MAGIC;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for PRACTICAL_DEFENSIVE_MAGIC that focus on Minecraft book constraints.
 * <p>
 * Minecraft Book Limits:<br>
 * - Title: 32 characters max<br>
 * - Pages: 50 max<br>
 * - Characters per page: 256 max (newlines count as 2)<br>
 * - Lines per page: 14 max<br>
 * </p>
 */
public class PracticalDefensiveMagicTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new PRACTICAL_DEFENSIVE_MAGIC(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
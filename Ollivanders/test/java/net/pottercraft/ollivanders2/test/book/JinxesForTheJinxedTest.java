package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.JINXES_FOR_THE_JINXED;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for JINXES_FOR_THE_JINXED that focus on Minecraft book constraints.
 * <p>
 * Minecraft Book Limits:<br>
 * - Title: 32 characters max<br>
 * - Pages: 50 max<br>
 * - Characters per page: 256 max (newlines count as 2)<br>
 * - Lines per page: 14 max<br>
 * </p>
 */
public class JinxesForTheJinxedTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new JINXES_FOR_THE_JINXED(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
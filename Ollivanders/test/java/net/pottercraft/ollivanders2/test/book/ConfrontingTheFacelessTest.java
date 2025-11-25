package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.CONFRONTING_THE_FACELESS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for CONFRONTING_THE_FACELESS that focus on Minecraft book constraints.
 * <p>
 * Minecraft Book Limits:<br>
 * - Title: 32 characters max<br>
 * - Pages: 50 max<br>
 * - Characters per page: 256 max (newlines count as 2)<br>
 * - Lines per page: 14 max<br>
 * </p>
 */
public class ConfrontingTheFacelessTest extends BookTestSuper {
    @BeforeEach
    void setUp() {
        book = new CONFRONTING_THE_FACELESS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
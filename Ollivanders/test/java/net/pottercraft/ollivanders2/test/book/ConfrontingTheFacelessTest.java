package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.CONFRONTING_THE_FACELESS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link CONFRONTING_THE_FACELESS}.
 */
public class ConfrontingTheFacelessTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new CONFRONTING_THE_FACELESS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.BOOK_OF_POTIONS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link BOOK_OF_POTIONS}.
 */
public class BookOfPotionsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new BOOK_OF_POTIONS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

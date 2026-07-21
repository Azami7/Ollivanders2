package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.FOR_THE_GREATER_GOOD;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link FOR_THE_GREATER_GOOD}.
 */
public class ForTheGreaterGoodTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new FOR_THE_GREATER_GOOD(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

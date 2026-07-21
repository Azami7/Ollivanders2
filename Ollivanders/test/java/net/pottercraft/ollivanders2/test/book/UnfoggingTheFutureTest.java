package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.UNFOGGING_THE_FUTURE;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link UNFOGGING_THE_FUTURE}.
 */
public class UnfoggingTheFutureTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new UNFOGGING_THE_FUTURE(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

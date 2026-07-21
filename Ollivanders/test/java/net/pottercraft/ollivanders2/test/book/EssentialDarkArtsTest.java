package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.ESSENTIAL_DARK_ARTS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link ESSENTIAL_DARK_ARTS}.
 */
public class EssentialDarkArtsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new ESSENTIAL_DARK_ARTS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.MAGICK_MOSTE_EVILE;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link MAGICK_MOSTE_EVILE}.
 */
public class MagickMosteEvileTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new MAGICK_MOSTE_EVILE(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

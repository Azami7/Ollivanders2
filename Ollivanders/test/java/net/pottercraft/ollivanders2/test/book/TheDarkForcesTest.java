package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.THE_DARK_FORCES;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link THE_DARK_FORCES}.
 */
public class TheDarkForcesTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new THE_DARK_FORCES(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
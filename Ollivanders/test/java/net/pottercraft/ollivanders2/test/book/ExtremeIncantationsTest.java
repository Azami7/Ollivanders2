package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.EXTREME_INCANTATIONS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link EXTREME_INCANTATIONS}.
 */
public class ExtremeIncantationsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new EXTREME_INCANTATIONS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
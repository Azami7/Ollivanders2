package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.ADVANCED_TRANSFIGURATION;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link ADVANCED_TRANSFIGURATION}.
 */
public class AdvancedTransfigurationTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new ADVANCED_TRANSFIGURATION(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
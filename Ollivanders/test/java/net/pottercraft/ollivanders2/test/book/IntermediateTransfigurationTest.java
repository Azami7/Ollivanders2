package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.INTERMEDIATE_TRANSFIGURATION;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link INTERMEDIATE_TRANSFIGURATION}.
 */
public class IntermediateTransfigurationTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new INTERMEDIATE_TRANSFIGURATION(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
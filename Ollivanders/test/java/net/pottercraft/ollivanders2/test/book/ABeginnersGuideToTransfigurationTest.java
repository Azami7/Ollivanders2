package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.A_BEGINNERS_GUIDE_TO_TRANSFIGURATION;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link A_BEGINNERS_GUIDE_TO_TRANSFIGURATION}.
 */
public class ABeginnersGuideToTransfigurationTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
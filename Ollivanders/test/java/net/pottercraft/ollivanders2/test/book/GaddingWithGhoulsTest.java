package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.GADDING_WITH_GHOULS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link GADDING_WITH_GHOULS}.
 */
public class GaddingWithGhoulsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new GADDING_WITH_GHOULS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
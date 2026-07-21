package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.POTION_OPUSCULE;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link POTION_OPUSCULE}.
 */
public class PotionOpusculeTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new POTION_OPUSCULE(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

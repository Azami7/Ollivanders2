package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.ADVANCED_POTION_MAKING;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link ADVANCED_POTION_MAKING}.
 */
public class AdvancedPotionMakingTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new ADVANCED_POTION_MAKING(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
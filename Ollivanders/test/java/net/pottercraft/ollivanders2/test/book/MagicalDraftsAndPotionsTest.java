package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.MAGICAL_DRAFTS_AND_POTIONS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link MAGICAL_DRAFTS_AND_POTIONS}.
 */
public class MagicalDraftsAndPotionsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new MAGICAL_DRAFTS_AND_POTIONS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
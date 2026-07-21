package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.ACHIEVEMENTS_IN_CHARMING;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link ACHIEVEMENTS_IN_CHARMING}.
 */
public class AchievementsInCharmingTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new ACHIEVEMENTS_IN_CHARMING(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
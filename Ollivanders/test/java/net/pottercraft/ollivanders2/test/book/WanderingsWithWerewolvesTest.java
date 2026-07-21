package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.WANDERINGS_WITH_WEREWOLVES;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link WANDERINGS_WITH_WEREWOLVES}.
 */
public class WanderingsWithWerewolvesTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new WANDERINGS_WITH_WEREWOLVES(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
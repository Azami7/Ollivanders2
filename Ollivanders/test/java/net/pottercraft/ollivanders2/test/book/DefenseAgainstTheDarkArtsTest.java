package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.DEFENSE_AGAINST_THE_DARK_ARTS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link DEFENSE_AGAINST_THE_DARK_ARTS}.
 */
public class DefenseAgainstTheDarkArtsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new DEFENSE_AGAINST_THE_DARK_ARTS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
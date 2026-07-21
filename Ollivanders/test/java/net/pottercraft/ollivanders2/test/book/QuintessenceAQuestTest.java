package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.QUINTESSENCE_A_QUEST;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link QUINTESSENCE_A_QUEST}.
 */
public class QuintessenceAQuestTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new QUINTESSENCE_A_QUEST(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
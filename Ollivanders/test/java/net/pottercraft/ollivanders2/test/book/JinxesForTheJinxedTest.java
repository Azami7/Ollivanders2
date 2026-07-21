package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.JINXES_FOR_THE_JINXED;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link JINXES_FOR_THE_JINXED}.
 */
public class JinxesForTheJinxedTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new JINXES_FOR_THE_JINXED(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
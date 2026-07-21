package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.CHADWICKS_CHARMS_VOLUME_1;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link CHADWICKS_CHARMS_VOLUME_1}.
 */
public class ChadwicksCharmsVolume1Test extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new CHADWICKS_CHARMS_VOLUME_1(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
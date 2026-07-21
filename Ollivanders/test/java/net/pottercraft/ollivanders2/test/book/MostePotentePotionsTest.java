package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.MOSTE_POTENTE_POTIONS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link MOSTE_POTENTE_POTIONS}.
 */
public class MostePotentePotionsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new MOSTE_POTENTE_POTIONS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.NUMEROLOGY_AND_GRAMMATICA;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link NUMEROLOGY_AND_GRAMMATICA}.
 */
public class NumerologyAndGrammaticaTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new NUMEROLOGY_AND_GRAMMATICA(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

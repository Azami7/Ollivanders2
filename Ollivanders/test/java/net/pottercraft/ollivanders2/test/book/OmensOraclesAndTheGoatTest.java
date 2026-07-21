package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.OMENS_ORACLES_AND_THE_GOAT;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link OMENS_ORACLES_AND_THE_GOAT}.
 */
public class OmensOraclesAndTheGoatTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new OMENS_ORACLES_AND_THE_GOAT(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

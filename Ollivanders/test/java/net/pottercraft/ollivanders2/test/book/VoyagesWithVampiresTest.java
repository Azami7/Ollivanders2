package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.VOYAGES_WITH_VAMPIRES;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link VOYAGES_WITH_VAMPIRES}.
 */
public class VoyagesWithVampiresTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new VOYAGES_WITH_VAMPIRES(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
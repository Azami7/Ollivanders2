package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.TETRABIBLIOS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link TETRABIBLIOS}.
 */
public class TetrabibliosTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new TETRABIBLIOS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

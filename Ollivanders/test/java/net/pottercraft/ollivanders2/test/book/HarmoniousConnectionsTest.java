package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.HARMONIOUS_CONNECTIONS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link HARMONIOUS_CONNECTIONS}.
 */
public class HarmoniousConnectionsTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new HARMONIOUS_CONNECTIONS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

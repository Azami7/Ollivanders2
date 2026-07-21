package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.SECRETS_OF_WANDLORE;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link SECRETS_OF_WANDLORE}.
 */
public class SecretsOfWandloreTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new SECRETS_OF_WANDLORE(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}

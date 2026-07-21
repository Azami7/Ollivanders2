package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.SECRETS_OF_THE_DARKEST_ART;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link SECRETS_OF_THE_DARKEST_ART}.
 */
public class SecretsOfTheDarkestArtTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new SECRETS_OF_THE_DARKEST_ART(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
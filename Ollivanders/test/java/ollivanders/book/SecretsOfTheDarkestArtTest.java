package ollivanders.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.book.SECRETS_OF_THE_DARKEST_ART;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;

/**
 * Unit tests for SECRETS_OF_THE_DARKEST_ART that focus on Minecraft book constraints.
 * <p>
 * Minecraft Book Limits:<br>
 * - Title: 32 characters max<br>
 * - Pages: 50 max<br>
 * - Characters per page: 256 max (newlines count as 2)<br>
 * - Lines per page: 14 max<br>
 * </p>
 */
public class SecretsOfTheDarkestArtTest extends BookTestSuper {
    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        Ollivanders2 testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/book_config.yml"));

        book = new SECRETS_OF_THE_DARKEST_ART(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
package ollivanders.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.book.DEFENSE_AGAINST_THE_DARK_ARTS;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;

/**
 * Unit tests for DEFENSE_AGAINST_THE_DARK_ARTS that focus on Minecraft book constraints.
 * <p>
 * Minecraft Book Limits:<br>
 * - Title: 32 characters max<br>
 * - Pages: 50 max<br>
 * - Characters per page: 256 max (newlines count as 2)<br>
 * - Lines per page: 14 max<br>
 * </p>
 */
public class DefenseAgainstTheDarkArtsTest extends BookTestSuper {
    @BeforeEach
    void setUp() {
        book = new DEFENSE_AGAINST_THE_DARK_ARTS(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
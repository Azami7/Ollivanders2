package ollivanders.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.book.CURSES_AND_COUNTERCURSES;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;

/**
 * Unit tests for CURSES_AND_COUNTERCURSES that focus on Minecraft book constraints.
 * <p>
 * Minecraft Book Limits:<br>
 * - Title: 32 characters max<br>
 * - Pages: 50 max<br>
 * - Characters per page: 256 max (newlines count as 2)<br>
 * - Lines per page: 14 max<br>
 * </p>
 */
public class CursesAndCountercursesTest extends BookTestSuper {
    @BeforeEach
    void setUp() {
        book = new CURSES_AND_COUNTERCURSES(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
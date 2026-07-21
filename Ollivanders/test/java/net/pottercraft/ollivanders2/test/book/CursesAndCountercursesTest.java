package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.CURSES_AND_COUNTERCURSES;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link CURSES_AND_COUNTERCURSES}.
 */
public class CursesAndCountercursesTest extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new CURSES_AND_COUNTERCURSES(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}
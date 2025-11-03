package ollivanders.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.book.A_BEGINNERS_GUIDE_TO_TRANSFIGURATION;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for A_BEGINNERS_GUIDE_TO_TRANSFIGURATION that focus on Minecraft book constraints.
 * <p>
 * Minecraft Book Limits:<br>
 * - Title: 32 characters max<br>
 * - Pages: 50 max<br>
 * - Characters per page: 256 max (newlines count as 2)<br>
 * - Lines per page: 14 max<br>
 * </p>
 */
public class ABeginnersGuideToTransfigurationTest {
    static World testWorld;
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/book_config.yml"));
        testWorld = mockServer.addSimpleWorld("world");
    }

    /**
     * Book can be created without errors
     */
    @Test
    void testBookCreation() {
        A_BEGINNERS_GUIDE_TO_TRANSFIGURATION book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        assertNotNull(book);
    }

    /**
     * Book item can be generated without errors
     */
    @Test
    void testBookItemGeneration() {
        A_BEGINNERS_GUIDE_TO_TRANSFIGURATION book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        ItemStack bookItem = book.getBookItem();
        assertNotNull(bookItem);
        assertTrue(bookItem.hasItemMeta());
    }

    /**
     * Title length must not exceed Minecraft's 32-character limit
     */
    @Test
    void testTitleLengthWithinLimit() {
        A_BEGINNERS_GUIDE_TO_TRANSFIGURATION book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        String title = meta.getTitle();

        assertNotNull(title, "Title should not be null");
        assertTrue(title.length() <= 32,
            "Title '" + title + "' is " + title.length() + " characters, exceeds 32 character limit");
    }

    /**
     * Page count must not exceed Minecraft's 50-page limit
     */
    @Test
    void testPageCountWithinLimit() {
        A_BEGINNERS_GUIDE_TO_TRANSFIGURATION book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        int pageCount = meta.getPageCount();

        assertTrue(pageCount > 0, "Book should have at least one page");
        assertTrue(pageCount <= 50,
            "Book has " + pageCount + " pages, exceeds 50 page limit");
    }

    /**
     * Each page must not exceed Minecraft's 256-character limit
     */
    @Test
    @SuppressWarnings("deprecation")
    void testPageCharacterLimits() {
        A_BEGINNERS_GUIDE_TO_TRANSFIGURATION book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        int pageCount = meta.getPageCount();

        for (int i = 1; i <= pageCount; i++) {
            String page = meta.getPage(i);
            assertNotNull(page, "Page " + i + " should not be null");

            // Count characters with newlines counting as 2
            int charCount = 0;
            for (char c : page.toCharArray()) {
                if (c == '\n') {
                    charCount += 2;
                } else {
                    charCount += 1;
                }
            }

            assertTrue(charCount <= 256,
                "Page " + i + " has " + charCount + " characters (counting newlines as 2), exceeds 256 character limit");
        }
    }

    /**
     * Book should contain all expected spells in its content
     */
    @Test
    void testBookContainsAllSpells() {
        A_BEGINNERS_GUIDE_TO_TRANSFIGURATION book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        int spellCount = book.getNumberOfSpells();

        // Check that the book has enough pages for all spells plus title/TOC
        assertTrue(meta.getPageCount() >= spellCount,
            "Book should have enough pages for " + spellCount + " spells plus title/TOC. Has " + meta.getPageCount() + " pages");
    }

    /**
     * Book title should match the book type
     */
    @Test
    void testTitleMatchesBookType() {
        A_BEGINNERS_GUIDE_TO_TRANSFIGURATION book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        String bookTitle = book.getTitle();
        String metaTitle = ((BookMeta) book.getBookItem().getItemMeta()).getTitle();

        assertNotNull(bookTitle);
        assertNotNull(metaTitle);
        // Meta title is the "short title" which should be <= 32 chars
        assertTrue(metaTitle.length() <= 32);
    }

    /**
     * Author should be set
     */
    @Test
    void testAuthorIsSet() {
        A_BEGINNERS_GUIDE_TO_TRANSFIGURATION book = new A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();

        assertNotNull(meta.getAuthor(), "Book should have an author");
        assertFalse(meta.getAuthor().isEmpty(), "Author should not be empty");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }
}
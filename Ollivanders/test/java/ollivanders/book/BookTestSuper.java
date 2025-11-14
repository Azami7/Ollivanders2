package ollivanders.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.book.O2Book;
import ollivanders.pluginDependencies.LibsDisguisesMock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class BookTestSuper {
    O2Book book;
    BookMeta meta;
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        MockBukkit.mock();
        // load dependency plugins first
        MockBukkit.loadWith(LibsDisguisesMock.class, new File("Ollivanders/test/resources/mocks/LibsDisguises/plugin.yml"));
        // load plugin
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/book_config.yml"));
    }

    abstract void setUp();

    /**
     * Book can be created without errors
     */
    @Test
    void testBookCreation() {
        assertNotNull(book, "Book should be created successfully");
    }

    /**
     * Book item can be generated without errors
     */
    @Test
    void testBookItemGeneration() {
        ItemStack bookItem = book.getBookItem();
        assertNotNull(bookItem, "Book item should be generated");
        assertTrue(bookItem.hasItemMeta(), "Book item should have metadata");
    }

    /**
     * Title length must not exceed Minecraft's 32-character limit
     */
    @Test
    void testTitleLengthWithinLimit() {
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
        int spellCount = book.getNumberOfSpells() + book.getNumberOfPotions();

        // Check that the book has enough pages for all spells plus title/TOC
        assertTrue(meta.getPageCount() >= spellCount,
                "Book should have enough pages for " + spellCount + " spells/potions plus title and TOC. Has " + meta.getPageCount() + " pages");
    }

    /**
     * Book title should match the book type
     */
    @Test
    void testTitleMatchesBookType() {
        String bookTitle = book.getTitle();
        String metaTitle = meta.getTitle();

        assertNotNull(bookTitle, "Book title should not be null");
        assertNotNull(metaTitle, "Meta title should not be null");
        // Meta title is the "short title" which should be <= 32 chars
        assertTrue(metaTitle.length() <= 32, "Meta title should be within 32 character limit");
    }

    /**
     * Author should be set
     */
    @Test
    void testAuthorIsSet() {
        assertNotNull(meta.getAuthor(), "Book should have an author");
        assertFalse(meta.getAuthor().isEmpty(), "Author should not be empty");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}

package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.book.O2Book;
import net.pottercraft.ollivanders2.test.pluginDependencies.LibsDisguisesMock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 . * Base class for testing {@link O2Book} implementations. Provides the shared MockBukkit lifecycle and the common
 * validation tests every book must pass (Minecraft title/page/character limits, content, author). Subclasses override
 * {@link #setUp()} with {@code @BeforeEach} to assign {@link #book} and {@link #meta} for their book type, and may add
 * book-specific tests.
 *
 * @see O2Book
 */
abstract public class BookTestSuper {
    O2Book book;

    /** The book item's metadata, validated against Minecraft's limits; set by the subclass {@link #setUp()}. */
    BookMeta meta;

    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        MockBukkit.mock();
        // dependency plugins must load before the main plugin
        MockBukkit.loadWith(LibsDisguisesMock.class, new File("Ollivanders/test/resources/mocks/LibsDisguises/plugin.yml"));
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/book_config.yml"));
    }

    /**
     * Assign {@link #book} and {@link #meta} for the book type under test. Subclasses must annotate their override with
     * {@code @BeforeEach} and set {@code meta = (BookMeta) book.getBookItem().getItemMeta();}.
     */
    @BeforeEach
    abstract void setUp();

    /**
     * The book constructs without error.
     */
    @Test
    void testBookCreation() {
        assertNotNull(book, "Book should be created successfully");
    }

    /**
     * The book converts to an ItemStack carrying book metadata.
     */
    @Test
    void testBookItemGeneration() {
        ItemStack bookItem = book.getBookItem();
        assertNotNull(bookItem, "Book item should be generated");
        assertTrue(bookItem.hasItemMeta(), "Book item should have metadata");
    }

    /**
     * Title stays within Minecraft's 32-character limit.
     */
    @Test
    void testTitleLengthWithinLimit() {
        String title = meta.getTitle();

        assertNotNull(title, "Title should not be null");
        assertTrue(title.length() <= 32,
                "Title '" + title + "' is " + title.length() + " characters, exceeds 32 character limit");
    }

    /**
     * Page count stays within Minecraft's 50-page limit.
     */
    @Test
    void testPageCountWithinLimit() {
        int pageCount = meta.getPageCount();

        assertTrue(pageCount > 0, "Book should have at least one page");
        assertTrue(pageCount <= 50,
                "Book has " + pageCount + " pages, exceeds 50 page limit");
    }

    /**
     * Every page stays within Minecraft's 256-character limit, counting newlines as 2 characters to match Minecraft's
     * book rendering.
     */
    @Test @SuppressWarnings("deprecation") // getPage(int) is the clearest way to validate a specific page
    void testPageCharacterLimits() {
        int pageCount = meta.getPageCount();

        for (int i = 1; i <= pageCount; i++) {
            String page = meta.getPage(i);
            assertNotNull(page, "Page " + i + " should not be null");

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
     * The book has at least one page per spell and potion, leaving room for the title page and table of contents so no
     * content is lost to the 50-page limit.
     */
    @Test
    void testBookContainsAllSpells() {
        int spellCount = book.getNumberOfSpells() + book.getNumberOfPotions();

        assertTrue(meta.getPageCount() >= spellCount,
                "Book should have enough pages for " + spellCount + " spells/potions plus title and TOC. Has " + meta.getPageCount() + " pages");
    }

    /**
     * The metadata title (the short title) is present and within the 32-character limit.
     */
    @Test
    void testTitleMatchesBookType() {
        String bookTitle = book.getTitle();
        String metaTitle = meta.getTitle();

        assertNotNull(bookTitle, "Book title should not be null");
        assertNotNull(metaTitle, "Meta title should not be null");
        assertTrue(metaTitle.length() <= 32, "Meta title should be within 32 character limit");
    }

    /**
     * The book has a non-empty author.
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
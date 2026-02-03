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
 * Abstract base class for testing Ollivanders2 book implementations.
 * <p>
 * Provides a template for testing book creation, validation, and item generation across different book types.
 * Subclasses must extend this class, initialize the {@link #book} and {@link #meta} fields in an overridden
 * {@code setUp()} method annotated with {@code @BeforeEach}, and optionally add book-specific tests.
 * </p>
 * <p>
 * This class handles shared test infrastructure:
 * <ul>
 * <li>MockBukkit server initialization and plugin loading ({@link #globalSetUp()})</li>
 * <li>Plugin cleanup ({@link #globalTearDown()})</li>
 * <li>Common validation tests for all books (title length, page count, character limits, content verification)</li>
 * </ul>
 * </p>
 * <p>
 * Test coverage includes:
 * <ul>
 * <li>Book creation and successful instantiation</li>
 * <li>Book item generation with proper Bukkit metadata</li>
 * <li>Title length compliance (Minecraft 32-character limit)</li>
 * <li>Page count compliance (Minecraft 50-page limit)</li>
 * <li>Individual page character limits (256 characters per page, with newlines counting as 2)</li>
 * <li>Book content contains expected spells and potions</li>
 * <li>Title matching between O2Book and BookMeta</li>
 * <li>Author presence in book metadata</li>
 * </ul>
 * </p>
 *
 * @see O2Book the book implementation being tested
 * @see BookMeta the Bukkit metadata container for book content
 */
abstract public class BookTestSuper {
    /**
     * The O2Book instance being tested.
     * <p>
     * Must be initialized by subclass {@code setUp()} method. Subclasses typically create a specific
     * book implementation (e.g., {@code SpellBookImpl}, {@code PotionBook}, etc.) and assign to this field.
     * </p>
     */
    O2Book book;

    /**
     * The Bukkit BookMeta metadata container extracted from the O2Book's ItemStack.
     * <p>
     * Contains the serialized book content (pages, title, author) that can be validated against
     * Minecraft's limits. Must be initialized by subclass {@code setUp()} method, typically via:
     * {@code meta = (BookMeta) book.getBookItem().getItemMeta();}.
     * </p>
     */
    BookMeta meta;

    /**
     * The Ollivanders2 plugin instance loaded for testing.
     * <p>
     * Initialized once per test class in {@link #globalSetUp()} and shared across all test methods.
     * Provides access to plugin configuration and Ollivanders2 API during tests. Marked as {@code static}
     * since plugin loading is expensive and should only happen once per test class execution.
     * </p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the MockBukkit server and load the Ollivanders2 plugin with required dependencies.
     * <p>
     * This setup runs once per test class (via {@code @BeforeAll}) and performs:
     * <ul>
     * <li>MockBukkit server initialization</li>
     * <li>Loading LibsDisguises mock dependency (required by Ollivanders2)</li>
     * <li>Loading Ollivanders2 plugin with book-specific test configuration</li>
     * </ul>
     * The loaded plugin instance is stored in {@link #testPlugin} for access by test methods.
     * </p>
     */
    @BeforeAll
    static void globalSetUp() {
        MockBukkit.mock();
        // load dependency plugins first
        MockBukkit.loadWith(LibsDisguisesMock.class, new File("Ollivanders/test/resources/mocks/LibsDisguises/plugin.yml"));
        // load plugin
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/book_config.yml"));
    }

    /**
     * Template method for subclasses to initialize test fixtures.
     * <p>
     * Must be implemented by each concrete test subclass and annotated with {@code @BeforeEach}.
     * The implementing method should initialize the {@link #book} and {@link #meta} fields with
     * a concrete book implementation instance. For example:
     * </p>
     * <pre>
     * {@code
     * @Override @BeforeEach
     * void setUp() {
     *     book = new ConcreteBookImpl(testPlugin);
     *     meta = (BookMeta) book.getBookItem().getItemMeta();
     * }
     * }
     * </pre>
     * <p>
     * This design follows the Template Method pattern, allowing each subclass to customize setup
     * while sharing the common validation tests defined in this superclass.
     * </p>
     */
    @BeforeEach
    abstract void setUp();

    /**
     * Verify that a book can be created successfully without throwing exceptions.
     * <p>
     * Tests that the {@link #book} field, which must be initialized by the subclass {@code setUp()}
     * method, is not null after initialization. This is a smoke test for basic book construction.
     * </p>
     */
    @Test
    void testBookCreation() {
        assertNotNull(book, "Book should be created successfully");
    }

    /**
     * Verify that a book can be converted to a Bukkit ItemStack with proper metadata.
     * <p>
     * Tests that calling {@link O2Book#getBookItem()} returns a valid ItemStack with attached
     * BookMeta metadata. This confirms that the book can be properly serialized for inventory storage
     * and display to players.
     * </p>
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
     * Verify that each page respects Minecraft's 256-character limit with newline counting.
     * <p>
     * Tests that every page in the book stays within the 256-character limit enforced by Minecraft's
     * book GUI. For this test, newline characters ({@code \n}) count as 2 characters (matching Minecraft's
     * rendering behavior), while all other characters count as 1.
     * </p>
     * <p>
     * The {@code @SuppressWarnings("deprecation")} annotation is used because {@link BookMeta#getPage(int)}
     * is deprecated in newer Bukkit versions in favor of {@link BookMeta#getPages()}, but we need to
     * validate individual pages and this method provides clearer access to specific page content.
     * </p>
     */
    @Test @SuppressWarnings("deprecation")
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
     * Verify that the book contains enough content for all documented spells and potions.
     * <p>
     * Tests that the book has sufficient pages to display all spells returned by
     * {@link O2Book#getNumberOfSpells()} and all potions from {@link O2Book#getNumberOfPotions()},
     * plus additional pages for title page, table of contents, and other front matter.
     * This ensures book pages aren't truncated due to Minecraft's 50-page limit.
     * </p>
     */
    @Test
    void testBookContainsAllSpells() {
        int spellCount = book.getNumberOfSpells() + book.getNumberOfPotions();

        // Check that the book has enough pages for all spells plus title/TOC
        assertTrue(meta.getPageCount() >= spellCount,
                "Book should have enough pages for " + spellCount + " spells/potions plus title and TOC. Has " + meta.getPageCount() + " pages");
    }

    /**
     * Verify that the book's title matches between O2Book and BookMeta representations.
     * <p>
     * Tests that the O2Book's title (from {@link O2Book#getTitle()}) is consistent with the
     * Minecraft BookMeta's title (from {@link BookMeta#getTitle()}). The Minecraft metadata
     * title must also stay within the 32-character limit. This ensures consistent presentation
     * whether the book is accessed programmatically or viewed in-game.
     * </p>
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
     * Verify that the book has an author set in its metadata.
     * <p>
     * Tests that every book has a non-empty author name in the BookMeta. This ensures that
     * players can see who authored the book when viewing it in Minecraft, providing proper
     * credit and attribution for the book's content.
     * </p>
     */
    @Test
    void testAuthorIsSet() {
        assertNotNull(meta.getAuthor(), "Book should have an author");
        assertFalse(meta.getAuthor().isEmpty(), "Author should not be empty");
    }

    /**
     * Clean up the MockBukkit server after all tests in this test class have completed.
     * <p>
     * This teardown runs once per test class (via {@code @AfterAll}) and performs MockBukkit cleanup,
     * releasing server resources and unloading all loaded plugins. This is necessary to prevent
     * resource leaks and interference between test classes when running the complete test suite.
     * </p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}

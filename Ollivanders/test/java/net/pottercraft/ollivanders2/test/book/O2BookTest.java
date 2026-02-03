package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.book.BOOK_OF_POTIONS;
import net.pottercraft.ollivanders2.book.O2Book;
import net.pottercraft.ollivanders2.book.O2BookType;
import net.pottercraft.ollivanders2.book.STANDARD_BOOK_OF_SPELLS_GRADE_1;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for O2Book.java
 */
public class O2BookTest {
    static World testWorld;
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/book_config.yml"));

        // set up world
        testWorld = mockServer.addSimpleWorld("world");
    }

    @BeforeEach
    void setUp() {}

    /**
     * Basic Constructor & Initialization Tests
     */
    @Test
    void testBookCreation() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        assertNotNull(book);
    }

    /**
     * Testing potion book creation
     */
    @Test
    void testPotionBookCreation() {
        BOOK_OF_POTIONS book = new BOOK_OF_POTIONS(testPlugin);
        assertNotNull(book);
    }

    /**
     * Title is set correctly
     */
    @Test
    void testGetTitle() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        String title = book.getTitle();
        assertNotNull(title);
        assertFalse(title.isEmpty());
        assertEquals(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1.getTitle(testPlugin), title);
    }

    /**
     * Subtitle is set correctly
     */
    @Test
    void testGetShortTitle() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        String shortTitle = book.getShortTitle();
        assertNotNull(shortTitle);
        assertFalse(shortTitle.isEmpty());
        assertEquals(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1.getShortTitle(testPlugin), shortTitle);
    }

    /**
     * Author is set correctly
     */
    @Test
    void testGetAuthor() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        String author = book.getAuthor();
        assertNotNull(author);
        assertFalse(author.isEmpty());
        assertEquals(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1.getAuthor(), author);
    }

    /**
     * Magical branch is set correctly
     */
    @Test
    void testGetBranch() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        assertNotNull(book.getBranch());
        assertEquals(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1.getBranch(), book.getBranch());
    }

    /**
     * Book items can be created and has only 1 item in the item stack
     */
    @Test
    void testGetBookItemNotNull() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        ItemStack bookItem = book.getBookItem();
        assertNotNull(bookItem);
        assertEquals(1, bookItem.getAmount());
    }

    /**
     * Book item is the correct type
     */
    @Test
    void testBookItemIsWrittenBook() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        ItemStack bookItem = book.getBookItem();
        assertEquals(Material.WRITTEN_BOOK, bookItem.getType());
    }

    /**
     * Test that the cached book is the same
     */
    @Test
    void testBookItemCaching() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        ItemStack first = book.getBookItem();
        ItemStack second = book.getBookItem();
        assertSame(first, second);
    }

    /**
     * Book item has book metadata
     */
    @Test
    void testBookHasMetadata() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        ItemStack bookItem = book.getBookItem();
        assertTrue(bookItem.hasItemMeta());
        assertInstanceOf(BookMeta.class, bookItem.getItemMeta());
    }

    /**
     * Book metadata has author and title
     */
    @Test
    void testBookMetaHasAuthorAndTitle() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        assertNotNull(meta.getAuthor());
        assertNotNull(meta.getTitle());
    }

    /**
     * Book has pages
     */
    @Test
    void testBookHasPages() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        assertTrue(meta.getPageCount() > 0);
    }

    /**
     * Book is generation original
     */
    @Test
    void testBookGenerationIsOriginal() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        assertEquals(BookMeta.Generation.ORIGINAL, meta.getGeneration());
    }

    /**
     * Book has NBT tags
     */
    @Test
    void testBookHasPersistentData() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        assertTrue(container.has(O2Book.o2BookTypeKey, PersistentDataType.STRING));
        assertTrue(container.has(O2Book.o2BookSpellsKey, PersistentDataType.STRING));
    }

    /**
     * Booktype NBT tag is set correctly
     */
    @Test
    void testBookTypeTagIsCorrect() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        String bookType = meta.getPersistentDataContainer()
                .get(O2Book.o2BookTypeKey, PersistentDataType.STRING);
        assertEquals("STANDARD_BOOK_OF_SPELLS_GRADE_1", bookType);
    }

    /**
     * Spells NBT tags are set correctly
     */
    @Test
    void testSpellsTagContainsSpells() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        String spells = meta.getPersistentDataContainer()
                .get(O2Book.o2BookSpellsKey, PersistentDataType.STRING);

        assertNotNull(spells);
        assertTrue(spells.contains("LUMOS"));
        assertTrue(spells.contains("NOX"));
    }

    /**
     * NBT tags for potions work
     */
    @Test
    void testPotionsTagForPotionBook() {
        BOOK_OF_POTIONS book = new BOOK_OF_POTIONS(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        String potions = meta.getPersistentDataContainer()
                .get(O2Book.o2BookPotionsKey, PersistentDataType.STRING);

        assertNotNull(potions);
        assertTrue(potions.contains("CURE_FOR_BOILS"));
    }

    /**
     * First page is title plus author
     */
    @Test
    @SuppressWarnings("deprecation")
    void testBookHasTitlePage() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        String firstPage = meta.getPage(1);
        assertTrue(firstPage.contains("by"));
    }

    /**
     * Book has a TOC
     */
    @Test
    @SuppressWarnings("deprecation")
    void testBookHasTableOfContents() {
        STANDARD_BOOK_OF_SPELLS_GRADE_1 book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
        String secondPage = meta.getPage(2);
        assertTrue(secondPage.contains("Contents:"));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testOpeningPageAppears() {
        BOOK_OF_POTIONS book = new BOOK_OF_POTIONS(testPlugin);
        BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();

        // Find page with opening text
        boolean found = false;
        for (int i = 1; i <= meta.getPageCount(); i++) {
            if (meta.getPage(i).contains("young potioneer")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
    }
}

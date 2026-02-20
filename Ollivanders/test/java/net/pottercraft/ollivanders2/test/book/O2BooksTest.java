package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.book.O2BookType;
import net.pottercraft.ollivanders2.book.O2Books;
import net.pottercraft.ollivanders2.book.events.OllivandersBookLearningSpellEvent;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.IMPROVED_BOOK_LEARNING;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import net.pottercraft.ollivanders2.test.pluginDependencies.LibsDisguisesMock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockbukkit.mockbukkit.matcher.plugin.PluginManagerFiredEventClassMatcher.hasFiredEventInstance;

/**
 * Unit tests for O2Books.java.
 * Tests book retrieval, book learning mechanics, and book-related commands.
 */
public class O2BooksTest {
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;
    static O2Books books;

    /**
     * Set up the mock server, load dependency plugins, load the main plugin with test config,
     * and get the books instance from the plugin.
     */
    @BeforeAll
    static void globalSetUp () {
        mockServer = MockBukkit.mock();

        // load dependency plugins first
        MockBukkit.loadWith(LibsDisguisesMock.class, new File("Ollivanders/test/resources/mocks/LibsDisguises/plugin.yml"));

        // load plugin
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/book_config.yml"));

        // Get the books instance from the plugin (initialized in onEnable)
        books = Ollivanders2API.getBooks();

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Test initial state of book manager and onEnable() function.
     */
    @Test
    void onEnableTest() {
        assertNotEquals(0, books.getAllBooks().size(), "books.getAllBooks() returned empty size");
        assertNotNull(mockServer.getPluginManager().getPlugin("LibsDisguises"), "LibsDisguises plugin not loaded");
        assertTrue(mockServer.getPluginManager().isPluginEnabled("LibsDisguises"), "LibsDisguises plugin not enabled");
    }

    /**
     * Test that reading a book triggers the book learning mechanic and increases
     * the player's spell level for spells contained in that book.
     * This test simulates a player right-clicking with a book and verifies the
     * spell count increases after scheduled tasks execute.
     */
    @Test
    void onBookReadTest() {
        // make sure bookLearning is on in the config
        assertTrue(Ollivanders2.bookLearning, "bookLearning is not enabled.");

        // create a book
        ItemStack book = books.getBookByType(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1);

        // get an O2Player
        PlayerMock player = mockServer.addPlayer();

        // set the book in the player's inventory
        player.getInventory().setItemInMainHand(book);

        // get the player's spell level before
        O2Player o2p = testPlugin.getO2Player(player);
        int spellLevel = o2p.getSpellCount(O2SpellType.LUMOS);

        // create a player interact event and fire it
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, book, null, BlockFace.UP, EquipmentSlot.HAND);
        mockServer.getPluginManager().callEvent(event);

        // fast-forward time so the readBook runnable will execute (2 seconds)
        // then the incrementSpell runnable will execute (another 2 seconds)
        // total of 4 seconds of scheduled delays
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 5);
        // check player spell level
        assertNotEquals(spellLevel, o2p.getSpellCount(O2SpellType.LUMOS), "Spell level did not change after book was read.");

        // make sure the OllivandersBookLearningSpellEvent event fired
        assertThat(mockServer.getPluginManager(), hasFiredEventInstance(OllivandersBookLearningSpellEvent.class));
    }

    /**
     * Test that getBookByTitle correctly retrieves a book by its full title
     * and returns a valid WRITTEN_BOOK item with correct metadata.
     */
    @Test
    void getBookByTitleTest() {
        String title = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1.getTitle(testPlugin);
        ItemStack book = books.getBookByTitle(title);
        assertNotNull(book, "books.getBookByTitle() returned null");

        assertNotNull(book.getType(), "book.getType() returned null.");
        assertEquals(Material.WRITTEN_BOOK, book.getType(), "book is not type WRITTEN_BOOK.");
        BookMeta bookMeta = (BookMeta)book.getItemMeta();
        assertEquals(title, bookMeta.getTitle(), "book title incorrect.");
    }

    /**
     * Test that getBookByType correctly retrieves a book by its O2BookType enum value
     * and returns a valid WRITTEN_BOOK item with correct metadata.
     */
    @Test
    void getBookByTypeTest() {
        String title = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1.getTitle(testPlugin);
        ItemStack book = books.getBookByType(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1);
        assertNotNull(book, "books.getBookByType() returned null");

        assertNotNull(book.getType(), "book.getType() returned null.");
        assertEquals(Material.WRITTEN_BOOK, book.getType(), "book is not type WRITTEN_BOOK.");
        BookMeta bookMeta = (BookMeta)book.getItemMeta();
        assertEquals(title, bookMeta.getTitle(), "book title incorrect.");
    }

    /**
     * Test that getBookTypeByTitle correctly retrieves a book type by title using
     * various search methods: full title (case-sensitive), partial title, and
     * case-insensitive matching.
     */
    @Test
    void getBookTypeByTitleTest() {
        // try full title, case-sensitive
        String title = "The Essential Defence Against the Dark Arts";
        O2BookType bookType = books.getBookTypeByTitle(title);
        assertNotNull(bookType, "getBookTypeByTitle() returned null for full title");
        assertNotNull(bookType.getTitle(testPlugin), "bookType.getTitle() returned null for full title");
        assertEquals(title, bookType.getTitle(testPlugin), "bookType.getTitle() does not match expected title");

        // try partial title
        String partialTitle = "The Essential";
        bookType = books.getBookTypeByTitle(partialTitle);
        assertNotNull(bookType, "getBookTypeByTitle() returned null for partial title");
        assertNotNull(bookType.getTitle(testPlugin), "bookType.getTitle() returned null for partial title");
        assertEquals(title, bookType.getTitle(testPlugin), "bookType.getTitle() does not match expected title");

        // try case-insensitive
        String lowercaseTitle = "the essential defence against the dark arts";
        bookType = books.getBookTypeByTitle(partialTitle);
        assertNotNull(bookType, "getBookTypeByTitle() returned null for case-insensitive search");
        assertNotNull(bookType.getTitle(testPlugin), "bookType.getTitle() returned null for case-insensitive search");
        assertEquals(title, bookType.getTitle(testPlugin), "bookType.getTitle() does not match expected title");
    }

    /**
     * Test that getAllBooks returns a non-empty list containing all book types
     * defined in the O2BookType enum.
     */
    @Test
    void getAllBooksTest() {
        List<ItemStack> bookStack = books.getAllBooks();
        assertFalse(bookStack.isEmpty(), "books.getAllBooks() returned empty list");
        assertEquals(O2BookType.values().length, bookStack.size(), "books.getAllBooks() did not return a list with all books");
    }

    /**
     * Test that getAllBookTitles returns a non-empty list containing the titles
     * of all books defined in the O2BookType enum.
     */
    @Test
    void getAllBookTitles() {
        List<String> bookTitles = books.getAllBookTitles();
        assertFalse(bookTitles.isEmpty(), "books.getAllBookTitles() returned empty list");
        for (O2BookType bookType : O2BookType.values())
        {
            String title = bookType.getTitle(testPlugin);
            assertTrue(bookTitles.contains(title), "books.getAllBookTitles() missing " + title);
        }
    }

    /**
     * Test running the books subcommand with no args - Ollivanders2 books - result in a usage message
     */
    @Test
    void runCommandUsageTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);

        // admin should get a usage message running the subcommand with no args
        String commandResponse = TestCommon.runCommand(player, "Ollivanders2 books", mockServer);
        assertNotNull(commandResponse, "command response was null");
        assertTrue(TestCommon.messageStartsWith("Usage:", commandResponse), "Expected usage message but got: " + commandResponse);
    }

    /**
     * Non-admin running the books subcommand results in a usage message
     */
    @Test
    void runCommandNonAdmin() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(false);

        // a non-admin should get a usage message if they try to run the /olli books command
        String commandResponse = TestCommon.runCommand(player, "Ollivanders2 books", mockServer);
        assertNotNull(commandResponse, "command response was null");
        assertTrue(TestCommon.messageStartsWith("Usage: /olli summary", commandResponse), "Expected usage message but got: " + commandResponse);
    }

    /**
     * Running /olli books allbooks gives all the loaded books to the command sender
     */
    @Test
    void runCommandAllBooksTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);

        TestCommon.runCommand(player, "Ollivanders2 books allbooks", mockServer);
        assertTrue(TestCommon.isInPlayerInventory(player, Material.WRITTEN_BOOK), "Player did not receive books from allbooks command");
    }

    /**
     * Test that the "/olli books list" command returns a list of all available books.
     * The response should contain book titles from the loaded books.
     */
    @Test
    void runCommandListTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);

        String commandResponse = TestCommon.runCommand(player, "Ollivanders2 books list", mockServer);
        assertNotNull(commandResponse, "command response was null");
        assertTrue(commandResponse.contains("Standard Book of Spells"), "Ollivanders2 books list did not return list of loaded books");
    }

    /**
     * Test that the "/olli books give [player] [book title]" command successfully
     * gives the specified book to the target player's inventory.
     */
    @Test
    void runCommandGiveTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);
        PlayerMock player2 = mockServer.addPlayer();

        TestCommon.runCommand(player, "Ollivanders2 books give " + player2.getName() + " Basic Hexes", mockServer);
        mockServer.getScheduler().performTicks(200);
        assertTrue(TestCommon.isInPlayerInventory(player2, Material.WRITTEN_BOOK, "Basic Hexes"), "Did not find the book in the player's inventory");
    }

    /**
     * Test that the "/olli books give [player] [book title]" command returns an
     * appropriate error message when the specified player is not found on the server.
     */
    @Test
    void runCommandGivePlayerNotFoundTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);

        String commandResponse = TestCommon.runCommand(player, "Ollivanders2 books give Bob Basic Hexes", mockServer);
        assertNotNull(commandResponse, "command response was null");
        assertTrue(TestCommon.messageStartsWith("Did not find player", commandResponse), "Unexpected command response. Expected: \"Did not find player\", Actual: " + commandResponse);
    }

    /**
     * Test that the "/olli books give [player]" command (without book title)
     * returns a usage message indicating the correct command syntax.
     */
    @Test
    void runCommandGiveUsageTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);

        String commandResponse = TestCommon.runCommand(player, "Ollivanders2 books give Fred", mockServer);
        assertNotNull(commandResponse, "command response was null");
        assertTrue(TestCommon.messageStartsWith("Usage:", commandResponse), "Unexpected command response. Expected: \"Usage:\", Actual: " + commandResponse);
    }

    /**
     * Test that the "/olli books [book title]" command successfully gives the
     * specified book to the command sender's inventory.
     */
    @Test
    void runCommandBookTitleTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);

        TestCommon.runCommand(player, "Ollivanders2 books Basic Hexes", mockServer);
        assertTrue(TestCommon.isInPlayerInventory(player, Material.WRITTEN_BOOK, "Basic Hexes"), "Did not find the book in the player's inventory");
    }

    /**
     * Test that the "/olli books [invalid book title]" command returns an
     * appropriate error message when the specified book title doesn't exist.
     */
    @Test
    void runCommandBookTitleDoesntExistTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);

        String commandResponse = TestCommon.runCommand(player, "Ollivanders2 books Lord of the Rings", mockServer);
        assertNotNull(commandResponse, "command response was null");
        assertTrue(TestCommon.messageStartsWith("No book named", commandResponse), "Unexpected command response. Expected: \"No book named\", Actual: " + commandResponse);
    }

    /**
     * Test that the IMPROVED_BOOK_LEARNING effect increases skill gain from reading books.
     *
     * <p>This test verifies that the IMPROVED_BOOK_LEARNING passive marker effect correctly
     * boosts the player's skill gain when reading a book. The test compares skill gain with
     * and without the effect to ensure the effect provides a meaningful learning boost.</p>
     */
    @Test
    void improvedBookLearningTest() {
        // make sure bookLearning is on in the config
        assertTrue(Ollivanders2.bookLearning, "bookLearning is not enabled.");

        // create a book
        ItemStack book = books.getBookByType(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1);

        // Test 1: Read book without IMPROVED_BOOK_LEARNING effect
        PlayerMock player1 = mockServer.addPlayer();
        player1.getInventory().setItemInMainHand(book);
        O2Player o2p1 = testPlugin.getO2Player(player1);
        int spellLevelBefore1 = o2p1.getSpellCount(O2SpellType.LUMOS);

        PlayerInteractEvent event1 = new PlayerInteractEvent(player1, Action.RIGHT_CLICK_BLOCK, book, null, BlockFace.UP, EquipmentSlot.HAND);
        mockServer.getPluginManager().callEvent(event1);

        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 5);
        int spellLevelAfter1 = o2p1.getSpellCount(O2SpellType.LUMOS);
        int gainWithout = spellLevelAfter1 - spellLevelBefore1;

        // Test 2: Read book with IMPROVED_BOOK_LEARNING effect
        PlayerMock player2 = mockServer.addPlayer();
        player2.getInventory().setItemInMainHand(book);
        O2Player o2p2 = testPlugin.getO2Player(player2);

        // Add IMPROVED_BOOK_LEARNING effect
        IMPROVED_BOOK_LEARNING effect = new IMPROVED_BOOK_LEARNING(testPlugin, 6000, false, player2.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        int spellLevelBefore2 = o2p2.getSpellCount(O2SpellType.LUMOS);

        PlayerInteractEvent event2 = new PlayerInteractEvent(player2, Action.RIGHT_CLICK_BLOCK, book, null, BlockFace.UP, EquipmentSlot.HAND);
        mockServer.getPluginManager().callEvent(event2);

        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 5);
        int spellLevelAfter2 = o2p2.getSpellCount(O2SpellType.LUMOS);
        int gainWith = spellLevelAfter2 - spellLevelBefore2;

        // Verify that skill gain with effect is greater than without
        assertTrue(gainWithout > 0, "Player without effect gained no skill from book");
        assertTrue(gainWith > gainWithout, "IMPROVED_BOOK_LEARNING effect did not increase skill gain. Gain without: " + gainWithout + ", Gain with: " + gainWith);
    }

    /**
     * Clean up the mock server after all tests have run.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}

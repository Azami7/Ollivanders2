package net.pottercraft.ollivanders2.test.item.wand;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for {@link net.pottercraft.ollivanders2.item.wand.O2Wands} wand management functionality.
 * <p>
 * Verifies wand creation, validation, NBT handling, inventory checks, and player wand assignment.
 * Tests cover both full wands (wood + core) and coreless wands, as well as random wand generation.
 * </p>
 */
public class O2WandsTest {
    /**
     * Mock Bukkit server instance used for all tests.
     * <p>
     * Set up during {@link #globalSetUp()} and torn down during {@link #globalTearDown()}.
     * This allows tests to interact with Minecraft server objects without requiring an actual server.
     * </p>
     */
    static ServerMock mockServer;

    /**
     * The Ollivanders2 plugin instance loaded with default test configuration.
     * <p>
     * Initialized during {@link #globalSetUp()} to provide access to plugin functionality and
     * allow proper initialization of all O2Items through the plugin's startup sequence.
     * </p>
     */
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#isWand(ItemStack)}.
     * <p>
     * Verifies that wand detection correctly identifies both Elder Wands and regular wands with NBT,
     * while rejecting non-wands and incomplete wands.
     * </p>
     * <ul>
     *   <li>Valid wand (wood + core NBT): Returns true</li>
     *   <li>Elder Wand: Returns true</li>
     *   <li>Non-wand item (APPLE): Returns false</li>
     *   <li>WAND O2Item without wood/core NBT: Returns false</li>
     * </ul>
     */
    @Test
    void isWandTest() {
        // valid wand made via makeWand
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), O2WandCoreType.UNICORN_HAIR.getLabel(), 1);
        assertNotNull(wand);
        assertTrue(Ollivanders2API.getItems().getWands().isWand(wand), "isWand() returned false for a valid wand");

        // Elder Wand
        ItemStack elderWand = Ollivanders2API.getItems().getItemByType(O2ItemType.ELDER_WAND, 1);
        assertNotNull(elderWand);
        assertTrue(Ollivanders2API.getItems().getWands().isWand(elderWand), "isWand() returned false for Elder Wand");

        // non-wand item
        ItemStack apple = new ItemStack(Material.APPLE, 1);
        assertFalse(Ollivanders2API.getItems().getWands().isWand(apple), "isWand() returned true for Material.APPLE");

        // WAND O2Item without wood/core NBT
        ItemStack wandNoNBT = Ollivanders2API.getItems().getItemByType(O2ItemType.WAND, 1);
        assertNotNull(wandNoNBT);
        assertFalse(Ollivanders2API.getItems().getWands().isWand(wandNoNBT), "isWand() returned true for a wand O2Item without wood/core NBT");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#checkNBT(ItemStack)}.
     * <p>
     * Verifies that NBT validation requires both wood and core keys to be present.
     * </p>
     * <ul>
     *   <li>Valid wand with both NBT keys: Returns true</li>
     *   <li>Coreless wand (only wood NBT): Returns false</li>
     *   <li>Non-wand item (no NBT): Returns false</li>
     * </ul>
     */
    @Test
    void checkNBTTest() {
        // valid wand with both wood and core NBT
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), O2WandCoreType.UNICORN_HAIR.getLabel(), 1);
        assertNotNull(wand);
        assertTrue(Ollivanders2API.getItems().getWands().checkNBT(wand), "checkNBT() returned false for a valid wand");

        // coreless wand (only wood NBT, no core NBT)
        ItemStack corelessWand = Ollivanders2API.getItems().getWands().createCorelessWand(O2WandWoodType.OAK, 1);
        assertNotNull(corelessWand);
        assertFalse(Ollivanders2API.getItems().getWands().checkNBT(corelessWand), "checkNBT() returned true for a coreless wand");

        // non-wand item (no NBT)
        ItemStack apple = new ItemStack(Material.APPLE, 1);
        assertFalse(Ollivanders2API.getItems().getWands().checkNBT(apple), "checkNBT() returned true for Material.APPLE");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#createLore(String, String)}.
     * <p>
     * Verifies that the lore string is correctly formatted as "wood and core".
     * </p>
     */
    @Test
    void createLoreTest() {
        String wood = O2WandWoodType.OAK.getLabel();
        String core = O2WandCoreType.UNICORN_HAIR.getLabel();
        String expected = wood + " and " + core;

        String lore = Ollivanders2API.getItems().getWands().createLore(wood, core);
        assertEquals(expected, lore, "createLore() did not return expected lore string");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#checkCoreNBT(String, ItemStack)}.
     * <p>
     * Verifies that core NBT matching is case-insensitive and correctly identifies mismatches.
     * </p>
     * <ul>
     *   <li>Matching core: Returns true</li>
     *   <li>Non-matching core: Returns false</li>
     *   <li>Item without core NBT: Returns false</li>
     * </ul>
     */
    @Test
    void checkCoreNBTTest() {
        // create a wand with known core
        String coreName = O2WandCoreType.UNICORN_HAIR.getLabel();
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), coreName, 1);
        assertNotNull(wand);

        // matching core
        assertTrue(Ollivanders2API.getItems().getWands().checkCoreNBT(coreName, wand), "checkCoreNBT() returned false for matching core");

        // non-matching core
        assertFalse(Ollivanders2API.getItems().getWands().checkCoreNBT(O2WandCoreType.DRAGON_HEARTSTRING.getLabel(), wand), "checkCoreNBT() returned true for non-matching core");

        // item with no core NBT
        ItemStack apple = new ItemStack(Material.APPLE, 1);
        assertFalse(Ollivanders2API.getItems().getWands().checkCoreNBT(coreName, apple), "checkCoreNBT() returned true for item without core NBT");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#checkWoodNBT(String, ItemStack)}.
     * <p>
     * Verifies that wood NBT matching is case-insensitive and correctly identifies mismatches.
     * </p>
     * <ul>
     *   <li>Matching wood: Returns true</li>
     *   <li>Non-matching wood: Returns false</li>
     *   <li>Item without wood NBT: Returns false</li>
     * </ul>
     */
    @Test
    void checkWoodNBTTest() {
        // create a wand with known wood
        String woodName = O2WandWoodType.OAK.getLabel();
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(woodName, O2WandCoreType.UNICORN_HAIR.getLabel(), 1);
        assertNotNull(wand);

        // matching wood
        assertTrue(Ollivanders2API.getItems().getWands().checkWoodNBT(woodName, wand), "checkWoodNBT() returned false for matching wood");

        // non-matching wood
        assertFalse(Ollivanders2API.getItems().getWands().checkWoodNBT(O2WandWoodType.ACACIA.getLabel(), wand), "checkWoodNBT() returned true for non-matching wood");

        // item with no wood NBT
        ItemStack apple = new ItemStack(Material.APPLE, 1);
        assertFalse(Ollivanders2API.getItems().getWands().checkWoodNBT(woodName, apple), "checkWoodNBT() returned true for item without wood NBT");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#holdsWand(Player)} and {@link net.pottercraft.ollivanders2.item.wand.O2Wands#holdsWand(Player, EquipmentSlot)}.
     * <p>
     * Verifies that wand detection in player hands works for both main and off-hand, and that the
     * default method checks only the main hand.
     * </p>
     * <ul>
     *   <li>Empty main hand: Returns false</li>
     *   <li>Wand in main hand: Returns true</li>
     *   <li>Non-wand in main hand: Returns false</li>
     *   <li>Wand in off-hand with EquipmentSlot.OFF_HAND: Returns true</li>
     *   <li>Wand only in off-hand, default method: Returns false</li>
     * </ul>
     */
    @Test
    void holdsWandTest() {
        PlayerMock player = mockServer.addPlayer();

        // empty hand
        assertFalse(Ollivanders2API.getItems().getWands().holdsWand(player), "holdsWand() returned true for player with empty hand");

        // wand in main hand
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), O2WandCoreType.UNICORN_HAIR.getLabel(), 1);
        assertNotNull(wand);
        player.getInventory().setItemInMainHand(wand);
        assertTrue(Ollivanders2API.getItems().getWands().holdsWand(player), "holdsWand() returned false for player holding a wand in main hand");

        // non-wand item in main hand
        player.getInventory().setItemInMainHand(new ItemStack(Material.APPLE, 1));
        assertFalse(Ollivanders2API.getItems().getWands().holdsWand(player), "holdsWand() returned true for player holding Material.APPLE");

        // wand in off-hand, main hand empty
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        player.getInventory().setItemInOffHand(wand);
        assertTrue(Ollivanders2API.getItems().getWands().holdsWand(player, EquipmentSlot.OFF_HAND), "holdsWand() returned false for player holding a wand in off hand");

        // default holdsWand only checks main hand
        assertFalse(Ollivanders2API.getItems().getWands().holdsWand(player), "holdsWand() returned true for main hand when wand is only in off hand");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#getAllWands()}.
     * <p>
     * Verifies that all possible wood/core combinations are returned and all are valid wands.
     * </p>
     * <ul>
     *   <li>List is not empty</li>
     *   <li>Size equals woods count × cores count</li>
     *   <li>All entries are valid wands</li>
     * </ul>
     */
    @Test
    void getAllWandsTest() {
        List<ItemStack> wands = Ollivanders2API.getItems().getWands().getAllWands();
        assertNotNull(wands);
        assertFalse(wands.isEmpty(), "getAllWands() returned an empty list");

        int expectedSize = O2WandWoodType.values().length * O2WandCoreType.values().length;
        assertEquals(expectedSize, wands.size(), "getAllWands() returned unexpected number of wands");

        // verify all wands are valid
        for (ItemStack wand : wands) {
            assertNotNull(wand, "getAllWands() contained a null wand");
            assertTrue(Ollivanders2API.getItems().getWands().isWand(wand), "getAllWands() contained an invalid wand");
        }
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#makeWand(String, String, int)}.
     * <p>
     * Verifies wand creation with valid and invalid wood/core combinations and various amounts.
     * </p>
     * <ul>
     *   <li>Valid wand (OAK + UNICORN_HAIR, amount 1): Returns non-null wand</li>
     *   <li>Valid wand with amount 3: Returns wand with correct amount</li>
     *   <li>Invalid wood: Returns null</li>
     *   <li>Invalid core: Returns null</li>
     * </ul>
     */
    @Test
    void makeWandTest() {
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), O2WandCoreType.UNICORN_HAIR.getLabel(), 1);
        assertNotNull(wand, "Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), O2WandCoreType.UNICORN_HAIR.getLabel(), 1) returned null");

        int amount = 3;
        wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), O2WandCoreType.UNICORN_HAIR.getLabel(), amount);
        assertNotNull(wand);
        assertEquals(amount, wand.getAmount(), "Ollivanders2API.getItems().getWands().makeWand() did not return expected amount.");

        wand = Ollivanders2API.getItems().getWands().makeWand("Invalid wood", O2WandCoreType.UNICORN_HAIR.getLabel(), 1);
        assertNull(wand, "Ollivanders2API.getItems().getWands().makeWand(\"Invalid wood\", O2WandCoreType.UNICORN_HAIR.getLabel(), 1) did not return null");

        wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), "Invalid core", 1);
        assertNull(wand, "Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), \"Invalid core\", 1) did not return null");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#isDestinedWand(Player, ItemStack)} and {@link net.pottercraft.ollivanders2.item.wand.O2Wands#isDestinedWand(O2Player, ItemStack)}.
     * <p>
     * Verifies that a wand is correctly matched to a player's destined wood and core.
     * </p>
     * <ul>
     *   <li>Wand matching both wood and core: Returns true for both overloads</li>
     *   <li>Wand with wrong wood, correct core: Returns false</li>
     *   <li>Wand with correct wood, wrong core: Returns false</li>
     * </ul>
     */
    @Test
    void isDestinedWandTest() {
        Player player = mockServer.addPlayer();

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(o2p);
        o2p.setWandCore(O2WandCoreType.UNICORN_HAIR.getLabel());
        o2p.setWandWood(O2WandWoodType.OAK.getLabel());

        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(o2p.getDestinedWandWood(), o2p.getDestinedWandCore(), 1);
        assertNotNull(wand);
        assertTrue(Ollivanders2API.getItems().getWands().isDestinedWand(o2p, wand));
        assertTrue(Ollivanders2API.getItems().getWands().isDestinedWand(player, wand));

        wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.ACACIA.getLabel(), o2p.getDestinedWandCore(), 1);
        assertNotNull(wand);
        assertFalse(Ollivanders2API.getItems().getWands().isDestinedWand(o2p, wand));

        wand = Ollivanders2API.getItems().getWands().makeWand(o2p.getDestinedWandWood(), O2WandCoreType.VEELA_HAIR.getLabel(), 1);
        assertNotNull(wand);
        assertFalse(Ollivanders2API.getItems().getWands().isDestinedWand(o2p, wand));
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#createCorelessWand(O2WandWoodType, int)}.
     * <p>
     * Verifies that coreless wands have wood NBT but no core NBT and are not detected as complete wands.
     * </p>
     * <ul>
     *   <li>Coreless wand: Has wood NBT</li>
     *   <li>Coreless wand: Does not have full wand NBT</li>
     *   <li>Coreless wand: Not detected as a wand</li>
     *   <li>Amount is preserved: Returns correct quantity</li>
     * </ul>
     */
    @Test
    void createCorelessWandTest() {
        ItemStack corelessWand = Ollivanders2API.getItems().getWands().createCorelessWand(O2WandWoodType.OAK, 1);
        assertNotNull(corelessWand, "createCorelessWand() returned null");

        // verify it has wood NBT
        assertTrue(Ollivanders2API.getItems().getWands().checkWoodNBT(O2WandWoodType.OAK.getLabel(), corelessWand), "createCorelessWand() did not set wood NBT");

        // verify it does not have full wand NBT (no core)
        assertFalse(Ollivanders2API.getItems().getWands().checkNBT(corelessWand), "createCorelessWand() should not have full wand NBT");

        // verify it is not detected as a wand
        assertFalse(Ollivanders2API.getItems().getWands().isWand(corelessWand), "createCorelessWand() should not be detected as a wand");

        // verify amount
        int amount = 3;
        corelessWand = Ollivanders2API.getItems().getWands().createCorelessWand(O2WandWoodType.OAK, amount);
        assertNotNull(corelessWand);
        assertEquals(amount, corelessWand.getAmount(), "createCorelessWand() did not return expected amount");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#makeWandFromCoreless(ItemStack, O2WandCoreType, int)}.
     * <p>
     * Verifies that a core can be added to a coreless wand, and that the wood type is preserved or
     * falls back to random if not present.
     * </p>
     * <ul>
     *   <li>Coreless wand with known wood + core: Wood and core preserved/set correctly</li>
     *   <li>Plain item without wood NBT: Falls back to random wood, creates valid wand</li>
     * </ul>
     */
    @Test
    void makeWandFromCorelessTest() {
        // create a coreless wand with known wood
        ItemStack corelessWand = Ollivanders2API.getItems().getWands().createCorelessWand(O2WandWoodType.OAK, 1);
        assertNotNull(corelessWand);

        // make a full wand from the coreless wand
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWandFromCoreless(corelessWand, O2WandCoreType.UNICORN_HAIR, 1);
        assertNotNull(wand, "makeWandFromCoreless() returned null");
        assertTrue(Ollivanders2API.getItems().getWands().isWand(wand), "makeWandFromCoreless() did not return a valid wand");

        // verify wood was preserved from the coreless wand
        assertTrue(Ollivanders2API.getItems().getWands().checkWoodNBT(O2WandWoodType.OAK.getLabel(), wand), "makeWandFromCoreless() did not preserve wood type");

        // verify core was set
        assertTrue(Ollivanders2API.getItems().getWands().checkCoreNBT(O2WandCoreType.UNICORN_HAIR.getLabel(), wand), "makeWandFromCoreless() did not set core type");

        // test with a plain item (no wood NBT) - should fall back to random wood
        ItemStack plainItem = new ItemStack(Material.STICK, 1);
        wand = Ollivanders2API.getItems().getWands().makeWandFromCoreless(plainItem, O2WandCoreType.DRAGON_HEARTSTRING, 1);
        assertNotNull(wand, "makeWandFromCoreless() returned null for plain item fallback");
        assertTrue(Ollivanders2API.getItems().getWands().isWand(wand), "makeWandFromCoreless() fallback did not return a valid wand");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#giveRandomWand(Player)}.
     * <p>
     * Verifies that a random wand is created and successfully added to the player's inventory.
     * </p>
     * <ul>
     *   <li>Method returns true indicating success</li>
     *   <li>Player inventory contains a valid wand</li>
     * </ul>
     */
    @Test
    void giveRandomWandTest() {
        PlayerMock player = mockServer.addPlayer();

        boolean result = Ollivanders2API.getItems().getWands().giveRandomWand(player);
        assertTrue(result, "giveRandomWand() returned false");

        // verify the player has a wand in their inventory
        boolean foundWand = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && Ollivanders2API.getItems().getWands().isWand(item)) {
                foundWand = true;
                break;
            }
        }
        assertTrue(foundWand, "giveRandomWand() did not add a wand to the player's inventory");
    }

    /**
     * Test {@link net.pottercraft.ollivanders2.item.wand.O2Wands#createRandomWand()}.
     * <p>
     * Verifies that a random wand is created with valid wood and core types.
     * </p>
     * <ul>
     *   <li>Returns a valid wand (not null)</li>
     *   <li>Wand is recognized as a wand</li>
     *   <li>Amount is exactly 1</li>
     * </ul>
     */
    @Test
    void createRandomWandTest() {
        ItemStack wand = Ollivanders2API.getItems().getWands().createRandomWand();
        assertNotNull(wand, "createRandomWand() returned null");
        assertTrue(Ollivanders2API.getItems().getWands().isWand(wand), "createRandomWand() did not return a valid wand");
        assertEquals(1, wand.getAmount(), "createRandomWand() did not return a single wand");
    }

    /**
     * Reset test state after each test method.
     * <p>
     * Ensures the debug flag is disabled after each test to prevent debug output from affecting
     * subsequent tests or polluting test logs.
     * </p>
     */
    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    /**
     * Clean up MockBukkit server after all tests complete.
     * <p>
     * Releases MockBukkit resources and unloads the mock server. This must be called after all
     * tests in the class have finished to properly clean up the test environment and allow
     * other test classes to create fresh mock servers.
     * </p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}

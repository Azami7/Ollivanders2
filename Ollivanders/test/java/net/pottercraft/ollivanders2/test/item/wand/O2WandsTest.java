package net.pottercraft.ollivanders2.test.item.wand;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.events.OllivandersPlayerFoundWandEvent;
import net.pottercraft.ollivanders2.player.events.OllivandersPlayerNotDestinedWandEvent;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for O2Wands.
 */
public class O2WandsTest {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once for the test class.
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
     * isWand() is true for the Elder Wand and for a WAND with wood+core NBT, and false for other items or a WAND
     * missing that NBT.
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
     * checkNBT() requires both wood and core NBT keys, so a coreless wand or a non-wand item fails.
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
     * createLore() formats the lore as "wood and core".
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
     * checkCoreNBT() is true only when the item's core NBT matches the given core, and false on mismatch or missing NBT.
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
     * checkWoodNBT() is true only when the item's wood NBT matches the given wood, and false on mismatch or missing NBT.
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
     * holdsWand() detects a wand in the specified hand; the no-arg overload checks only the main hand.
     */
    @Test
    void holdsWandTest() {
        PlayerMock player = mockServer.addPlayer();

        // empty hand
        assertFalse(Ollivanders2API.getItems().getWands().holdsWandInPrimary(player), "holdsWand() returned true for player with empty hand");

        // wand in main hand
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), O2WandCoreType.UNICORN_HAIR.getLabel(), 1);
        assertNotNull(wand);
        player.getInventory().setItemInMainHand(wand);
        assertTrue(Ollivanders2API.getItems().getWands().holdsWandInPrimary(player), "holdsWand() returned false for player holding a wand in main hand");

        // non-wand item in main hand
        player.getInventory().setItemInMainHand(new ItemStack(Material.APPLE, 1));
        assertFalse(Ollivanders2API.getItems().getWands().holdsWandInPrimary(player), "holdsWand() returned true for player holding Material.APPLE");

        // wand in off-hand, main hand empty
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        player.getInventory().setItemInOffHand(wand);
        assertTrue(Ollivanders2API.getItems().getWands().holdsWandInOff(player), "holdsWand() returned false for player holding a wand in off hand");

        // default holdsWand only checks main hand
        assertFalse(Ollivanders2API.getItems().getWands().holdsWandInPrimary(player), "holdsWand() returned true for main hand when wand is only in off hand");
    }

    /**
     * getAllWands() returns a valid wand for every wood × core combination.
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
     * makeWand() builds a wand of the requested amount for a valid wood+core, and returns null if either is invalid.
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
     * isDestinedWand() (both overloads) is true only when the wand's wood and core both match the player's destined wand.
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
     * waveWand() reports the player has found their destined wand, and flags it on their player, only when the wand
     * they hold is allied to them; any other wand fires the not-destined event instead.
     */
    @Test
    void waveWandTest() {
        World testWorld = mockServer.addSimpleWorld("waveWandWorld");

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 100, 40, 100));

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(o2p);
        o2p.setWandCore(O2WandCoreType.UNICORN_HAIR.getLabel());
        o2p.setWandWood(O2WandWoodType.OAK.getLabel());

        // the events are the only output of waveWand that a caller can observe, so listen for both
        AtomicBoolean foundWandFired = new AtomicBoolean(false);
        AtomicBoolean notDestinedFired = new AtomicBoolean(false);
        mockServer.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onFoundWand(OllivandersPlayerFoundWandEvent event) {
                if (event.getPlayer().equals(player))
                    foundWandFired.set(true);
            }

            @EventHandler
            public void onNotDestinedWand(OllivandersPlayerNotDestinedWandEvent event) {
                if (event.getPlayer().equals(player))
                    notDestinedFired.set(true);
            }
        }, testPlugin);

        // a wand that is not the player's destined wand is reported as such
        ItemStack otherWand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.ACACIA.getLabel(), O2WandCoreType.VEELA_HAIR.getLabel(), 1);
        assertNotNull(otherWand);
        player.getInventory().setItemInMainHand(otherWand);

        Ollivanders2API.getItems().getWands().waveWand(player);

        assertTrue(notDestinedFired.get(), "waving a wand that is not the player's should fire the not-destined event");
        assertFalse(foundWandFired.get(), "waving a wand that is not the player's should not fire the found wand event");
        assertFalse(o2p.foundWand(), "waving a wand that is not the player's should not flag them as having found it");

        // the player's destined wand is reported as found
        notDestinedFired.set(false);
        ItemStack destinedWand = Ollivanders2API.getItems().getWands().makeWand(o2p.getDestinedWandWood(), o2p.getDestinedWandCore(), 1);
        assertNotNull(destinedWand);
        player.getInventory().setItemInMainHand(destinedWand);

        Ollivanders2API.getItems().getWands().waveWand(player);

        assertTrue(foundWandFired.get(), "waving their destined wand should fire the found wand event");
        assertFalse(notDestinedFired.get(), "waving their destined wand should not fire the not-destined event");
        assertTrue(o2p.foundWand(), "waving their destined wand should flag them as having found it");
    }

    /**
     * createCorelessWand() sets wood NBT but no core NBT, so it is not a complete wand, and preserves the amount.
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
     * makeWandFromCoreless() adds the core and keeps the coreless wand's wood, falling back to a random wood when the
     * source has no wood NBT.
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
     * giveRandomWand() succeeds and leaves a valid wand in the player's inventory.
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
     * createRandomWand() returns a single valid wand.
     */
    @Test
    void createRandomWandTest() {
        ItemStack wand = Ollivanders2API.getItems().getWands().createRandomWand();
        assertNotNull(wand, "createRandomWand() returned null");
        assertTrue(Ollivanders2API.getItems().getWands().isWand(wand), "createRandomWand() did not return a valid wand");
        assertEquals(1, wand.getAmount(), "createRandomWand() did not return a single wand");
    }

    /**
     * isCorelessWand() is true only for an item with wood NBT but no core NBT.
     */
    @Test
    void isCorelessWandTest() {
        // coreless wand should return true
        ItemStack corelessWand = Ollivanders2API.getItems().getWands().createCorelessWand(O2WandWoodType.OAK, 1);
        assertNotNull(corelessWand);
        assertTrue(Ollivanders2API.getItems().getWands().isCorelessWand(corelessWand), "isCorelessWand() returned false for a coreless wand");

        // complete wand should return false
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.OAK.getLabel(), O2WandCoreType.UNICORN_HAIR.getLabel(), 1);
        assertNotNull(wand);
        assertFalse(Ollivanders2API.getItems().getWands().isCorelessWand(wand), "isCorelessWand() returned true for a complete wand");

        // non-wand item should return false
        ItemStack apple = new ItemStack(Material.APPLE, 1);
        assertFalse(Ollivanders2API.getItems().getWands().isCorelessWand(apple), "isCorelessWand() returned true for Material.APPLE");
    }

    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}

package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive test suite for the {@link ALIQUAM_FLOO} stationary spell.
 *
 * <p>Tests all Floo Network functionality including:
 * <ul>
 *   <li>Fireplace registration and network management</li>
 *   <li>Floo powder item consumption and spell activation</li>
 *   <li>Player teleportation between registered floo locations</li>
 *   <li>Fire and combustion damage prevention within spell area</li>
 *   <li>Block type transformations (fire/soul fire, campfire/soul campfire)</li>
 *   <li>Floo network deactivation on spell termination</li>
 *   <li>Spell serialization and deserialization</li>
 * </ul>
 * </p>
 *
 * <p>Note: All floo functionality is tested in a single test method ({@link #aliquamFlooTest()})
 * because the static floo network list can cause test interference when tests run in parallel.
 * This prevents flaky test results while still providing comprehensive coverage.</p>
 *
 * @author Azami7
 */
public class AliquamFlooTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded fresh before each test method with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Comprehensive test of all ALIQUAM_FLOO spell functionality.
     *
     * <p>This test method exercises all aspects of the Floo Network spell including:
     * <ul>
     *   <li>Creating and registering floo fireplace locations</li>
     *   <li>Verifying fireplace metadata (permanence, radius)</li>
     *   <li>Testing floo powder consumption and fireplace activation</li>
     *   <li>Verifying fire/combustion damage prevention within the spell area</li>
     *   <li>Testing player teleportation between registered fireplace locations</li>
     *   <li>Verifying location validation (preventing teleport outside spell area)</li>
     *   <li>Testing spell deactivation and fireplace state restoration</li>
     *   <li>Testing block type transformations (fire ↔ soul fire, campfire ↔ soul campfire)</li>
     *   <li>Testing configuration option for soul fire visual effects</li>
     *   <li>Testing floo network clearing and spell batch operations</li>
     *   <li>Testing serialization and deserialization of spell state</li>
     * </ul>
     * </p>
     *
     * <p>All floo functionality is tested in this single method to avoid test interference
     * caused by the static floo network list during parallel test execution.</p>
     */
    @Test
    void aliquamFlooTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location1 = new Location(testWorld, 100, 40, 100);
        Location location2 = new Location(testWorld, 200, 40, 200);
        String floo1Name = "One";
        String floo2Name = "Two";
        PlayerMock player = mockServer.addPlayer();

        // add a fire block to the location
        Block floo1FireBlock = testWorld.getBlockAt(location1);
        Material fireFlooBlockType = Material.FIRE;
        floo1FireBlock.getRelative(BlockFace.DOWN).setType(Material.NETHERRACK);
        floo1FireBlock.setType(fireFlooBlockType);

        // create the floo fireplace
        ALIQUAM_FLOO aliquamFloo1 = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location1, floo1Name);
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aliquamFloo1.isPermanent(), "Aliquam Floo not permanent");
        assertEquals(ALIQUAM_FLOO.minRadiusConfig, aliquamFloo1.getRadius(), "Aliquam floo radius expected");
        assertFalse(ALIQUAM_FLOO.getFlooFireplaceNames().isEmpty(), "floo1 is not registered in the network");

        // create a second floo fireplace
        ALIQUAM_FLOO aliquamFloo2 = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location2, floo2Name);
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo2);
        assertEquals(2, ALIQUAM_FLOO.getFlooFireplaceNames().size(), "floo2 is not registered in the network");

        // dropping random item in the fire does nothing
        ItemStack notFlooPowder = O2ItemType.ACONITE.getItem(1);
        assertNotNull(notFlooPowder);
        Item droppedItem = testWorld.dropItem(location1, notFlooPowder);
        mockServer.getScheduler().performTicks(20);
        assertFalse(aliquamFloo1.isWorking(), "Floo1 is active when no floo powder added");
        droppedItem.remove();

        // dropping floo powder item in the fire consumes item and activates fireplace
        ItemStack flooPowder = O2ItemType.FLOO_POWDER.getItem(1);
        assertNotNull(flooPowder);
        droppedItem = testWorld.dropItem(location1, flooPowder);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aliquamFloo1.isWorking(), "Floo not active when floo powder present");
        assertTrue(droppedItem.isDead(), "Consumed floo powder not removed");

        // put the player in the now active fireplace
        player.setLocation(location1);

        // damage from fire is canceled
        DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                .withDamageLocation(player.getLocation())  // location of the fire block
                .build();
        EntityDamageEvent damageEvent = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(damageEvent);
        mockServer.getScheduler().performTicks(20);
        assertTrue(damageEvent.isCancelled(), "EntityDamageEvent caused by fire not cancelled");

        // damage from other sources is not canceled
        damageSource = DamageSource.builder(DamageType.ARROW)
                .withDamageLocation(player.getLocation())  // location of the fire block
                .build();
        damageEvent = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(damageEvent);
        mockServer.getScheduler().performTicks(20);
        assertFalse(damageEvent.isCancelled(), "EntityDamageEvent not caused by fire was cancelled");

        // combust events are canceled
        EntityCombustEvent combustEvent = new EntityCombustEvent(player, 5f);
        mockServer.getPluginManager().callEvent(combustEvent);
        mockServer.getScheduler().performTicks(20);
        assertTrue(combustEvent.isCancelled(), "EntityCombustEvent not cancelled");

        // chatting a floo name teleports the player and then deactivates the fireplace
        player.chat(floo2Name);
        AsyncPlayerChatEvent chatEvent = new AsyncPlayerChatEvent(false, player, floo2Name, new HashSet<>());
        mockServer.getPluginManager().callEvent(chatEvent);
        mockServer.getScheduler().performTicks(500);
        assertFalse(aliquamFloo1.isWorking(), "floo1 not disabled");
        assertEquals(location2, player.getLocation(), "Player not teleported to floo2");
        String message = player.nextMessage();
        assertNotNull(message, "Player did not receive successful floo event message");
        assertEquals(ALIQUAM_FLOO.successMessage, TestCommon.cleanChatMessage(message), "Player did not receive expected success message");

        // player chatting floo name when fireplace inactive does nothing
        player.chat(floo1Name);
        chatEvent = new AsyncPlayerChatEvent(false, player, floo1Name, new HashSet<>());
        mockServer.getPluginManager().callEvent(chatEvent);
        mockServer.getScheduler().performTicks(500);
        assertEquals(location2, player.getLocation(), "Player teleported when floo2 inactive");

        // player chatting floo network name outside of spell area while fireplace active does nothing
        Location outsideFloo2 = new Location(location2.getWorld(), location2.getX() + aliquamFloo2.getRadius() + 1, location2.getY(), location2.getZ());
        assertFalse(aliquamFloo2.isLocationInside(outsideFloo2));
        player.setLocation(outsideFloo2);
        testWorld.dropItem(location2, flooPowder);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aliquamFloo2.isWorking());
        player.chat(floo1Name);
        chatEvent = new AsyncPlayerChatEvent(false, player, floo1Name, new HashSet<>());
        mockServer.getPluginManager().callEvent(chatEvent);
        mockServer.getScheduler().performTicks(500);
        assertEquals(outsideFloo2, player.getLocation(), "Player teleported when not standing in the spell area");
        assertTrue(aliquamFloo2.isWorking(), "Floo2 deactivated when player chatted outside the spell area");

        // player successfully teleports back to floo1 when chatting in the spell area
        player.setLocation(location2);
        player.chat(floo1Name);
        chatEvent = new AsyncPlayerChatEvent(false, player, floo1Name, new HashSet<>());
        mockServer.getPluginManager().callEvent(chatEvent);
        mockServer.getScheduler().performTicks(500);
        assertEquals(location1, player.getLocation(), "Player not teleported back to floo1");

        // player gets a message when teleport is successful
        message = player.nextMessage();
        assertNotNull(message, "Player did not get message on floo event");

        // doCleanUp - fireplace is deactivated if floo spell is killed
        testWorld.dropItem(location2, flooPowder);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aliquamFloo2.isWorking());
        aliquamFloo2.kill();
        assertFalse(aliquamFloo2.isWorking(), "floo2 still working after being killed");

        // test soul fire block change
        assertEquals(fireFlooBlockType, floo1FireBlock.getType());
        testWorld.dropItem(location1, flooPowder);
        mockServer.getScheduler().performTicks(20);
        assertEquals(Material.SOUL_FIRE, location1.getBlock().getType(), "Floo1 block type not changed to soul fire");

        aliquamFloo1.kill();
        mockServer.getScheduler().performTicks(20);
        assertFalse(aliquamFloo1.isWorking());
        assertEquals(fireFlooBlockType, location1.getBlock().getType(), "Floo1 block type not reset when fireplace disabled");

        // test campfire block
        Material campfireFlooBlockType = Material.CAMPFIRE;
        location2.getBlock().setType(campfireFlooBlockType);
        aliquamFloo2 = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location2, floo2Name);
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo2);
        testWorld.dropItem(location2, flooPowder);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aliquamFloo2.isWorking());
        assertEquals(Material.SOUL_CAMPFIRE, location2.getBlock().getType(), "floo2 not changed to soul campfire when active");
        aliquamFloo2.kill();
        mockServer.getScheduler().performTicks(20);
        assertFalse(aliquamFloo2.isWorking());
        assertEquals(campfireFlooBlockType, location2.getBlock().getType(), "floo2 block not reset after floo deactivation");

        // test without soulfire floo effect
        testPlugin.getConfig().set("soulFireFlooEffect", false);
        location1.getBlock().setType(fireFlooBlockType);
        aliquamFloo1 = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location1, floo1Name);
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo1);
        testWorld.dropItem(location1, flooPowder);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aliquamFloo1.isWorking());
        assertEquals(fireFlooBlockType, location1.getBlock().getType(), "floo1 block type changed when soulfireFlooEffect disabled");

        // test clearing floo network
        aliquamFloo1 = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location1, floo1Name);
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo1);
        aliquamFloo2 = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location2, floo2Name);
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo2);
        ALIQUAM_FLOO.clearFlooNetwork();
        mockServer.getScheduler().performTicks(20);
        assertTrue(aliquamFloo1.isKilled(), "Floo1 not removed");
        assertTrue(aliquamFloo2.isKilled(), "Floo2 not removed");
        assertTrue(ALIQUAM_FLOO.getFlooFireplaceNames().isEmpty(), "floo network not cleared");

        // test serialization and deserialization
        aliquamFloo1 = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location1, floo1Name);
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo1);
        aliquamFloo2 = new ALIQUAM_FLOO(testPlugin, player.getUniqueId(), location2, floo2Name);
        Ollivanders2API.getStationarySpells().addStationarySpell(aliquamFloo2);

        // save the spells to disk
        int activeSpellsBefore = Ollivanders2API.getStationarySpells().getActiveStationarySpells().size();
        Ollivanders2API.getStationarySpells().saveO2StationarySpells();

        // clear the active spells and floo network
        for (O2StationarySpell spell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
            spell.kill();
        }
        mockServer.getScheduler().performTicks(20);
        ALIQUAM_FLOO.clearFlooNetwork();
        assertTrue(Ollivanders2API.getStationarySpells().getActiveStationarySpells().isEmpty(), "Active spells not cleared");
        assertTrue(ALIQUAM_FLOO.getFlooFireplaceNames().isEmpty(), "Floo network not cleared");

        // load the spells from disk
        Ollivanders2API.getStationarySpells().loadO2StationarySpells();
        mockServer.getScheduler().performTicks(20);

        // verify the spells were loaded
        int activeSpellsAfter = Ollivanders2API.getStationarySpells().getActiveStationarySpells().size();
        assertEquals(activeSpellsBefore, activeSpellsAfter, "Loaded spell count does not match saved spell count");
        assertEquals(2, ALIQUAM_FLOO.getFlooFireplaceNames().size(), "Floo network not restored from save");
    }

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}

package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the HORCRUX stationary spell.
 *
 * <p>Tests spell-specific behavior for the resurrection anchor that allows players to respawn
 * at the horcrux location when killed. Verifies protection of the horcrux item, player
 * resurrection mechanics, debilitation effects on nearby players, and cooldown management.</p>
 *
 * @author Azami7
 */
public class HorcruxTest {
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

    @Test
    void upkeepTest() {
        // tested by doOnPlayerMoveEventTest()
    }

    /**
     * Tests horcrux item respawning on player join.
     *
     * <p>Verifies that when a player who owns a horcrux rejoins the server, the horcrux item
     * is respawned at its original location if it had been removed or despawned.</p>
     */
    @Test
    void doOnPlayerJoinEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        // create a solid base so that the horcrux item stays in place, all blocks are air by default so the dropped item would fall
        Block baseBlock = testWorld.getBlockAt(location).getRelative(BlockFace.DOWN);
        baseBlock.setType(Material.DIRT);

        // create horcrux
        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.COMPASS, 1)));
        Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);
        mockServer.getScheduler().performTicks(20);

        Item horcruxItem = EntityCommon.getItemAtLocation(location);
        assertNotNull(horcruxItem);

        // log player off and remove item
        caster.disconnect();
        horcruxItem.remove();
        mockServer.getScheduler().performTicks(20);
        assertTrue(horcruxItem.isDead());

        // log player back on and confirm the horcrux is respawned
        caster.reconnect();
        PlayerJoinEvent event = new PlayerJoinEvent(caster, "message");
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        Item newHorcruxItem = EntityCommon.getItemAtLocation(location);
        assertNotNull(newHorcruxItem, "Horcrux item not respawned on player join");
        assertEquals(horcruxItem.getItemStack().getType(), newHorcruxItem.getItemStack().getType(), "respawned horcrux item not expected type");
    }

    /**
     * Tests player death handling with horcrux resurrection.
     *
     * <p>Verifies that when the horcrux owner dies, they are respawned at the horcrux location
     * and their spell level and experience are preserved despite death.</p>
     */
    @Test
    void doOnPlayerDeathEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 40, 100);
        Location otherLocation = new Location(location.getWorld(), location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.SPECTRAL_ARROW, 1)));
        Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);
        mockServer.getScheduler().performTicks(20);

        caster.setLocation(otherLocation);
        caster.setRespawnLocation(otherLocation, true);
        assertNotNull(caster.getRespawnLocation());
        int expectedLevel = 10;
        caster.setLevel(expectedLevel);
        int expectedExperience = 100;
        caster.setTotalExperience(expectedExperience);

        DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                .withDamageLocation(caster.getLocation())  // location of the fire block
                .build();

        PlayerDeathEvent event = new PlayerDeathEvent(caster, damageSource, new ArrayList<>(), 10, 90, 90, 9, "you have died");
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        // player should be teleported to their horcrux location
        assertNotNull(caster.getRespawnLocation());
        assertEquals(location, caster.getRespawnLocation(), "Caster respawn location not set to horcrux location");
        assertEquals(expectedLevel, caster.getLevel(), "Caster level affected by death");
        assertEquals(expectedExperience, caster.getTotalExperience(), "Caster experience affected by death");
    }

    /**
     * Tests player debilitation effects and cooldown mechanics.
     *
     * <p>Verifies that non-owner players entering the horcrux radius receive blindness and wither effects,
     * the horcrux owner is immune to these effects, and the spell implements a cooldown preventing
     * repeated effect applications. Also tests upkeep behavior for cooldown decrements.</p>
     */
    @Test
    void doOnPlayerMoveEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.APPLE, 1)));
        Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);
        mockServer.getScheduler().performTicks(20);

        // a player is affected by the horcrux if they are in its radius
        PlayerMock player = mockServer.addPlayer();
        Location outsideLocation = new Location(location.getWorld(), location.getX() + HORCRUX.maxRadiusConfig + 1, location.getY(), location.getZ());
        player.setLocation(outsideLocation);
        assertFalse(horcrux.isLocationInside(player.getLocation()));

        PlayerMoveEvent event = new PlayerMoveEvent(player, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(horcrux.isAffected(player), "Player was not affected when moving within the radius of a horcrux");
        assertTrue(player.hasPotionEffect(PotionEffectType.BLINDNESS), "blindness effect not added");
        assertTrue(player.hasPotionEffect(PotionEffectType.WITHER), "wither effect not added");

        // the caster is not affected by the horcrux
        caster.setLocation(outsideLocation);
        assertFalse(horcrux.isLocationInside(caster.getLocation()));

        event = new PlayerMoveEvent(caster, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertFalse(horcrux.isAffected(caster), "horcrux affected caster");
        assertFalse(caster.hasPotionEffect(PotionEffectType.BLINDNESS), "blindness effect added to caster");
        assertFalse(caster.hasPotionEffect(PotionEffectType.WITHER), "wither effect added to caster");

        // upkeep prevents a player being affected again too soon by the horcrux and decrements the cooldown
        // test upkeep here since it needs the code above for set up
        PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.BLINDNESS);
        assertNotNull(potionEffect);
        int duration = potionEffect.getDuration();

        player.setLocation(outsideLocation);
        event = new PlayerMoveEvent(player, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        potionEffect = player.getPotionEffect(PotionEffectType.BLINDNESS);
        assertNotNull(potionEffect);
        // potion duration should have gone down
        assertTrue(duration > potionEffect.getDuration(), "Potion duration increased when player moved back in horcrux range while cooldown still running");

        player.setLocation(outsideLocation);
        mockServer.getScheduler().performTicks(HORCRUX.effectDuration);
        assertFalse(horcrux.isAffected(player), "player still affected after cooldown time past");
    }

    /**
     * Tests horcrux item despawn protection.
     *
     * <p>Verifies that the horcrux item cannot be despawned naturally, preventing its loss
     * during server cleanup of dropped items.</p>
     */
    @Test
    void doOnItemDespawnEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 400, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.APPLE, 1)));
        Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);
        mockServer.getScheduler().performTicks(20);

        Item horcruxItem = EntityCommon.getItemAtLocation(location);
        assertNotNull(horcruxItem);
        ItemDespawnEvent event = new ItemDespawnEvent(horcruxItem, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(event.isCancelled(), "horcrux item despawn event not canceled");
    }

    /**
     * Tests horcrux item entity pickup protection.
     *
     * <p>Verifies that the horcrux item cannot be picked up by any entity (players, mobs),
     * keeping it protected at its location.</p>
     */
    @Test
    void doOnEntityPickupItemEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 500, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.APPLE, 1)));
        Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);
        mockServer.getScheduler().performTicks(20);

        Item horcruxItem = EntityCommon.getItemAtLocation(location);
        assertNotNull(horcruxItem);
        EntityPickupItemEvent event = new EntityPickupItemEvent(caster, horcruxItem, 0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(event.isCancelled(), "entity item pickup event not cancelled");
    }

    /**
     * Tests horcrux item inventory system pickup protection.
     *
     * <p>Verifies that the horcrux item cannot be collected by inventory systems like hoppers,
     * minecarts with hoppers, or other automated item collectors.</p>
     */
    @Test
    void doOnInventoryItemPickupEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 600, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.APPLE, 1)));
        Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);
        mockServer.getScheduler().performTicks(20);

        Item horcruxItem = EntityCommon.getItemAtLocation(location);
        assertNotNull(horcruxItem);
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER);
        InventoryPickupItemEvent event = new InventoryPickupItemEvent(inventory, horcruxItem);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(event.isCancelled(), "inventory item pickup event not cancelled");
    }

    /**
     * Tests serialization and deserialization of horcrux spell data.
     *
     * <p>Verifies that the horcrux material type and world name are correctly serialized to a map,
     * and that deserializing valid data restores the spell state. Also verifies that deserializing
     * with an invalid material name kills the spell, and that missing data kills the spell.</p>
     */
    @Test
    void serializeAndDeserializeSpellDataTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 700, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.DIAMOND, 1)));

        // serialize and verify data
        Map<String, String> spellData = horcrux.serializeSpellData();
        assertFalse(spellData.isEmpty(), "serialized spell data is empty");
        assertEquals(Material.DIAMOND.toString(), spellData.get("itemType"), "serialized material not correct");
        assertEquals(testWorld.getName(), spellData.get("world"), "serialized world not correct");

        // deserialize valid data into a new spell created with the full constructor
        // the full constructor is needed because kill() requires a non-null playerUUID
        HORCRUX deserialized = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.STICK, 1)));
        deserialized.deserializeSpellData(spellData);
        assertFalse(deserialized.isKilled(), "spell killed after deserializing valid data");

        // deserialize with invalid material kills the spell
        HORCRUX invalidMaterial = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.STICK, 1)));
        Map<String, String> badMaterialData = new HashMap<>();
        badMaterialData.put("itemType", "NOT_A_REAL_MATERIAL");
        badMaterialData.put("world", testWorld.getName());
        invalidMaterial.deserializeSpellData(badMaterialData);
        assertTrue(invalidMaterial.isKilled(), "spell not killed after deserializing invalid material");
    }

    /**
     * Tests spell deserialization validation.
     *
     * <p>Verifies that a horcrux created via the deserialization constructor fails
     * checkSpellDeserialization() because it lacks required data (player UUID, location,
     * and horcrux material), while a properly initialized spell passes validation.</p>
     */
    @Test
    void checkSpellDeserializationTest() {
        O2StationarySpell stationarySpell = Ollivanders2API.getStationarySpells().createStationarySpellByType(O2StationarySpellType.HORCRUX);
        assertNotNull(stationarySpell);
        assertFalse(stationarySpell.checkSpellDeserialization(), "Deserialized spell should fail check without required data");

        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 800, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), location, testWorld.dropItem(location, new ItemStack(Material.APPLE, 1)));
        assertTrue(horcrux.checkSpellDeserialization(), "Properly initialized horcrux failed deserialization check");
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

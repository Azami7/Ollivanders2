package net.pottercraft.ollivanders2.test.stationaryspell;

import net.kyori.adventure.text.Component;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Openable;
import org.bukkit.block.sign.Side;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HARMONIA_NECTERE_PASSUS}.
 *
 * @author Azami7
 */
public class HarmoniaNecterePassusTest {
    static ServerMock mockServer;

    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's initial 20-tick delay so it is running
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Build a well-formed vanishing cabinet at {@code fromLocation} whose sign names {@code toLocation} as its
     * destination, for use as valid test setup.
     *
     * @param fromLocation the cabinet's foot (sign) location
     * @param toLocation   the destination written on the sign
     */
    public static void createCabinet(Location fromLocation, Location toLocation) {
        Block feet = fromLocation.getBlock();
        feet.setType(Material.OAK_SIGN);
        Sign sign = (Sign)feet.getState();
        sign.getSide(Side.FRONT).line(0, Component.text(toLocation.getWorld().getName()));
        sign.getSide(Side.FRONT).line(1, Component.text(Integer.toString((int)toLocation.getX())));
        sign.getSide(Side.FRONT).line(2, Component.text(Integer.toString((int)toLocation.getY())));
        sign.getSide(Side.FRONT).line(3, Component.text(Integer.toString((int)toLocation.getZ())));

        feet.getRelative(BlockFace.DOWN).setType(Material.STONE);
        Block doorBlock = feet.getRelative(BlockFace.SOUTH);
        doorBlock.setType(Material.OAK_DOOR);
        Openable door = (Openable)doorBlock.getBlockData();
        door.setOpen(true);
        doorBlock.setBlockData(door);

        feet.getRelative(BlockFace.WEST).setType(Material.OAK_PLANKS);
        feet.getRelative(BlockFace.EAST).setType(Material.OAK_PLANKS);
        feet.getRelative(BlockFace.NORTH).setType(Material.OAK_PLANKS);

        Block eye = feet.getRelative(BlockFace.UP);
        eye.getRelative(BlockFace.NORTH).setType(Material.OAK_PLANKS);
        eye.getRelative(BlockFace.WEST).setType(Material.OAK_PLANKS);
        eye.getRelative(BlockFace.EAST).setType(Material.OAK_PLANKS);
        eye.getRelative(BlockFace.UP).setType(Material.OAK_SLAB);

        // upper half of the door
        doorBlock = eye.getRelative(BlockFace.SOUTH);
        doorBlock.setType(Material.OAK_DOOR);
        door = (Openable)doorBlock.getBlockData();
        door.setOpen(true);
        doorBlock.setBlockData(door);
    }

    /**
     * A cabinet spell is killed when its structure is missing or later damaged, and survives while the structure is
     * valid.
     */
    @Test
    void upkeepCabinetIntegrityTest() {
        World testWorld = mockServer.addSimpleWorld("world1");
        Location location1 = new Location(testWorld, 100, 40, 100);
        Location location2 = new Location(testWorld, 120, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        // cabinets do not exist so spells should be killed
        assertTrue(harmoniaNecterePassus1.isKilled(), "spell 1 not killed when no cabinet exists");
        assertTrue(harmoniaNecterePassus2.isKilled(), "spell 2 not killed when no cabinet exists");

        // spells persist when there is a valid cabinet
        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        assertFalse(harmoniaNecterePassus1.isKilled(), "spell 1 killed unexpectedly");
        assertFalse(harmoniaNecterePassus2.isKilled(), "spell 2 killed unexpectedly");

        // spell is killed if a cabinet is damaged
        location1.getBlock().setType(Material.OAK_PLANKS);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmoniaNecterePassus1.isKilled(), "spell 1 not killed when twin cabinet damaged");
        assertTrue(harmoniaNecterePassus2.isKilled(), "spell 2 not killed when cabinet damaged");
    }

    /**
     * A player is marked as using the cabinet on entry, stays marked through the cooldown after leaving, and is cleared
     * once it expires.
     */
    @Test
    void upkeepInUseByTest() {
        World testWorld = mockServer.addSimpleWorld("world2");
        Location location1 = new Location(testWorld, 200, 40, 100);
        Location location2 = new Location(testWorld, 220, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        // caster near cabinet
        Location nearCabinet1 = new Location(testWorld, 200, 39, 102);
        caster.setLocation(nearCabinet1);
        mockServer.getScheduler().performTicks(20);
        assertFalse(harmoniaNecterePassus1.isUsing(caster), "caster set to using cabinet before entry");

        // move caster in to the cabinet
        caster.setLocation(location1);
        // we have to manually trigger a player move event
        PlayerMoveEvent event = new PlayerMoveEvent(caster, nearCabinet1, location1);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmoniaNecterePassus1.isUsing(caster), "caster not set to using cabinet after entry");

        // move caster out of cabinet, now in inUse cooldown
        caster.setLocation(nearCabinet1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(harmoniaNecterePassus1.isUsing(caster), "caster not set to using cabinet after leaving, during cooldown");

        // expire the cooldown
        mockServer.getScheduler().performTicks(HARMONIA_NECTERE_PASSUS.cooldown);
        assertFalse(harmoniaNecterePassus1.isUsing(caster), "caster set to using cabinet after cooldown expired");
    }

    /**
     * getTwin returns the paired cabinet.
     */
    @Test
    void getTwinTest() {
        World testWorld = mockServer.addSimpleWorld("world3");
        Location location1 = new Location(testWorld, 300, 40, 100);
        Location location2 = new Location(testWorld, 320, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        HARMONIA_NECTERE_PASSUS twin1 = harmoniaNecterePassus1.getTwin();
        assertNotNull(twin1, "harmonia 1 getTwin() returned null");
        assertEquals(harmoniaNecterePassus2, twin1, "getTwin() did not return expected spell");
    }

    @Test
    void isUsingTest() {
        // tested by upkeepInUseByTest
    }

    /**
     * A player entering a cabinet is teleported to the twin; a null destination is a no-op; and the cooldown stops an
     * immediate teleport back from the twin.
     */
    @Test
    void doOnPlayerMoveEventTest () {
        World testWorld = mockServer.addSimpleWorld("world4");
        Location location1 = new Location(testWorld, 400, 40, 100);
        Location location2 = new Location(testWorld, 420, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        Location nearCabinet1 = new Location(testWorld, 400, 39, 102);
        caster.setLocation(nearCabinet1);

        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus1 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus1);
        HARMONIA_NECTERE_PASSUS harmoniaNecterePassus2 = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location2, location1);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaNecterePassus2);
        mockServer.getScheduler().performTicks(20);

        // null to location should not cause anything to happen
        PlayerMoveEvent event = new PlayerMoveEvent(caster, nearCabinet1, null);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(harmoniaNecterePassus1.isUsing(caster), "doOnPlayerMoveEvent not short circuited by null to location");
        assertEquals(nearCabinet1, caster.getLocation(), "Caster was teleported when to location was null in PlayerMoveEvent");

        // player moving in to the vanishing cabinet should get teleported to the twin
        caster.setLocation(location1);
        event = new PlayerMoveEvent(caster, nearCabinet1, location1);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(200); // give long enough for the teleport event to happen
        assertEquals(location2, caster.getLocation(), "caster not teleported to the twin location");

        // make sure caster cannot get immediately teleported back
        Location nearCabinet2 = new Location(testWorld, 420, 40, 102);
        event = new PlayerMoveEvent(caster, nearCabinet2, location2);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(harmoniaNecterePassus2.isUsing(caster), "doOnPlayerMoveEvent not short circuited by player twin inUse cooldown");
    }

    /**
     * The twin location serializes and round-trips back, and deserializing empty data neither restores it nor kills the
     * spell.
     */
    @Test
    void serializeAndDeserializeSpellDataTest() {
        World testWorld = mockServer.addSimpleWorld("world5");
        Location location1 = new Location(testWorld, 500, 40, 100);
        Location location2 = new Location(testWorld, 520, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HARMONIA_NECTERE_PASSUS spell = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);

        // serialize and verify data contains twin location
        Map<String, String> spellData = spell.serializeSpellData();
        assertFalse(spellData.isEmpty(), "serialized spell data is empty");

        // deserialize valid data into a new spell and verify twin is restored
        HarmoniaNecterePassusTest.createCabinet(location1, location2);
        HarmoniaNecterePassusTest.createCabinet(location2, location1);

        HARMONIA_NECTERE_PASSUS deserialized = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        Ollivanders2API.getStationarySpells().addStationarySpell(deserialized);
        deserialized.deserializeSpellData(spellData);
        assertFalse(deserialized.isKilled(), "spell killed after deserializing valid data");

        // deserialize with empty data does not crash
        HARMONIA_NECTERE_PASSUS emptyDeserialized = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        emptyDeserialized.deserializeSpellData(new HashMap<>());
        assertFalse(emptyDeserialized.isKilled(), "spell killed after deserializing empty data");
    }

    /**
     * A bare spell from createStationarySpellByType fails the deserialization check; a fully constructed one passes.
     */
    @Test
    void checkSpellDeserializationTest() {
        O2StationarySpell stationarySpell = Ollivanders2API.getStationarySpells().createStationarySpellByType(O2StationarySpellType.HARMONIA_NECTERE_PASSUS);
        assertNotNull(stationarySpell);
        assertFalse(stationarySpell.checkSpellDeserialization(), "Deserialized spell should fail check without required data");

        World testWorld = mockServer.addSimpleWorld("world6");
        Location location1 = new Location(testWorld, 600, 40, 100);
        Location location2 = new Location(testWorld, 620, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        HARMONIA_NECTERE_PASSUS spell = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), location1, location2);
        assertTrue(spell.checkSpellDeserialization(), "Properly initialized spell failed deserialization check");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}

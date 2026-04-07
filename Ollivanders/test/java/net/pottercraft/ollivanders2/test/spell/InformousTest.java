package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.INFORMOUS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO;
import net.pottercraft.ollivanders2.test.stationaryspell.HarmoniaNecterePassusTest;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link INFORMOUS} spell.
 *
 * <p>Verifies that the spell reports information about nearby living entities, players, stationary spells at
 * the target location, and weather when none of the above are present.</p>
 */
public class InformousTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.INFORMOUS;
    }

    /**
     * Verify the spell reports health information when cast at a non-player living entity.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Cow cow = (Cow) testWorld.spawnEntity(targetLocation, EntityType.COW);
        cow.setHealth(8.0);

        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when entity found");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive any messages");
        assertTrue(message.contains("COW"), "message did not contain entity type name: " + message);
        assertTrue(message.contains("8.0 health"), "message did not contain health value: " + message);
    }

    /**
     * Verify the spell reports player-specific information (health, food, exhaustion, line of sight) when cast
     * at another player.
     */
    @Test
    void playerInfoTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Player");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();
        target.teleport(targetLocation);

        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when player found");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive any messages");
        assertTrue(message.contains(target.getName()), "message did not contain target player name: " + message);
        assertTrue(message.contains("health"), "message did not contain health info: " + message);
        assertTrue(message.contains("food level"), "message did not contain food level info: " + message);
        assertTrue(message.contains("exhaustion level"), "message did not contain exhaustion level info: " + message);
        assertTrue(message.contains("can see you"), "message did not contain positive line of sight info: " + message);
    }

    /**
     * Verify the spell reports "cannot see you" when the target player has hidden the caster.
     */
    @Test
    void playerCannotSeeTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "CannotSee");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();
        target.teleport(targetLocation);

        // hide the caster from the target so target.canSee(caster) returns false
        target.hidePlayer(testPlugin, caster);

        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when player found");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive any messages");
        assertTrue(message.contains("cannot see you"), "message did not contain negative line of sight info: " + message);
    }

    /**
     * Verify the spell reports weather information when cast at a target with no entities or stationary spells.
     * Tests both clear and stormy conditions, including the thunder branch.
     */
    @Test
    void weatherInfoTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Weather");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // place a block at the target so the projectile actually hits something
        targetLocation.getBlock().setType(Material.STONE);

        // clear weather
        testWorld.setStorm(false);
        testWorld.setThundering(false);
        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when target hit");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive weather message");
        assertTrue(message.contains("clear skies"), "message did not report clear skies: " + message);

        // storm with thunder
        testWorld.setStorm(true);
        testWorld.setThundering(true);
        informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when target hit");

        message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive weather message");
        assertTrue(message.contains("rain"), "message did not report rain: " + message);
        assertTrue(message.contains("thunder"), "message did not report thunder: " + message);
    }

    /**
     * Verify the spell reports info about a generic non-permanent stationary spell with duration remaining.
     */
    @Test
    void stationarySpellInfoMuffliatoTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Muffliato");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        targetLocation.getBlock().setType(Material.STONE);

        MUFFLIATO muffliato = new MUFFLIATO(testPlugin, caster.getUniqueId(), targetLocation, 5, 400);
        Ollivanders2API.getStationarySpells().addStationarySpell(muffliato);

        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when target hit");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive stationary spell message");
        assertTrue(message.contains("MUFFLIATO"), "message did not name the stationary spell: " + message);
        assertTrue(message.contains("radius 5"), "message did not contain stationary spell radius: " + message);
        assertTrue(message.contains("seconds left"), "message did not contain duration info: " + message);

        muffliato.kill();
    }

    /**
     * Verify the spell reports info about a permanent (non-special-cased) stationary spell without duration.
     */
    @Test
    void stationarySpellInfoColloportusTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Colloportus");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        targetLocation.getBlock().setType(Material.STONE);

        COLLOPORTUS colloportus = new COLLOPORTUS(testPlugin, caster.getUniqueId(), targetLocation);
        Ollivanders2API.getStationarySpells().addStationarySpell(colloportus);

        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when target hit");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive stationary spell message");
        assertTrue(message.contains("COLLOPORTUS"), "message did not name the stationary spell: " + message);
        assertTrue(message.contains("radius"), "message did not contain stationary spell radius: " + message);
        assertFalse(message.contains("seconds left"), "permanent spell message should not contain duration info: " + message);

        colloportus.kill();
    }

    /**
     * Verify the spell reports horcrux info including the player name who cast it.
     */
    @Test
    void stationarySpellInfoHorcruxTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Horcrux");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        targetLocation.getBlock().setType(Material.STONE);

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), targetLocation,
                testWorld.dropItem(targetLocation, new ItemStack(Material.APPLE, 1)));
        Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);

        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when target hit");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive horcrux message");
        assertTrue(message.contains("HORCRUX"), "message did not name the spell: " + message);
        assertTrue(message.contains(caster.getName()), "message did not contain horcrux caster name: " + message);
        assertTrue(message.contains("radius"), "message did not contain radius: " + message);

        horcrux.kill();
    }

    /**
     * Verify the spell reports the floo network registration name when cast at an Aliquam Floo location.
     */
    @Test
    void stationarySpellInfoAliquamFlooTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Floo");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        targetLocation.getBlock().setType(Material.STONE);

        String flooName = "informous floo test";
        ALIQUAM_FLOO floo = new ALIQUAM_FLOO(testPlugin, caster.getUniqueId(), targetLocation, flooName);
        Ollivanders2API.getStationarySpells().addStationarySpell(floo);

        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when target hit");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive floo message");
        assertTrue(message.contains("Floo registration"), "message did not identify floo: " + message);
        assertTrue(message.contains(flooName), "message did not contain floo name: " + message);

        floo.kill();
    }

    /**
     * Verify the spell identifies a Vanishing Cabinet when cast at a Harmonia Nectere Passus location.
     */
    @Test
    void stationarySpellInfoHarmoniaNecterePassusTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Harmonia");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        Location twinLocation = new Location(testWorld, location.getX() + 20, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // build proper cabinet structures or upkeep will kill the spells
        HarmoniaNecterePassusTest.createCabinet(targetLocation, twinLocation);
        HarmoniaNecterePassusTest.createCabinet(twinLocation, targetLocation);

        HARMONIA_NECTERE_PASSUS cabinet = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), targetLocation, twinLocation);
        HARMONIA_NECTERE_PASSUS twin = new HARMONIA_NECTERE_PASSUS(testPlugin, caster.getUniqueId(), twinLocation, targetLocation);
        Ollivanders2API.getStationarySpells().addStationarySpell(cabinet);
        Ollivanders2API.getStationarySpells().addStationarySpell(twin);

        INFORMOUS informous = (INFORMOUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(informous.isKilled(), "spell not killed when target hit");

        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive harmonia message");
        assertTrue(message.contains("Vanishing Cabinet"), "message did not identify vanishing cabinet: " + message);

        cabinet.kill();
        twin.kill();
    }

    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
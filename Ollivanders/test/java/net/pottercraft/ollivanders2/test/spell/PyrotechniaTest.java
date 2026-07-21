package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.Pyrotechnia;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link Pyrotechnia} spell implementations.
 *
 * @author Azami7
 * @see Pyrotechnia
 */
abstract public class PyrotechniaTest extends O2SpellTestSuper {
    /**
     * Verify the spell spawns the expected number of fireworks, each with the configured power, type, colors, and
     * effects. Everything is asserted in one method because the counts and spawned entities are order- and
     * timing-dependent and cannot run in parallel.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Pyrotechnia pyrotechnia = (Pyrotechnia) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(1);

        assertTrue(pyrotechnia.getNumberOfFireworks() >= 1, "number of fireworks < 1");
        assertTrue(pyrotechnia.getNumberOfFireworks() <= pyrotechnia.getMaxFireworks(), "number of fireworks > maxFireworks");

        Firework firework = null;
        for (Entity entity : pyrotechnia.getNearbyEntities(2)) { // at 1 tick, the spawned firework should be near the player
            if (entity.getType() == EntityType.FIREWORK_ROCKET)
                firework = (Firework) entity;
        }
        assertNotNull(firework, "Firework was not spawned");
        FireworkMeta meta = firework.getFireworkMeta();

        assertEquals(pyrotechnia.getFireworkPower(), meta.getPower(), "firework power not expected");
        assertEquals(pyrotechnia.getFireworkType(), meta.getEffects().getFirst().getType(), "firework type not expected");
        // the color getters must always return a non-null defensive copy, including for spells that set no fade colors
        assertNotNull(pyrotechnia.getFireworkColors(), "getFireworkColors() returned null");
        assertNotNull(pyrotechnia.getFadeColors(), "getFadeColors() returned null");

        for (Color color : pyrotechnia.getFireworkColors()) {
            assertTrue(meta.getEffects().getFirst().getColors().contains(color), "firework color missing");
        }
        if (pyrotechnia.hasFade()) {
            for (Color color : pyrotechnia.getFadeColors()) {
                assertTrue(meta.getEffects().getFirst().getFadeColors().contains(color), "firework fade color missing");
            }
        }

        mockServer.getScheduler().performTicks((long) 10 * pyrotechnia.getNumberOfFireworks());
        assertEquals(pyrotechnia.getFireworksCount(), testWorld.getEntitiesByClass(Firework.class).size(), "did not spawn expected number of fireworks");
        assertEquals(pyrotechnia.getNumberOfFireworks(), pyrotechnia.getFireworksCount(), "count of fireworks created != number that should be created");
    }

    /**
     * Overridden to do nothing. Pyrotechnia spells do not have revert actions since
     * fireworks are one-time effects that cannot be undone.
     */
    @Override
    @Test
    void revertTest() {
        // pyrotechnia has no revert actions
    }
}

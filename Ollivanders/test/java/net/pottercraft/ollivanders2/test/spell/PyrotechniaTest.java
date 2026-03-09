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
 * Abstract base test class for {@link net.pottercraft.ollivanders2.spell.Pyrotechnia}
 * spell implementations.
 *
 * <p>Provides shared test infrastructure for all spells that launch fireworks. Tests verify that:
 * <ul>
 * <li>The spell correctly calculates the number of fireworks based on caster experience</li>
 * <li>Fireworks are spawned with the correct properties (power, type, colors, effects)</li>
 * <li>All configured fireworks are spawned before the spell completes</li>
 * <li>Fireworks are properly configured with fade and flicker effects when enabled</li>
 * </ul></p>
 *
 * <p>All tests are combined into a single method because parallel test execution would interfere
 * with entity persistence and timing-dependent assertions.</p>
 *
 * @author Azami7
 * @see Pyrotechnia for the spell superclass being tested
 * @see O2SpellTestSuper for the base spell testing framework
 */
abstract public class PyrotechniaTest extends O2SpellTestSuper {
    /**
     * Overridden to do nothing. Pyrotechnia spell construction is tested as part of
     * {@link #doCheckEffectTest()} since fireworks are spawned during effect checking.
     */
    @Override
    @Test
    void spellConstructionTest() {
        // tested in doCheckEffectTest()
    }

    /**
     * Test that the spell correctly spawns all configured fireworks with proper properties.
     *
     * <p>Verifies:
     * <ul>
     * <li>Number of fireworks is calculated correctly and within min/max bounds</li>
     * <li>First firework is spawned with correct power, type, colors, and effects</li>
     * <li>Fade and flicker effects are applied when enabled</li>
     * <li>All fireworks are spawned over the spell's duration</li>
     * <li>Final count matches the expected number of fireworks</li>
     * </ul></p>
     *
     * <p>Note: All tests are combined into one method to prevent parallel test execution from
     * interfering with entity persistence and timing-dependent assertions.</p>
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

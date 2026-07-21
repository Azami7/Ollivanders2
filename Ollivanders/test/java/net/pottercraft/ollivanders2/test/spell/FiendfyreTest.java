package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.FIENDFYRE;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link FIENDFYRE} spell, a Dark Arts curse that summons a mix of ghasts, blazes, and magma cubes
 * where its projectile lands, and destroys any {@link HORCRUX} the projectile passes through.
 *
 * <p>The split between creature types is random, but the total number summoned is deterministic from the caster's
 * skill, so these tests assert against the total rather than the per-type counts.</p>
 *
 * @author Azami7
 */
public class FiendfyreTest extends O2SpellTestSuper {
    /**
     * Experience giving a usesModifier above 100, which both limits the creature count to the maximum and enables
     * all three spawn branches (ghasts require {@code > 100}, blazes {@code > 50}).
     */
    private static final int highExperience = 150;

    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FIENDFYRE;
    }

    /**
     * Count the fiendfyre creatures present in a world.
     *
     * @param world the world to scan
     * @return the number of ghast, blaze, and magma cube entities in the world
     */
    private long fireCreatureCount(@NotNull World world) {
        return world.getEntities().stream()
                .map(Entity::getType)
                .filter(type -> type == EntityType.GHAST || type == EntityType.BLAZE || type == EntityType.MAGMA_CUBE)
                .count();
    }

    /**
     * Verify the spell summons exactly its computed total number of creatures where it hits a block, then ends. The
     * count is read before resolving (spawning consumes it) and only the total is asserted, since the per-type split
     * is random.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // a solid block at the target stops the projectile so it manifests creatures here
        targetLocation.getBlock().setType(Material.STONE);

        FIENDFYRE fiendfyre = (FIENDFYRE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, highExperience);
        // read the count before the spell resolves - spawnCreatures consumes the field as it spawns
        int expectedCreatures = fiendfyre.getNumCreatures();
        assertTrue(expectedCreatures >= 1, "cast should summon at least one creature");

        mockServer.getScheduler().performTicks(20);

        assertTrue(fiendfyre.isKilled(), "spell did not end after summoning creatures");
        assertEquals(expectedCreatures, fireCreatureCount(testWorld), "unexpected number of creatures summoned");
    }

    /**
     * Verify the spell destroys a horcrux in its path and ends without summoning creatures. The horcrux check runs
     * every tick regardless of a block hit, so the spell never reaches the creature-summoning branch (which requires
     * hitting a block).
     */
    @Test
    void horcruxDestructionTest() {
        World testWorld = mockServer.addSimpleWorld("FiendfyreHorcrux");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // a floor under the horcrux so its dropped item does not fall out of the spell's path
        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);

        HORCRUX horcrux = new HORCRUX(testPlugin, caster.getUniqueId(), targetLocation, testWorld.dropItem(targetLocation, new ItemStack(Material.COMPASS, 1)));
        Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);

        FIENDFYRE fiendfyre = (FIENDFYRE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, highExperience);
        mockServer.getScheduler().performTicks(20);

        assertTrue(horcrux.isKilled(), "horcrux was not destroyed by the spell");
        assertTrue(fiendfyre.isKilled(), "spell did not end after destroying the horcrux");
        assertEquals(0, fireCreatureCount(testWorld), "creatures were summoned on the horcrux-destruction path");
    }

    /**
     * Verifies that the creature count scales with caster skill and is limited to [minCreatures, maxCreatures].
     *
     * <p>The count is read from the spell immediately after creation (before any creature spawns). Low experience
     * must floor to {@link FIENDFYRE#getMinCreatures()} and very high experience must limit to
     * {@link FIENDFYRE#getMaxCreatures()}.</p>
     */
    @Test
    void creatureCountTest() {
        World testWorld = mockServer.addSimpleWorld("FiendfyreCreatureCount");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // low experience floors at the minimum creature count
        FIENDFYRE low = (FIENDFYRE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        assertEquals(low.getMinCreatures(), low.getNumCreatures(), "low-skill cast should floor to the minimum creature count");

        // high experience limits to the maximum creature count
        FIENDFYRE high = (FIENDFYRE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1000);
        assertEquals(high.getMaxCreatures(), high.getNumCreatures(), "high-skill cast should limit to the maximum creature count");

        assertFalse(low.isKilled(), "spell should still be active before resolving");
    }

    /**
     * FIENDFYRE has no revert action - summoned creatures and destroyed horcruxes are not restored.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }
}
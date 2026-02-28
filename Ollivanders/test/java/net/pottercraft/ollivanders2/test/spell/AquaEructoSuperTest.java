package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.AQUA_ERUCTO;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for AQUA_ERUCTO spell variants.
 *
 * <p>Provides comprehensive test coverage for AQUA_ERUCTO and its subclasses (e.g., AQUA_ERUCTO_DUO).
 * Subclasses must implement abstract methods to define what constitutes a valid/invalid target and
 * how to verify the spell's effect on those targets.</p>
 *
 * <p>Test coverage includes:
 * <ul>
 * <li><strong>Target Detection:</strong> Verifies the spell finds appropriate targets as the projectile travels</li>
 * <li><strong>Effect Application:</strong> Confirms the spell applies its effect (extinguish or damage) to targets</li>
 * <li><strong>Water Block Effect:</strong> Tests water block placement at the target's eye location</li>
 * <li><strong>Caster Protection:</strong> Ensures the spell doesn't affect the caster</li>
 * <li><strong>No Target Handling:</strong> Validates spell behavior when no valid target exists</li>
 * <li><strong>Water Block Lifespan:</strong> Tests water block TTL and cleanup</li>
 * <li><strong>Reversion:</strong> Verifies temporary blocks are properly reverted</li>
 * </ul>
 */
abstract public class AquaEructoSuperTest extends O2SpellTestSuper {
    /**
     * Tests spell construction. No special construction code needed for AQUA_ERUCTO variants.
     */
    @Override
    @Test
    void spellConstructionTest() {
        // no special construction code
    }

    /**
     * Creates an entity that is NOT a valid target for the spell.
     *
     * @param location the location to spawn the entity at
     * @return an entity that the spell should not target
     */
    abstract Entity getInvalidEntity(Location location);

    /**
     * Creates an entity that IS a valid target for the spell.
     *
     * @param location the location to spawn the entity at
     * @return an entity that the spell should target
     */
    abstract Entity getValidEntity(Location location);

    /**
     * Verifies that the spell's effect was successfully applied to the target.
     *
     * <p>For AQUA_ERUCTO, this typically checks that fire ticks were set to 0.
     * For variants like AQUA_ERUCTO_DUO, this checks that the entity took damage.</p>
     *
     * @param affectedEntity the entity to verify
     */
    abstract void checkEffect(Entity affectedEntity);

    /**
     * Tests basic spell effect: finding and extinguishing a burning entity.
     *
     * <p>Verifies that:
     * <ul>
     * <li>The spell doesn't hit the target immediately (avoids targeting caster)</li>
     * <li>The spell stops when it finds a burning entity</li>
     * <li>The spell successfully extinguishes the target</li>
     * <li>The entity's fire ticks are set to 0</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("Aqua_Eructo");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        targetLocation.getBlock().getRelative(BlockFace.DOWN).setType(Material.STONE); // block for target entity to stand on
        Entity entity = getValidEntity(targetLocation);

        AQUA_ERUCTO aquaEructo = (AQUA_ERUCTO) castSpell(caster, location, entity.getLocation());
        mockServer.getScheduler().performTicks(2);
        assertFalse(aquaEructo.hasHitTarget(), "hit target too soon, potentially targeted the caster");

        mockServer.getScheduler().performTicks(20);
        assertTrue(aquaEructo.hasHitTarget(), "projectile not stopped when entity on fire found");
        assertTrue(aquaEructo.isExtinguished(), "target not extinguished");
        checkEffect(entity);
    }

    /**
     * Tests water block effect placement on any valid target.
     *
     * <p>Uses the helper method to avoid code duplication with other tests that also need
     * to verify water block placement.</p>
     */
    @Test
    void waterBlockEffectTest() {
        waterBlockEffectHelper();
    }

    /**
     * Helper method to set up a water block effect test.
     *
     * <p>Creates a valid target, casts the spell on it, and verifies the water block was placed
     * correctly. Returns the spell instance for further testing by the caller.</p>
     *
     * @return the cast spell instance with a water block effect active
     */
    AQUA_ERUCTO waterBlockEffectHelper() {
        World testWorld = mockServer.addSimpleWorld("Aqua_Eructo");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation.getBlock().getRelative(BlockFace.DOWN).getLocation(), 3); // blocks for target entity to stand on
        Entity entity = getValidEntity(targetLocation);

        AQUA_ERUCTO aquaEructo = (AQUA_ERUCTO) castSpell(caster, location, entity.getLocation());
        mockServer.getScheduler().performTicks(20);
        assertTrue(aquaEructo.hasHitTarget());
        assertTrue(aquaEructo.isExtinguished(), "target not extinguished");

        Block waterBlock;
        if (entity instanceof LivingEntity)
            waterBlock = ((LivingEntity) entity).getEyeLocation().getBlock();
        else
            waterBlock = targetLocation.getBlock();

        assertEquals(Material.WATER, waterBlock.getType(), "water block not set to water");
        assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(waterBlock), "water block not added to temporary block tracking");

        return aquaEructo;
    }

    /**
     * Tests spell behavior when the target is NOT on fire.
     *
     * <p>Verifies that:
     * <ul>
     * <li>The spell does not extinguish if no fire is present</li>
     * <li>No water block effect is created</li>
     * <li>The spell kills itself when it hits a target with no fire</li>
     * </ul>
     */
    @Test
    void noEffectTest() {
        World testWorld = mockServer.addSimpleWorld("Aqua_Eructo");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation.getBlock().getRelative(BlockFace.DOWN).getLocation(), 3); // blocks for target entity to stand on and to stop the projectile
        Entity entity = getInvalidEntity(targetLocation);

        AQUA_ERUCTO aquaEructo = (AQUA_ERUCTO) castSpell(caster, location, entity.getLocation());
        mockServer.getScheduler().performTicks(11);

        assertFalse(aquaEructo.isExtinguished(), "target extinguished when they were not on fire");
        assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(aquaEructo).isEmpty(), "water block effect turned on when target was not on fire");
        assertTrue(aquaEructo.hasHitTarget());
        assertTrue(aquaEructo.isKilled(), "spell not killed when failed to find a target");
    }

    /**
     * Tests that the spell does not extinguish the caster.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Even if the caster is on fire, the spell will not target them</li>
     * <li>The spell continues searching for other targets instead</li>
     * <li>The caster remains on fire</li>
     * </ul>
     */
    @Test
    void targetCasterTest() {
        World testWorld = mockServer.addSimpleWorld("Aqua_Eructo");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation.getBlock().getRelative(BlockFace.DOWN).getLocation(), 3); // blocks for spell to stop on
        caster.setFireTicks(100);

        AQUA_ERUCTO aquaEructo = (AQUA_ERUCTO) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(aquaEructo.hasHitTarget());
        assertTrue(caster.getFireTicks() > 0, "caster no longer on fire");
        assertFalse(aquaEructo.isExtinguished(), "spell targeted caster");
    }

    /**
     * Tests water block time-to-live (TTL) and spell cleanup.
     *
     * <p>Verifies that:
     * <ul>
     * <li>The spell remains alive while the water block is active</li>
     * <li>The spell kills itself after the water block TTL expires</li>
     * <li>The TTL duration matches the configured waterBlockTTL constant</li>
     * </ul>
     */
    @Test
    void waterBlockTTLTest() {
        AQUA_ERUCTO aquaEructo = waterBlockEffectHelper();
        assertTrue(aquaEructo.isExtinguished());

        mockServer.getScheduler().performTicks(AQUA_ERUCTO.waterBlockTTL - 100);
        assertFalse(aquaEructo.isKilled(), "spell killed before water block ttl passed");

        mockServer.getScheduler().performTicks(100);
        assertTrue(aquaEructo.isKilled(), "spell not killed after water block ttl passed");
    }

    /**
     * Tests water block reversion when the spell ends.
     *
     * <p>Verifies that:
     * <ul>
     * <li>When the spell is killed, the water block is reverted to its original material</li>
     * <li>The water block is removed from temporary block tracking</li>
     * </ul>
     */
    @Override
    @Test
    void revertTest() {
        AQUA_ERUCTO aquaEructo = waterBlockEffectHelper();
        Block affectedBlock = Ollivanders2API.getBlocks().getBlocksChangedBySpell(aquaEructo).getFirst();
        assertNotNull(affectedBlock);

        aquaEructo.kill();
        mockServer.getScheduler().performTicks(1);
        assertNotEquals(Material.WATER, affectedBlock.getType(), "changed block not reverted");
        assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(aquaEructo).isEmpty(), "changed block not removed from tracking");
    }
}

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
 * Base test class for {@link AQUA_ERUCTO} and its variants. Subclasses define what counts as a valid/invalid target
 * and how to verify the spell's effect on it.
 */
abstract public class AquaEructoBaseTest extends O2SpellTestSuper {
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
     * Assert the spell's effect was applied to the target (fire extinguished, damage dealt, etc.).
     *
     * @param affectedEntity the entity to verify
     */
    abstract void checkEffect(Entity affectedEntity);

    /**
     * Verify the projectile passes the caster, stops at the first burning entity, and extinguishes it.
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
        assertFalse(aquaEructo.hasHitBlock(), "hit target too soon, potentially targeted the caster");

        mockServer.getScheduler().performTicks(20);
        assertTrue(aquaEructo.hasHitBlock(), "projectile not stopped when entity on fire found");
        assertTrue(aquaEructo.isExtinguished(), "target not extinguished");
        checkEffect(entity);
    }

    /**
     * Verify a water block is placed on a valid target.
     */
    @Test
    void waterBlockEffectTest() {
        waterBlockEffectHelper();
    }

    /**
     * Cast the spell on a valid target, assert the water block was placed, and return the spell for further checks.
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
        assertTrue(aquaEructo.hasHitBlock());
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
     * Verify the spell does nothing and kills itself when it hits a target that is not a valid target.
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
        assertTrue(aquaEructo.hasHitBlock());
        assertTrue(aquaEructo.isKilled(), "spell not killed when failed to find a target");
    }

    /**
     * Verify the spell never targets the caster, even when the caster is on fire.
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

        assertTrue(aquaEructo.hasHitBlock());
        assertTrue(caster.getFireTicks() > 0, "caster no longer on fire");
        assertFalse(aquaEructo.isExtinguished(), "spell targeted caster");
    }

    /**
     * Verify the spell stays alive while the water block is active and kills itself once its TTL expires.
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
     * Verify the water block is reverted and untracked when the spell is killed.
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

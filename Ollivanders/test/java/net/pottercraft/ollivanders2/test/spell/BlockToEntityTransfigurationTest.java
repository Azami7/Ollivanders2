package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.BlockToEntityTransfiguration;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link BlockToEntityTransfiguration} spells, covering block-to-entity targeting, invalid target
 * rejection, reversion, permanence, and termination when the spawned entity dies. Subclasses supply the valid target
 * material and may override {@link #customizeEntityTest()}.
 *
 * @author Azami7
 * @see BlockToEntityTransfiguration
 * @see O2SpellTestSuper
 */
abstract public class BlockToEntityTransfigurationTest extends O2SpellTestSuper {
    /**
     * @return a block material this spell can transfigure
     */
    @NotNull
    abstract Material getValidTargetType();

    /**
     * Verify the spell replaces the target block with air and spawns the mapped entity type, tracked as transfigured.
     */
    @Override @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        TestCommon.createBlockBase(target.getRelative(BlockFace.DOWN).getLocation(), 3);

        BlockToEntityTransfiguration blockToEntityTransfiguration = (BlockToEntityTransfiguration) castSpell(caster, location, targetLocation);

        Map<Material, EntityType> transMap = blockToEntityTransfiguration.getEntityTransfigurationMap();
        assertFalse(transMap.isEmpty(), "transfiguration map is empty");

        assertFalse(blockToEntityTransfiguration.isTransfigured(caster), "isEntityTransfigured(caster) returned true");

        Material material = getValidTargetType();
        target.setType(material);
        EntityType expectedType = transMap.get(material);
        assertNotNull(expectedType);

        mockServer.getScheduler().performTicks(20);
        assertTrue(blockToEntityTransfiguration.hasHitBlock(), "spell did not hit the target");
        assertEquals(Material.AIR, target.getType(), "target block not changed to air");

        LivingEntity entity = EntityCommon.getLivingEntityAtLocation(target.getLocation());
        assertNotNull(entity, "entity was not spawned");
        assertEquals(expectedType, entity.getType(), "entity was not expected type");
        assertTrue(blockToEntityTransfiguration.isTransfigured(entity), "isEntityTransfigured(entity) returned false");

        // and just to complete isEntityTransfigured()
        assertFalse(blockToEntityTransfiguration.isTransfigured(caster));
    }

    /**
     * Verify the spell leaves an unbreakable target block unchanged.
     */
    @Test
    void invalidTargetTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Material originalMaterial = Ollivanders2Common.getUnbreakableMaterials().getFirst();
        assertNotNull(originalMaterial);
        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(originalMaterial);
        BlockToEntityTransfiguration blockToEntityTransfiguration = (BlockToEntityTransfiguration) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(blockToEntityTransfiguration.hasHitBlock());
        assertFalse(blockToEntityTransfiguration.isTransfigured(target), "block was transfigured when it is not a transfigurable block");
        assertEquals(originalMaterial, target.getType(), "block type changed when it is not a transfigurable block");
    }

    /**
     * Hook for subclasses to verify spell-specific customization of the spawned entity. The default is a no-op.
     */
    @Test
    void customizeEntityTest() {
    }

    /**
     * Verify a temporary transfiguration removes its entity when the duration expires (a permanent one keeps it), and
     * that the spell ends immediately if the entity dies first.
     */
    @Override
    @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(getValidTargetType());

        BlockToEntityTransfiguration blockToEntityTransfiguration = (BlockToEntityTransfiguration) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(blockToEntityTransfiguration.hasHitBlock());
        LivingEntity entity = EntityCommon.getLivingEntityAtLocation(target.getLocation());
        assertNotNull(entity);

        mockServer.getScheduler().performTicks(blockToEntityTransfiguration.getEffectDuration());
        if (blockToEntityTransfiguration.isPermanent()) {
            assertFalse(entity.isDead(), "entity despawned when spell is permanent");
        }
        else {
            assertTrue(entity.isDead(), "entity not removed when spell ended and is not permanent");
        }

        if (!blockToEntityTransfiguration.isPermanent()) {
            testWorld.getBlockAt(targetLocation).setType(getValidTargetType());
            blockToEntityTransfiguration = (BlockToEntityTransfiguration) castSpell(caster, location, targetLocation);
            mockServer.getScheduler().performTicks(20);
            entity = EntityCommon.getLivingEntityAtLocation(target.getLocation());
            assertNotNull(entity);

            DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                    .withDamageLocation(caster.getLocation())
                    .build();
            EntityDeathEvent event = new EntityDeathEvent(entity, damageSource, new ArrayList<>());
            mockServer.getPluginManager().callEvent(event);
            mockServer.getScheduler().performTicks(5);
            assertTrue(blockToEntityTransfiguration.isKilled(), "spell not killed when entity died before duration expired");
        }
    }
}

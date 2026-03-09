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
 * Abstract base test class for {@link net.pottercraft.ollivanders2.spell.BlockToEntityTransfiguration}
 * spell implementations.
 *
 * <p>Provides shared test infrastructure for all spells that transfigure blocks into entities.
 * Tests verify that:
 * <ul>
 * <li>The spell correctly targets and transfigures blocks into the expected entity types</li>
 * <li>Non-transfigurable blocks are rejected (e.g., unbreakable blocks)</li>
 * <li>Spawned entities are created at the correct location (the target block's position)</li>
 * <li>Entity tracking and spell lifecycle work correctly</li>
 * <li>Temporary transfigurations are reverted after duration expires</li>
 * <li>Entity death during transfiguration triggers spell termination</li>
 * <li>Permanent transfigurations persist indefinitely</li>
 * </ul></p>
 *
 * <p>Subclasses must implement {@link #getValidTargetType()} to specify which block material
 * can be transfigured by the spell under test. Subclasses may override {@link #customizeEntityTest()}
 * to test spell-specific entity customization behavior.</p>
 *
 * @author Azami7
 * @see BlockToEntityTransfiguration for the spell superclass being tested
 * @see O2SpellTestSuper for the base spell testing framework
 */
abstract public class BlockToEntityTransfigurationTest extends O2SpellTestSuper {
    /**
     * Overridden to do nothing. Block-to-entity transfiguration spells have no construction
     * requirements beyond those covered by inherited base class tests.
     */
    @Override @Test
    void spellConstructionTest() {
    }

    /**
     * Get a valid target material that this spell can transfigure.
     *
     * @return a block material that the spell can transfigure
     */
    @NotNull
    abstract Material getValidTargetType();

    /**
     * Test that the spell correctly targets and transfigures blocks into entities.
     *
     * <p>Verifies:
     * <ul>
     * <li>The transfiguration map is populated</li>
     * <li>The caster is not marked as transfigured</li>
     * <li>The target block is converted to the expected entity type</li>
     * <li>The target block becomes air after transfiguration</li>
     * <li>The spawned entity is tracked as transfigured</li>
     * </ul></p>
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

        assertFalse(blockToEntityTransfiguration.isEntityTransfigured(caster), "isEntityTransfigured(caster) returned true");

        Material material = getValidTargetType();
        target.setType(material);
        EntityType expectedType = transMap.get(material);
        assertNotNull(expectedType);

        mockServer.getScheduler().performTicks(20);
        assertTrue(blockToEntityTransfiguration.hasHitTarget(), "spell did not hit the target");
        assertEquals(Material.AIR, target.getType(), "target block not changed to air");

        LivingEntity entity = EntityCommon.getLivingEntityAtLocation(target.getLocation());
        assertNotNull(entity, "entity was not spawned");
        assertEquals(expectedType, entity.getType(), "entity was not expected type");
        assertTrue(blockToEntityTransfiguration.isEntityTransfigured(entity), "isEntityTransfigured(entity) returned false");

        // and just to complete isEntityTransfigured()
        assertFalse(blockToEntityTransfiguration.isEntityTransfigured(caster));
    }

    /**
     * Test that non-transfigurable blocks are rejected.
     *
     * <p>Verifies that the spell correctly rejects unbreakable and other invalid target blocks
     * without transfiguring them.</p>
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

        assertTrue(blockToEntityTransfiguration.hasHitTarget());
        assertFalse(blockToEntityTransfiguration.isBlockTransfigured(target), "block was transfigured when it is not a transfigurable block");
        assertEquals(originalMaterial, target.getType(), "block type changed when it is not a transfigurable block");
    }

    /**
     * Test spell-specific entity customization.
     *
     * <p>No-op by default. Subclasses may override this method to verify that spawned entities
     * are customized according to spell-specific requirements (e.g., equipment, attributes, names).</p>
     */
    @Test
    void customizeEntityTest() {
    }

    /**
     * Test that temporary transfigurations are reverted and permanent ones persist.
     *
     * <p>Verifies:
     * <ul>
     * <li>Entity persists when the transfiguration is permanent</li>
     * <li>Entity is removed when the transfiguration is temporary and duration expires</li>
     * <li>Spell terminates immediately if the entity dies before the effect duration expires</li>
     * </ul></p>
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

        assertTrue(blockToEntityTransfiguration.hasHitTarget());
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
                    .withDamageLocation(caster.getLocation())  // location of the fire block
                    .build();
            EntityDeathEvent event = new EntityDeathEvent(entity, damageSource, new ArrayList<>());
            mockServer.getPluginManager().callEvent(event);
            mockServer.getScheduler().performTicks(5);
            assertTrue(blockToEntityTransfiguration.isKilled(), "spell not killed when entity died before duration expired");
        }
    }
}

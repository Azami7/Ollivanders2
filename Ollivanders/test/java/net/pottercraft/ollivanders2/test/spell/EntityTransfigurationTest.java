package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.EntityTransfiguration;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract test suite for entity transfiguration spells.
 *
 * <p>Tests the common transfiguration behavior: entity targeting, type validation, success rate,
 * duration bounds, reversion, permanent vs temporary behavior, and caster messaging. Subclasses
 * provide the valid/invalid/same entity types and implement spell-specific transfiguration tests.</p>
 *
 * @see EntityTransfiguration
 * @see O2SpellTestSuper
 */
abstract public class EntityTransfigurationTest extends O2SpellTestSuper {
    /**
     * Get an entity type that can be transfigured by this spell.
     *
     * @return a targetable entity
     */
    @NotNull
    abstract EntityType getValidEntityType();

    /**
     * Get an entity that cannot be transformed by this spell. If the spell has a blocked list, this entity should be
     * from that list. This entity should not meet the criteria for getSameEntityType().
     *
     * @return a non-targetable entity, or null if all entities can be targeted
     */
    @Nullable
    abstract EntityType getInvalidEntityType();

    /**
     * Get an entity that can be targeted by this spell but is already the type the spell transforms in to.
     *
     * @return an entity already the transformation type, or null if this cannot happen in this spell
     */
    @Nullable
    abstract EntityType getSameEntityType();

    /**
     * For entities that are items, a valid item material that can be targeted by this spell.
     *
     * @return a targetable item material
     */
    @NotNull
    Material getValidMaterialType() {
        return Material.WOODEN_AXE;
    }

    /**
     * For entities that are items, a invalid item material that cannot be targeted by this spell.
     *
     * @return a non-targetable item material, or null if all materials can be targeted
     */
    @Nullable
    Material getInvalidMaterialType() {
        return null;
    }

    /**
     * Child test classes should override if the test can target item entities.
     */
    boolean transfiguresItems() {
        return false;
    }

    /**
     * Not applicable for entity transfiguration spells.
     */
    @Override
    @Test
    void spellConstructionTest() {
    }

    /**
     * Spawn a specified entity type at a specified location.
     *
     * @param location the spawn location
     * @param entityType the entity type
     * @param itemType if an item, the material type
     * @return the spawned entity
     */
    Entity spawnEntityAtLocation(@NotNull Location location, @NotNull EntityType entityType, @Nullable Material itemType) {
        if (entityType == EntityType.ITEM) {
            if (itemType != null)
                return location.getWorld().dropItem(location, new ItemStack(itemType, 1));
            else
                return location.getWorld().dropItem(location, new ItemStack(getValidMaterialType(), 1));
        }
        else
            return location.getWorld().spawnEntity(location, entityType);
    }

    /**
     * Test that the spell projectile hits a valid target entity and transfigures it.
     */
    @Test
    void doCheckEffectTest() {
        // test for valid target case, other cases in other tests
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        assertInstanceOf(EntityTransfiguration.class, spell);
        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) spell;

        mockServer.getScheduler().performTicks(20);

        assertTrue(entityTransfiguration.hasHitTarget(), "hitTarget not set when spell hit valid target");
        assertTrue(entityTransfiguration.isTransfigured(), "target was not transfigured");
    }

    /**
     * Each specific spell needs to implement since they all do different things.
     */
    @Test
    abstract void transfigureTest();

    /**
     * Tests success rate mechanics based on player spell skill level.
     *
     * <p>Verifies that spells fail with low skill (when success rate < 100%) and succeed
     * at spell mastery level (skill = 100).</p>
     */
    @Test
    void effectSuccessRateTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());

        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);

        if (entityTransfiguration.getSuccessRate() < 100) {
            mockServer.getScheduler().performTicks(20);
            assertFalse(entityTransfiguration.isTransfigured(), "transfiguration succeeded when skill is 0 and success rate < 100%");

            entityTransfiguration.kill();
            entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
            mockServer.getScheduler().performTicks(20);
            assertTrue(entityTransfiguration.isTransfigured(), "transfiguration failed when skill is mastery and success rate < 100%");
        }
    }

    /**
     * Test canTransfigure for success rate, invalid entity types, same entity type, and
     * already-transfigured entities.
     */
    @Test
    void canTransfigureTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        Entity entity = spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());

        // 0 experience fails success check
        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertFalse(entityTransfiguration.canTransfigure(entity), "canTransfigure() returned true for 0 experience");
        entityTransfiguration.kill();

        // mastery level experience passes success check
        entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        assertTrue(entityTransfiguration.canTransfigure(entity), "canTransfigure() returned false for double mastery level experience");

        // invalid entity cannot be targeted
        if (getInvalidEntityType() != null) {
            Entity invalidEntity = spawnEntityAtLocation(targetLocation, getInvalidEntityType(), getInvalidMaterialType());
            assertFalse(entityTransfiguration.canTransfigure(invalidEntity), "canTransfigure() returned true for invalid entity type");
            invalidEntity.remove();
        }

        // same entity type cannot be targeted
        if (getSameEntityType() != null) {
            Entity sameEntity = spawnEntityAtLocation(targetLocation, getSameEntityType(), getValidMaterialType());
            assertFalse(entityTransfiguration.canTransfigure(sameEntity), "canTransfigure() returned true for same entity type");
            sameEntity.remove();
        }
    }

    /**
     * Test canTransfigure for already transfigured entities. Split out from main canTransfigure test because some child
     * classes will need to override this test.
     */
    @Test
    void canTransfigureAlreadyTransfiguredTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        Entity entity = spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());

        // already transfigured entity cannot be targeted
        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(entityTransfiguration.hasHitTarget(), "spell did not hit target");
        assertTrue(entityTransfiguration.isTransfigured(), "target was not transfigured");

        EntityTransfiguration entityTransfiguration2 = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        assertFalse(entityTransfiguration2.canTransfigure(entity), "canTransfigure() returned true when target already transfigured");
        mockServer.getScheduler().performTicks(20);
        assertFalse(entityTransfiguration2.isTransfigured(), "target was transfigured when invalid");
        assertTrue(entityTransfiguration2.isKilled(), "spell not killed when hitting invalid target");
    }

    /**
     * Test that enchanted items are handled correctly based on the spell's {@code transfigureEnchantedItems} setting.
     */
    @Test
    void enchantedItemCanTransfigureTest() {
        if (transfiguresItems()) {
            World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
            Location location = getNextLocation(testWorld);
            Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
            PlayerMock caster = mockServer.addPlayer();

            TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
            ItemStack itemStack = O2ItemType.BROOMSTICK.getItem(1);
            assertNotNull(itemStack);
            testWorld.dropItem(targetLocation, itemStack);

            EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
            mockServer.getScheduler().performTicks(20);

            if (entityTransfiguration.doesTransfigureEnchantedItems()) {
                assertTrue(entityTransfiguration.isTransfigured(), "Enchanted item was not transfigured");
            }
            else {
                assertFalse(entityTransfiguration.isTransfigured(), "Enchanted item was transfigured");
                assertTrue(entityTransfiguration.isKilled(), "spell not killed when invalid entity targeted");
            }
        }
    }

    /**
     * Tests spell duration and automatic termination for temporary transfigurations.
     *
     * <p>Verifies that non-permanent spells run for their specified duration and are
     * killed when the duration expires.</p>
     */
    @Test
    void ageAndKillTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());

        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        if (!entityTransfiguration.isPermanent()) {
            mockServer.getScheduler().performTicks(20);
            assertTrue(entityTransfiguration.hasHitTarget());
            assertTrue(entityTransfiguration.isTransfigured(), "entity was not transfigured");
            assertFalse(entityTransfiguration.isKilled(), "transfiguration killed unexpectedly");

            int ticksRemaining = entityTransfiguration.getEffectDuration() - entityTransfiguration.getAge();
            mockServer.getScheduler().performTicks(ticksRemaining - 1);
            assertFalse(entityTransfiguration.isKilled(), "spell killed before duration expired");

            mockServer.getScheduler().performTicks(2);
            assertTrue(entityTransfiguration.isKilled(), "spell not killed when duration passed");
        }
    }

    /**
     * Tests permanent transfiguration spells that are not reverted after duration expires.
     *
     * <p>Verifies that permanent spells are immediately killed after transfiguration
     * and transfigured blocks remain in their changed state.</p>
     */
    @Test
    void isPermanentTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());

        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        if (entityTransfiguration.isPermanent()) {
            mockServer.getScheduler().performTicks(20);

            assertTrue(entityTransfiguration.isKilled());
            assertTrue(entityTransfiguration.isTransfigured());
            assertTrue(entityTransfiguration.getOriginalEntity().isDead(), "original entity was not removed");
            assertFalse(entityTransfiguration.getTransfiguredEntity().isDead(), "transfigured entity was removed");
        }
    }

    /**
     * Tests that spell duration is within configured min/max bounds for temporary spells.
     *
     * <p>Verifies that the duration of temporary transfigurations is properly calculated
     * and falls within the valid range.</p>
     */
    @Test
    void effectDurationTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());

        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);

        if (!entityTransfiguration.isPermanent()) {
            int duration = entityTransfiguration.getEffectDuration();

            assertTrue(duration >= entityTransfiguration.getMinDuration(), "duration not >= minDuration");
            assertTrue(duration <= entityTransfiguration.getMaxDuration(), "duration not <= maxDuration");
        }
    }

    /**
     * Tests that appropriate success and failure messages are sent to the caster.
     *
     * <p>Verifies that the caster receives the success message when transfiguration succeeds
     * and the failure message when transfiguration fails.</p>
     */
    @Test
    void successAndFailureMessageTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop

        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        if (getInvalidEntityType() != null) {
            spawnEntityAtLocation(targetLocation, getInvalidEntityType(), getInvalidMaterialType());
            mockServer.getScheduler().performTicks(20);

            assertTrue(entityTransfiguration.isKilled());
            assertFalse(entityTransfiguration.isTransfigured()); // should fail because the block is already transfigured
            String message = caster.nextMessage();
            assertNotNull(message, "caster did not receive failure message");
            assertEquals(entityTransfiguration.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected failure message");
            TestCommon.clearMessageQueue(caster);
        }

        spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());
        entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);

        assertTrue(entityTransfiguration.isTransfigured());
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive success message");
        assertEquals(entityTransfiguration.getSuccessMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected success message");
    }

    /**
     * Test that killing the spell reverts the transfiguration and restores an entity at the target location.
     */
    @Override
    @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        spawnEntityAtLocation(targetLocation, getValidEntityType(), getValidMaterialType());

        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        if (!entityTransfiguration.isPermanent()) {
            mockServer.getScheduler().performTicks(20);
            assertTrue(entityTransfiguration.isTransfigured());
            entityTransfiguration.kill();

            assertTrue(entityTransfiguration.getTransfiguredEntity().isDead(), "transfigured entity not removed");

            if (!entityTransfiguration.isConsumeOriginal()) {
                Collection<Entity> entities = EntityCommon.getEntitiesInRadius(targetLocation, 1);
                assertFalse(entities.isEmpty(), "entity not found when spell exited");
            }
        }
    }

    @Test
    void inventoryHolderRevertTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        spawnEntityAtLocation(targetLocation, EntityType.BAMBOO_CHEST_RAFT, null);

        EntityTransfiguration entityTransfiguration = (EntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        if (!entityTransfiguration.isPermanent()) {
            mockServer.getScheduler().performTicks(20);
            assertTrue(entityTransfiguration.isTransfigured());
            entityTransfiguration.kill();

            Collection<Entity> entities = EntityCommon.getEntitiesInRadius(targetLocation, 1);
            assertFalse(entities.isEmpty(), "entity not found when spell exited");
        }
    }
}

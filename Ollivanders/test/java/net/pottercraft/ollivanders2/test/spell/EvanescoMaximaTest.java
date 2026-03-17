package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.EVANESCO;
import net.pottercraft.ollivanders2.spell.EVANESCO_MAXIMA;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the EVANESCO_MAXIMA spell (permanent vanishing of non-living entities).
 *
 * @see EVANESCO_MAXIMA
 * @see EntityTransfigurationTest
 */
public class EvanescoMaximaTest extends EntityTransfigurationTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.EVANESCO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.EVANESCO_MAXIMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    EntityType getValidEntityType() {
        return EntityType.MINECART;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    EntityType getInvalidEntityType() {
        return EntityType.TURTLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    EntityType getSameEntityType() {
        return null;
    }

    /**
     * Test that EVANESCO_MAXIMA permanently removes the entity with no entities remaining at the target.
     */
    @Override
    @Test
    void isPermanentTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        testWorld.spawnEntity(targetLocation, getValidEntityType());

        EVANESCO_MAXIMA evanescoMaxima = (EVANESCO_MAXIMA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        if (evanescoMaxima.isPermanent()) {
            mockServer.getScheduler().performTicks(20);

            assertTrue(evanescoMaxima.isKilled());
            assertTrue(evanescoMaxima.isTransfigured());
            assertTrue(EntityCommon.getEntitiesInRadius(targetLocation, 2).isEmpty(), "target location still has an entity present");
        }
    }

    /**
     * We have to override because the generic way is to cast the same spell twice but since evanesco permanently removes items, we
     * need to cast a different spell.
     */
    @Override
    @Test
    void canTransfigureAlreadyTransfiguredTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        Entity entity = testWorld.spawnEntity(targetLocation, getValidEntityType());

        // already transfigured entity cannot be targeted
        EVANESCO evanesco = (EVANESCO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2, O2SpellType.EVANESCO);
        mockServer.getScheduler().performTicks(20);
        assertTrue(evanesco.hasHitTarget(), "spell did not hit target");
        assertTrue(evanesco.isTransfigured(), "target was not transfigured");

        EVANESCO_MAXIMA evanescoMaxima = (EVANESCO_MAXIMA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        assertFalse(evanescoMaxima.canTransfigure(entity), "canTransfigure() returned true when target already transfigured");
        mockServer.getScheduler().performTicks(20);
        assertFalse(evanescoMaxima.isTransfigured(), "target was transfigured when invalid");
        assertTrue(evanescoMaxima.isKilled(), "spell not killed when hitting invalid target");
    }

    /**
     * Test that EVANESCO_MAXIMA removes the target entity from the world.
     */
    @Override
    @Test
    void transfigureTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        Entity target = testWorld.spawnEntity(targetLocation, getValidEntityType());

        EVANESCO_MAXIMA evanescoMaxima = (EVANESCO_MAXIMA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(evanescoMaxima.hasHitTarget());
        assertTrue(evanescoMaxima.isTransfigured());

        // target entity was removed
        assertTrue(target.isDead(), "Target entity was not removed");
    }

    /**
     * Test that the permanent vanishing is not reverted - no entities should be present after the spell ends.
     */
    @Override
    @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        Entity entity = testWorld.spawnEntity(targetLocation, getValidEntityType());

        EVANESCO_MAXIMA evanescoMaxima = (EVANESCO_MAXIMA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(evanescoMaxima.isTransfigured());
        assertTrue(evanescoMaxima.isKilled());

        assertTrue(EntityCommon.getEntitiesInRadius(targetLocation, 2).isEmpty(), "target location still has an entity present");
    }
}

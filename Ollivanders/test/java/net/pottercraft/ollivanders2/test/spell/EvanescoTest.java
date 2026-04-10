package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.EVANESCO;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the EVANESCO spell (temporary vanishing of non-living entities).
 *
 * @see EVANESCO
 * @see EntityTransfigurationTest
 */
public class EvanescoTest extends EntityTransfigurationTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.EVANESCO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.EVANESCO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    EntityType getValidEntityType() {
        return EntityCommon.getBoats().getFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    EntityType getInvalidEntityType() {
        return EntityType.RABBIT;
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
     * Test that EVANESCO removes the target entity from the world.
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

        EVANESCO evanesco = (EVANESCO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(evanesco.hasHitBlock());
        assertTrue(evanesco.isTransfigured());

        // target entity was removed
        assertTrue(target.isDead(), "Target entity was not removed");
    }
}

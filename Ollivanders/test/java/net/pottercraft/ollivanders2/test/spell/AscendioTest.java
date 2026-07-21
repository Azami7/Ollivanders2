package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.ASCENDIO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.ASCENDIO}. Extends {@link KnockbackTest} for the shared
 * knockback tests.
 *
 * @author Azami7
 */
public class AscendioTest extends KnockbackTest {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ASCENDIO;
    }

    /**
     * Ascendio targets self so this is irrelevant
     *
     * @return null
     */
    @Override @Nullable
    EntityType getInvalidEntityType() {
        return null;
    }

    /**
     * Ascendio targets self so this is irrelevant
     *
     * @return a valid entity
     */
    @Override @NotNull
    EntityType getValidEntityType() {
        return EntityType.PLAYER;
    }

    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        ASCENDIO ascendio = (ASCENDIO) castSpell(caster, location, targetLocation);
        assertTrue(ascendio.isVertical(), "not set to is vertical");
        assertTrue(ascendio.isTargetsSelf(), "not set to targets self");
        assertEquals(ASCENDIO.minDistanceConfig, ascendio.getMinDistance(), "unexpected minDistance");
        assertEquals(ASCENDIO.maxDistanceConfig, ascendio.getMaxDistance(), "unexpected maxDistance");
    }

    /**
     * Ascendio has no revert actions
     */
    @Override @Test
    void revertTest() {

    }

    /**
     * Verify casting ASCENDIO gives the caster a 30-second SLOW_FALLING effect.
     */
    @Test
    void addOtherEffectsSlowFallingTest() {
        World testWorld = mockServer.addSimpleWorld("AscendioSlowFalling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(2);

        assertTrue(caster.hasPotionEffect(PotionEffectType.SLOW_FALLING), "caster does not have SLOW_FALLING effect");

        PotionEffect effect = caster.getPotionEffect(PotionEffectType.SLOW_FALLING);
        assertNotNull(effect);
        assertEquals((Ollivanders2Common.ticksPerSecond * 30) - 1, effect.getDuration(), "SLOW_FALLING duration is incorrect");
    }

    /**
     * Verify ASCENDIO does not overwrite a SLOW_FALLING effect the caster already has.
     */
    @Test
    void addOtherEffectsAlreadyHasSlowFallingTest() {
        World testWorld = mockServer.addSimpleWorld("AscendioAlreadySlowFalling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        int existingDuration = 10;
        caster.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, existingDuration, 0));

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(2);

        PotionEffect effect = caster.getPotionEffect(PotionEffectType.SLOW_FALLING);
        assertNotNull(effect);
        assertTrue(effect.getDuration() < Ollivanders2Common.ticksPerSecond * 30, "existing SLOW_FALLING effect was overridden by spell");
    }
}

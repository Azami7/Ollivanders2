package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.CARPE_RETRACTUM;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for CARPE_RETRACTUM spell functionality.
 *
 * <p>Tests the seize and pull charm including:</p>
 * <ul>
 * <li>Spell configuration (pull, min/max distance, strength reducer)</li>
 * <li>Entity targeting (non-living entities only)</li>
 * <li>Pull velocity application</li>
 * </ul>
 *
 * @author Azami7
 */
public class CarpeRetractumTest extends KnockbackTest {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CARPE_RETRACTUM;
    }

    /**
     * Carpe retractcum only works on non-living entities
     *
     * @return a living entity
     */
    @Override @Nullable
    EntityType getInvalidEntityType() {
        return EntityType.RABBIT;
    }

    /**
     * Get a valid entity type - can be any non-living
     *
     * @return a valid entity
     */
    @Override @NotNull
    EntityType getValidEntityType() {
        return EntityType.MINECART;
    }

    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        CARPE_RETRACTUM carpeRetractum = (CARPE_RETRACTUM) castSpell(caster, location, targetLocation);

        assertTrue(carpeRetractum.isPull(), "not set to pull");
        assertEquals(CARPE_RETRACTUM.minDistanceConfig, carpeRetractum.getMinDistance(), "unexpected minDistance");
        assertEquals(CARPE_RETRACTUM.maxDistanceConfig, carpeRetractum.getMaxDistance(), "unexpected maxDistance");
        assertEquals(CARPE_RETRACTUM.strengthReducerConfig, carpeRetractum.getStrengthReducer(), "unexpected strengthReducer");
    }
}

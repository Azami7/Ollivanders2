package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.FLIPENDO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for FLIPENDO spell functionality.
 *
 * <p>Tests the knockback jinx including:</p>
 * <ul>
 * <li>Spell configuration (min/max distance, strength reducer)</li>
 * <li>Entity targeting (all entity types can be targeted)</li>
 * <li>Velocity application (horizontal push, more powerful than Depulso)</li>
 * </ul>
 *
 * @author autumnwoz
 * @author Azami7
 */
public class FlipendoTest extends KnockbackTest {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FLIPENDO;
    }

    /**
     * No invalid entity types for Flipendo
     *
     * @return null
     */
    @Override @Nullable
    EntityType getInvalidEntityType() {
        return null;
    }

    /**
     * Get a valid entity type - can be any type for Flipendo
     *
     * @return a valid entity
     */
    @Override @NotNull
    EntityType getValidEntityType() {
        return EntityType.EGG;
    }

    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        FLIPENDO flipendo = (FLIPENDO) castSpell(caster, location, targetLocation);

        assertEquals(FLIPENDO.minDistanceConfig, flipendo.getMinDistance(), "unexpected minDistance");
        assertEquals(FLIPENDO.maxDistanceConfig, flipendo.getMaxDistance(), "unexpected maxDistance");
        assertEquals(FLIPENDO.strengthReducerConfig, flipendo.getStrengthReducer(), "unexpected strengthReducer");
    }

    /**
     * Flipendo has no revert actions
     */
    @Override @Test
    void revertTest() {

    }
}

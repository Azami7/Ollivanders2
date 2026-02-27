package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.ACCIO;
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
 * Unit tests for ACCIO spell functionality.
 *
 * <p>Tests the summoning charm including:</p>
 * <ul>
 * <li>Spell configuration (pull, min/max distance, strength reducer)</li>
 * <li>Entity targeting (items only)</li>
 * <li>Pull velocity application</li>
 * </ul>
 *
 * @author Azami7
 */
public class AccioTest extends KnockbackTest {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ACCIO;
    }

    /**
     * Accio can only target items
     *
     * @return a non-item
     */
    @Override @Nullable
    EntityType getInvalidEntityType() {
        return EntityType.MINECART;
    }

    /**
     * Get a valid entity type - can be an Item
     *
     * @return a valid entity
     */
    @Override @NotNull
    EntityType getValidEntityType() {
        return EntityType.ITEM;
    }

    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        ACCIO accio = (ACCIO)castSpell(caster, location, targetLocation);

        assertTrue(accio.isPull(), "not set to pull");
        assertEquals(ACCIO.minDistanceConfig, accio.getMinDistance(), "unexpected minDistance");
        assertEquals(ACCIO.maxDistanceConfig, accio.getMaxDistance(), "unexpected maxDistance");
        assertEquals(ACCIO.strengthReducerConfig, accio.getStrengthReducer(), "unexpected strengthReducer");
    }

    /**
     * Accio has no revert actions.
     */
    @Override @Test
    void revertTest() {

    }
}

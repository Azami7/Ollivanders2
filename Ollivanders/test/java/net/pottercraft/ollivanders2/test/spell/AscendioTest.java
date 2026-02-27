package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.ASCENDIO;
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
 * Unit tests for ASCENDIO spell functionality.
 *
 * <p>Tests the climbing charm including:</p>
 * <ul>
 * <li>Spell configuration (vertical, targets self, min/max distance)</li>
 * <li>Self-targeting behavior (spell affects caster only)</li>
 * <li>Upward velocity application</li>
 * <li>Underwater handling (propels to surface)</li>
 * </ul>
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
}

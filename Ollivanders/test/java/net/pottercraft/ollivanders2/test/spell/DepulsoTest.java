package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.DEPULSO;
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
 * Unit tests for DEPULSO spell functionality.
 *
 * <p>Tests the banishing charm including:</p>
 * <ul>
 * <li>Spell configuration (min/max distance)</li>
 * <li>Entity targeting (all entity types can be targeted)</li>
 * <li>Velocity application (horizontal push)</li>
 * </ul>
 *
 * @author Azami7
 */
public class DepulsoTest extends KnockbackTest {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DEPULSO;
    }

    /**
     * No invalid entity types for Depulso
     *
     * @return null
     */
    @Override @Nullable
    EntityType getInvalidEntityType() {
        return null;
    }

    /**
     * Get a valid entity type - can be any type for Depulso
     *
     * @return a valid entity
     */
    @Override @NotNull
    EntityType getValidEntityType() {
        return EntityType.ARROW;
    }

    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        DEPULSO depulso = (DEPULSO) castSpell(caster, location, targetLocation);

        assertEquals(DEPULSO.minDistanceConfig, depulso.getMinDistance(), "unexpected minDistance");
        assertEquals(DEPULSO.maxDistanceConfig, depulso.getMaxDistance(), "unexpected maxDistance");
        assertEquals(DEPULSO.maxRadiusConfig, depulso.getMaxRadius(), "unexpected maxRadius");
        assertEquals(DEPULSO.maxTargetConfig, depulso.getMaxTargets(), "unexpected maxTargets");
    }
}

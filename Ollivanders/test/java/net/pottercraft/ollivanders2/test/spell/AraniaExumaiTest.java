package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.ARANIA_EXUMAI;
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
 * Unit tests for {@link ARANIA_EXUMAI}. Extends {@link KnockbackTest} for the shared knockback tests.
 *
 * @author Azami7
 */
public class AraniaExumaiTest extends KnockbackTest {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ARANIA_EXUMAI;
    }

    /**
     * Only targets spiders
     *
     * @return a non-spider
     */
    @Override @Nullable
    EntityType getInvalidEntityType() {
        return EntityType.SKELETON;
    }

    /**
     * Get a valid entity type - can be a spider
     *
     * @return a valid entity
     */
    @Override @NotNull
    EntityType getValidEntityType() {
        return EntityType.SPIDER;
    }

    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        ARANIA_EXUMAI araniaExumai = (ARANIA_EXUMAI) castSpell(caster, location, targetLocation);

        assertEquals(ARANIA_EXUMAI.minDistanceConfig, araniaExumai.getMinDistance(), "unexpected minDistance");
        assertEquals(ARANIA_EXUMAI.maxDistanceConfig, araniaExumai.getMaxDistance(), "unexpected maxDistance");
        assertEquals(ARANIA_EXUMAI.strengthReducerConfig, araniaExumai.getStrengthReducer(), "unexpected strengthReducer");
        assertEquals(ARANIA_EXUMAI.maxRadiusConfig, araniaExumai.getMaxRadius(), "unexpected maxRadius");
        assertEquals(ARANIA_EXUMAI.maxTargetConfig, araniaExumai.getMaxTargets(), "unexpected maxTargets");
    }

    /**
     * Arania Exumai has no revert actions
     */
    @Override @Test
    void revertTest() {

    }
}

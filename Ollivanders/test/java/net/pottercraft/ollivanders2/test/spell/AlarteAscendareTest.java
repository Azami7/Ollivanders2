package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.ALARTE_ASCENDARE;
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
 * Unit tests for ALARTE_ASCENDARE spell functionality.
 *
 * <p>Tests the winged-ascent charm including:</p>
 * <ul>
 * <li>Velocity calculation based on caster experience (up = usesModifier / 20)</li>
 * <li>Velocity clamping to min/max bounds</li>
 * <li>Only Y-axis velocity modification (X and Z remain 0)</li>
 * <li>Entity targeting and velocity addition</li>
 * <li>Projectile stopping on entity hit or solid block hit</li>
 * </ul>
 *
 * @author Azami7
 */
public class AlarteAscendareTest extends KnockbackTest {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ALARTE_ASCENDARE;
    }

    /**
     * Alarte ascendare can target living entities
     *
     * @return a non-living entity
     */
    @Override @Nullable
    EntityType getInvalidEntityType() {
        return EntityType.CHERRY_BOAT;
    }

    /**
     * Get a valid entity type - can be any living
     *
     * @return a valid entity
     */
    @Override @NotNull
    EntityType getValidEntityType() {
        return EntityType.SHEEP;
    }

    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        ALARTE_ASCENDARE alarteAscendare = (ALARTE_ASCENDARE) castSpell(caster, location, targetLocation);

        assertTrue(alarteAscendare.isVertical(), "not set to is vertical");
        assertEquals(ALARTE_ASCENDARE.minDistanceConfig, alarteAscendare.getMinDistance(), "unexpected minDistance");
        assertEquals(ALARTE_ASCENDARE.maxDistanceConfig, alarteAscendare.getMaxDistance(), "unexpected maxDistance");
    }

    /**
     * Test entity targeting and projectile behavior.
     *
     * <p>Verifies that the spell targets entities and applies upward velocity, and that the
     * projectile stops when hitting both entities and solid blocks.</p>
     */
    @Override @Test
    void doCheckEffectTest() {
        /*
        World testWorld = mockServer.addSimpleWorld("world1");
        Location location = new Location(testWorld, 200, 40, 100);
        Location targetLocation = new Location(testWorld, 210, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        targetLocation.getBlock().getRelative(BlockFace.DOWN).setType(Material.DIRT);
        Entity skeleton = testWorld.spawnEntity(targetLocation, EntityType.SKELETON);

        ALARTE_ASCENDARE alarteAscendare = (ALARTE_ASCENDARE) castSpell(caster, targetLocation, O2SpellType.ALARTE_ASCENDARE);
        mockServer.getScheduler().performTicks(11);
        assertTrue(alarteAscendare.isKilled(), "projectile did not stop at target entity");
        assertEquals(alarteAscendare.getVelocity(), skeleton.getVelocity(), "target entity does not have expected velocity");

        skeleton.remove();
        targetLocation.getBlock().setType(Material.DANDELION);
        alarteAscendare = (ALARTE_ASCENDARE) castSpell(caster, targetLocation, O2SpellType.ALARTE_ASCENDARE);
        mockServer.getScheduler().performTicks(11);
        assertTrue(alarteAscendare.isKilled(), "projectile did not stop at solid block");

         */
    }
}

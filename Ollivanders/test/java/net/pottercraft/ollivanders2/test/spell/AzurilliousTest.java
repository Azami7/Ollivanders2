package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.AZURILLIOUS}. Extends {@link SparksTest} for the shared
 * sparks tests.
 *
 * @author Azami7
 */
public class AzurilliousTest extends SparksTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AZURILLIOUS;
    }

    /**
     * Verify a hit spawns an area effect cloud at the target with the expected radius (2.0) and duration (100 ticks).
     */
    @Test
    void doOtherEffectsTest() {
        World testWorld = mockServer.addSimpleWorld("AzurilliousOtherEffects");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        AreaEffectCloud cloud = null;
        for (Entity entity : testWorld.getEntities()) {
            if (entity.getType() == EntityType.AREA_EFFECT_CLOUD &&
                    entity.getLocation().distanceSquared(targetLocation) < 4) {
                cloud = (AreaEffectCloud) entity;
                break;
            }
        }

        assertNotNull(cloud, "AreaEffectCloud was not spawned at target location");
        assertEquals(2.0f, cloud.getRadius(), "AreaEffectCloud radius is incorrect");
        assertEquals(100, cloud.getDuration(), "AreaEffectCloud duration is incorrect");
    }
}
package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the AZURILLIOUS blue sparks spell.
 *
 * <p>Provides test coverage for the spell's area effect cloud functionality beyond the
 * base Sparks tests:</p>
 * <ul>
 * <li><strong>Cloud Spawning:</strong> Verifies an AreaEffectCloud is spawned at the target location</li>
 * <li><strong>Cloud Configuration:</strong> Confirms the cloud has the correct radius and duration</li>
 * </ul>
 *
 * <p>Inherits sound and projectile travel testing from {@link SparksTest}.</p>
 *
 * @author Azami7
 */
@Isolated
public class AzurilliousTest extends SparksTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AZURILLIOUS;
    }

    /**
     * Tests that doOtherEffects() spawns an AreaEffectCloud at the target's location.
     *
     * <p>Verifies the following when the spell hits a target entity:</p>
     * <ul>
     * <li>An AreaEffectCloud is spawned at the target player's location</li>
     * <li>The cloud has radius 2.0 blocks</li>
     * <li>The cloud has duration 100 ticks (5 seconds)</li>
     * </ul>
     *
     * <p>Places a target player 10 blocks away and advances ticks to allow the
     * projectile to travel to the target and apply effects.</p>
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
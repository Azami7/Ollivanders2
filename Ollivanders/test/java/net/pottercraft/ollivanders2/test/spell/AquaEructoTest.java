package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.AQUA_ERUCTO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the AQUA_ERUCTO water extinguishing spell.
 *
 * <p>Provides comprehensive test coverage for the spell's core functionality:
 * <ul>
 * <li><strong>Target Detection:</strong> Verifies the spell finds on-fire entities and items</li>
 * <li><strong>Extinguishing:</strong> Confirms fire ticks are removed from burning targets</li>
 * <li><strong>Water Block Effect:</strong> Tests water block placement and tracking</li>
 * <li><strong>Caster Protection:</strong> Ensures the spell doesn't extinguish the caster</li>
 * <li><strong>No Target Handling:</strong> Validates spell behavior when no on-fire target exists</li>
 * <li><strong>Water Block Lifespan:</strong> Tests water block TTL and cleanup</li>
 * <li><strong>Reversion:</strong> Verifies temporary water blocks are properly reverted</li>
 * </ul>
 */
@Isolated
public class AquaEructoTest extends AquaEructoSuperTest {
    /**
     * Returns the spell type being tested.
     *
     * @return AQUA_ERUCTO spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AQUA_ERUCTO;
    }

    /**
     * Creates a burning entity that is a valid target for AQUA_ERUCTO.
     *
     * <p>Returns a CAMEL with fire ticks set to 100, making it a burning entity that the spell
     * should target and extinguish.</p>
     *
     * @param location the location to spawn the entity at
     * @return a burning CAMEL entity
     */
    @Override
    @NotNull
    Entity getValidEntity(@NotNull Location location) {
        Entity entity = location.getWorld().spawnEntity(location, EntityType.CAMEL);
        entity.setFireTicks(100);

        return entity;
    }

    /**
     * Creates a non-burning entity that is not a valid target for AQUA_ERUCTO.
     *
     * <p>Returns a CAMEL without fire ticks, making it an invalid target that the spell
     * should ignore.</p>
     *
     * @param location the location to spawn the entity at
     * @return a non-burning CAMEL entity
     */
    @Override
    @NotNull
    Entity getInvalidEntity(@NotNull Location location) {
        return location.getWorld().spawnEntity(location, EntityType.CAMEL);
    }

    /**
     * Verifies that AQUA_ERUCTO successfully extinguished the target.
     *
     * <p>Checks that the entity's fire ticks were set to 0, confirming the extinguishing effect.</p>
     *
     * @param affectedEntity the entity that was targeted by the spell
     */
    void checkEffect(Entity affectedEntity) {
        assertEquals(0, affectedEntity.getFireTicks(), "Entity fireTicks not set to 0");
    }

    /**
     * Tests spell effect on burning items.
     *
     * <p>Verifies that the spell can target and extinguish burning items (dropped items on the ground).</p>
     */
    @Test
    void itemTargetTest() {
        World testWorld = mockServer.addSimpleWorld("Aqua_Eructo");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        targetLocation.getBlock().getRelative(BlockFace.DOWN).setType(Material.STONE); // block for target item to stand on
        Item item = testWorld.dropItem(targetLocation, new ItemStack(Material.ARROW, 1));
        item.setFireTicks(100);

        AQUA_ERUCTO aquaEructo = (AQUA_ERUCTO) castSpell(caster, location, item.getLocation());
        mockServer.getScheduler().performTicks(20);
        assertTrue(aquaEructo.hasHitTarget());
        assertTrue(aquaEructo.isExtinguished(), "target not extinguished");
    }
}

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
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AQUA_ERUCTO}. Extends {@link AquaEructoBaseTest} for the shared water-spell tests.
 */
public class AquaEructoTest extends AquaEructoBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AQUA_ERUCTO;
    }

    /**
     * @param location the location to spawn the entity at
     * @return a burning camel, a valid target for AQUA_ERUCTO
     */
    @Override
    @NotNull
    Entity getValidEntity(@NotNull Location location) {
        Entity entity = location.getWorld().spawnEntity(location, EntityType.CAMEL);
        entity.setFireTicks(100);

        return entity;
    }

    /**
     * @param location the location to spawn the entity at
     * @return a non-burning camel, not a valid target for AQUA_ERUCTO
     */
    @Override
    @NotNull
    Entity getInvalidEntity(@NotNull Location location) {
        return location.getWorld().spawnEntity(location, EntityType.CAMEL);
    }

    void checkEffect(Entity affectedEntity) {
        assertEquals(0, affectedEntity.getFireTicks(), "Entity fireTicks not set to 0");
    }

    /**
     * Verify the spell can extinguish a burning dropped item.
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
        assertTrue(aquaEructo.hasHitBlock());
        assertTrue(aquaEructo.isExtinguished(), "target not extinguished");
    }
}

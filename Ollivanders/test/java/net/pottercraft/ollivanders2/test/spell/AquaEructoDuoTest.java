package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.AQUA_ERUCTO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.AQUA_ERUCTO_DUO}. Extends {@link AquaEructoBaseTest} for
 * the shared water-spell tests.
 */
public class AquaEructoDuoTest extends AquaEructoBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AQUA_ERUCTO_DUO;
    }

    /**
     * @param location the location to spawn the entity at
     * @return a blaze, a fire mob that is a valid target for AQUA_ERUCTO_DUO
     */
    @Override
    @NotNull
    Entity getValidEntity(@NotNull Location location) {
        return location.getWorld().spawnEntity(location, EntityType.BLAZE);
    }

    /**
     * @param location the location to spawn the entity at
     * @return a camel, not a fire mob and not a valid target for AQUA_ERUCTO_DUO
     */
    @Override
    @NotNull
    Entity getInvalidEntity(@NotNull Location location) {
        return location.getWorld().spawnEntity(location, EntityType.CAMEL);
    }

    void checkEffect(Entity affectedEntity) {
        AttributeInstance healthAttribute = ((LivingEntity)affectedEntity).getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(healthAttribute);
        double maxHealth = healthAttribute.getValue();

        assertTrue(((LivingEntity)affectedEntity).getHealth() < maxHealth, "entity was not damaged");
    }

    /**
     * Verify the spell does not damage the caster.
     */
    @Override
    @Test
    void targetCasterTest() {
        World testWorld = mockServer.addSimpleWorld("Aqua_Eructo");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation.getBlock().getRelative(BlockFace.DOWN).getLocation(), 3); // blocks for spell to stop on

        AQUA_ERUCTO aquaEructo = (AQUA_ERUCTO) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(aquaEructo.hasHitBlock());
        AttributeInstance healthAttribute = caster.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(healthAttribute);
        assertEquals(healthAttribute.getValue(), caster.getHealth(), "caster was damaged");
        assertFalse(aquaEructo.isExtinguished(), "spell targeted caster");
    }
}

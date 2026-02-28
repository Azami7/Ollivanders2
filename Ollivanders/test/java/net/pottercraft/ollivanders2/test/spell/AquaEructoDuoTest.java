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
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the AQUA_ERUCTO_DUO fire-damaging spell.
 *
 * <p>Provides comprehensive test coverage for the spell's core functionality:
 * <ul>
 * <li><strong>Target Detection:</strong> Verifies the spell finds fire-based mobs (Blazes, Magma Cubes)</li>
 * <li><strong>Damage Effect:</strong> Confirms damage is dealt to fire mobs based on caster skill</li>
 * <li><strong>Water Block Effect:</strong> Tests water block placement at the target's eye level</li>
 * <li><strong>Caster Protection:</strong> Ensures the spell doesn't damage the caster</li>
 * <li><strong>No Target Handling:</strong> Validates spell behavior when no fire mob target exists</li>
 * <li><strong>Water Block Lifespan:</strong> Tests water block TTL and cleanup</li>
 * <li><strong>Reversion:</strong> Verifies temporary water blocks are properly reverted</li>
 * </ul>
 */
@Isolated
public class AquaEructoDuoTest extends AquaEructoSuperTest {
    /**
     * Returns the spell type being tested.
     *
     * @return AQUA_ERUCTO_DUO spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AQUA_ERUCTO_DUO;
    }

    /**
     * Creates a fire-based mob that is a valid target for AQUA_ERUCTO_DUO.
     *
     * <p>Returns a BLAZE, which is a fire-based entity that the spell should target and damage.</p>
     *
     * @param location the location to spawn the entity at
     * @return a BLAZE fire mob entity
     */
    @Override
    @NotNull
    Entity getValidEntity(@NotNull Location location) {
        return location.getWorld().spawnEntity(location, EntityType.BLAZE);
    }

    /**
     * Creates a non-fire-based mob that is not a valid target for AQUA_ERUCTO_DUO.
     *
     * <p>Returns a CAMEL, which is not a fire-based entity that the spell should ignore.</p>
     *
     * @param location the location to spawn the entity at
     * @return a CAMEL entity (not a fire mob)
     */
    @Override
    @NotNull
    Entity getInvalidEntity(@NotNull Location location) {
        return location.getWorld().spawnEntity(location, EntityType.CAMEL);
    }

    /**
     * Verifies that AQUA_ERUCTO_DUO successfully damaged the target.
     *
     * <p>Checks that the entity's health is less than its maximum health, confirming the damage effect
     * was applied.</p>
     *
     * @param affectedEntity the entity that was targeted by the spell
     */
    void checkEffect(Entity affectedEntity) {
        AttributeInstance healthAttribute = ((LivingEntity)affectedEntity).getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(healthAttribute);
        double maxHealth = healthAttribute.getValue();

        assertTrue(((LivingEntity)affectedEntity).getHealth() < maxHealth, "entity was not damaged");
    }

    /**
     * Tests that the spell does not damage the caster.
     *
     * <p>Verifies that:
     * <ul>
     * <li>The spell hits a target (the block base)</li>
     * <li>The caster's health remains unchanged</li>
     * <li>The spell did not target the caster</li>
     * </ul>
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

        assertTrue(aquaEructo.hasHitTarget());
        AttributeInstance healthAttribute = caster.getAttribute(Attribute.MAX_HEALTH);
        assertEquals(healthAttribute.getValue(), caster.getHealth(), "caster was damaged");
        assertFalse(aquaEructo.isExtinguished(), "spell targeted caster");
    }
}

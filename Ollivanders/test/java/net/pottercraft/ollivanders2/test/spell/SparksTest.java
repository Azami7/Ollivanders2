package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.Sparks;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link Sparks} spells, covering the firework sound on launch and, for damaging spells, that the
 * projectile reaches and damages the target.
 *
 * @author Azami7
 */
abstract public class SparksTest extends O2SpellTestSuper {
    /**
     * Verify the spell plays the firework sound and, when it deals damage, hits and damages the target player.
     */
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        World spyWorld = Mockito.spy(testWorld);
        Location location = getNextLocation(spyWorld);
        Location targetLocation = new Location(spyWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);

        Sparks sparks = (Sparks) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(2);

        Mockito.verify(spyWorld).playSound(
                ArgumentMatchers.any(Location.class),
                Mockito.eq(Sound.ENTITY_FIREWORK_ROCKET_BLAST),
                Mockito.eq(1.0f),
                Mockito.eq(0.0f)
        );

        mockServer.getScheduler().performTicks(10);
        if (sparks.doesDamage()) { // spell should have hit target player
            assertTrue(sparks.isKilled(), "spell did not hit target player");
            AttributeInstance healthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
            assertNotNull(healthAttribute);
            assertNotEquals(healthAttribute.getValue(), target.getHealth(), "target was not damaged");
        }
    }

    /**
     * No-op: Sparks spells have no revert action.
     */
    @Override
    @Test
    void revertTest() {
        // sparks spells don't have revert actions
    }
}

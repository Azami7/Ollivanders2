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
 * Abstract base test class for Sparks spell variants.
 *
 * <p>Provides shared test infrastructure for testing Sparks spell implementations including:</p>
 * <ul>
 * <li><strong>Sound Verification:</strong> Verifies the spell plays a firework sound on impact</li>
 * <li><strong>Damage Testing:</strong> For damage-dealing spells, confirms the spell hits targets and deals damage</li>
 * <li><strong>Projectile Travel:</strong> Tests that projectiles travel to the target location</li>
 * </ul>
 *
 * <p>Subclasses must implement {@link #getSpellType()} to specify which Sparks variant to test.</p>
 *
 * @author Azami7
 */
abstract public class SparksTest extends O2SpellTestSuper {
    /**
     * Tests core Sparks spell functionality.
     *
     * <p>Verifies the following:</p>
     * <ul>
     * <li>The spell plays a firework sound on impact using Mockito spy verification</li>
     * <li>For damage-dealing spells, the projectile reaches the target and deals damage</li>
     * </ul>
     *
     * <p>The test casts the spell at a target player 10 blocks away and allows sufficient ticks
     * for the projectile to travel and impact.</p>
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
     * Overridden to disable revert testing.
     *
     * <p>Sparks spells have no revert actions since they don't apply temporary effects
     * or alter the world state in ways that need reverting.</p>
     */
    @Override
    @Test
    void revertTest() {
        // sparks spells don't have revert actions
    }
}

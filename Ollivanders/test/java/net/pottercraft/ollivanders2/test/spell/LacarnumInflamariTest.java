package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.LACARNUM_INFLAMARI;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.SmallFireball;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link LACARNUM_INFLAMARI} spell, which launches a single small fireball from the caster.
 *
 * <p>The spell is a thin launcher: on its first tick it fires a Bukkit {@link SmallFireball} from the caster via
 * {@code LivingEntity.launchProjectile} and immediately ends.</p>
 *
 * <p><strong>MockBukkit limitation:</strong> {@code launchProjectile} is implemented by calling
 * {@code World.createEntity}, which builds the projectile but does not register it in the world - the launched
 * fireball never appears in {@code World.getEntities()} and no {@code ProjectileLaunchEvent} is fired. The
 * fireball is therefore not directly observable in tests, and its subsequent flight and ignition are Bukkit
 * entity physics MockBukkit does not model. These tests verify the part the spell actually controls: that the
 * launch call succeeds (an unsupported projectile type would throw and leave the spell un-killed) and that the
 * spell ends after launching rather than firing repeatedly.</p>
 *
 * @author Azami7
 */
public class LacarnumInflamariTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LACARNUM_INFLAMARI;
    }

    /**
     * Verifies that the spell launches its fireball without error and then ends.
     *
     * <p>{@code doCheckEffect} launches the fireball and only then calls {@code kill()}, so a launch failure (e.g.
     * an unsupported projectile type) would propagate or leave the spell alive. Reaching a killed state after the
     * first tick confirms the launch succeeded; ticking well past the first tick confirms the spell does not keep
     * launching on later ticks.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        LACARNUM_INFLAMARI lacarnumInflamari = (LACARNUM_INFLAMARI) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(5);

        assertTrue(lacarnumInflamari.isKilled(), "spell did not launch and end as expected");
    }

    /**
     * LACARNUM_INFLAMARI has no revert action - the launched fireball is not recalled.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }
}
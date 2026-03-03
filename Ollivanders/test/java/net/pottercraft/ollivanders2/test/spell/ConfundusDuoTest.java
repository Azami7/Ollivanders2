package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AddPotionEffect;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the CONFUNDUS_DUO spell.
 *
 * <p>CONFUNDUS_DUO is the Confundus Duo, an enhanced version of the Confundus Charm that applies
 * a stronger Confusion effect to targets for 30-240 seconds depending on caster skill level. This
 * test class overrides the amplifier test to verify that the confusion effect uses a fixed amplifier
 * of 1 that does not scale with skill level.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.CONFUNDUS_DUO for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
public class ConfundusDuoTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.CONFUNDUS_DUO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CONFUNDUS_DUO;
    }

    /**
     * Test that the amplifier is fixed at 1 and does not scale with skill level.
     *
     * <p>Verifies that the Confusion amplifier remains constant at 1 across all skill levels,
     * demonstrating that this spell's effect strength is fixed and stronger than CONFUNDO (which has
     * amplifier 0).</p>
     */
    @Override
    @Test
    void amplifierTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(targetLocation);

        double level = 1;
        AddPotionEffect addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(1, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level 1");

        level = O2Spell.spellMasteryLevel + 1;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(1, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level O2Spell.spellMasteryLevel + 1");
    }
}

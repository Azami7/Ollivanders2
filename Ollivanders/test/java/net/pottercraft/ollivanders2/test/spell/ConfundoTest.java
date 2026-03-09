package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AddPotionEffect;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the CONFUNDO spell.
 *
 * <p>CONFUNDO is the Confundus Charm that applies Confusion effect to targets, disorienting them
 * for 15-120 seconds depending on caster skill level. This test class overrides the amplifier test
 * to verify that the confusion effect uses a fixed amplifier that does not scale with skill level.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.CONFUNDO for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
@Isolated
public class ConfundoTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.CONFUNDO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CONFUNDO;
    }

    /**
     * Test that the amplifier is fixed and does not scale with skill level.
     *
     * <p>Verifies that the Confusion amplifier remains constant at 0 across all skill levels,
     * demonstrating that this spell's effect strength is not affected by caster skill progression.</p>
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
        assertEquals(0, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level 1");

        level = O2Spell.spellMasteryLevel + 1;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(0, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level O2Spell.spellMasteryLevel + 1");
    }
}

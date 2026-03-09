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
 * Unit tests for the IMPEDIMENTA spell.
 *
 * <p>IMPEDIMENTA is the Impediment Jinx that applies Slowness effect to targets, slowing
 * their movement for 5-60 seconds depending on caster skill level. This test class overrides
 * the amplifier test to verify the continuous amplifier scaling (Slowness I through V) specific
 * to this spell.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.IMPEDIMENTA for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
@Isolated
public class ImpedimentaTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.IMPEDIMENTA
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.IMPEDIMENTA;
    }

    /**
     * Test the continuous amplifier scaling for IMPEDIMENTA.
     *
     * <p>Verifies that the Slowness amplifier scales continuously with skill level according
     * to the formula amplifier = usesModifier / 20, with a maximum cap of amplifier 4
     * (Slowness V). Tests at multiple skill levels to verify the continuous scaling.</p>
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

        level = 40;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(2, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level O2Spell.spellMasteryLevel - 1");

        level = O2Spell.spellMasteryLevel;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(4, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level O2Spell.spellMasteryLevel - 1");
    }
}

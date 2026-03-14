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
 * Unit tests for the IMMOBULUS spell.
 *
 * <p>IMMOBULUS is the Freezing Charm that applies Slowness and Slow Falling effects to targets,
 * immobilizing them for 15-180 seconds depending on caster skill level. This test class overrides
 * the amplifier test to verify the three-tier Slowness scaling (Slowness I/II/III) specific to
 * this spell.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.IMMOBULUS for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
public class ImmobulusTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.IMMOBULUS
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.IMMOBULUS;
    }

    /**
     * Test the three-tier amplifier scaling for IMMOBULUS.
     *
     * <p>Verifies that the Slowness amplifier scales correctly through three tiers:
     * Slowness I (amplifier 0) at low skill levels, Slowness II (amplifier 1) at
     * mid-skill levels, and Slowness III (amplifier 2) at high skill levels.</p>
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

        level = O2Spell.spellMasteryLevel - 1;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(1, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level O2Spell.spellMasteryLevel - 1");

        level = O2Spell.spellMasteryLevel + 1;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(2, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level O2Spell.spellMasteryLevel - 1");
    }
}

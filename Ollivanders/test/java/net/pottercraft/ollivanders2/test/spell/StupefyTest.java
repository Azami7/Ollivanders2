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
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.STUPEFY}. Extends {@link AddPotionEffectTest} for the
 * shared potion-effect tests.
 */
public class StupefyTest extends AddPotionEffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.STUPEFY;
    }

    /**
     * Verify the amplifier steps through its three skill tiers: 0 at low skill, 1 near mastery, 2 above mastery.
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
        assertEquals(2, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level O2Spell.spellMasteryLevel + 1");
    }
}

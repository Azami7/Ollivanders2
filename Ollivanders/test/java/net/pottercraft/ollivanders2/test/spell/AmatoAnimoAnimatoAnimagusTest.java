package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AMATO_ANIMO_ANIMATO_ANIMAGUS;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link AMATO_ANIMO_ANIMATO_ANIMAGUS} spell, which handles the Animagus incantation and
 * transformation process.
 *
 * <p>Verifies the incantation effect is applied with and without strict conditions, including the dawn/sunset
 * time requirement.</p>
 *
 * @author Azami7
 * @see AMATO_ANIMO_ANIMATO_ANIMAGUS
 */
@Isolated
public class AmatoAnimoAnimatoAnimagusTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS;
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void spellConstructionTest() {
    }

    /**
     * Verifies the animagus incantation and transformation behavior.
     *
     * <ul>
     * <li>Non-animagus with strict conditions disabled: incantation effect is applied regardless of time</li>
     * <li>Non-animagus with strict conditions enabled: incantation fails outside dawn/sunset windows</li>
     * <li>Non-animagus with strict conditions enabled: incantation succeeds at dawn and sunset</li>
     * <li>Animagus: transforms to animal form with sufficient spell experience</li>
     * <li>Animagus in animal form: reverts to human form</li>
     * <li>Animagus with {@link HIGHER_SKILL}: transforms with doubled uses modifier</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Ollivanders2.useStrictAnimagusConditions = false;

        // player is not an animagus, use strict conditions false
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION), "animagus incantation effect not set");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not get success message");

        // cleanup
        Ollivanders2API.getPlayers().playerEffects.removeEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION);
        mockServer.getScheduler().performTicks(20);
        TestCommon.clearMessageQueue(caster);

        // player is not animagus, use strict conditions true
        Ollivanders2.useStrictAnimagusConditions = true;
        castSpell(caster, location, targetLocation);
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION), "animagus incantation effect set in strict conditions when conditions not met");
        mockServer.getScheduler().performTicks(20);
        message = caster.nextMessage();
        assertNotNull(message, "caster did not get failure message when use strict conditions true and not correct conditions");

        // player is not an animagus, strict conditions, dawn
        testWorld.setFullTime(TimeCommon.DAWN.getTick());
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION), "animagus incantation effect not set at dawn");

        // cleanup
        Ollivanders2API.getPlayers().playerEffects.removeEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION);
        mockServer.getScheduler().performTicks(20);

        // player is not an animagus, strict conditions, sunset
        testWorld.setFullTime(TimeCommon.SUNSET.getTick());
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION), "animagus incantation effect not set at sunset");

        // cleanup
        Ollivanders2API.getPlayers().playerEffects.removeEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION);
        mockServer.getScheduler().performTicks(20);

        // player is an animagus
        O2Player casterO2P = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(casterO2P);
        casterO2P.setAnimagusForm(EntityType.RABBIT);
        assertTrue(casterO2P.isAnimagus());
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2); // set to double mastery to ensure 100% success rate
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "animagus caster did not change form");

        // player is an animagus in animal form
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "caster in animal form did not change back");

        // player is animagus and has HIGHER_SKILL
        HIGHER_SKILL higherSkill = new HIGHER_SKILL(testPlugin, 200, false, caster.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(higherSkill);
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel); // with higher skill, this should be 200, which will result in 100% success rate
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "animagus caster did not change form with HIGHER_SKILL");
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
    }
}

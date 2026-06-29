package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.SILENCIO} spell (Silencing Charm).
 *
 * <p>SILENCIO applies the {@link net.pottercraft.ollivanders2.effect.O2EffectType#MUTED_SPEECH} O2Effect to a target
 * player, leaving them able to cast only nonverbal spells. The inherited {@link AddO2EffectTest} coverage verifies the
 * effect is applied to the target, expires after its duration, and stays within the spell's configured bounds.</p>
 *
 * @author Azami7
 */
public class SilencioTest extends AddO2EffectTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.SILENCIO;
    }

    /** {@inheritDoc} */
    @Override
    boolean addsPotionEffect() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    List<PotionEffectType> getPotionEffects() {
        return null;
    }
}
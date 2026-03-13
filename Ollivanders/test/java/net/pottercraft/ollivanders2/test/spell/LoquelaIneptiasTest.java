package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.LOQUELA_INEPTIAS} spell (Babbling Curse).
 *
 * <p>LOQUELA_INEPTIAS is a dark arts spell that applies the BABBLING O2Effect to a target player,
 * causing them to speak nonsense for the duration of the effect.</p>
 *
 * @author Azami7
 */
public class LoquelaIneptiasTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LOQUELA_INEPTIAS;
    }

    @Override
    boolean addsPotionEffect() {
        return false;
    }

    @Override
    List<PotionEffectType> getPotionEffects() {
        return null;
    }
}

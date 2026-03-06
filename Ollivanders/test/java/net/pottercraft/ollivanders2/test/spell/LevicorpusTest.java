package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.LEVICORPUS} spell (Suspension Jinx).
 *
 * <p>LEVICORPUS is a dark arts spell that applies the SUSPENSION O2Effect to a target player,
 * hoisting them into the air for the duration of the effect.</p>
 *
 * @author Azami7
 */
@Isolated
public class LevicorpusTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LEVICORPUS;
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

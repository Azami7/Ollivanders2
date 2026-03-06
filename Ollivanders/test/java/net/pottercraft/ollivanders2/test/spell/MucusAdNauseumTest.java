package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.MUCUS_AD_NAUSEAM} spell (Curse of the Bogies).
 *
 * <p>MUCUS_AD_NAUSEAM is a dark arts spell that applies the MUCUS O2Effect to a target player.</p>
 *
 * @author Azami7
 */
@Isolated
public class MucusAdNauseumTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MUCUS_AD_NAUSEAM;
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

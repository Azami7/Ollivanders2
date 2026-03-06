package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.TARANTALLEGRA} spell (Dancing Feet Spell).
 *
 * <p>TARANTALLEGRA applies the DANCING_FEET O2Effect to a target player, causing their legs
 * to spasm uncontrollably.</p>
 *
 * @author Azami7
 */
@Isolated
public class TarantallegraTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.TARANTALLEGRA;
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

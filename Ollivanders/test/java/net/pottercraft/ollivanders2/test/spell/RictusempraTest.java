package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.RICTUSEMPRA} spell (Tickling Charm).
 *
 * <p>RICTUSEMPRA applies both the LAUGHING and TICKLING O2Effects to a target player,
 * causing uncontrollable laughter and tickling.</p>
 *
 * @author Azami7
 */
@Isolated
public class RictusempraTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.RICTUSEMPRA;
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

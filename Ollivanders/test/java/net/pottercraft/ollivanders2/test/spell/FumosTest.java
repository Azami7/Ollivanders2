package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.FUMOS} spell (Smoke-Screen Spell).
 *
 * <p>FUMOS is a self-targeting charm that applies the FUMOS O2Effect to the caster,
 * creating a defensive smoke cloud.</p>
 *
 * @author Azami7
 */
@Isolated
public class FumosTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FUMOS;
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

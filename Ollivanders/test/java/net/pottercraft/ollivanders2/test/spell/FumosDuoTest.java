package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.FUMOS_DUO} spell (Stronger Smoke-Screen Spell).
 *
 * <p>FUMOS_DUO is a self-targeting charm that applies the FUMOS_DUO O2Effect to the caster,
 * creating a larger defensive smoke cloud than {@link net.pottercraft.ollivanders2.spell.FUMOS}.</p>
 *
 * @author Azami7
 */
@Isolated
public class FumosDuoTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FUMOS_DUO;
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

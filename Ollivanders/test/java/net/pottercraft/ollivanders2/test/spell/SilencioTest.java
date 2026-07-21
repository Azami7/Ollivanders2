package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.SILENCIO}. Extends {@link AddO2EffectTest} for the shared
 * effect tests.
 *
 * @author Azami7
 */
public class SilencioTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.SILENCIO;
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
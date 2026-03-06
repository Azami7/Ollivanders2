package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.ANAPNEO} spell (Bubble-Head Charm).
 *
 * <p>ANAPNEO is a self-targeting charm that grants the caster water breathing via a Bukkit potion
 * effect. Effect presence is verified via {@link org.bukkit.entity.Player#hasPotionEffect}.</p>
 *
 * @author Azami7
 */
@Isolated
public class AnapneoTest extends AddO2EffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ANAPNEO;
    }

    @Override
    boolean addsPotionEffect() {
        return true;
    }

    @Override
    List<PotionEffectType> getPotionEffects() {
        return new ArrayList<>() {{
            add(PotionEffectType.WATER_BREATHING);
        }};
    }
}

package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.PROTEGO} spell (Shield Charm).
 *
 * <p>PROTEGO is a self-targeting charm that applies the PROTEGO O2Effect to the caster,
 * providing a shield that deflects spells and projectiles.</p>
 *
 * @author Azami7
 */
public class ProtegoTest extends AddO2EffectTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PROTEGO;
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

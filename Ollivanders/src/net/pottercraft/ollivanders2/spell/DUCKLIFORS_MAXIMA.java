package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Jinx that transfigures the target player into a chicken for a limited duration.
 */
public class DUCKLIFORS_MAXIMA extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DUCKLIFORS_MAXIMA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.DUCKLIFORS_MAXIMA;
        branch = O2MagicBranch.JINX;

        text = "Jinx that turns the target player in to a chicken in appearance and behavior.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DUCKLIFORS_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DUCKLIFORS_MAXIMA;
        branch = O2MagicBranch.JINX;

        effectsToAdd.add(O2EffectType.DUCKLIFORS_MAXIMA);
        strengthModifier = 1;
        minDurationInSeconds = 30;

        initSpell();
    }
}

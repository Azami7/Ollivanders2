package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AGNIFORS_MAXIMA extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AGNIFORS_MAXIMA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AGNIFORS_MAXIMA;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "Jinx that changes the target player in to a sheep.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AGNIFORS_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AGNIFORS_MAXIMA;
        branch = O2MagicBranch.TRANSFIGURATION;

        effectsToAdd.add(O2EffectType.AGNIFORS_MAXIMA);
        strengthModifier = 1;
        minDurationInSeconds = 30;
        targetSelf = true;

        initSpell();
    }
}

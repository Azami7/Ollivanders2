package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Jinx that transforms the target player into a cat.
 * <p>
 * This is the most powerful variant of the Felifors transfiguration line. It extends {@link AddO2Effect} to apply
 * the {@link O2EffectType#FELIFORS_MAXIMA} effect to the target, which handles the cat transformation. The effect
 * lasts at least 30 seconds, scaled by the caster's experience.
 * </p>
 * <p>
 * Unlike {@link FELIFORS_MEDIUS}, which applies a reversible disguise to any entity, this variant targets players
 * and works through the effect system. {@code targetSelf} is enabled so the jinx can resolve onto the casting
 * player when no other target is in range.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Felifors_Spell">Harry Potter Wiki - Felifors Spell</a>
 */
public class FELIFORS_MAXIMA extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FELIFORS_MAXIMA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FELIFORS_MAXIMA;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "Jinx that transforms the target player in to a cat.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FELIFORS_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FELIFORS_MAXIMA;
        branch = O2MagicBranch.TRANSFIGURATION;

        effectsToAdd.add(O2EffectType.FELIFORS_MAXIMA);
        strengthModifier = 1;
        minDurationInSeconds = 30;
        targetSelf = true;

        initSpell();
    }
}

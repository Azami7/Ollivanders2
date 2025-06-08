package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Causes blindness in a radius
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class FUMOS extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FUMOS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FUMOS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Smoke-Screen Spell");
        }};

        text = "Fumos creates a defensive cloud around the caster which prevents those outside the cloud accurately targeting anyone inside.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FUMOS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.FUMOS;
        branch = O2MagicBranch.CHARMS;

        effectsToAdd.add(O2EffectType.FUMOS);
        strengthModifier = 1;
        minDurationInSeconds = 10;
        maxDurationInSeconds = 60;
        targetSelf = true;

        initSpell();
    }

    /**
     * Initialize the parts of the spell that are based on experience, the player, etc. and not on class
     * constants.
     */
    @Override
    void doInitSpell() {
        durationInSeconds = (int) (usesModifier / 2);
        if (durationInSeconds < minDurationInSeconds)
            durationInSeconds = minDurationInSeconds;
        else if (durationInSeconds > maxDurationInSeconds)
            durationInSeconds = maxDurationInSeconds;
    }
}

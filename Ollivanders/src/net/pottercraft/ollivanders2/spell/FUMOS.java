package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Smoke-Screen Spell - applies the {@link net.pottercraft.ollivanders2.effect.O2EffectType#FUMOS} effect
 * to the caster, creating a defensive smoke cloud that causes blindness to those outside it.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Smokescreen_Spell">Smoke-Screen Spell</a>
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
        durationMultiplier = 0.5;
        durationModifier = 0;
        targetSelf = true;

        initSpell();
    }
}

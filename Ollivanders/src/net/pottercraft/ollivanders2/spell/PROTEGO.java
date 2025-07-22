package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Shield Charm - Protego - was a charm that protected the caster with an invisible shield that deflects spells
 * and projectiles.
 * <p>
 * {@link net.pottercraft.ollivanders2.effect.PROTEGO}
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Shield_Charm">https://harrypotter.fandom.com/wiki/Shield_Charm</a>
 * {@link net.pottercraft.ollivanders2.stationaryspell.ShieldSpell}
 */
public final class PROTEGO extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROTEGO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PROTEGO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"I don't remember telling you to use a Shield Charm... but there is no doubt that it was effective...\" -Severus Snape");
            add("\"You wouldn't believe how many people, even people who work at the Ministry, can't do a decent Shield Charm.\" -George Weasley");
            add("The Shield Charm");
        }};

        text = "Protego is a shield spell which protects the caster from spells and projectiles.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PROTEGO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.FUMOS;
        branch = O2MagicBranch.CHARMS;

        effectsToAdd.add(O2EffectType.PROTEGO);

        strengthModifier = 1;
        minDurationInSeconds = 30;
        maxDurationInSeconds = 180;
        targetSelf = true;

        initSpell();
    }
}
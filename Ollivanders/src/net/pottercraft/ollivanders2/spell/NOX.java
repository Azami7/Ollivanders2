package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Wand-Extinguishing Charm — cancels the effect of {@link LUMOS} or removes a Night Vision potion effect.
 *
 * <p>NOX removes Night Vision from the caster and all nearby entities within a radius that scales
 * with the caster's experience. The radius uses the same bounds as {@link LUMOS} so that a NOX cast
 * at the same skill level can fully counter a LUMOS. Success is guaranteed regardless of experience
 * level ({@code successModifier = 0.01f}).</p>
 *
 * @author Azami7
 * @see LUMOS
 * @see <a href="https://harrypotter.fandom.com/wiki/Wand-Extinguishing_Charm">Harry Potter Wiki - Wand-Extinguishing Charm</a>
 */
public final class NOX extends RemovePotionEffectInRadius {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public NOX(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.NOX;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Wand-Extinguishing Charm");
            add("With difficulty he dragged it over himself, murmured, 'Nox,' extinguishing his wand light, and continued on his hands and knees, as silently as possible, all his senses straining, expecting every second to be discovered, to hear a cold clear voice, see a flash of green light.");
        }};

        text = "Cancels the effect of the Lumos spell or removes the effect of a Night Vision potion.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public NOX(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.NOX;
        branch = O2MagicBranch.CHARMS;
        targetSelf = true;

        potionEffectTypes.add(PotionEffectType.NIGHT_VISION);
        successModifier = 0.01f; // this will make casting NOX 100% successful

        initSpell();
    }

    /**
     * Calculate the effect radius based on the caster's experience with this spell.
     *
     * <p>Uses the same minimum and maximum radius as {@link LUMOS} so that NOX can fully
     * counter a LUMOS cast at the same skill level.</p>
     */
    @Override
    void doInitSpell() {
        effectRadius = usesModifier / 10;
        if (effectRadius < LUMOS.minEffectRadiusConfig)
            effectRadius = LUMOS.minEffectRadiusConfig;
        else if (effectRadius > LUMOS.maxEffectRadiusConfig)
            effectRadius = LUMOS.maxEffectRadiusConfig;
    }
}

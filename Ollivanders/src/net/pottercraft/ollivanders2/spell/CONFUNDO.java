package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Casts a powerful confusion potion effect on the player that scales with the caster's level in this spell.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Confundus_Charm
 */
public final class CONFUNDO extends ConfundusSuper
{
    static final int minDurationInSecondsConfig = 15;
    static final int maxDurationInSecondsConfig = 120;
    static final int minAmplifierConfig = 0;
    static final int maxAmplifierConfig = 0;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CONFUNDO(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.CONFUNDO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("The Confundus Charm");
            add("\"Look who's talking. Confunded anyone lately?\" -Harry Potter");
        }};

        text = "Confundo causes the target to become confused.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CONFUNDO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CONFUNDO;
        branch = O2MagicBranch.CHARMS;

        minAmplifier = minAmplifierConfig;
        maxAmplifier = maxAmplifierConfig;
        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        durationModifier = 0.25; // 25%

        initSpell();
    }
}
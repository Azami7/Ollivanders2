package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Blinds and slows the target entity for a duration depending on the spell's level.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Stunning_Spell">https://harrypotter.fandom.com/wiki/Stunning_Spell</a>
 */
public final class STUPEFY extends AddPotionEffect
{
    private static final int minDurationInSecondsConfig = 5;
    private static final int maxDurationInSecondsConfig = 180;
    private static final int minAmplifierConfig = 0;
    private static final int maxAmplifierConfig = 2;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public STUPEFY(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.STUPEFY;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("The Stunning Spell");
            add("\"Stunning is one of the most useful spells in your arsenal. It's sort of a wizard's bread and butter, really.\" -Harry Potter");
        }};

        text = "Stupefy will stun an opponent for a duration.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public STUPEFY(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.STUPEFY;
        branch = O2MagicBranch.CHARMS;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        durationModifier = 1.0;
        minAmplifier = minAmplifierConfig;
        maxAmplifier = maxAmplifierConfig;
        amplifierModifier = 0.02; // 1/50th usesModifier

        effectTypes.add(PotionEffectType.BLINDNESS);
        effectTypes.add(PotionEffectType.SLOWNESS);

        initSpell();
    }
}
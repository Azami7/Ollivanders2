package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * The Stunning Spell: applies Blindness and Slowness to the target for a skill-scaled duration, with a strength that
 * rises in three tiers with the caster's skill.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Stunning_Spell">Harry Potter Wiki - Stunning Spell</a>
 */
public final class STUPEFY extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 5;
    private static final int maxDurationInSecondsConfig = 180;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public STUPEFY(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.STUPEFY;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
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
    public STUPEFY(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.STUPEFY;
        branch = O2MagicBranch.CHARMS;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;

        potionEffectTypes.add(PotionEffectType.BLINDNESS);
        potionEffectTypes.add(PotionEffectType.SLOWNESS);

        initSpell();
    }

    /**
     * Set the effect amplifier in three skill tiers: 0 below half of spell mastery, 1 below mastery, 2 at or above.
     */
    @Override
    void calculateAmplifier() {
        if (usesModifier < (double) (O2Spell.spellMasteryLevel / 2))
            amplifier = 0;
        else if (usesModifier < O2Spell.spellMasteryLevel)
            amplifier = 1;
        else // usesModifier >= O2Spell.spellMasteryLevel
            amplifier = 2;
    }
}
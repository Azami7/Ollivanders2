package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Freezing Charm: applies Slowness and Slow Falling to the target for a skill-scaled duration, with a strength
 * that rises in three tiers with the caster's skill.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Freezing_Charm">Harry Potter Wiki - Freezing Charm</a>
 */
public final class IMMOBULUS extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 15;
    private static final int maxDurationInSecondsConfig = 180;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public IMMOBULUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.IMMOBULUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Freezing Charm");
            add("\"[…] immobilising two pixies at once with a clever Freezing Charm and stuffing them back into their cage.\"");
            add("The Freezing Charm is a spell which immobilises living targets.");
        }};

        text = "Slows entity movement for a time period.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public IMMOBULUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.IMMOBULUS;
        branch = O2MagicBranch.CHARMS;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        durationModifier = 0.5; // 50%

        potionEffectTypes.add(PotionEffectType.SLOWNESS);
        potionEffectTypes.add(PotionEffectType.SLOW_FALLING);

        initSpell();
    }

    /**
     * Set the Slowness amplifier in three skill tiers: 0 below half of spell mastery, 1 below mastery, 2 at or above.
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
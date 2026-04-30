package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Freezing Charm that immobilizes targets by slowing their movement.
 *
 * <p>Immobulus is a projectile charm that applies Slowness and Slow Falling effects to targets,
 * immobilizing them for a duration based on the caster's spell level. Duration ranges from
 * 15 to 180 seconds and is calculated at 50% of the caster's skill level modifier. The amplifier
 * (effect strength) scales in three tiers with skill level: Slowness I (0-spellMasteryLevel/2),
 * Slowness II (spellMasteryLevel/2 to spellMasteryLevel), and Slowness III (spellMasteryLevel+).</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Freezing_Charm">Freezing Charm</a>
 */
public final class IMMOBULUS extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 15;
    private static final int maxDurationInSecondsConfig = 180;

    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
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
     * Constructor for casting the spell.
     *
     * <p>Fires a projectile that applies Slowness and Slow Falling effects to targets.
     * Duration ranges from 15 to 180 seconds based on caster skill level. The amplifier
     * scales in three tiers with skill progression.</p>
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
     * Calculate the potion effect amplifier based on caster skill level.
     *
     * <p>Immobulus scales the Slowness effect in three tiers:</p>
     *
     * <ul>
     * <li>Amplifier 0 (Slowness I): usesModifier &lt; spellMasteryLevel / 2</li>
     * <li>Amplifier 1 (Slowness II): usesModifier &lt; spellMasteryLevel</li>
     * <li>Amplifier 2 (Slowness III): usesModifier &gt;= spellMasteryLevel</li>
     * </ul>
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
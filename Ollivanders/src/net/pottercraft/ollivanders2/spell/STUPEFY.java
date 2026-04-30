package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * The Stunning Spell that stuns targets with blindness and slowness.
 *
 * <p>Stupefy is a projectile charm that applies Blindness and Slowness effects to targets,
 * incapacitating them for a duration based on the caster's spell level. Duration ranges from
 * 5 to 180 seconds. The amplifier (effect strength) scales in three tiers with skill level:
 * Blindness I / Slowness I (0-spellMasteryLevel/2), Blindness II / Slowness II
 * (spellMasteryLevel/2 to spellMasteryLevel), and Blindness III / Slowness III
 * (spellMasteryLevel+).</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Stunning_Spell">Stunning Spell</a>
 */
public final class STUPEFY extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 5;
    private static final int maxDurationInSecondsConfig = 180;

    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
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
     * Constructor for casting the spell.
     *
     * <p>Fires a projectile that applies Blindness and Slowness effects to targets.
     * Duration ranges from 5 to 180 seconds based on caster skill level. The amplifier
     * scales in three tiers with skill progression.</p>
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
     * Calculate the potion effect amplifier based on caster skill level.
     *
     * <p>Stupefy scales the Blindness and Slowness effects in three tiers:</p>
     *
     * <ul>
     * <li>Amplifier 0 (Blindness I / Slowness I): usesModifier &lt; spellMasteryLevel / 2</li>
     * <li>Amplifier 1 (Blindness II / Slowness II): usesModifier &lt; spellMasteryLevel</li>
     * <li>Amplifier 2 (Blindness III / Slowness III): usesModifier &gt;= spellMasteryLevel</li>
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
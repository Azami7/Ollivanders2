package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * The Impediment Jinx that slows the movement of targets.
 *
 * <p>Impedimenta is a projectile Dark Arts jinx that applies Slowness effect to targets,
 * slowing their movement for a duration based on the caster's spell level. Duration ranges from
 * 5 to 60 seconds and is calculated at 50% of the caster's skill level modifier. The amplifier
 * (Slowness strength) scales continuously with skill level, calculated as usesModifier / 20,
 * clamped to a maximum of Slowness V (amplifier 4).</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Impediment_Jinx">Impediment Jinx</a>
 */
public final class IMPEDIMENTA extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 5;
    private static final int maxDurationInSecondsConfig = 60;

    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public IMPEDIMENTA(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.IMPEDIMENTA;

        flavorText = new ArrayList<>() {{
            add("The Impediment Hex");
            add("Swift use of this jinx can freeze an attacker for a few moments, or stop a magical beast in its tracks. The jinx is a vital part of any duellist’s arsenal.");
            add("\"I like the look of this one, this Impediment Jinx. Should slow down anything that’s trying to attack you, Harry. We’ll start with that one.\" -Hermione Granger");
        }};

        text = "Slows a living entity's movements.";
    }

    /**
     * Constructor for casting the spell.
     *
     * <p>Fires a projectile that applies Slowness effect to targets, slowing their movement.
     * Duration ranges from 5 to 60 seconds based on caster skill level. The amplifier scales
     * continuously with skill level, with a maximum cap of Slowness V.</p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public IMPEDIMENTA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.IMPEDIMENTA;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        durationModifier = 0.5; // 50%

        effectTypes.add(PotionEffectType.SLOWNESS);

        initSpell();
    }

    /**
     * Calculate the potion effect amplifier based on caster skill level.
     *
     * <p>Impedimenta scales the Slowness effect continuously with skill level using the formula:
     * amplifier = usesModifier / 20, clamped to a maximum of 4 (Slowness V).</p>
     */
    @Override
    void calculateAmplifier() {
        amplifier = (int) usesModifier / 20;

        if (amplifier > 4)
            amplifier = 4;
    }
}
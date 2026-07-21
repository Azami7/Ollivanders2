package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * The Impediment Jinx: applies Slowness to the target for a skill-scaled duration, with a strength that rises with
 * the caster's skill up to Slowness V.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Impediment_Jinx">Harry Potter Wiki - Impediment Jinx</a>
 */
public final class IMPEDIMENTA extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 5;
    private static final int maxDurationInSecondsConfig = 60;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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
     * Constructor.
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

        potionEffectTypes.add(PotionEffectType.SLOWNESS);

        initSpell();
    }

    /**
     * Set the Slowness amplifier to {@code usesModifier / 20}, limited to 4 (Slowness V).
     */
    @Override
    void calculateAmplifier() {
        amplifier = (int) usesModifier / 20;

        if (amplifier > 4)
            amplifier = 4;
    }
}
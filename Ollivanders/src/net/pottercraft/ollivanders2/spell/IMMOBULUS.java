package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Immobilizes a player for an amount of time depending on the player's spell level.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Freezing_Charm">https://harrypotter.fandom.com/wiki/Freezing_Charm</a>
 */
public final class IMMOBULUS extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 15;
    private static final int maxDurationInSecondsConfig = 180;
    private static final int minAmplifierConfig = 0;
    private static final int maxAmplifierConfig = 2;

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
        minAmplifier = minAmplifierConfig;
        maxAmplifier = maxAmplifierConfig;
        amplifierModifier = 0.02; // 1/50th usesModifier

        effectTypes.add(PotionEffectType.SLOWNESS);
        effectTypes.add(PotionEffectType.SLOW_FALLING);

        initSpell();
    }
}
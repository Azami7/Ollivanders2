package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Episkey healing charm: applies Regeneration to the target for a skill-scaled duration; its strength does not
 * scale with skill.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Episkey">Harry Potter Wiki - Episkey</a>
 */
public final class EPISKEY extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 15;
    private static final int maxDurationInSecondsConfig = 120;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EPISKEY(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.HEALING;
        spellType = O2SpellType.EPISKEY;

        flavorText = new ArrayList<>() {{
            add("\"Episkey,\" said Tonks. Harry's nose felt very hot, then very cold. He raised a hand and felt it gingerly. It seemed to be mended.");
            add("A minor healing spell.");
        }};

        text = "Episkey will heal minor injuries.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EPISKEY(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        branch = O2MagicBranch.HEALING;
        spellType = O2SpellType.EPISKEY;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        durationModifier = 0.5; // 50%

        potionEffectTypes.add(PotionEffectType.REGENERATION);

        initSpell();
    }
}
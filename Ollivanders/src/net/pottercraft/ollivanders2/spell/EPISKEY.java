package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Episkey spell that provides healing to targets.
 *
 * <p>Episkey is a projectile healing charm that applies Regeneration effect to targets,
 * allowing them to heal minor injuries. Duration ranges from 15 to 120 seconds and is
 * calculated at 50% of the caster's skill level modifier. The spell's amplifier does not
 * scale with skill level, providing consistent healing strength.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Episkey">Episkey</a>
 */
public final class EPISKEY extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 15;
    private static final int maxDurationInSecondsConfig = 120;

    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
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
     * Constructor for casting the spell.
     *
     * <p>Fires a projectile that applies Regeneration effect to targets, healing their injuries.
     * Duration ranges from 15 to 120 seconds based on caster skill level. The healing effect
     * strength is consistent and does not scale with skill.</p>
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

        effectTypes.add(PotionEffectType.REGENERATION);

        initSpell();
    }
}
package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Gives a target blindness
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Obscuro
 */
public final class OBSCURO extends AddPotionEffect
{
    private static final int minDurationInSecondsConfig = 30;
    private static final int maxDurationInSecondsConfig = 120;
    private static final int minAmplifierConfig = 0;
    private static final int maxAmplifierConfig = 1;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public OBSCURO(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.OBSCURO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("A black blindfold appeared over Phineas Nigellus' clever, dark eyes, causing him to bump into the frame and shriek with pain.");
        }};

        text = "Obscuro will blind the target.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public OBSCURO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.OBSCURO;
        branch = O2MagicBranch.CHARMS;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        durationModifier = 1.0;
        minAmplifier = minAmplifierConfig;
        maxAmplifier = maxAmplifierConfig;
        amplifierModifier = 0.01; // 1/100th usesModifier

        effectTypes.add(PotionEffectType.BLINDNESS);

        initSpell();
    }
}

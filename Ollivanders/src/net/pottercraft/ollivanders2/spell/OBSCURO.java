package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Obscuro spell that blinds targets.
 *
 * <p>Obscuro is a projectile charm that applies Blindness effect to targets, preventing them
 * from seeing. Duration ranges from 30 to 120 seconds and is calculated at 100% of the caster's
 * skill level modifier. The spell's amplifier does not scale with skill level, providing
 * consistent blindness strength.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Obscuro">Obscuro</a>
 */
public final class OBSCURO extends AddPotionEffect {
    private static final int minDurationInSecondsConfig = 30;
    private static final int maxDurationInSecondsConfig = 120;

    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public OBSCURO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.OBSCURO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("A black blindfold appeared over Phineas Nigellus' clever, dark eyes, causing him to bump into the frame and shriek with pain.");
        }};

        text = "Obscuro will blind the target.";
    }

    /**
     * Constructor for casting the spell.
     *
     * <p>Fires a projectile that applies Blindness effect to targets, preventing them from seeing.
     * Duration ranges from 30 to 120 seconds based on caster skill level. The blindness effect
     * strength is consistent and does not scale with skill.</p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public OBSCURO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.OBSCURO;
        branch = O2MagicBranch.CHARMS;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        durationModifier = 1.0;

        effectTypes.add(PotionEffectType.BLINDNESS);

        initSpell();
    }
}

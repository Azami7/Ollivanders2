package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Confundus Charm: applies the Confusion effect to the target for a skill-scaled duration.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Confundus_Charm">Harry Potter Wiki - Confundus Charm</a>
 */
public final class CONFUNDO extends ConfundoBase {
    static final int minDurationInSecondsConfig = 15;
    static final int maxDurationInSecondsConfig = 120;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CONFUNDO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CONFUNDO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Confundus Charm");
            add("\"Look who's talking. Confunded anyone lately?\" -Harry Potter");
        }};

        text = "Confundo causes the target to become confused.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CONFUNDO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CONFUNDO;
        branch = O2MagicBranch.CHARMS;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        durationModifier = 0.25;

        initSpell();
    }

    /**
     * Set a fixed amplifier of 0; Confundo's strength does not scale with skill.
     */
    @Override
    void calculateAmplifier() {
        amplifier = 0;
    }
}
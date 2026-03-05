package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Confundus Charm that confuses targets.
 *
 * <p>Confundo is a projectile charm that applies Confusion effect to targets, disorienting them
 * for a duration based on the caster's spell level. Duration ranges from 15 to 120 seconds and
 * is calculated at 25% of the caster's skill level modifier. The confusion effect has a fixed
 * amplifier and does not scale with skill level.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Confundus_Charm">Confundus Charm</a>
 */
public final class CONFUNDO extends ConfundoBase {
    static final int minDurationInSecondsConfig = 15;
    static final int maxDurationInSecondsConfig = 120;

    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
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
     * Constructor for casting the spell.
     *
     * <p>Fires a projectile that applies Confusion effect to targets, disorienting them.
     * Duration ranges from 15 to 120 seconds based on caster skill level. The confusion effect
     * has a fixed amplifier that does not scale with skill.</p>
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
        durationModifier = 0.25; // 25%

        initSpell();
    }

    /**
     * Calculate the potion effect amplifier.
     *
     * <p>Confundo uses a fixed amplifier that does not scale with skill level.</p>
     */
    @Override
    void calculateAmplifier() {
        amplifier = 0;
    }
}
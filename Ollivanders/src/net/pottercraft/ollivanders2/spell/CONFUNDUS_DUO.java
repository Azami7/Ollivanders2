package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Confundus Duo, an enhanced version of the Confundus Charm.
 *
 * <p>Confundus Duo is a more powerful variation of the Confundus Charm that applies a stronger
 * Confusion effect to targets. Duration ranges from 30 to 240 seconds (double the duration of
 * Confundo) and is calculated at 50% of the caster's skill level modifier. The confusion effect
 * has an amplifier of 1 (twice as strong as Confundo's amplifier of 0) and does not scale with
 * skill level.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Confundus_Duo">Confundus Duo</a>
 */
public final class CONFUNDUS_DUO extends ConfundoBase {
    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CONFUNDUS_DUO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CONFUNDUS_DUO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Confundus Charm");
            add("The guard was confused. He stared down at the thin, golden Probe and then at his companion, who said in a slightly dazed voice, 'Yeah, you've just checked them, Marius.'");
        }};

        text = "Confundus Duo is a stronger variation of the Confundus Charm. Effects are twice as strong and last twice as long as Confundo.";

    }

    /**
     * Constructor for casting the spell.
     *
     * <p>Fires a projectile that applies a stronger Confusion effect to targets, disorienting them.
     * Duration ranges from 30 to 240 seconds (double Confundo's duration) based on caster skill level.
     * The confusion effect has a fixed amplifier of 1 that does not scale with skill.</p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CONFUNDUS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CONFUNDUS_DUO;
        branch = O2MagicBranch.CHARMS;

        minDurationInSeconds = CONFUNDO.minDurationInSecondsConfig * 2;
        maxDurationInSeconds = CONFUNDO.maxDurationInSecondsConfig * 2;
        durationModifier = 0.5; // 50%

        initSpell();
    }

    /**
     * Calculate the potion effect amplifier.
     *
     * <p>Confundus Duo uses a fixed amplifier of 1 (stronger than Confundo's amplifier of 0)
     * that does not scale with skill level.</p>
     */
    @Override
    void calculateAmplifier() {
        amplifier = 1;
    }
}

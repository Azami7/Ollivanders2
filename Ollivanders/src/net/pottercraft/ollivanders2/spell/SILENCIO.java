package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.MUTED_SPEECH;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Silences a player for a duration depending on the spell's level. The target player can only use nonverbal spells.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Silencing_Charm">https://harrypotter.fandom.com/wiki/Silencing_Charm</a>
 */
public final class SILENCIO extends O2Spell {
    /**
     * The minimum time the target will be silenced for
     */
    private static final int minDuration = 5 * Ollivanders2Common.ticksPerSecond; // 5 seconds

    /**
     * The maximum time the target will be silenced for
     */
    private static final int maxDuration = 2 * Ollivanders2Common.ticksPerMinute; // 2 minutes

    /**
     * The duration the target will be silenced for
     */
    private int duration;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SILENCIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.SILENCIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The raven continued to open and close its sharp beak, but no sound came out.");
            add("The Silencing Charm");
        }};

        text = "Mutes the target for a time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SILENCIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.SILENCIO;
        branch = O2MagicBranch.CHARMS;
        duration = minDuration;

        initSpell();
    }

    /**
     * Set the duration based on the caster's skill.
     */
    @Override
    void doInitSpell() {
        duration = (int) usesModifier * Ollivanders2Common.ticksPerSecond;
        if (duration < minDuration)
            duration = minDuration;
        else if (duration > maxDuration)
            duration = maxDuration;
    }

    /**
     * Find a target player and mute their typing
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
            kill();

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId() == player.getUniqueId())
                continue;

            MUTED_SPEECH effect = new MUTED_SPEECH(p, duration, target.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            kill();
            return;
        }
    }
}
package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Causes the player to be surrounded in smoke and not targetable by projectile spells
 *
 * @since 2.21
 * @author Azami7
 */
public class FUMOS extends O2Effect {
    /**
     * The player this fumos is protecting
     */
    private Player player = null;

    /**
     * The radius of the smoke cloud
     */
    private int radius = 5;

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public FUMOS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.FUMOS;

        player = p.getServer().getPlayer(pid);
        if (player == null) {
            common.printDebugMessage("O2Effect.FUMOS: target player is null", null, null, false);
            kill();
        }
    }

    /**
     * Age this effect each game tick.
     */
    @Override
    public void checkEffect()
    {
        if (!permanent)
        {
            age(1);
        }

        if (isKilled())
            return;

        // flair
        if ((duration % 10) == 0) {
            Ollivanders2Common.flair(player.getLocation(), radius, 10, Particle.CAMPFIRE_COSY_SMOKE);
        }
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {}
}

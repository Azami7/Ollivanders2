package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Makes the target player invisible to all other players for the duration of the effect.
 *
 * <p>When a new player joins while this effect is active, they are also prevented from seeing the target.
 * On removal, visibility is restored for all online players.</p>
 *
 * @author Azami7
 */
public class INVISIBILITY extends O2Effect {
    /**
     * Whether the target player is currently hidden from other players.
     */
    boolean invisible = false;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public INVISIBILITY(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.INVISIBILITY;
        checkDurationBounds();
    }

    /**
     * Age the effect and hide the target player from all online players if not already hidden.
     */
    @Override
    public void checkEffect() {
        // age the effect
        age(1);

        if (!invisible) {
            makeInvisible();
        }
    }

    /**
     * Hide the target player from all currently online players.
     */
    private void makeInvisible() {
        for (Player viewer : p.getServer().getOnlinePlayers()) {
            viewer.hidePlayer(p, target);
        }

        invisible = true;
    }

    /**
     * Hide the target from a newly joined player if the effect is active.
     */
    @Override
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        if (invisible) {
            Player viewer = event.getPlayer();

            viewer.hidePlayer(p, target);
        }
    }

    /**
     * Restore the target player's visibility to all online players.
     */
    @Override
    public void doRemove() {
        for (Player viewer : p.getServer().getOnlinePlayers()) {
            viewer.showPlayer(p, target);
        }

        invisible = false;
    }
}

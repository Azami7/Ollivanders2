package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Tickling effect that forces the affected player into a continuous sneak state.
 *
 * <p>TICKLING is a debilitating effect that causes the target player to be overcome with tickling,
 * forcing them to crouch (sneak) continuously. The player is forced into a sneak state and cannot
 * toggle out of it while the effect is active. Any attempts to stop sneaking are cancelled. When
 * the effect expires, the player is restored to normal standing state. The effect is detectable by
 * both mind-reading spells (Legilimens) and information spells (Informous) which report the target
 * "is doubled-over from tickling".</p>
 *
 * @author Azami7
 */
public class TICKLING extends O2Effect {
    Player target = null;

    /**
     * Constructor for creating a tickling effect.
     *
     * <p>Creates a tickling effect that forces the target player into a sneak state. The sneak state
     * is applied with a 5-tick delay to ensure proper initialization. Detection text is set for
     * information spells and player notifications are configured. If the player is offline when the
     * effect is created, the effect is immediately terminated.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the tickling effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to tickle
     */
    public TICKLING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.TICKLING;
        checkDurationBounds();

        informousText = "is doubled-over from tickling.";
        affectedPlayerText = "You buckle due to excessive tickling.";
    }

    /**
     *
     */
    @Override
    public void checkEffect() {
        // on first pass, keep track of the target player so we do not have to call getPlayer() every tick, and add
        // the laughing effect as well
        if (target == null) {
            Player player = p.getServer().getPlayer(targetID);

            // if player is still null, player not found, kill and return
            if (player == null) {
                kill();
                return;
            }

            Ollivanders2API.getPlayers().playerEffects.addEffect(new LAUGHING(p, -1, true, targetID));
        }

        // age this effect
        age(1);

        // every 5 seconds, make the player sneak to "double-over" with laughter
        if ((duration % (5 * Ollivanders2Common.ticksPerSecond)) == 0) {
            target.setSneaking(true);
        }
    }

    /**
     * Prevent the tickled player from toggling out of sneak.
     *
     * <p>Cancels any sneak toggle events from the affected player, ensuring they remain locked in the
     * sneak state for the duration of the tickling effect.</p>
     *
     * @param event the sneak toggle event to cancel
     */
    @Override
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     *
     */
    @Override
    public void doRemove() {
        Player player = p.getServer().getPlayer(targetID);

        // remove laughing effect
        Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.LAUGHING);
    }
}

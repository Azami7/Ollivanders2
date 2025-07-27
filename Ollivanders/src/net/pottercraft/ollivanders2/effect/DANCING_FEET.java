package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Make a player move around uncontrollably
 *
 * @since 2.21
 * @author Azami7
 */
public class DANCING_FEET extends O2Effect {
    /**
     * The movement cooldown counter
     */
    int cooldownCounter = 0;

    /**
     * The movement cooldown - half second
     */
    final int cooldown = Ollivanders2Common.ticksPerSecond / 2;

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public DANCING_FEET(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.DANCING_FEET;
        informousText = "cannot control their feet.";
        affectedPlayerText = "You start dancing.";
    }

    /**
     * Age this effect each game tick.
     */
    @Override
    public void checkEffect() {
        // age this effect
        age(1);

        // decrement cooldown counter
        if (cooldownCounter > 0)
           cooldownCounter = cooldownCounter - 1;
        // move the player if the cooldown counter is complete
        else {
            Player player = p.getServer().getPlayer(targetID);
            if (player == null) {
                common.printDebugMessage("DANCING_FEET.checkEffect(): player is null", null, null, true);
                kill();
                return;
            }

            cooldownCounter = cooldown;

            // toggle sneaking
            if (player.isSneaking()) {
                player.setSneaking(false);
            }
            else {
                player.setSneaking(true);
                return;
            }

            // pick a random direction to face
            int rand = Math.abs(Ollivanders2Common.random.nextInt() % 360);
            float currentYaw = player.getLocation().getYaw();
            Location newLocation = player.getLocation();
            newLocation.setYaw(currentYaw + rand);

            // teleport the player 5 ticks later so that their movements do not seem to all happen at once
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(newLocation);
                }
            }.runTaskLater(p, 5);
        }
    }

    /**
     * Cancel any sneak toggle events
     *
     * @param event the event
     */
    @Override
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancel any sprint toggle events
     *
     * @param event the event
     */
    @Override
    void doOnPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancel any player move events.
     *
     * @param event the event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * No cleanup for this spell
     */
    @Override
    public void doRemove() { }
}

package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Causes the player to be surrounded in smoke and not targetable by projectile spells
 *
 * @author Azami7
 * @since 2.21
 */
public class FUMOS extends O2Effect {
    /**
     * The player this fumos is protecting
     */
    Player player;

    /**
     * The radius of the smoke cloud
     */
    int radius = 3;

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
    public void checkEffect() {
        if (!permanent) {
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
    public void doRemove() {
    }

    /**
     * handle any effects when a spell projectile moves
     *
     * @param event the event
     */
    void doOnOllivandersSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        Location projectileLocation = event.getTo();

        if (Ollivanders2Common.isInside(projectileLocation, player.getLocation(), radius)) {
            boolean canceled = true;

            // if years enabled, this spell can only protect against spells up to one level higher than this spell
            if (Ollivanders2.useYears) {
                O2Spell spell = event.getSpell();
                if (spell.spellType.getLevel().ordinal() > effectType.getLevel().ordinal() + 1)
                    canceled = false;
            }

            event.setCancelled(canceled);
        }
    }

    /**
     * handle on player quit event
     *
     * @param event the event
     */
    void doOnPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId() == targetID)
            kill();
    }

    /**
     * handle entity target event
     *
     * @param event the event
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (target == null)
            return;

        if (target.getUniqueId() == targetID)
            event.setCancelled(true);
    }
}

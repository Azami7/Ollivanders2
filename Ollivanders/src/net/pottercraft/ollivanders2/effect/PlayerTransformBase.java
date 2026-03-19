package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Base class for effects that transform a player into a creature.
 *
 * <p>Spawns an entity of the configured type at the player's location, hides the player via an
 * INVISIBILITY effect, and mounts the player as a passenger on the spawned entity. The entity's
 * AI controls all movement while the player rides along. On removal, the entity is despawned
 * and the player is made visible again.</p>
 *
 * <p>Event handling while transformed:</p>
 * <ul>
 * <li>Damage to the spawned entity is forwarded to the player and ends the effect</li>
 * <li>Death of the spawned entity deals half the player's health and ends the effect</li>
 * <li>Teleportation and apparition are blocked</li>
 * <li>Dismounting and vehicle exit are blocked</li>
 * </ul>
 *
 * <p>Subclasses must set {@code entityType} in their constructor and may override
 * {@link #setAnimalVariant()} for entity types with variants.</p>
 *
 * @author Azami7
 */
abstract public class PlayerTransformBase extends O2Effect {
    /**
     * The entity type to spawn for the transformation.
     */
    EntityType entityType = null;

    /**
     * The entity spawned to represent the transformed player.
     */
    LivingEntity spawnedEntity = null;

    /**
     * Whether the player has been transformed (entity spawned and player mounted).
     */
    boolean transformed = false;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public PlayerTransformBase(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);
    }

    /**
     * Age the effect and, on the first tick, spawn the entity and mount the player on it.
     */
    @Override
    public void checkEffect()  {
        age(1);

        if (!transformed) {
            spawnEntity();
            setAnimalVariant();

            // rename the entity the player's name
            spawnedEntity.setCustomName(target.getDisplayName());
            spawnedEntity.setCustomNameVisible(true);

            // hide the player from all players
            INVISIBILITY invisibility = new INVISIBILITY(p, duration + 10, true, targetID);
            Ollivanders2API.getPlayers().playerEffects.addEffect(invisibility);

            // force mount the player on the target
            target.leaveVehicle(); // in case they are in a vehicle or riding an entity already

            spawnedEntity.addPassenger(target);

            transformed = true;
        }
    }

    /**
     * Spawn the entity type at the target player's location. Kills the effect if entityType is null.
     */
    void spawnEntity() {
        // spawn the entity type
        if (entityType == null) {
            common.printDebugMessage("PlayerTransformBase.checkEffect: entityType is null", null, null, false);
            kill();
            return;
        }
        World world = target.getWorld();
        spawnedEntity = (LivingEntity) world.spawnEntity(target.getLocation(), entityType);
    }

    /**
     * Set the variant of the animal type, if it has variants. This must be overriden by child classes that set animal
     * types with variants.
     */
    void setAnimalVariant() {}

    /**
     * Dismount the player, despawn the entity, and remove the INVISIBILITY effect.
     */
    @Override
    public void doRemove() {
        // dismount player
        spawnedEntity.removePassenger(target);

        // despawn entity
        spawnedEntity.remove();

        // make player visible to all players again
        Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.INVISIBILITY);
    }

    /**
     * Forward damage to the player and end the effect if the spawned entity is damaged.
     * Uses a delayed task to allow other handlers to cancel the event first.
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity.getUniqueId().equals(spawnedEntity.getUniqueId())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!event.isCancelled()) {
                        target.damage(event.getDamage()); // damage the player by the amount the entity was damaged by
                        kill(); // end the effect
                    }
                }
            }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
        }
    }

    /**
     * Deal half the player's health in damage and end the effect if the spawned entity dies.
     */
    @Override
    void doOnEntityDeathEvent(@NotNull EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity.getUniqueId().equals(spawnedEntity.getUniqueId())) {
            target.damage(target.getHealth() / 2); // damage the player by half their current health
            kill(); // end the effect
        }
    }

    /**
     * Cancel teleportation for the transformed player.
     */
    @Override
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID))
            event.setCancelled(true);
    }

    /**
     * Cancel apparition by coordinates for the transformed player.
     */
    @Override
    void doOnOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID))
            event.setCancelled(true);
    }

    /**
     * Cancel apparition by name for the transformed player.
     */
    @Override
    void doOnOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID))
            event.setCancelled(true);
    }

    /**
     * Cancel dismounting from the spawned entity. Only active after transformation is complete so
     * the initial leaveVehicle call in checkEffect is not blocked.
     */
    @Override
    void doOnEntityDismountEvent(@NotNull EntityDismountEvent event) {
        if (transformed) {
            if (event.getDismounted().getUniqueId().equals(spawnedEntity.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancel vehicle exit for the transformed player. Only active after transformation is complete
     * so the initial leaveVehicle call in checkEffect is not blocked.
     */
    @Override
    void doOnVehicleExitEvent(@NotNull VehicleExitEvent event) {
        if (transformed) {
            if (event.getExited().getUniqueId().equals(targetID)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Get the entity type used for the transformation.
     *
     * @return the entity type
     */
    public EntityType getEntityType() {
        return entityType;
    }
}

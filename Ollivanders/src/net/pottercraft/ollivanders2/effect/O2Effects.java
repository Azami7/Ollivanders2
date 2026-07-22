package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * Central manager for all magical effects applied to players, handling their full lifecycle: adding, per-tick
 * processing, removal, and persistence across logout. Registers as a Bukkit {@link Listener} and distributes server
 * events to active effects.
 * <p>
 * Effects for online players are held as live {@link O2Effect} objects; when a player logs out they move to a saved
 * map keyed by type and duration, restored on login. Adding an effect a player already has stacks the durations
 * rather than replacing it. Access to both maps is guarded by a shared {@link Semaphore} via the inner
 * {@code EffectsData} for thread-safe use from the scheduler and event handlers.
 * </p>
 *
 * @author Azami7
 * @see O2Effect
 * @see O2EffectType
 */
public class O2Effects implements Listener {
    /**
     * Reference to the plugin for server API access and configuration.
     */
    final Ollivanders2 p;

    /**
     * Common utility functions for the plugin.
     */
    final Ollivanders2Common common;

    /**
     * Guards access to the {@code EffectsData} maps; shared across all instances so locking is consistent.
     */
    final static Semaphore semaphore = new Semaphore(1);

    /**
     * Serialization label for the target player id.
     */
    public static final String effectPIDLabel = "Effect_target_pid";

    /**
     * Serialization label for isPermanent
     */
    public static final String effectPermanentLabel = "Effect_is_permanent";

    /**
     * Serialization label for duration
     */
    public static final String effectDurationLabel = "Effect_duration";

    /**
     * Serialization label for effectType
     */
    public static final String effectTypeLabel = "Effect_type";

    /**
     * Semaphore-guarded storage for active effects (online players) and saved effects (offline players).
     */
    private class EffectsData {
        /**
         * A list of all active effects on all online players
         */
        final private Map<UUID, Map<O2EffectType, O2Effect>> activeEffects = new HashMap<>();

        /**
         * A list of all saved effects for all offline players
         */
        final private Map<UUID, Map<O2EffectType, O2Effect>> savedEffects = new HashMap<>();

        /**
         * Constructor
         */
        EffectsData() {
        }

        /**
         * Get a copy of the effects active on this player.
         *
         * @param pid the id of the player
         * @return a map of all the effects on this player
         */
        @NotNull
        synchronized Map<O2EffectType, O2Effect> getPlayerActiveEffects(@NotNull UUID pid) {
            Map<O2EffectType, O2Effect> effects = new HashMap<>();

            try {
                semaphore.acquire();

                if (activeEffects.containsKey(pid))
                    effects = new HashMap<>(activeEffects.get(pid));
            }
            catch (Exception e) {
                common.printDebugMessage("O2Effects.getPlayerActiveEffects: failed to acquire mutex in getPlayerActiveEffects", e, null, true);
            }
            finally {
                semaphore.release();
            }

            return effects;
        }

        /**
         * Get all active effects across all players.
         *
         * @return the active effects for all players
         */
        @NotNull
        synchronized ArrayList<O2Effect> getAllActiveEffects() {
            ArrayList<O2Effect> effects = new ArrayList<>();

            for (Map<O2EffectType, O2Effect> playerEffects : activeEffects.values()) {
                effects.addAll(playerEffects.values());
            }

            return effects;
        }

        /**
         * Get an effect object for a player.
         *
         * @param pid        the id of the player
         * @param effectType the effect type to search for
         * @return the effect object if it was found
         */
        @Nullable
        synchronized O2Effect getActiveEffect(@NotNull UUID pid, @NotNull O2EffectType effectType) {
            O2Effect effect = null;

            try {
                semaphore.acquire();

                if (activeEffects.containsKey(pid)) {
                    Map<O2EffectType, O2Effect> effects = activeEffects.get(pid);
                    if (effects.containsKey(effectType))
                        effect = effects.get(effectType);
                }
            }
            catch (Exception e) {
                common.printDebugMessage("O2Effects.getActiveEffect: failed to acquire mutex in getActiveEffect", e, null, true);
            }
            finally {
                semaphore.release();
            }

            return effect;
        }

        /**
         * Get a copy of the effects saved for this player.
         *
         * @param pid the id of the player
         * @return a map of all the saved effects for this player
         */
        @NotNull
        synchronized Map<O2EffectType, O2Effect> getPlayerSavedEffects(@NotNull UUID pid) {
            Map<O2EffectType, O2Effect> effects = new HashMap<>();

            try {
                semaphore.acquire();

                if (savedEffects.containsKey(pid))
                    effects = new HashMap<>(savedEffects.get(pid));
            }
            catch (Exception e) {
                common.printDebugMessage("O2Effects.getPlayerSavedEffects: failed to acquire mutex in getPlayerSavedEffects", e, null, true);
            }
            finally {
                semaphore.release();
            }

            return effects;
        }

        /**
         * Update the map of saved effects for this player.
         *
         * @param pid     the id of the player
         * @param effects the map of effects and durations
         */
        synchronized void updatePlayerSavedEffects(@NotNull UUID pid, @NotNull Map<O2EffectType, O2Effect> effects) {
            try {
                semaphore.acquire();
                savedEffects.remove(pid);
                savedEffects.put(pid, effects);
            }
            catch (Exception e) {
                common.printDebugMessage("O2Effects.updatePlayerSavedEffects: failed to acquire mutex in updatePlayerSavedEffects", e, null, true);
            }
            finally {
                semaphore.release();
            }
        }

        /**
         * Update the map of active effects for this player.
         *
         * @param pid     the id of the player
         * @param effects the map of effects and durations
         */
        synchronized void updatePlayerActiveEffects(@NotNull UUID pid, @NotNull Map<O2EffectType, O2Effect> effects) {
            try {
                semaphore.acquire();
                activeEffects.remove(pid);
                activeEffects.put(pid, effects);
            }
            catch (Exception e) {
                common.printDebugMessage("O2Effects.updatePlayerActiveEffects: failed to acquire mutex in updatePlayerActiveEffects", e, null, true);
            }
            finally {
                semaphore.release();
            }
        }

        /**
         * Clear all effects for this player.
         *
         * @param pid the id of the player
         */
        synchronized void resetEffects(@NotNull UUID pid) {
            try {
                semaphore.acquire();

                if (activeEffects.containsKey(pid)) {
                    Map<O2EffectType, O2Effect> playerEffects = activeEffects.get(pid);

                    for (O2Effect effect : playerEffects.values()) {
                        effect.kill();
                    }
                }

                savedEffects.remove(pid);
            }
            catch (Exception e) {
                common.printDebugMessage("O2Effects.resetEffects: failed to acquire mutex in resetEffects", e, null, true);
            }
            finally {
                semaphore.release();
            }
        }

        /**
         * Kill every active effect on every player and discard all saved effects. Destructive global reset, intended
         * for test cleanup or server-wide resets.
         *
         * @see #resetEffects(UUID) for clearing effects for a single player
         */
        synchronized void killAllEffects() {
            try {
                semaphore.acquire();

                // clear saved effects, they are not in use while here since they are for offline players
                savedEffects.clear();

                for (Map<O2EffectType, O2Effect> effects : activeEffects.values()) {
                    for (O2Effect effect : effects.values()) {
                        // kill every effect on every player
                        effect.kill();
                    }
                }
            }
            catch (Exception e) {
                common.printDebugMessage("O2Effects.resetAllEffects: failed to acquire mutex in resetAllEffects", e, null, true);
            }
            finally {
                semaphore.release();
            }
        }
    }

    /**
     * The data for all active effects
     */
    EffectsData effectsData = new EffectsData();

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public O2Effects(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);

        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Initialize the effects system on plugin startup: load saved effects and apply per-effect config toggles.
     */
    public void onEnable() {
        loadEffects();

        O2EffectType.LYCANTHROPY.setEnabled(p.getConfig().getBoolean("enableLycanthropy"));
        if (O2EffectType.LYCANTHROPY.isEnabled())
            p.getLogger().info("Enabling lycanthropy.");
        else
            p.getLogger().info("Disabling lycanthropy.");
    }

    /**
     * Dispatch this event to the active effects of both the damaged entity and the damager. The damaged entity is
     * processed first so a protective effect can cancel the damage before the damager's effects react.
     *
     * @param event the entity damage by entity event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        // process the affected player first so a protective effect can cancel this damage before the damager reacts
        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(entity.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnEntityDamageByEntityEvent(event);
        }

        // was this event caused by someone with an effect (like lycanthropy)
        activeEffects = effectsData.getPlayerActiveEffects(damager.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnEntityDamageByEntityEvent(event);
        }
    }

    /**
     * Dispatch this event to the interacting player's active effects.
     *
     * @param event the player interact event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerInteractEvent(event);
        }
    }

    /**
     * Dispatch this event to the chatting player's active effects.
     *
     * @param event the async player chat event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnAsyncPlayerChatEvent(event);
        }
    }

    /**
     * Dispatch this event to the player's active effects.
     *
     * @param event the player bed enter event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedEnterEvent(@NotNull PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerBedEnterEvent(event);
        }
    }

    /**
     * Dispatch this event to the player's active effects.
     *
     * @param event the player toggle flight event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerToggleFlightEvent(event);
        }
    }

    /**
     * Dispatch this event to the player's active effects.
     *
     * @param event the player toggle sneak event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerToggleSneakEvent(event);
        }
    }

    /**
     * Dispatch this event to the player's active effects.
     *
     * @param event the player toggle sprint event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerToggleSprintEvent(event);
        }
    }

    /**
     * Dispatch this event to the affected player's active effects.
     *
     * @param event the player velocity event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerVelocityEvent(@NotNull PlayerVelocityEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerVelocityEvent(event);
        }
    }

    /**
     * Dispatch this event to the picking-up player's active effects; ignored for non-player entities.
     *
     * @param event the entity pickup item event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPickupItemEvent(@NotNull EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(entity.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerPickupItemEvent(event);
        }
    }

    /**
     * Dispatch this event to the player's active effects.
     *
     * @param event the player item held event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemHeldEvent(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerItemHeldEvent(event);
        }
    }

    /**
     * Dispatch this event to the consuming player's active effects.
     *
     * @param event the player item consume event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemConsumeEvent(@NotNull PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerItemConsumeEvent(event);
        }
    }

    /**
     * Dispatch this event to the dropping player's active effects.
     *
     * @param event the player drop item event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItemEvent(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerDropItemEvent(event);
        }
    }

    /**
     * Dispatch this event to the moving player's active effects.
     *
     * @param event the player move event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerMoveEvent(event);
        }
    }

    /**
     * Dispatch this event to every active effect on all players.
     *
     * @param event the Ollivanders spell projectile move event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onOllivandersSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        for (Map<O2EffectType, O2Effect> activeEffects : effectsData.activeEffects.values()) {
            for (O2Effect effect : activeEffects.values()) {
                effect.doOnOllivandersSpellProjectileMoveEvent(event);
            }
        }
    }

    /**
     * Dispatch this event to the targeted entity's active effects; no-op if the target is null.
     *
     * @param event the entity target event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityTargetEvent(@NotNull EntityTargetEvent event) {
        Entity entity = event.getTarget();

        if (entity == null) {
            common.printDebugMessage("O2Effects: target entity is null", null, null, true);
            return;
        }

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(entity.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnEntityTargetEvent(event);
        }
    }

    /**
     * Dispatch this event to the departing player's active effects.
     *
     * @param event the player quit event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerQuitEvent(event);
        }

        onQuit(event.getPlayer().getUniqueId());
    }

    /**
     * Dispatch this event to every active effect on all players.
     *
     * @param event the player join event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        onJoin(event.getPlayer().getUniqueId());

        for (O2Effect effect : effectsData.getAllActiveEffects()) {
            effect.doOnPlayerJoinEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeathEvent(@NotNull EntityDeathEvent event) {
        for (O2Effect effect : effectsData.getAllActiveEffects()) {
            effect.doOnEntityDeathEvent(event);
        }

        onDeath(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageEvent(@NotNull EntityDamageEvent event) {
        for (O2Effect effect : effectsData.getAllActiveEffects()) {
            effect.doOnEntityDamageEvent(event);
        }
    }

    /**
     * Dispatch this event to every active effect on all players.
     *
     * @param event the projectile launch event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        for (O2Effect effect : effectsData.getAllActiveEffects()) {
            effect.doOnProjectileLaunchEvent(event);
        }
    }

    /**
     * Dispatch this event to every active effect on all players.
     *
     * @param event the projectile hit event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        for (O2Effect effect : effectsData.getAllActiveEffects()) {
            effect.doOnProjectileHitEvent(event);
        }
    }

    /**
     * Distribute player teleport events to all active effects on all players.
     *
     * @param event the player teleport event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerTeleportEvent(event);
        }
    }

    /**
     * Distribute apparate-by-coordinates events to all active effects on all players.
     *
     * @param event the apparate by coordinates event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnOllivandersApparateByCoordinatesEvent(event);
        }
    }

    /**
     * Distribute apparate-by-name events to all active effects on all players.
     *
     * @param event the apparate by name event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnOllivandersApparateByNameEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDismountEvent(@NotNull EntityDismountEvent event) {
        for (O2Effect effect : effectsData.getAllActiveEffects()) {
            effect.doOnEntityDismountEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleExitEvent(@NotNull VehicleExitEvent event) {
        for (O2Effect effect : effectsData.getAllActiveEffects()) {
            effect.doOnVehicleExitEvent(event);
        }
    }

    /**
     * Get the types of all effects currently active on a player.
     *
     * @param pid the unique ID of the player
     * @return the active effect types, empty if none
     */
    @NotNull
    public List<O2EffectType> getEffects(@NotNull UUID pid) {
        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        return new ArrayList<>(playerEffects.keySet());
    }

    /**
     * Check whether a player has a specific effect active. Saved effects for offline players are not checked.
     *
     * @param pid        the unique ID of the player to check
     * @param effectType the effect type to look for
     * @return true if the player has this effect active
     */
    public boolean hasEffect(@NotNull UUID pid, @NotNull O2EffectType effectType) {
        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        return playerEffects.containsKey(effectType);
    }

    /**
     * Check whether a player has any effect active. Saved effects for offline players are not considered.
     *
     * @param pid the unique ID of the player to check
     * @return true if the player has any active effects
     */
    public boolean hasEffects(@NotNull UUID pid) {
        return !effectsData.getPlayerActiveEffects(pid).isEmpty();
    }

    /**
     * Restore a joining player's saved effects (each keeping its remaining duration) and clear them from saved
     * storage.
     *
     * @param pid the unique ID of the joining player
     */
    public synchronized void onJoin(@NotNull UUID pid) {
        Map<O2EffectType, O2Effect> savedEffects = effectsData.getPlayerSavedEffects(pid);
        if (savedEffects.isEmpty())
            return;

        for (O2Effect effect : savedEffects.values()) {
            addEffect(effect);
        }

        effectsData.updatePlayerSavedEffects(pid, new HashMap<>());
    }

    /**
     * Save a departing player's active effects for restoration on rejoin.
     *
     * @param pid the unique ID of the departing player
     */
    public synchronized void onQuit(@NotNull UUID pid) {
        moveActiveEffectsToSaved(pid);
    }

    /**
     * Move a player's active effects into saved storage, dropping any killed effects, and clear their active map.
     *
     * @param pid the unique ID of the player whose effects should be saved
     */
    private void moveActiveEffectsToSaved(@NotNull UUID pid) {
        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        if (activeEffects.isEmpty())
            return;

        HashMap<O2EffectType, O2Effect> savedEffects = new HashMap<>();
        for (Entry<O2EffectType, O2Effect> entry : activeEffects.entrySet()) {
            if (!entry.getValue().isKilled())
                savedEffects.put(entry.getKey(), entry.getValue());
        }

        effectsData.updatePlayerSavedEffects(pid, savedEffects);
        effectsData.updatePlayerActiveEffects(pid, new HashMap<>());
    }

    /**
     * Kill all of a dead player's active effects and discard their saved effects so they respawn clean.
     *
     * @param pid the unique ID of the deceased player
     */
    public synchronized void onDeath(@NotNull UUID pid) {
        effectsData.resetEffects(pid);
    }

    /**
     * Persist all effects to disk when the plugin is disabled.
     */
    public void onDisable() {
        p.getLogger().info("Saving players effects.");
        saveEffects();
    }

    /**
     * Serialize all active and saved effects across all players and write them to the effects JSON file.
     */
    public void saveEffects() {
        List<Map<String, String>> effects = serializeEffects();

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeSaveData(effects, GsonDAO.o2EffectsJSONFile);
    }

    /**
     * Load saved effects from the effects JSON file and re-add them to their target players. No-op if the file is
     * missing or empty.
     */
    public void loadEffects() {
        GsonDAO gsonLayer = new GsonDAO();
        List<Map<String, String>> effects = gsonLayer.readSavedDataListMap(GsonDAO.o2EffectsJSONFile);

        int count = 0;

        if (effects == null) {
            p.getLogger().info("No saved effects.");
            return;
        }

        for (Map<String, String> effectAttributes : effects) {
            O2Effect effect = deserializeEffect(effectAttributes);

            if (effect != null) {
                addEffect(effect);
                count = count + 1;
            }
        }

        p.getLogger().info("Loaded " + count + " saved effects.");
    }

    /**
     * Serialize every active and saved effect across all players into attribute maps for persistence.
     *
     * @return one serialized attribute map per effect
     */
    @NotNull
    public List<Map<String, String>> serializeEffects() {
        List<Map<String, String>> serializedEffects = new ArrayList<>();

        List<Map<O2EffectType, O2Effect>> allEffects = getAllEffects();
        for (Map<O2EffectType, O2Effect> effectMap : allEffects) {
            for (O2Effect effect : effectMap.values()) {
                Map<String, String> serializedEffect = serializeEffect(effect);

                serializedEffects.add(serializedEffect);
            }
        }

        return serializedEffects;
    }

    /**
     * Get the effect map for every player: active effects if online, otherwise saved effects. A player never has
     * both at once.
     *
     * @return one effect map per player
     */
    @NotNull
    private List<Map<O2EffectType, O2Effect>> getAllEffects() {
        List<Map<O2EffectType, O2Effect>> effects = new ArrayList<>();

        for (UUID playerID : Ollivanders2API.getPlayers().getPlayerIDs()) {
            Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(playerID);

            if (!activeEffects.isEmpty()) {
                effects.add(activeEffects);
            }
            // a player has either active or saved effects, never both
            else
                // either put the saved effects or, if the player has no effects, an empty map
                effects.add(effectsData.getPlayerSavedEffects(playerID));
        }

        return effects;
    }

    /**
     * Serialize a single effect to a string attribute map keyed by the effect serialization labels.
     *
     * @param effect the effect to serialize
     * @return the effect's type, target id, permanent flag, and remaining duration as strings
     */
    private Map<String, String> serializeEffect(@NotNull O2Effect effect) {
        Map<String, String> effectAttributes = new HashMap<>();

        effectAttributes.put(effectTypeLabel, effect.effectType.toString());
        effectAttributes.put(effectPIDLabel, effect.getTargetID().toString());
        effectAttributes.put(effectPermanentLabel, Boolean.toString(effect.isPermanent()));
        effectAttributes.put(effectDurationLabel, Integer.toString(effect.getRemainingDuration()));

        return effectAttributes;
    }

    /**
     * Reconstruct an effect from a serialized attribute map. Returns null if any required field (type, target id,
     * duration, permanent flag) is missing or invalid.
     *
     * @param effectAttributes the serialized effect attributes, keyed by the effect serialization labels
     * @return the reconstructed effect, or null if deserialization fails
     */
    @Nullable
    public O2Effect deserializeEffect(@NotNull Map<String, String> effectAttributes) {
        UUID targetID = null;
        Integer duration = null;
        Boolean isPermanent = null;
        O2EffectType effectType = null;

        for (Map.Entry<String, String> entry : effectAttributes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            try {
                switch (key) {
                    case String s when s.equals(effectTypeLabel) -> effectType = O2EffectType.valueOf(value);
                    case String s when s.equals(effectDurationLabel) -> duration = Integer.valueOf(value);
                    case String s when s.equals(effectPermanentLabel) -> isPermanent = Boolean.valueOf(value);
                    case String s when s.equals(effectPIDLabel) -> targetID = UUID.fromString(value);
                    default -> {
                    } // ignore unknown fields
                }
            }
            catch (Exception e) {
                common.printDebugMessage("Failure reading saved effect data.", e, null, true);
            }
        }

        // only try to create the effect if all required fields were present
        if (effectType != null && targetID != null && duration != null && isPermanent != null) {
            return createEffectFromSavedData(effectType, targetID, duration, isPermanent);
        }

        return null;
    }

    /**
     * Instantiate the effect class for the given type by reflection, invoking its standard constructor. Returns null
     * if reflection fails.
     *
     * @param effectType  the effect type to instantiate
     * @param targetID    the unique ID of the affected player
     * @param duration    the remaining duration in game ticks
     * @param isPermanent whether the effect should not expire
     * @return the instantiated effect, or null if creation fails
     */
    @Nullable
    private O2Effect createEffectFromSavedData(@NotNull O2EffectType effectType, @NotNull UUID targetID, @NotNull Integer duration, @NotNull Boolean isPermanent) {
        O2Effect effect;

        Class<?> effectClass = effectType.getClassName();
        try {
            effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, boolean.class, UUID.class).newInstance(p, duration, isPermanent, targetID);
        }
        catch (Exception e) {
            common.printDebugMessage("Exception trying to create new instance of " + effectType, e, null, true);
            return null;
        }

        return effect;
    }

    /**
     * Add an effect to its target player. Disabled effect types are silently rejected. If the target is offline the
     * effect is saved for restoration on rejoin. A {@link ShapeShift} effect is rejected if the player already has
     * one. Adding a type the player already has stacks the durations rather than replacing it. Any affected-player
     * message is sent on a 5-tick delay.
     *
     * @param effect the effect to add
     */
    public synchronized void addEffect(@NotNull O2Effect effect) {
        if (!effect.effectType.isEnabled())
            return;

        UUID pid = effect.getTargetID();

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(pid);
        if (o2p == null)
            return;

        if (!o2p.isOnline()) {
            addSavedEffect(effect);
            return;
        }

        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        // shape-shift effects are mutually exclusive; reject a new one if the player already has any
        if (effect instanceof ShapeShift) {
            for (O2Effect ef : playerEffects.values()) {
                if (ef instanceof ShapeShift) {
                    return;
                }
            }
        }

        if (playerEffects.containsKey(effect.effectType)) {
            // stack durations rather than replacing an existing effect of the same type
            O2Effect ef = playerEffects.get(effect.effectType);
            effect.duration += ef.duration;
            playerEffects.replace(effect.effectType, effect);
        }
        else {
            playerEffects.put(effect.effectType, effect);
        }

        effectsData.updatePlayerActiveEffects(pid, playerEffects);
        common.printDebugMessage("Added effect " + effect.effectType.toString() + " to " + pid, null, null, false);

        String affectedPlayerMessage = effect.getAffectedPlayerText();
        if (affectedPlayerMessage != null) {
            Player player = p.getServer().getPlayer(pid);
            if (player == null) {
                common.printDebugMessage("O2Effects.addEffect: player is null", null, null, true);
                effect.kill();
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(Ollivanders2.chatColor + affectedPlayerMessage);
                }
            }.runTaskLater(p, 5);
        }
    }

    /**
     * Store an effect in an offline player's saved effects, to be restored on rejoin via {@link #onJoin(UUID)}.
     *
     * @param effect the effect to save
     */
    private void addSavedEffect(@NotNull O2Effect effect) {
        UUID pid = effect.getTargetID();

        Map<O2EffectType, O2Effect> savedEffects = effectsData.getPlayerSavedEffects(pid);
        savedEffects.put(effect.effectType, effect);

        effectsData.updatePlayerSavedEffects(pid, savedEffects);
    }

    /**
     * Remove an effect from a player: kill it, run its {@link O2Effect#doRemove()} cleanup, and drop it from the
     * active map. No-op (with a debug log) if the player does not have the effect.
     *
     * @param pid        the unique ID of the player whose effect should be removed
     * @param effectType the type of effect to remove
     */
    public synchronized void removeEffect(@NotNull UUID pid, @NotNull O2EffectType effectType) {
        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        O2Effect effect = playerEffects.get(effectType);

        if (effect != null) {
            effect.kill();
            playerEffects.get(effectType).doRemove();
            playerEffects.remove(effectType);
        }
        else
            common.printDebugMessage("O2Effects.removeEffect: effect to remove is null.", null, null, false);

        effectsData.updatePlayerActiveEffects(pid, playerEffects);
        common.printDebugMessage("Removed effect " + effectType + " from " + pid, null, null, false);
    }

    /**
     * Get the active effect object of a given type on a player, for direct access to its state and duration.
     *
     * @param pid        the unique ID of the player
     * @param effectType the type of effect to get
     * @return the active effect, or null if the player does not have it
     * @see #hasEffect(UUID, O2EffectType) for a presence check without retrieving the object
     */
    public synchronized O2Effect getEffect(@NotNull UUID pid, @NotNull O2EffectType effectType) {
        return effectsData.getActiveEffect(pid, effectType);
    }

    /**
     * Run one tick of upkeep for a player's active effects: call {@link O2Effect#checkEffect()} on each, then
     * remove any that are killed or whose type has been disabled.
     *
     * @param pid the unique ID of the player whose effects should be processed
     */
    public void upkeep(@NotNull UUID pid) {
        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);

        for (O2Effect effect : activeEffects.values()) {
            effect.checkEffect();

            if (effect.isKilled() || !effect.effectType.isEnabled())
                removeEffect(pid, effect.effectType);
        }
    }

    /**
     * Age every non-permanent effect on a player by the given number of ticks; permanent effects are skipped.
     *
     * @param pid    the unique ID of the player whose effects should be aged
     * @param amount the number of ticks to subtract from each non-permanent effect's duration
     * @see #ageEffect(UUID, O2EffectType, int) for aging a specific effect
     * @see #ageEffectByPercent(UUID, O2EffectType, int) for aging by percentage instead of absolute amount
     */
    public void ageAllEffects(@NotNull UUID pid, int amount) {
        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        Collection<O2Effect> effects = activeEffects.values();

        for (O2Effect effect : effects) {
            if (!effect.isPermanent())
                effect.age(amount);
        }

        effectsData.updatePlayerActiveEffects(pid, activeEffects);
    }

    /**
     * Age a specific effect on a player by the given number of ticks. No-op if the player does not have it.
     * Unlike {@link #ageAllEffects(UUID, int)}, this does not skip permanent effects.
     *
     * @param pid        the unique ID of the player whose effect should be aged
     * @param effectType the type of effect to age
     * @param amount     the number of ticks to subtract from the effect's duration
     * @see #ageEffectByPercent(UUID, O2EffectType, int) for aging by percentage instead of absolute amount
     */
    public void ageEffect(@NotNull UUID pid, @NotNull O2EffectType effectType, int amount) {
        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);

        if (activeEffects.containsKey(effectType)) {
            O2Effect effect = activeEffects.get(effectType);
            effect.duration = effect.duration - amount;
        }

        effectsData.updatePlayerActiveEffects(pid, activeEffects);
    }

    /**
     * Age a specific effect on a player by a percentage of its current duration. Permanent effects are skipped, and
     * a missing effect is a no-op. {@code percent} is clamped to a minimum of 1; a value of 100 or more sets the
     * duration to 0.
     *
     * @param pid        the unique ID of the player whose effect should be aged
     * @param effectType the type of effect to age by percentage
     * @param percent    the percentage of the current duration to subtract
     * @see #ageEffect(UUID, O2EffectType, int) for aging by absolute amount instead of percentage
     */
    public void ageEffectByPercent(@NotNull UUID pid, @NotNull O2EffectType effectType, int percent) {
        if (percent < 1)
            percent = 1;

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        if (activeEffects.containsKey(effectType)) {
            O2Effect effect = activeEffects.get(effectType);

            if (!effect.isPermanent()) {
                if (percent >= 100) {
                    effect.duration = 0;
                }
                else {
                    double durationDouble = effect.duration;
                    double percentReduction = ((double) percent / 100);

                    double reduction = durationDouble * percentReduction;
                    effect.duration = effect.duration - (int) reduction;
                }
            }
        }

        effectsData.updatePlayerActiveEffects(pid, activeEffects);
    }

    /**
     * Get the Informous detection text of the first Informous-detectable effect active on a player.
     *
     * @param pid the unique ID of the player to check
     * @return the informousText of the first detectable effect, or null if none is detectable
     * @see #detectEffectWithLegilimens(UUID) for Legilimens detection
     * @see net.pottercraft.ollivanders2.spell.INFORMOUS for the spell that uses this method
     */
    @Nullable
    public String detectEffectWithInformous(@NotNull UUID pid) {
        common.printDebugMessage("O2Effects.detectEffectWithInformous: detecting effects with Informous", null, null, false);
        String infoText = null;

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        Collection<O2Effect> effects = activeEffects.values();

        common.printDebugMessage("O2Effects.detectEffectWithInformous: found " + activeEffects.size() + " active effects", null, null, false);

        for (O2Effect effect : effects) {
            common.printDebugMessage("O2Effects.detectEffectWithInformous: checking effect " + effect.effectType.toString(), null, null, false);

            if (effect.informousText != null) {
                infoText = effect.informousText;
                break;
            }
        }

        return infoText;
    }

    /**
     * Get the Legilimens detection text of the first Legilimens-detectable effect active on a player.
     *
     * @param pid the unique ID of the player to probe
     * @return the legilimensText of the first detectable effect, or null if none is detectable
     * @see #detectEffectWithInformous(UUID) for Informous detection
     * @see net.pottercraft.ollivanders2.spell.LEGILIMENS for the spell that uses this method
     */
    @Nullable
    public String detectEffectWithLegilimens(@NotNull UUID pid) {
        common.printDebugMessage("O2Effects.detectEffectWithLegilimens", null, null, false);
        String infoText = null;

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        Collection<O2Effect> effects = activeEffects.values();

        for (O2Effect effect : effects) {
            common.printDebugMessage("O2Effects.detectEffectWithLegilimens: found " + effect.effectType.toString(), null, null, false);
            if (effect.legilimensText != null) {
                infoText = effect.legilimensText;
                break;
            }
        }

        return infoText;
    }

    /**
     * Handle the {@code /ollivanders2 effect <effect_name> [player_name]} admin command, toggling the named effect on
     * the sender or the named player. Requires the {@code Ollivanders2.admin} permission.
     *
     * @param sender the player issuing the command
     * @param args   the command arguments: [effect_name] or [effect_name, player_name]
     * @param p      a callback to the plugin
     * @return false if the sender is not a player, lacks permission, or gave the wrong argument count; true otherwise,
     *         including when the effect name or target player is invalid
     * @see #toggleEffect(CommandSender, Player, O2EffectType, Ollivanders2)
     */
    public static boolean runCommand(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Ollivanders2 p) {
        if (!(sender instanceof Player))
            return false;

        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        if (args.length < 2 || args.length > 3)
            return commandUsage(sender);

        String effectName = args[1].toUpperCase();

        O2EffectType effectType;
        try {
            effectType = O2EffectType.valueOf(effectName);
        }
        catch (Exception e) {
            sender.sendMessage(Ollivanders2.chatColor + "No effect named " + effectName + ".\n");
            return true;
        }

        Player targetPlayer;
        if (args.length == 3) {
            String playerName = args[2];
            targetPlayer = p.getServer().getPlayer(playerName);
            if (targetPlayer == null) {
                sender.sendMessage(Ollivanders2.chatColor + "Unable to find player " + playerName + ".\n");
                return true;
            }
        }
        else
            targetPlayer = (Player) sender;

        toggleEffect(sender, targetPlayer, effectType, p);

        return true;
    }

    /**
     * Toggle an effect on a player: remove it if present, otherwise apply a fresh instance with a 60-second (1200
     * tick) duration. Disabled effect types are rejected. Status and error messages are sent to the admin.
     *
     * @param sender     the admin that issued the command
     * @param player     the target player whose effect should be toggled
     * @param effectType the effect type to toggle
     * @param p          a callback to the plugin
     * @see #runCommand(CommandSender, String[], Ollivanders2) for the command entry point
     */
    private static void toggleEffect(@NotNull CommandSender sender, @NotNull Player player, @NotNull O2EffectType effectType, @NotNull Ollivanders2 p) {
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), effectType)) {
            Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), effectType);
            sender.sendMessage(Ollivanders2.chatColor + "Removed " + effectType + " from " + player.getName() + ".\n");
        }
        else {
            Class<?> effectClass = effectType.getClassName();

            if (Ollivanders2.debug)
                p.getLogger().info("Trying to add effect " + effectType);

            O2Effect effect;

            try {
                effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, boolean.class, UUID.class).newInstance(p, 1200, false, player.getUniqueId());
            }
            catch (Exception e) {
                sender.sendMessage(Ollivanders2.chatColor + "Failed to add effect " + effectType + " to " + player.getName() + ".\n");
                e.printStackTrace();
                return;
            }

            if (effect.effectType.isEnabled()) {
                Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
                sender.sendMessage(Ollivanders2.chatColor + "Added " + effectType + " to " + player.getName() + ".\n");
            }
            else
                sender.sendMessage(Ollivanders2.chatColor + effectType.toString() + " is currently disabled in your server config.\n");
        }
    }

    /**
     * Send the effect subcommand usage help to the sender.
     *
     * @param sender the recipient of the usage help
     * @return always true, to indicate the command was handled
     */
    public static boolean commandUsage(@NotNull CommandSender sender) {
        sender.sendMessage(Ollivanders2.chatColor
                + "/ollivanders2 effect effect_name - toggles the named effect on the command sender" + "\n"
                + "/ollivanders2 effect effect_name player_name - toggles the named effect on the player");

        return true;
    }

    /**
     * Kill all active effects and discard all saved effects for every player. Destructive global reset, intended for
     * test cleanup or server-wide resets.
     */
    public void removeAllEffects() {
        common.printDebugMessage("Removing all effects from all players", null, null, false);
        effectsData.killAllEffects();
    }
}

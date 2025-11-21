package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
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
 * Central manager for all magical effects applied to players.
 *
 * <p>O2Effects manages the complete effect lifecycle: adding effects, processing them each game tick,
 * removing effects, and persisting effects across player logouts. This class implements Bukkit's Listener
 * interface to hook into server events and distribute them to active effects.</p>
 *
 * <p>Thread Safety: This class uses a Semaphore and EffectsData wrapper to ensure thread-safe access to
 * effect maps, preventing race conditions when multiple threads access player effects simultaneously.
 * All public methods are synchronized or use the internal EffectsData synchronization.</p>
 *
 * <p>Data Structure: Effects are stored in two separate maps:</p>
 * <ul>
 * <li><strong>Active Effects:</strong> O2Effect objects for online players, actively processed each tick</li>
 * <li><strong>Saved Effects:</strong> Effect type and duration pairs for offline players, restored on login</li>
 * </ul>
 *
 * <p>Effect Stacking: When an effect is added to a player who already has that effect type,
 * the durations are combined rather than replacing the effect, allowing effect stacking.</p>
 *
 * @author Azami7
 * @see O2Effect for the abstract base class of all effects
 * @see O2EffectType for available effect types
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
     * Shared semaphore for thread-safe access to effect data maps.
     * Static and shared across all O2Effects instances to provide consistent locking
     * for the EffectsData internal maps. Prevents race conditions when multiple threads
     * read/write effect maps simultaneously.
     */
    final static Semaphore semaphore = new Semaphore(1);

    /**
     * Prefix for effects for serializing
     */
    public static final String effectLabelPrefix = "Effect_";

    /**
     * Thread-safe storage container for active and saved player effects.
     *
     * <p>EffectsData encapsulates the effect data management for all players, both online and offline.
     * All access to the internal effect maps is protected by a shared semaphore to ensure thread-safe
     * read and write operations. This prevents race conditions when multiple threads (scheduler, event
     * handlers) access player effect data simultaneously.</p>
     *
     * <p>Data Organization:</p>
     * <ul>
     * <li><strong>Active Effects:</strong> Currently applied O2Effect objects for online players,
     * processed each game tick</li>
     * <li><strong>Saved Effects:</strong> Effect types and remaining durations for offline players,
     * restored on login</li>
     * </ul>
     *
     * <p>All public methods are synchronized and acquire the semaphore before accessing maps, ensuring
     * consistent state across concurrent access patterns.</p>
     */
    private class EffectsData {
        /**
         * A list of all active effects on all online players
         */
        final private Map<UUID, Map<O2EffectType, O2Effect>> activeEffects = new HashMap<>();

        /**
         * A list of all saved effects for all offline players
         */
        final private Map<UUID, Map<O2EffectType, Integer>> savedEffects = new HashMap<>();

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
        synchronized Map<O2EffectType, Integer> getPlayerSavedEffects(@NotNull UUID pid) {
            Map<O2EffectType, Integer> effects = new HashMap<>();

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
        synchronized void updatePlayerSavedEffects(@NotNull UUID pid, @NotNull Map<O2EffectType, Integer> effects) {
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
         * Update the map of saved effects for this player.
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
         * Terminate all active effects on all players and clear all saved effects.
         *
         * <p>Performs a complete global cleanup of the effects system by killing every active effect
         * on every online player and clearing all saved effects for offline players. This is a destructive
         * operation that should only be used in specific contexts: test cleanup to reset game state between
         * test runs, or during server-wide resets. All effects are terminated via their kill() method,
         * and all persistent effect data is discarded.</p>
         *
         * <p>Thread Safety: This method is synchronized and acquires the semaphore before accessing the
         * effects maps to prevent concurrent modification issues.</p>
         *
         * @see #resetEffects(UUID) for clearing effects for a single player
         * @see #onDeath(UUID) for clearing effects when a single player dies
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
     * Initialize the effects system on plugin startup.
     *
     * <p>Reads configuration to determine which effects are enabled or disabled. Currently only
     * lycanthropy configuration is loaded, as other effects are enabled by default. This allows
     * servers to disable specific effects if they conflict with gameplay or other plugins.</p>
     */
    public void onEnable() {
        //
        // lycanthropy
        //
        O2EffectType.LYCANTHROPY.setEnabled(p.getConfig().getBoolean("enableLycanthropy"));
        if (O2EffectType.LYCANTHROPY.isEnabled())
            p.getLogger().info("Enabling lycanthropy.");
        else
            p.getLogger().info("Disabling lycanthropy.");
    }

    /**
     * Distribute entity damage events to all active effects on the damaged player.
     *
     * <p>When a player takes damage from another entity, this event handler distributes the damage event
     * to all active effects on that player. Effects can use this event to implement damage prevention,
     * redirection, mitigation, or other protective mechanics. This handler processes at HIGHEST priority
     * to ensure effects are evaluated before other plugins can modify the event.</p>
     *
     * <p>Only players who are the damage target are processed; damage from other entity types is ignored.
     * Each active effect on the damaged player receives the event via its doOnEntityDamageByEntityEvent()
     * callback method.</p>
     *
     * @param event the entity damage by entity event, containing damage amount, source entity, and target player
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(entity.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnEntityDamageByEntityEvent(event);
        }
    }

    /**
     * Distribute player interact events to all active effects on the interacting player.
     *
     * <p>When a player interacts with the world (right-click on blocks/entities, left-click, etc.),
     * this handler distributes the interaction event to all active effects on that player. Effects can
     * use this to intercept interactions, prevent certain actions, or trigger special behaviors based
     * on what the player is interacting with.</p>
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
     * Distribute chat messages to all active effects on the chatting player.
     *
     * <p>When a player sends a chat message, this handler distributes it to all active effects.
     * Effects can use this to modify chat messages, prevent chat, or respond to specific keywords.
     * This uses the deprecated AsyncPlayerChatEvent which processes asynchronously.</p>
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
     * Distribute bed enter events to all active effects on the player.
     *
     * <p>When a player attempts to enter a bed, this handler distributes the event to all active
     * effects. Effects can use this to prevent sleeping or trigger special sleep-related mechanics.</p>
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
     * Distribute player flight toggle events to all active effects.
     *
     * <p>When a player attempts to toggle flight mode (if allowed), this handler distributes the
     * event to all active effects. Effects can use this to prevent or modify flight behavior.</p>
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
     * Distribute player sneak toggle events to all active effects.
     *
     * <p>When a player toggles sneak mode, this handler distributes the event to all active effects.
     * Effects can use this to prevent sneaking or enforce sneaking behavior.</p>
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
     * Distribute player sprint toggle events to all active effects.
     *
     * <p>When a player toggles sprint mode, this handler distributes the event to all active effects.
     * Effects can use this to prevent sprinting or enforce sprinting behavior.</p>
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
     * Distribute velocity change events to all active effects on the affected player.
     *
     * <p>When a player's velocity is modified (knockback, launcher, etc.), this handler distributes
     * the event to all active effects. Effects can use this to modify or negate velocity changes.</p>
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
     * Distribute item pickup events to all active effects on the player picking up the item.
     *
     * <p>When a player picks up an item, this handler distributes the event to all active effects.
     * Effects can use this to prevent item pickup or trigger special behavior on pickup.</p>
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
     * Distribute item held change events to all active effects on the player.
     *
     * <p>When a player changes which hotbar slot is selected, this handler distributes the event to
     * all active effects. Effects can use this to track item changes or prevent slot switching.</p>
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
     * Distribute item consumption events to all active effects on the consuming player.
     *
     * <p>When a player consumes an item (food, potion, etc.), this handler distributes the event to
     * all active effects. Effects can use this to prevent consumption or modify what happens when items are consumed.</p>
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
     * Distribute item drop events to all active effects on the dropping player.
     *
     * <p>When a player drops an item from their inventory, this handler distributes the event to all
     * active effects. Effects can use this to prevent item dropping or track dropped items.</p>
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
     * Distribute player move events to all active effects on the moving player.
     *
     * <p>When a player moves, this handler distributes the movement event to all active effects.
     * Effects can use this to prevent movement, teleport the player, or trigger location-based behavior.</p>
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
     * Distribute spell projectile move events to all active effects on all players.
     *
     * <p>When a custom Ollivanders spell projectile moves, this handler distributes the event to all
     * active effects on all online players. Effects can use this to interact with projectiles or
     * trigger special mechanics when projectiles are nearby.</p>
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
     * Distribute entity target events to all active effects on the targeted entity.
     *
     * <p>When an entity attempts to target another entity, this handler distributes the event to all
     * active effects on the targeted entity. Effects can use this to prevent targeting or change what
     * the entity is targeting.</p>
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
     * Distribute player quit events to all active effects on the departing player.
     *
     * <p>When a player leaves the server, this handler distributes the quit event to all active
     * effects on that player. Effects can use this to perform cleanup or save state information.</p>
     *
     * @param event the player quit event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
        for (O2Effect effect : activeEffects.values()) {
            effect.doOnPlayerQuitEvent(event);
        }
    }

    /**
     * Distribute projectile launch events to all active effects on all players.
     *
     * <p>When any projectile is launched in the world, this handler distributes the event to all
     * active effects on all online players. Effects can use this to interact with projectiles,
     * prevent launches, or trigger projectile-based mechanics.</p>
     *
     * @param event the projectile launch event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        for (Map<O2EffectType, O2Effect> activeEffects : effectsData.activeEffects.values()) {
            for (O2Effect effect : activeEffects.values()) {
                effect.doOnProjectileLaunchEvent(event);
            }
        }
    }

    /**
     * Distribute projectile hit events to all active effects on all players.
     *
     * <p>When any projectile hits an entity or block, this handler distributes the event to all
     * active effects on all online players. Effects can use this to intercept projectile impacts
     * or trigger special mechanics when projectiles hit.</p>
     *
     * @param event the projectile hit event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        for (Map<O2EffectType, O2Effect> activeEffects : effectsData.activeEffects.values()) {
            for (O2Effect effect : activeEffects.values()) {
                effect.doOnProjectileHitEvent(event);
            }
        }
    }

    /**
     * Get a list of all effect types currently active on a player.
     *
     * <p>Returns the effect type enumeration values for all active effects on the specified player.
     * This returns only the effect types, not the actual O2Effect objects with their state and duration.
     * Returns an empty list if the player has no active effects.</p>
     *
     * @param pid the unique ID of the player
     * @return a list of O2EffectType values for all active effects, empty if none
     */
    @NotNull
    public List<O2EffectType> getEffects(@NotNull UUID pid) {
        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        return new ArrayList<>(playerEffects.keySet());
    }

    /**
     * Check if a player has a specific active effect.
     *
     * <p>Determines if a particular effect is currently active on the player. Only searches the
     * player's active effects; saved effects for offline players are not checked. This method is
     * useful for conditional logic that depends on specific magical effects being applied.</p>
     *
     * @param pid        the unique ID of the player to check
     * @param effectType the specific effect type to look for
     * @return true if the player has this effect active, false otherwise
     */
    public boolean hasEffect(@NotNull UUID pid, @NotNull O2EffectType effectType) {
        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        return playerEffects.containsKey(effectType);
    }

    /**
     * Check if a player has any active effects.
     *
     * <p>Determines if a player currently has one or more active effects applied. This is a
     * convenience method for checking if a player is affected by any magical effect without
     * needing to check specific effect types. Only checks active effects; saved effects for
     * offline players are not considered.</p>
     *
     * @param pid the unique ID of the player to check
     * @return true if the player has any active effects, false if they have no active effects
     */
    public boolean hasEffects(@NotNull UUID pid) {
        return !effectsData.getPlayerActiveEffects(pid).isEmpty();
    }

    /**
     * Apply all saved effects when a player joins the server.
     *
     * <p>When a player logs back in, this method retrieves any effects that were active when they
     * logged out and reinstantiates them. Each saved effect is reconstructed with its remaining duration.
     * Effects are only applied if they are currently enabled via configuration. This method is called
     * by the player management system when a player enters the server.</p>
     *
     * @param pid the unique ID of the joining player
     */
    public synchronized void onJoin(@NotNull UUID pid) {
        Map<O2EffectType, Integer> savedEffects = effectsData.getPlayerSavedEffects(pid);
        Map<O2EffectType, O2Effect> activeEffects = new HashMap<>();

        if (savedEffects.isEmpty())
            return;

        Player player = p.getServer().getPlayer(pid);
        if (player == null) {
            common.printDebugMessage("O2Effects.onJoin: player is null", null, null, true);
            return;
        }

        common.printDebugMessage("Applying effects for " + player.getDisplayName(), null, null, false);

        for (Entry<O2EffectType, Integer> entry : savedEffects.entrySet()) {
            O2EffectType effectType = entry.getKey();
            int duration = entry.getValue();

            Class<?> effectClass = effectType.getClassName();

            O2Effect effect;
            try {
                effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(p, duration, pid);
            }
            catch (Exception e) {
                common.printDebugMessage("O2Effects.onJoin: failed to create class for " + effectType, e, null, true);

                continue;
            }

            if (effectType.isEnabled()) {
                activeEffects.put(effectType, effect);
                common.printDebugMessage("   added " + effectType, null, null, false);
            }
        }

        effectsData.updatePlayerActiveEffects(pid, activeEffects);
    }

    /**
     * Save all active effects when a player quits the server.
     *
     * <p>When a player logs out, this method persists all their currently active effects to saved storage.
     * Each active effect's type and remaining duration are recorded, allowing the effects to be
     * restored when the player logs back in via the onJoin() method. Effects that have no duration
     * or have been killed will not be saved. This method is called by the player management system
     * when a player leaves the server.</p>
     *
     * @param pid the unique ID of the departing player
     */
    public synchronized void onQuit(@NotNull UUID pid) {
        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        Map<O2EffectType, Integer> savedEffects = new HashMap<>();

        for (Entry<O2EffectType, O2Effect> entry : activeEffects.entrySet()) {
            savedEffects.put(entry.getKey(), entry.getValue().duration);
        }

        effectsData.updatePlayerSavedEffects(pid, savedEffects);
    }

    /**
     * Reset all effects when a player dies.
     *
     * <p>When a player dies, this method performs a complete cleanup of all their effects. All active
     * effects are terminated by calling their kill() method, and both the active effects and saved effects
     * are cleared. This ensures the player starts fresh with no lingering effects when they respawn.
     * This method is called by the player management system when a player's death is processed.</p>
     *
     * @param pid the unique ID of the deceased player
     */
    public synchronized void onDeath(@NotNull UUID pid) {
        effectsData.resetEffects(pid);
    }

    /**
     * Serialize all saved effects for a player to string format.
     *
     * <p>Converts a player's saved effects into a string map suitable for persistence and storage.
     * Each effect type is prefixed with "Effect_" to distinguish effect entries from other persisted data.
     * The effect type name and duration are both stored as strings for serialization. This method is
     * typically called when saving player data to persistent storage.</p>
     *
     * @param pid the unique ID of the player whose effects should be serialized
     * @return a map of serialized effect strings (keys like "Effect_EFFECTNAME", values are durations as strings)
     */
    @NotNull
    public Map<String, String> serializeEffects(@NotNull UUID pid) {
        Map<String, String> serialized = new HashMap<>();

        Map<O2EffectType, Integer> savedEffects = effectsData.getPlayerSavedEffects(pid);
        for (Entry<O2EffectType, Integer> entry : savedEffects.entrySet()) {
            serialized.put(effectLabelPrefix + entry.getKey().toString(), entry.getValue().toString());
        }

        return serialized;
    }

    /**
     * Deserialize an effect from string representation and add it to the player's saved effects.
     *
     * <p>Reconstructs an effect from its serialized string form and adds it to the player's saved effects
     * map. The effect string is expected to have the "Effect_" prefix which is stripped to retrieve the
     * actual effect type name. If the duration string cannot be parsed as an integer or the effect type
     * is not found, the deserialization fails silently without throwing exceptions. This method is
     * typically called when loading player data from persistent storage.</p>
     *
     * @param pid            the unique ID of the player to deserialize effects for
     * @param effectsString  the serialized effect string (format: "Effect_EFFECTNAME")
     * @param durationString the effect duration as a string (must parse to valid integer)
     */
    public void deserializeEffect(@NotNull UUID pid, @NotNull String effectsString, @NotNull String durationString) {
        Map<O2EffectType, Integer> savedEffects = effectsData.getPlayerSavedEffects(pid);

        String effectName = effectsString.replaceFirst(effectLabelPrefix, "");
        Integer duration = Ollivanders2API.common.integerFromString(durationString);

        if (duration != null) {
            try {
                O2EffectType effectType = O2EffectType.valueOf(effectName);
                savedEffects.put(effectType, duration);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to deserialize effect " + effectName, null, null, false);
            }
        }

        effectsData.updatePlayerSavedEffects(pid, savedEffects);
    }

    /**
     * Add an effect to a player, applying effect stacking and configuration checks.
     *
     * <p>Applies a new effect to the player after verifying it is enabled via configuration. If the player
     * already has the same effect type active, the durations are combined instead of replacing the effect,
     * allowing effect stacking. Shape-shifting effects are mutually exclusive; if the player already has
     * one shape-shifting effect, new shape-shifting effects are rejected. Once added, the effect is stored
     * in the player's active effects map and processed on each game tick. If the effect has an affected player
     * message, it is sent to the player with a 5-tick delay.</p>
     *
     * <p>This method is thread-safe and synchronized. Only enabled effects are added; disabled effects are
     * silently rejected. Shape-shifting effects include transformations and other appearance-changing effects.</p>
     *
     * @param effect the O2Effect object to add to the player (must not be null)
     */
    public synchronized void addEffect(@NotNull O2Effect effect) {
        if (!effect.effectType.isEnabled())
            return;

        UUID pid = effect.getTargetID();

        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        // Prevent multiple shape-shifting effects: only one transformation allowed at a time
        // Shape-shifting effects are mutually exclusive to prevent conflicting appearance changes
        if (effect instanceof ShapeShiftSuper) {
            for (O2Effect ef : playerEffects.values()) {
                if (ef instanceof ShapeShiftSuper) {
                    return;  // Already has a shape-shift effect, reject this new one
                }
            }
        }

        if (playerEffects.containsKey(effect.effectType)) {
            // Effect stacking: if player already has this effect, combine durations
            // This allows multiple casts of the same spell to extend the effect's duration
            O2Effect ef = playerEffects.get(effect.effectType);
            effect.duration += ef.duration;
            playerEffects.replace(effect.effectType, effect);
        }
        else {
            // New effect: add it to the player's active effects
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
     * Remove an effect from a player, cleaning up state and message handling.
     *
     * <p>Terminates an active effect on the player by calling its kill() method and executing its doRemove()
     * cleanup logic. The effect is then removed from the player's active effects map. If the specified effect
     * type is not found on the player, a debug message is logged but no error is thrown. This method is
     * primarily called internally by the upkeep system when effects expire or are disabled via configuration.</p>
     *
     * <p>This method is thread-safe and synchronized. The effect's kill() and doRemove() methods handle any
     * cleanup specific to that effect type (e.g., removing potion effects, restoring player appearance).</p>
     *
     * @param pid        the unique ID of the player whose effect should be removed
     * @param effectType the type of effect to remove from the player
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
     * Get an active effect object for a player by effect type.
     *
     * <p>Retrieves the O2Effect object for a specific effect type that is currently active on the player.
     * This allows direct access to the effect's state, duration, and other properties. Returns null if the
     * player does not have the specified effect type active. This method is thread-safe and synchronized
     * to prevent race conditions when accessing effect objects.</p>
     *
     * @param pid        the unique ID of the player whose effect should be retrieved
     * @param effectType the type of effect to get
     * @return the O2Effect object if the effect is active on the player, null if not found
     * @see #hasEffect(UUID, O2EffectType) for checking if an effect exists without retrieving the object
     */
    public synchronized O2Effect getEffect(@NotNull UUID pid, @NotNull O2EffectType effectType) {
        return effectsData.getActiveEffect(pid, effectType);
    }

    /**
     * Process game heartbeat upkeep for all effects on a player.
     *
     * <p>Runs the periodic game tick processing for all active effects on the specified player. This method
     * calls checkEffect() on each active effect to allow them to perform their per-tick logic and update
     * their state. After checking, any effects that have been killed or are no longer enabled are automatically
     * removed via removeEffect(). This method is called once per game tick for each online player by the
     * OllivandersScheduler to keep effects synchronized with the server heartbeat.</p>
     *
     * <p>Note: This method only processes online players; effects for offline players are stored and restored
     * when the player rejoins via onJoin().</p>
     *
     * @param pid the unique ID of the player whose effects should be processed
     * @see #removeEffect(UUID, O2EffectType) for effect removal during upkeep
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
     * Reduce the duration of all non-permanent effects on a player by a specified amount.
     *
     * <p>Decrements the duration of every non-permanent effect currently active on the player by the given
     * amount. Permanent effects are skipped and remain unaffected. This is commonly used during the game
     * heartbeat to age all effects by the number of ticks that have passed since the last update. If an
     * effect's duration reaches zero or below, it will be removed during the next upkeep cycle.</p>
     *
     * @param pid    the unique ID of the player whose effects should be aged
     * @param amount the number of ticks to subtract from all non-permanent effect durations
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
     * Reduce the duration of a specific effect on a player by a specified amount.
     *
     * <p>Decrements the duration of a specific effect currently active on the player by the given amount.
     * The effect is located by its type, and if found, its duration is directly reduced. If the effect is not
     * found, this method does nothing. The effect map is updated after the duration change. If the effect's
     * duration reaches zero or below, it will be removed during the next upkeep cycle.</p>
     *
     * @param pid        the unique ID of the player whose effect should be aged
     * @param effectType the type of effect to age
     * @param amount     the number of ticks to subtract from the effect's duration
     * @see #ageAllEffects(UUID, int) for aging all effects at once
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
     * Reduce the duration of a specific effect on a player by a percentage of its current duration.
     *
     * <p>Decrements the duration of a specific effect by reducing it based on a percentage of its current
     * value. The percent parameter is clamped to a valid range of 1-100; values outside this range are
     * automatically adjusted. Permanent effects are skipped and remain unaffected. If the effect is not
     * found on the player, this method does nothing. If the effect's duration reaches zero or below after
     * percentage reduction, it will be removed during the next upkeep cycle.</p>
     *
     * @param pid        the unique ID of the player whose effect should be aged
     * @param effectType the type of effect to age by percentage
     * @param percent    the percentage of the effect's current duration to subtract (1-100; values outside
     *                   this range are automatically clamped)
     * @see #ageEffect(UUID, O2EffectType, int) for aging by absolute amount instead of percentage
     */
    public void ageEffectByPercent(@NotNull UUID pid, @NotNull O2EffectType effectType, int percent) {
        if (percent > 100)
            percent = 100;
        else if (percent < 1)
            percent = 1;

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        if (activeEffects.containsKey(effectType)) {
            O2Effect effect = activeEffects.get(effectType);

            if (!effect.isPermanent())
                effect.duration = effect.duration - (effect.duration * (percent / 100));
        }

        effectsData.updatePlayerActiveEffects(pid, activeEffects);
    }

    /**
     * Get the detection text for an active effect that can be detected by the Informous spell.
     *
     * <p>Searches through all active effects on the player to find the first one that has detectable
     * information for the Informous spell. Each effect can define informousText that describes what a
     * caster would learn about it when using Informous. Only the first detectable effect found is returned.
     * If no active effects have informousText configured, this method returns null. This is commonly used
     * by the INFORMOUS spell to provide intelligence about what effects are on a target player.</p>
     *
     * @param pid the unique ID of the player to check for detectable effects
     * @return the informousText of the first detectable effect found, or null if no detectable effects exist
     * @see #detectEffectWithLegilimens(UUID) for mind-reading detection via Legilimens spell
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
     * Detect an active effect on the target player using mind-reading perception.
     *
     * <p>Searches for detectable magical effects affecting the target player for the LEGILIMENS
     * (mind-reading) spell. Returns the {@code legilimensText} property of the first active effect
     * that has detectable mental traces. This method is used by the LEGILIMENS spell to provide
     * intelligence about the target player's condition, revealing hidden effects that can be
     * mentally perceived. Returns null if the target has no mentally-detectable effects.</p>
     *
     * @param pid the unique ID of the player to probe for detectable effects
     * @return the mental perception text of the first detectable effect, or null if no mentally-detectable effects are found
     * @see #detectEffectWithInformous(UUID) for information gathering detection via Informous spell
     * @see net.pottercraft.ollivanders2.spell.LEGILIMENS for the mind-reading spell using this detection
     */
    @Nullable
    public String detectEffectWithLegilimens(@NotNull UUID pid) {
        String infoText = null;

        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        Collection<O2Effect> effects = activeEffects.values();

        for (O2Effect effect : effects) {
            if (effect.legilimensText != null) {
                infoText = effect.legilimensText;
                break;
            }
        }

        return infoText;
    }

    /**
     * Handle the /ollivanders2 effect admin command for effect toggling.
     *
     * <p>Processes the effect subcommand for administrative management of magical effects on players.
     * The command requires admin permission (Ollivanders2.admin) and can be used to toggle effects
     * on the command sender or a specified target player. The effect is toggled: if the effect is
     * currently applied to the target player, it is removed; if not applied, a new effect instance
     * is created and applied with a 60-second duration.</p>
     *
     * <p>Command Syntax and Behavior:</p>
     * <ul>
     * <li>/ollivanders2 effect effect_name - toggles the named effect on the command sender</li>
     * <li>/ollivanders2 effect effect_name player_name - toggles the named effect on the specified player</li>
     * <li>Effect names are case-insensitive and must match a valid O2EffectType enum constant</li>
     * <li>Only enabled effects (configured in server config) can be applied</li>
     * <li>Sends feedback messages confirming the action or explaining errors</li>
     * </ul>
     *
     * @param sender the player issuing the command (must have Ollivanders2.admin permission)
     * @param args   the command arguments: [effect_name] or [effect_name, player_name]
     * @param p      a callback to the Ollivanders2 plugin for server access
     * @return true if the command was processed successfully (even if effect toggle failed), false if sender lacks permission or wrong argument count
     * @see #toggleEffect(CommandSender, Player, O2EffectType, Ollivanders2) for the effect toggle mechanism
     * @see #commandUsage(CommandSender) for command syntax documentation
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
     * Toggle an effect on or off for a player via admin command.
     *
     * <p>Performs the actual effect toggling logic for the effect admin command. If the player
     * already has the specified effect active, it is removed and the admin is notified. If the
     * player does not have the effect, a new effect instance is created with a 60-second duration
     * (1200 ticks) and applied to the player. Only enabled effects can be added; disabled effects
     * fail with an informative error message. Success and error messages are sent to the admin
     * with color formatting.</p>
     *
     * <p>Effect Creation: Uses reflection to dynamically instantiate the appropriate effect class
     * and applies it via the playerEffects system. Debug logging is performed if server debug mode
     * is enabled.</p>
     *
     * @param sender     the admin that issued the command (receives status messages)
     * @param player     the target player whose effect should be toggled
     * @param effectType the effect type to toggle on or off
     * @param p          a callback to the Ollivanders2 plugin for class instantiation and logging
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
                effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(p, 1200, player.getUniqueId());
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
     * Display the effect command syntax and usage help to an admin.
     *
     * <p>Sends formatted help text to the command sender displaying the complete syntax for the
     * effect subcommand. Two usage patterns are shown: toggling an effect on the command sender
     * themselves, and toggling an effect on a specified target player. This is typically called
     * when the command is invoked with invalid arguments.</p>
     *
     * @param sender the admin requesting the usage help or receiving syntax error feedback
     * @return always true to indicate the command was processed
     * @see #runCommand(CommandSender, String[], Ollivanders2) for the command that calls this method
     */
    public static boolean commandUsage(@NotNull CommandSender sender) {
        sender.sendMessage(Ollivanders2.chatColor
                + "/ollivanders2 effect effect_name - toggles the named effect on the command sender" + "\n"
                + "/ollivanders2 effect effect_name player_name - toggles the named effect on the player");

        return true;
    }

    /**
     * Remove all active and saved effects from all players system-wide.
     *
     * <p>Clears all effects from both the active effects list (online players) and saved effects
     * list (offline players), performing a complete global reset of the effects system. This is a
     * destructive operation that should only be used in specific contexts: test cleanup to reset game
     * state between test runs, or during server-wide game resets where all effects must be wiped.
     * This method logs a debug message when invoked and delegates to the internal EffectsData.killAllEffects()
     * method for the actual cleanup.</p>
     */
    public void removeAllEffects() {
        common.printDebugMessage("Removing all effects from all players", null, null, false);
        effectsData.killAllEffects();
    }
}

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
     * Thread-safe storage class for the effect data on players.
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
     * Read all effect config
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
     * Listen to entity damage entity events
     *
     * @param event the event
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
     * Listen to player interact events
     *
     * @param event the event
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
     * Listen to player chat events
     *
     * @param event the event
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
     * Listen to player sleep events
     *
     * @param event the event
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
     * Listen to player toggle flight events.
     *
     * @param event the event
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
     * Listen to player toggle sneak events.
     *
     * @param event the event
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
     * Listen to player toggle sprint events.
     *
     * @param event the event
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
     * Listen to player velocity change events
     *
     * @param event the event
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
     * Listen to entity pickup item events
     *
     * @param event the event
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
     * Listen to player hold item events
     *
     * @param event the event
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
     * Listen to player consume item events
     *
     * @param event the event
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
     * Listen to player drop item events
     *
     * @param event the event
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
     * Listen to player move events
     *
     * @param event the event
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
     * Listen to spell projectile move events
     *
     * @param event the event
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
     * Listen to entity target events
     *
     * @param event the event
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
     * Listen to player quit events
     *
     * @param event the event
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
     * Listen to projectile launch events
     *
     * @param event the event
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
     * Listen to projectile hit events
     *
     * @param event the event
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
     * Get a list of all the Ollivanders effects this player has on them.
     *
     * @param pid the id of the player
     * @return a list of the effects active on this player
     */
    @NotNull
    public List<O2EffectType> getEffects(@NotNull UUID pid) {
        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        return new ArrayList<>(playerEffects.keySet());
    }

    /**
     * Determines if this player is affected by this effect. Only checks active effects.
     *
     * @param pid        the id of the player
     * @param effectType the effect type to check for
     * @return true if they have this effect, false otherwise
     */
    public boolean hasEffect(@NotNull UUID pid, @NotNull O2EffectType effectType) {
        Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

        return playerEffects.containsKey(effectType);
    }

    /**
     * Determines if this player is affected by any effect. Only checks active effects.
     *
     * @param pid the id of the player
     * @return true if they have this effect, false otherwise
     */
    public boolean hasEffects(@NotNull UUID pid) {
        return !effectsData.getPlayerActiveEffects(pid).isEmpty();
    }

    /**
     * On player join, add any saved effects.
     *
     * @param pid the id of the player.
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
     * On quit, save all active effects.
     *
     * @param pid the id of the player
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
     * On death, reset all the effects for a player.
     *
     * @param pid the id of the player
     */
    public synchronized void onDeath(@NotNull UUID pid) {
        effectsData.resetEffects(pid);
    }

    /**
     * Serialize effects data to strings.
     *
     * @param pid the id of the player to serialize
     * @return a map of the effect type and duration serialized as strings
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
     * Deserialize saved effect from strings.
     *
     * @param pid            the id of the player this is saved for
     * @param effectsString  the effect string
     * @param durationString the duration string
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
     * Add an effect to this player.
     *
     * @param effect the effect to add to this player
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
     * Remove an effect from this player.
     *
     * @param pid        the id of the player
     * @param effectType the effect to remove from this player
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
     * Get an effect for a player.
     *
     * @param pid        the id of the player
     * @param effectType the type of effect to get
     * @return the effect object if found, null otherwise
     */
    public synchronized O2Effect getEffect(@NotNull UUID pid, @NotNull O2EffectType effectType) {
        return effectsData.getActiveEffect(pid, effectType);
    }

    /**
     * Run game heartbeat upkeep for all effects for this player. This is not done for every player because
     * we only want to update the ones that are currently online, therefore we let the OllivandersScheduler
     * class tell us which ones to update.
     *
     * @param pid the id of the player.
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
     * Age all non-permanent spell effects by a specified amount.
     *
     * @param pid    the id of the player
     * @param amount the amount to age the effects by
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
     * Age a specific effect on a player by a specified amount.
     *
     * @param pid        the id of the player
     * @param effectType the effect to age
     * @param amount     the amount to age the effect
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
     * Age a specific effect on a player by a specified percent.
     *
     * @param pid        the id of the player
     * @param effectType the effect to age
     * @param percent    the percent to age the effect
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
     * Age all effects on a player by a specified percent.
     *
     * @param pid     the id of the player
     * @param percent the percent to age the effect
     */
    public void ageAllEffectsByPercent(@NotNull UUID pid, int percent) {
        Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
        Collection<O2Effect> effects = activeEffects.values();

        for (O2Effect effect : effects) {
            if (!effect.isPermanent())
                ageEffectByPercent(pid, effect.effectType, percent);
        }
    }

    /**
     * Get the information for a detectable effect, if any can be detected.
     * <p>
     * {@link net.pottercraft.ollivanders2.spell.INFORMOUS}
     *
     * @param pid the id of the player to check
     * @return text about detectable effect or null if none found.
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
     * Get the information for a mind readable effect, if any can be detected. This is primarily used for the spell Legilimens.
     * <p>
     * {@link net.pottercraft.ollivanders2.spell.LEGILIMENS}
     *
     * @param pid the id of the player to check
     * @return text about detectable effect or null if none found.
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
     * The effects command for ops management of effects
     *
     * @param sender the player that issued the command
     * @param args   the arguments for the command, if any
     * @param p      callback to the Ollivanders plugin
     * @return true unless an error occurred
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
     * Toggle an effect on a player - admin command
     *
     * @param sender     the admin that issued the command
     * @param player     the target player
     * @param effectType the effect type
     * @param p          a callback to the plugin
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
     * Usage message for Effect subcommands.
     *
     * @param sender the player that issued the command
     * @return true
     */
    public static boolean commandUsage(@NotNull CommandSender sender) {
        sender.sendMessage(Ollivanders2.chatColor
                + "/ollivanders2 effect effect_name - toggles the named effect on the command sender" + "\n"
                + "/ollivanders2 effect effect_name player_name - toggles the named effect on the player");

        return true;
    }
}

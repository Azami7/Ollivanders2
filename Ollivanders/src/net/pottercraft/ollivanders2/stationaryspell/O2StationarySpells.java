package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Manager for all active and inactive stationary spells in the game.
 *
 * <p>This class handles lifecycle management of stationary spells, including creation, activation,
 * deactivation, and persistence. It acts as an event listener that broadcasts Bukkit events to all
 * active stationary spells, allowing them to respond to world events. Stationary spells are
 * area-of-effect spells that persist at specific locations and affect players and entities within
 * their radius.</p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 * <li>Managing the collection of all stationary spells</li>
 * <li>Broadcasting Bukkit events to active spells for custom behavior</li>
 * <li>Serialization and persistence of spell data to disk</li>
 * <li>Spell lifecycle management (loading, upkeep, removal)</li>
 * </ul>
 *
 * @author Azami7
 * @version Ollivanders2
 */
public class O2StationarySpells implements Listener {
    /**
     * The list of all stationary spells currently in the game.
     *
     * <p>Contains both active and inactive spells. Inactive spells are marked for removal but may
     * still be in the list until the next upkeep() cycle.</p>
     */
    private List<O2StationarySpell> stationarySpells = new ArrayList<>();

    /**
     * Reference to the plugin instance.
     *
     * <p>Used for accessing plugin services, scheduler, logger, and other plugin functionality.</p>
     */
    final Ollivanders2 p;

    /**
     * Common utility functions for logging and data manipulation.
     */
    Ollivanders2Common common;

    /**
     * JSON key for the caster's UUID in serialized spell data.
     */
    private final String playerUUIDLabel = "Player_UUID";

    /**
     * JSON key for the spell type/name in serialized spell data.
     */
    private final String spellLabel = "Name";

    /**
     * JSON key for the spell's duration in serialized spell data.
     */
    private final String durationLabel = "Duration";

    /**
     * JSON key for the spell's radius in serialized spell data.
     */
    private final String radiusLabel = "Radius";

    /**
     * JSON key for the spell's location in serialized spell data.
     */
    private final String spellLocLabel = "Spell_Loc";

    /**
     * Constructs a new O2StationarySpells manager.
     *
     * @param plugin the Ollivanders2 plugin instance (not null)
     */
    public O2StationarySpells(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);
    }

    /**
     * Initializes the stationary spells system when the plugin enables.
     *
     * <p>Loads previously saved stationary spells from disk and registers this manager as a
     * Bukkit event listener to receive world events for distribution to active spells.</p>
     */
    public void onEnable() {
        loadO2StationarySpells();

        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Cleanup when the plugin disables.
     *
     * <p>Called when the Ollivanders2 plugin is being shut down. The O2StationarySpells manager persists all active
     * stationary spell data to disk. Stationary spells (such as enchanted areas or persistent magical effects) are
     * serialized and saved so they can be restored when the server restarts, maintaining long-term magical effects
     * in the world.</p>
     *
     * <p>Saved Data:</p>
     * <ul>
     * <li>All active stationary spells in their current state</li>
     * <li>Spell location, duration, and radius information</li>
     * <li>Caster UUID and spell-specific properties</li>
     * <li>Data is persisted as JSON via GsonDAO for restoration on server startup</li>
     * </ul>
     *
     * @see #saveO2StationarySpells() for stationary spell persistence implementation
     * @see #loadO2StationarySpells() for restoring stationary spells on plugin startup
     */
    public void onDisable() {
        p.getLogger().info("Saving " + stationarySpells.size() + " stationary spells.");

        saveO2StationarySpells();
    }

    /**
     * Iterates over all active stationary spells and applies the given action to each.
     *
     * @param action the action to perform on each active stationary spell
     */
    private void forEachActiveSpell(@NotNull Consumer<O2StationarySpell> action) {
        for (O2StationarySpell stationary : stationarySpells) {
            if (stationary.isActive())
                action.accept(stationary);
        }
    }

    /**
     * Broadcasts a player move event to all active stationary spells.
     *
     * <p>Fired when a player moves to a new block. Stationary spells can use this to detect
     * player movement within their area of effect.</p>
     *
     * @param event the player move event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        forEachActiveSpell(spell -> spell.doOnPlayerMoveEvent(event));
    }

    /**
     * Handle when creatures spawn
     *
     * @param event the creature spawn event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(@NotNull CreatureSpawnEvent event) {
        forEachActiveSpell(spell -> spell.doOnCreatureSpawnEvent(event));
    }

    /**
     * Handle when entities target
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTargetEvent(@NotNull EntityTargetEvent event) {
        forEachActiveSpell(spell -> spell.doOnEntityTargetEvent(event));
    }

    /**
     * Handle when players chat
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        forEachActiveSpell(spell -> spell.doOnAsyncPlayerChatEvent(event));
    }

    /**
     * Handle block break event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(@NotNull BlockBreakEvent event) {
        forEachActiveSpell(spell -> spell.doOnBlockBreakEvent(event));
    }

    /**
     * Handle entity break door event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityBreakDoorEvent(@NotNull EntityBreakDoorEvent event) {
        forEachActiveSpell(spell -> spell.doOnEntityBreakDoorEvent(event));
    }

    /**
     * Handle entity break door event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityChangeBlockEvent(@NotNull EntityChangeBlockEvent event) {
        forEachActiveSpell(spell -> spell.doOnEntityChangeBlockEvent(event));
    }

    /**
     * Handle entity interact event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteractEvent(@NotNull EntityInteractEvent event) {
        forEachActiveSpell(spell -> spell.doOnEntityInteractEvent(event));
    }

    /**
     * Handle entity damage
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(@NotNull EntityDamageEvent event) {
        forEachActiveSpell(spell -> spell.doOnEntityDamageEvent(event));
    }

    /**
     * Handle entity damage event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        forEachActiveSpell(spell -> spell.doOnPlayerInteractEvent(event));
    }

    /**
     * Handle apparate by name event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        forEachActiveSpell(spell -> spell.doOnOllivandersApparateByNameEvent(event));
    }

    /**
     * Handle apparate by coordinate event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        forEachActiveSpell(spell -> spell.doOnOllivandersApparateByCoordinatesEvent(event));
    }

    /**
     * Handle entity teleport events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTeleportEvent(@NotNull EntityTeleportEvent event) {
        forEachActiveSpell(spell -> spell.doOnEntityTeleportEvent(event));
    }

    /**
     * Handle player teleport events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        forEachActiveSpell(spell -> spell.doOnPlayerTeleportEvent(event));
    }

    /**
     * Handle entity combust by block events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustEvent(@NotNull EntityCombustEvent event) {
        forEachActiveSpell(spell -> spell.doOnEntityCombustEvent(event));
    }

    /**
     * Handle spell projectile move events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        forEachActiveSpell(spell -> spell.doOnSpellProjectileMoveEvent(event));
    }

    /**
     * Handle world load events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        forEachActiveSpell(spell -> spell.doOnPlayerJoinEvent(event));
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDespawnEvent(@NotNull ItemDespawnEvent event) {
        forEachActiveSpell(spell -> spell.doOnItemDespawnEvent(event));
    }

    /**
     * Handle items being picked up by entities
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPickupItemEvent(@NotNull EntityPickupItemEvent event) {
        forEachActiveSpell(spell -> spell.doOnEntityPickupItemEvent(event));
    }

    /**
     * Handle items being picked up by things like hoppers
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryItemPickupEvent(@NotNull InventoryPickupItemEvent event) {
        forEachActiveSpell(spell -> spell.doOnInventoryItemPickupEvent(event));
    }

    /**
     * Handle player deal events
     *
     * @param event the player death event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathEvent(@NotNull PlayerDeathEvent event) {
        forEachActiveSpell(spell -> spell.doOnPlayerDeathEvent(event));
    }

    /**
     * Handle block from to events (like water or lava flowing)
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromToEvent(@NotNull BlockFromToEvent event) {
        forEachActiveSpell(spell -> spell.doOnBlockFromToEvent(event));
    }

    /**
     * Handle player bucket empty events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmptyEvent(@NotNull PlayerBucketEmptyEvent event) {
        forEachActiveSpell(spell -> spell.doOnPlayerBucketEmptyEvent(event));
    }

    /**
     * Handle projectile launch events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        forEachActiveSpell(spell -> spell.doOnProjectileLaunchEvent(event));
    }

    /**
     * Handle projectile hit events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        forEachActiveSpell(spell -> spell.doOnProjectileHitEvent(event));
    }

    /**
     * Adds a new stationary spell to the manager.
     *
     * <p>The spell is added to the internal list and will immediately be eligible to receive
     * Bukkit events if it is marked as active.</p>
     *
     * @param spell the stationary spell to add (not null)
     */
    public void addStationarySpell(@NotNull O2StationarySpell spell) {
        common.printDebugMessage("O2StationarySpells.addStationarySpell: adding " + spell.getSpellType() + " with duration " + spell.duration + " and radius of " + spell.radius, null, null, false);
        stationarySpells.add(spell);
    }

    /**
     * Marks a stationary spell for removal.
     *
     * <p>Instead of immediately removing the spell, this marks it as killed and allows the next
     * upkeep() cycle to clean it up. This prevents iterator corruption during event broadcasts.</p>
     *
     * @param spell the stationary spell to remove (not null)
     */
    public void removeStationarySpell(@NotNull O2StationarySpell spell) {
        common.printDebugMessage("O2StationarySpells.removeStationarySpell: removing " + spell.getSpellType(), null, null, false);
        spell.kill();
    }

    /**
     * Return a list of active stationary spells
     *
     * @return a list of active stationary spells
     */
    @NotNull
    public List<O2StationarySpell> getActiveStationarySpells() {
        List<O2StationarySpell> active = new ArrayList<>();

        for (O2StationarySpell spell : stationarySpells) {
            if (!spell.isKilled() && spell.isActive())
                active.add(spell);
        }

        return active;
    }

    /**
     * Finds all stationary spells (active or inactive) at a specific location.
     *
     * <p>Checks all spells in the manager to see if the given location falls within their radius
     * of effect, regardless of whether the spells are currently active or marked for removal.</p>
     *
     * @param targetLoc the location to check (not null)
     * @return a list of all stationary spells containing this location, empty if none found
     */
    @NotNull
    public List<O2StationarySpell> getStationarySpellsAtLocation(@NotNull Location targetLoc) {
        List<O2StationarySpell> inside = new ArrayList<>();

        for (O2StationarySpell stationary : stationarySpells) {
            if (stationary.isLocationInside(targetLoc))
                inside.add(stationary);
        }

        return inside;
    }

    /**
     * Finds all active stationary spells of a specific type at a location.
     *
     * <p>Returns only spells that are both active and of the specified type, located at the
     * given position. Useful for checking if a specific spell effect is active at a location.</p>
     *
     * @param location  the location to check (not null)
     * @param spellType the type of spell to search for (not null)
     * @return a list of active spells of that type at the location, empty if none found
     */
    @NotNull
    public List<O2StationarySpell> getActiveStationarySpellsAtLocationByType(@NotNull Location location, @NotNull O2StationarySpellType spellType) {
        List<O2StationarySpell> spells = getStationarySpellsAtLocation(location);
        List<O2StationarySpell> found = new ArrayList<>();

        for (O2StationarySpell spell : spells) {
            if (spell.getSpellType() == spellType && spell.active)
                found.add(spell);
        }

        return found;
    }

    /**
     * Check for a specific type of stationary spell at a location
     *
     * @param location            the location to check
     * @param stationarySpellType the stationary spell type to check for
     * @return true if spell of that type exists at that location, false otherwise
     */
    public boolean checkLocationForStationarySpell(@NotNull Location location, @NotNull O2StationarySpellType stationarySpellType) {
        List<O2StationarySpell> spellsAtLocation = getStationarySpellsAtLocation(location);

        for (O2StationarySpell statSpell : spellsAtLocation) {
            if (statSpell.getSpellType() == stationarySpellType)
                return true;
        }

        return false;
    }

    /**
     * Determine if the location is inside a stationary spell area.
     *
     * @param stationarySpell the stationary spell to check
     * @param loc             the location to check
     * @return true if the location is inside this stationary spell, false otherwise
     */
    public boolean isInsideOf(@NotNull O2StationarySpellType stationarySpell, @NotNull Location loc) {
        for (O2StationarySpell spell : stationarySpells) {
            if (spell.getSpellType() == stationarySpell) {
                if (spell.isLocationInside(loc) && !spell.isKilled() && spell.isActive())
                    return true;
            }
        }
        return false;
    }

    /**
     * Runs the upkeep cycle for all active spells and removes killed spells.
     *
     * <p>Called periodically (e.g., each server tick) to allow spells to update their state
     * (decrement duration, apply effects, etc.) and to clean up spells marked for removal.
     * Uses a copy of the spell list to safely remove spells during iteration.</p>
     */
    public void upkeep() {
        List<O2StationarySpell> stationarySpells = new ArrayList<>(this.stationarySpells);

        for (O2StationarySpell stationarySpell : stationarySpells) {
            stationarySpell.upkeep();

            if (stationarySpell.kill)
                this.stationarySpells.remove(stationarySpell);
        }
    }

    /**
     * Persists all stationary spells to disk as JSON.
     *
     * <p>Called when the server shuts down to save the current state of all spells. Spells can
     * be restored on the next server start via loadO2StationarySpells().</p>
     */
    public void saveO2StationarySpells() {
        List<Map<String, String>> serializedList = serializeO2StationarySpells();

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeSaveData(serializedList, GsonDAO.o2StationarySpellsJSONFile);
    }

    /**
     * Loads saved stationary spells from disk.
     *
     * <p>Called on plugin startup to restore spells that were persisted during the previous
     * shutdown. If no saved spell data exists, an empty list is created.</p>
     */
    public void loadO2StationarySpells() {
        GsonDAO gsonLayer = new GsonDAO();
        List<Map<String, String>> serializedSpells = gsonLayer.readSavedDataListMap(GsonDAO.o2StationarySpellsJSONFile);

        if (serializedSpells == null) {
            common.printLogMessage("Unable to load saved stationary spells.", null, null, false);
        }
        else {
            common.printLogMessage("Reading saved stationary spells", null, null, false);
            stationarySpells = deserializeO2StationarySpells(serializedSpells);
        }
    }

    /**
     * Converts all active stationary spells to a JSON-serializable format.
     *
     * <p>Each spell is converted to a map containing its type, caster UUID, location,
     * duration, radius, and spell-specific data. Used by saveO2StationarySpells().</p>
     *
     * @return a list of maps containing serialized spell data
     */
    @NotNull
    private List<Map<String, String>> serializeO2StationarySpells() {
        List<Map<String, String>> serializedList = new ArrayList<>();

        common.printDebugMessage("Serializing O2StationarySpells...", null, null, false);

        for (O2StationarySpell spell : stationarySpells) {
            Map<String, String> spellData = new HashMap<>();

            //
            // Spell type
            //
            spellData.put(spellLabel, spell.getSpellType().toString());

            //
            // Player UUID
            //
            spellData.put(playerUUIDLabel, spell.playerUUID.toString());

            //
            // Location
            //
            Map<String, String> locData = Ollivanders2API.common.serializeLocation(spell.location, spellLocLabel);
            if (locData != null) {
                spellData.putAll(locData);
            }

            //
            // Duration
            //
            spellData.put(durationLabel, Integer.toString(spell.duration));

            //
            // Radius
            //
            spellData.put(radiusLabel, Integer.toString(spell.radius));

            //
            // get spell-specific data
            //
            Map<String, String> uniqueData = spell.serializeSpellData();
            spellData.putAll(uniqueData);

            serializedList.add(spellData);
        }

        return serializedList;
    }

    /**
     * Converts serialized spell data from JSON maps into O2StationarySpell instances.
     *
     * <p>Each map in the input list is processed to reconstruct a stationary spell with its
     * original properties (location, duration, radius, caster UUID, type). Spells that fail
     * validation are silently skipped. Used by loadO2StationarySpells().</p>
     *
     * @param serializedSpells a list of spell data maps from JSON
     * @return a list of reconstructed O2StationarySpell objects ready to be activated
     */
    @NotNull
    private List<O2StationarySpell> deserializeO2StationarySpells(@NotNull List<Map<String, String>> serializedSpells) {
        List<O2StationarySpell> statSpells = new ArrayList<>();

        for (Map<String, String> spellData : serializedSpells) {
            //
            // spell name
            //
            if (!spellData.containsKey(spellLabel))
                continue;
            String name = spellData.get(spellLabel);
            O2StationarySpellType spellType = O2StationarySpellType.getStationarySpellTypeFromString(name);
            if (spellType == null)
                continue;

            O2StationarySpell statSpell = createStationarySpellByType(spellType);
            if (statSpell == null)
                continue;

            //
            // caster uuid
            //
            if (!spellData.containsKey(playerUUIDLabel))
                continue;
            UUID pid = Ollivanders2API.common.uuidFromString(spellData.get(playerUUIDLabel));
            if (pid == null)
                continue;
            statSpell.setPlayerID(pid);

            //
            // spell radius
            //
            if (!spellData.containsKey(radiusLabel))
                continue;
            Integer radius = Ollivanders2API.common.integerFromString(spellData.get(radiusLabel));
            if (radius == null)
                continue;
            statSpell.setRadius(radius);

            //
            // spell duration
            //
            if (!spellData.containsKey(durationLabel))
                continue;
            Integer duration = Ollivanders2API.common.integerFromString(spellData.get(durationLabel));
            if (duration == null)
                continue;
            statSpell.setDuration(duration);

            //
            // spell location
            //
            Location location = Ollivanders2API.common.deserializeLocation(spellData, spellLocLabel);
            if (location == null)
                continue;
            statSpell.setLocation(location);

            //
            // spell unique data
            //
            statSpell.deserializeSpellData(spellData);

            if (statSpell.checkSpellDeserialization()) {
                statSpell.setActive(true);
                statSpells.add(statSpell);
            }
        }

        return statSpells;
    }

    /**
     * Creates a new instance of a stationary spell by its type using reflection.
     *
     * <p>Instantiates the spell class associated with the given spell type. The spell is created
     * with default/uninitialized properties and will be inactive by default. Additional properties
     * (location, duration, radius, caster) must be set after creation via setter methods.</p>
     *
     * @param spellType the type of spell to create (not null)
     * @return a new spell instance, or null if instantiation fails
     */
    @Nullable
    public O2StationarySpell createStationarySpellByType(@NotNull O2StationarySpellType spellType) {
        O2StationarySpell statSpell;

        Class<?> spellClass = spellType.getClassName();
        try {
            statSpell = (O2StationarySpell) spellClass.getConstructor(Ollivanders2.class).newInstance(p);
        }
        catch (Exception e) {
            common.printDebugMessage("Exception trying to create new instance of " + spellType, e, null, true);
            return null;
        }

        // spell is incomplete until all data is loaded, so set inactive
        statSpell.setActive(false);

        return statSpell;
    }
}

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
 * Manages the lifecycle of all stationary spells: creation, upkeep, removal, and persistence to disk. As a Bukkit
 * {@link Listener}, it forwards world events to every active stationary spell so they can react within their area.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public class O2StationarySpells implements Listener {
    /**
     * All stationary spells, active and killed. Killed spells linger here until the next {@link #upkeep()} removes them.
     */
    private List<O2StationarySpell> stationarySpells = new ArrayList<>();

    final Ollivanders2 p;

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
     * @param plugin the Ollivanders2 plugin instance
     */
    public O2StationarySpells(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);
    }

    /**
     * Load saved stationary spells from disk and register this manager as a Bukkit event listener.
     */
    public void onEnable() {
        loadO2StationarySpells();

        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Persist all stationary spells to disk so they can be restored on the next server start.
     *
     * @see #saveO2StationarySpells()
     * @see #loadO2StationarySpells()
     */
    public void onDisable() {
        p.getLogger().info("Saving " + stationarySpells.size() + " stationary spells.");

        saveO2StationarySpells();
    }

    /**
     * Apply an action to every active stationary spell. The shared dispatch used by all the event handlers below.
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
     * Forward a player move event to every active stationary spell.
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
     * Handle entity change block event
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
     * Handle player interact event
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
     * Handle player join event
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
     * Handle player death event
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
     * Add a stationary spell to the manager; it starts receiving events immediately if active.
     *
     * @param spell the stationary spell to add
     */
    public void addStationarySpell(@NotNull O2StationarySpell spell) {
        common.printDebugMessage("O2StationarySpells.addStationarySpell: adding " + spell.getSpellType() + " with duration " + spell.duration + " and radius of " + spell.radius, null, null, false);
        stationarySpells.add(spell);
    }

    /**
     * Mark a stationary spell for removal by killing it. The next {@link #upkeep()} drops it from the list; removing it
     * here would corrupt iteration during an event broadcast.
     *
     * @param spell the stationary spell to remove
     */
    public void removeStationarySpell(@NotNull O2StationarySpell spell) {
        common.printDebugMessage("O2StationarySpells.removeStationarySpell: removing " + spell.getSpellType(), null, null, false);
        spell.kill();
    }

    /**
     * Get the active (not killed) stationary spells.
     *
     * @return the active stationary spells; empty if none
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
     * Get every stationary spell whose radius contains a location, including inactive and killed spells.
     *
     * @param targetLoc the location to check
     * @return the stationary spells containing this location; empty if none
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
     * Get the active stationary spells of a given type whose radius contains a location.
     *
     * @param location  the location to check
     * @param spellType the type of spell to search for
     * @return the matching active spells; empty if none
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
     * Run each active spell's upkeep and drop any that were killed. Iterates over a copy so spells can remove
     * themselves during the pass.
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
     * Persist all stationary spells to disk as JSON. Restore them with {@link #loadO2StationarySpells()}.
     */
    public void saveO2StationarySpells() {
        List<Map<String, String>> serializedList = serializeO2StationarySpells();

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeSaveData(serializedList, GsonDAO.o2StationarySpellsJSONFile);
    }

    /**
     * Load saved stationary spells from disk, replacing the current list. Leaves the list unchanged if no save data
     * exists.
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
     * Serialize every stationary spell to a list of string maps for {@link #saveO2StationarySpells()}. Each map holds
     * the spell's type, caster UUID, location, duration, radius, and any spell-specific data.
     *
     * @return the serialized spell data
     */
    @NotNull
    private List<Map<String, String>> serializeO2StationarySpells() {
        List<Map<String, String>> serializedList = new ArrayList<>();

        common.printDebugMessage("Serializing O2StationarySpells...", null, null, false);

        for (O2StationarySpell spell : stationarySpells) {
            Map<String, String> spellData = new HashMap<>();

            spellData.put(spellLabel, spell.getSpellType().toString());
            spellData.put(playerUUIDLabel, spell.playerUUID.toString());

            Map<String, String> locData = Ollivanders2API.common.serializeLocation(spell.location, spellLocLabel);
            if (locData != null) {
                spellData.putAll(locData);
            }

            spellData.put(durationLabel, Integer.toString(spell.duration));
            spellData.put(radiusLabel, Integer.toString(spell.radius));

            Map<String, String> uniqueData = spell.serializeSpellData();
            spellData.putAll(uniqueData);

            serializedList.add(spellData);
        }

        return serializedList;
    }

    /**
     * Reconstruct stationary spells from serialized string maps for {@link #loadO2StationarySpells()}. Any entry that
     * is missing a required field or fails validation is skipped. Reconstructed spells are set active.
     *
     * @param serializedSpells the serialized spell data maps
     * @return the reconstructed spells
     */
    @NotNull
    private List<O2StationarySpell> deserializeO2StationarySpells(@NotNull List<Map<String, String>> serializedSpells) {
        List<O2StationarySpell> statSpells = new ArrayList<>();

        for (Map<String, String> spellData : serializedSpells) {
            if (!spellData.containsKey(spellLabel))
                continue;
            String name = spellData.get(spellLabel);
            O2StationarySpellType spellType = O2StationarySpellType.getStationarySpellTypeFromString(name);
            if (spellType == null)
                continue;

            O2StationarySpell statSpell = createStationarySpellByType(spellType);
            if (statSpell == null)
                continue;

            if (!spellData.containsKey(playerUUIDLabel))
                continue;
            UUID pid = Ollivanders2API.common.uuidFromString(spellData.get(playerUUIDLabel));
            if (pid == null)
                continue;
            statSpell.setPlayerID(pid);

            if (!spellData.containsKey(radiusLabel))
                continue;
            Integer radius = Ollivanders2API.common.integerFromString(spellData.get(radiusLabel));
            if (radius == null)
                continue;
            statSpell.setRadius(radius);

            if (!spellData.containsKey(durationLabel))
                continue;
            Integer duration = Ollivanders2API.common.integerFromString(spellData.get(durationLabel));
            if (duration == null)
                continue;
            statSpell.setDuration(duration, true);

            Location location = Ollivanders2API.common.deserializeLocation(spellData, spellLocLabel);
            if (location == null)
                continue;
            statSpell.setLocation(location);

            statSpell.deserializeSpellData(spellData);

            if (statSpell.checkSpellDeserialization()) {
                statSpell.setActive(true);
                statSpells.add(statSpell);
            }
        }

        return statSpells;
    }

    /**
     * Create a new, inactive instance of the given spell type via reflection. The caller must populate its location,
     * duration, radius, and caster before activating it.
     *
     * @param spellType the type of spell to create
     * @return a new inactive spell instance, or null if instantiation fails
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

        statSpell.setActive(false);

        return statSpell;
    }
}

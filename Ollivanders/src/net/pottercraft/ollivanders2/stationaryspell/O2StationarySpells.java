package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
 * Manager for all stationary spells
 *
 * @author Azami7
 * @version Ollivanders2
 */
public class O2StationarySpells implements Listener {
    /**
     * The list of all stationary spells in the game
     */
    private List<O2StationarySpell> O2StationarySpells = new ArrayList<>();

    /**
     * A reference to the plugin
     */
    Ollivanders2 p;

    /**
     * Common functions
     */
    Ollivanders2Common common;

    /**
     * The label for UUID of the player who cast the spell for serializing
     */
    private final String playerUUIDLabel = "Player_UUID";

    /**
     * The label for the spell name/type for serializing
     */
    private final String spellLabel = "Name";

    /**
     * The label for the remaining duration of this spell for serializing
     */
    private final String durationLabel = "Duration";

    /**
     * The label for the radius of this spell for serializing
     */
    private final String radiusLabel = "Radius";

    /**
     * The label for the location for this spell
     */
    private final String spellLocLabel = "Spell_Loc";

    /**
     * Constructor
     *
     * @param plugin a reference to the plugin
     */
    public O2StationarySpells(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);
    }

    /**
     * Load all stationary spells on plugin start and register listeners
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
        p.getLogger().info("Saving " + O2StationarySpells.size() + " stationary spells.");

        saveO2StationarySpells();
    }

    /**
     * Handle when players move
     *
     * @param event the player move event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnPlayerMoveEvent(event);
        }
    }

    /**
     * Handle when creatures spawn
     *
     * @param event the creature spawn event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(@NotNull CreatureSpawnEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnCreatureSpawnEvent(event);
        }
    }

    /**
     * Handle when entities target
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTargetEvent(@NotNull EntityTargetEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnEntityTargetEvent(event);
        }
    }

    /**
     * Handle when players chat
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnAsyncPlayerChatEvent(event);
        }
    }

    /**
     * Handle block break event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(@NotNull BlockBreakEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnBlockBreakEvent(event);
        }
    }

    /**
     * Handle entity break door event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityBreakDoorEvent(@NotNull EntityBreakDoorEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnEntityBreakDoorEvent(event);
        }
    }

    /**
     * Handle entity break door event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityChangeBlockEvent(@NotNull EntityChangeBlockEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnEntityChangeBlockEvent(event);
        }
    }

    /**
     * Handle entity interact event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteractEvent(@NotNull EntityInteractEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnEntityInteractEvent(event);
        }
    }

    /**
     * Handle entity damage
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(@NotNull EntityDamageEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnEntityDamageEvent(event);
        }
    }

    /**
     * Handle entity damage event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnPlayerInteractEvent(event);
        }
    }

    /**
     * Handle apparate by name event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnOllivandersApparateByNameEvent(event);
        }
    }

    /**
     * Handle apparate by coordinate event
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnOllivandersApparateByCoordinatesEvent(event);
        }
    }

    /**
     * Handle entity teleport events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTeleportEvent(@NotNull EntityTeleportEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnEntityTeleportEvent(event);
        }
    }

    /**
     * Handle player teleport events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnPlayerTeleportEvent(event);
        }
    }

    /**
     * Handle entity combust by block events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityCombustEvent(@NotNull EntityCombustEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnEntityCombustEvent(event);
        }
    }

    /**
     * Handle spell projectile move events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnSpellProjectileMoveEvent(event);
        }
    }

    /**
     * Handle world load events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnPlayerJoinEvent(event);
        }
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDespawnEvent(@NotNull ItemDespawnEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnItemDespawnEvent(event);
        }
    }

    /**
     * Handle items being picked up by entities
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPickupItemEvent(@NotNull EntityPickupItemEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnEntityPickupItemEvent(event);
        }
    }

    /**
     * Handle items being picked up by things like hoppers
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryItemPickupEvent(@NotNull InventoryPickupItemEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnInventoryItemPickupEvent(event);
        }
    }

    /**
     * Handle player deal events
     *
     * @param event the player death event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathEvent(@NotNull PlayerDeathEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnPlayerDeathEvent(event);
        }
    }

    /**
     * Handle block from to events (like water or lava flowing)
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromToEvent(@NotNull BlockFromToEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnBlockFromToEvent(event);
        }
    }

    /**
     * Handle player bucket empty events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmptyEvent(@NotNull PlayerBucketEmptyEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnPlayerBucketEmptyEvent(event);
        }
    }

    /**
     * Handle projectile launch events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnProjectileLaunchEvent(event);
        }
    }

    /**
     * Handle projectile hit events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isActive())
                stationary.doOnProjectileHitEvent(event);
        }
    }

    /**
     * Add a stationary spell
     *
     * @param spell the stationary spell to add
     */
    public void addStationarySpell(@NotNull O2StationarySpell spell) {
        common.printDebugMessage("O2StationarySpells.addStationarySpell: adding " + spell.getSpellType() + " with duration " + spell.duration + " and radius of " + spell.radius, null, null, false);
        O2StationarySpells.add(spell);
    }

    /**
     * Remove a stationary spell. Since we do not want to lose track of a spell, we set kill to true and let
     * upkeep() clean it up.
     *
     * @param spell the stationary spell to remove
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

        for (O2StationarySpell spell : O2StationarySpells) {
            if (!spell.kill && spell.active)
                active.add(spell);
        }

        return active;
    }

    /**
     * Checks if the location is within one or more stationary spell objects, regardless of whether they are active.
     *
     * @param targetLoc location to check
     * @return a list of StationarySpellObj that the location is inside
     */
    @NotNull
    public List<O2StationarySpell> getStationarySpellsAtLocation(@NotNull Location targetLoc) {
        List<O2StationarySpell> inside = new ArrayList<>();

        for (O2StationarySpell stationary : O2StationarySpells) {
            if (stationary.isLocationInside(targetLoc))
                inside.add(stationary);
        }

        return inside;
    }

    /**
     * Get all active stationary spells of a specific type at the location
     *
     * @param location  the location
     * @param spellType the spell type
     * @return a list of spells of that type found at the location
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
    public boolean checkLocationForSpell(@NotNull Location location, @NotNull O2StationarySpellType stationarySpellType) {
        List<O2StationarySpell> spellsAtLocation = getStationarySpellsAtLocation(location);

        for (O2StationarySpell statSpell : spellsAtLocation) {
            if (statSpell.spellType == stationarySpellType)
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
        for (O2StationarySpell spell : O2StationarySpells) {
            if (spell.getSpellType() == stationarySpell) {
                if (spell.isLocationInside(loc) && !spell.kill && spell.active)
                    return true;
            }
        }
        return false;
    }

    /**
     * Run each spell's upkeep and clean up killed spells
     */
    public void upkeep() {
        List<O2StationarySpell> stationarySpells = new ArrayList<>(O2StationarySpells);

        for (O2StationarySpell stationarySpell : stationarySpells) {
            stationarySpell.upkeep();

            if (stationarySpell.kill)
                O2StationarySpells.remove(stationarySpell);
        }
    }

    /**
     * Save stationary spells
     */
    public void saveO2StationarySpells() {
        List<Map<String, String>> serializedList = serializeO2StationarySpells();

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeSaveData(serializedList, GsonDAO.o2StationarySpellsJSONFile);
    }

    /**
     * Load stationary spells
     */
    void loadO2StationarySpells() {
        GsonDAO gsonLayer = new GsonDAO();
        List<Map<String, String>> serializedSpells = gsonLayer.readSavedDataListMap(GsonDAO.o2StationarySpellsJSONFile);

        if (serializedSpells == null) {
            common.printLogMessage("Unable to load saved stationary spells.", null, null, false);
        }
        else {
            common.printLogMessage("Reading saved stationary spells", null, null, false);
            O2StationarySpells = deserializeO2StationarySpells(serializedSpells);
        }
    }

    /**
     * Serialize stationary spells for saving
     *
     * @return a list of the serialized stationary spell data
     */
    @NotNull
    private List<Map<String, String>> serializeO2StationarySpells() {
        List<Map<String, String>> serializedList = new ArrayList<>();

        common.printDebugMessage("Serializing O2StationarySpells...", null, null, false);

        for (O2StationarySpell spell : O2StationarySpells) {
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

            serializedList.add(spellData);

            //
            // get spell-specific data
            //
            Map<String, String> uniqueData = spell.serializeSpellData();
            spellData.putAll(uniqueData);
        }

        return serializedList;
    }

    /**
     * Deserialize stationary spells
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

            statSpell.setActive(true);
            statSpells.add(statSpell);
        }

        return statSpells;
    }

    /**
     * Create a basic stationary spell object by type. This will not have all the fields set to be active so will be set inactive by default.
     *
     * @param spellType the type of spell to create
     * @return the spell if it could be created, null otherwise
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

        return statSpell;
    }
}

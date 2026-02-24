package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.common.Cuboid;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Central manager for all Ollivanders2 spells.
 *
 * <p>Manages spell registration, loading, and lifecycle. Maintains active spell projectiles and
 * performs per-tick updates. Coordinates spell permission validation through a zone-based system that
 * supports global, world-wide, WorldGuard region, and cuboid-based allow/disallow lists.</p>
 *
 * <p><strong>Spell Permission Hierarchy:</strong></p>
 * <ol>
 * <li>If a global allow list is defined, only globally allowed spells can be cast</li>
 * <li>Check zone-specific allow lists (WORLD, WORLD_GUARD, CUBOID) - if found, only zone-allowed spells pass</li>
 * <li>Check zone-specific disallow lists - if spell is found in any zone's disallow list, deny it</li>
 * <li>Check global disallow list - if spell is found, deny it</li>
 * <li>If no restrictions apply, allow the spell</li>
 * </ol>
 *
 * @author Azami7
 */
public class O2Spells {
    /**
     * Callback to the plugin
     */
    private final Ollivanders2 p;

    /**
     * Common functions
     */
    private final Ollivanders2Common common;

    /**
     * All active spells
     */
    final private List<O2Spell> activeSpells = new ArrayList<>();

    /**
     * List of loaded spells
     */
    final static private Map<String, O2SpellType> O2SpellMap = new HashMap<>();

    /**
     * Wandless spells
     */
    public static final List<O2SpellType> wandlessSpells = new ArrayList<>() {{
        add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
    }};

    /**
     * Spell allowed/disallowed zone config
     */
    private ConfigurationSection zoneConfig;

    /**
     * Globally allowed spells. If this is set, it will take precedence over disallowed spells.
     */
    private ArrayList<O2SpellType> globalDisallowedSpells = new ArrayList<>();

    /**
     * Globally disallowed spells. If this is set, it will take precedence over regionally disallowed spells.
     */
    private ArrayList<O2SpellType> globalAllowedSpells = new ArrayList<>();

    /**
     * Spell allow/disallow zones.
     */
    final ArrayList<SpellZone> spellZones = new ArrayList<>();

    /**
     * Constructor
     *
     * @param plugin a callback to the MC plugin
     */
    public O2Spells(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);
    }

    /**
     * Initializes spell data when the plugin is enabled.
     *
     * <p>Called during plugin startup. Performs the following initialization steps:</p>
     * <ul>
     * <li>Loads all available spell types from {@link O2SpellType}, skipping spells that depend on
     *     unavailable plugins (e.g., LibsDisguises spells if LibsDisguises is not installed)</li>
     * <li>Logs the total number of loaded spells</li>
     * <li>Loads spell-specific static data (e.g., Apparate locations)</li>
     * <li>Loads zone-based spell permission configuration from config.yml</li>
     * <li>Adds divination spells to the wandless spells list</li>
     * </ul>
     */
    public void onEnable() {
        // load enabled spells
        for (O2SpellType spellType : O2SpellType.values()) {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.requiresLibsDisguises(spellType))
                continue;

            O2SpellMap.put(spellType.getSpellName().toLowerCase(), spellType);
        }
        p.getLogger().info("Loaded " + O2SpellMap.size() + " spells.");

        // load any spell static data
        APPARATE.loadApparateLocations(p);

        // load the zone config
        loadZoneConfig();

        // add divination spells to wandless spells
        wandlessSpells.addAll(Divination.divinationSpells);
    }

    /**
     * Cleanup when the plugin disables.
     *
     * <p>Called when the Ollivanders2 plugin is being shut down. Spell projectile management
     * (killing active projectiles) is currently handled by the main Ollivanders2.onDisable()
     * method. When projectile management is moved to this class, cleanup will be performed here.</p>
     */
    public void onDisable() {
        // kill all active spells
        for (O2Spell spell : activeSpells) {
            spell.kill();
        }
    }

    /**
     * Updates all active spell projectiles for one game tick.
     *
     * <p>Called each server tick by the main scheduler. Iterates through all active spells and:</p>
     * <ul>
     * <li>Calls {@link O2Spell#checkEffect()} to update each spell's state</li>
     * <li>Removes spells that have been killed from the active spell list</li>
     * </ul>
     *
     * <p>Uses a temporary copy of the active spells list to safely remove spells during iteration.</p>
     */
    public void upkeep() {
        if (activeSpells.isEmpty())
            return;

        List<O2Spell> spellsTemp = new ArrayList<>(activeSpells);
        for (O2Spell spell : spellsTemp) {
            spell.checkEffect();
            if (spell.isKilled()) {
                activeSpells.remove(spell);
            }
        }
    }

    /**
     * Get all loaded spells
     *
     * @return a list of all loaded spell types
     */
    public static List<O2SpellType> getAllSpellTypes() {
        return new ArrayList<>(O2SpellMap.values());
    }

    /**
     * Get an O2Spell object from its type.
     *
     * @param spellType the type of spell to get
     * @return the O2Spell object, if it could be created, or null otherwise
     */
    @Nullable
    private O2Spell getSpellFromType(@NotNull O2SpellType spellType) {
        O2Spell spell;

        Class<?> spellClass = spellType.getClassName();

        try {
            spell = (O2Spell) spellClass.getConstructor().newInstance();
        }
        catch (Exception exception) {
            common.printDebugMessage("Exception trying to create new instance of " + spellType, exception, null, true);
            return null;
        }

        return spell;
    }

    /**
     * Get a spell type by name.
     *
     * @param name the name of the spell or potion
     * @return the type if found, null otherwise
     */
    @Nullable
    public O2SpellType getSpellTypeByName(@NotNull String name) {
        return O2SpellMap.get(name.toLowerCase());
    }

    /**
     * Verify this spell type is loaded. A spell may not be loaded if it depends on something such as LibsDisguises and that
     * dependency plugin does not exist.
     *
     * @param spellType the spell type to check
     * @return true if this spell type is loaded, false otherwise
     */
    public boolean isLoaded(@NotNull O2SpellType spellType) {
        return O2SpellMap.containsValue(spellType);
    }

    /**
     * Load the zone config for spells
     */
    public void loadZoneConfig() {
        zoneConfig = p.getConfig().getConfigurationSection("zones");

        if (zoneConfig == null)
            return;

        for (String zone : zoneConfig.getKeys(false)) {
            if (zone.equalsIgnoreCase(SpellZone.globalZoneName)) {
                common.printDebugMessage("Loading global zone config:", null, null, false);

                globalAllowedSpells = getSpellsForZone(zone, SpellZone.allowedList);
                globalDisallowedSpells = getSpellsForZone(zone, SpellZone.disallowList);
            }
            else {
                common.printDebugMessage("Loading zone config for " + zone + ":", null, null, false);

                loadZoneConfig(zone);
            }
        }
    }

    /**
     * Load zone config for allowed and disallowed spells
     *
     * @param zoneName the name of the zone
     */
    private void loadZoneConfig(@NotNull String zoneName) {
        String typeString = zoneConfig.getString(zoneName + "." + "type");
        if (typeString == null || typeString.isEmpty())
            return;

        SpellZone.SpellZoneType type;
        try {
            type = SpellZone.SpellZoneType.valueOf(typeString.toUpperCase());
        }
        catch (Exception e) {
            common.printDebugMessage("O2Spells.loadZoneConfig: zone " + zoneName + " has invalid type " + typeString, null, null, true);
            return;
        }

        String world = "";
        if (type == SpellZone.SpellZoneType.WORLD || type == SpellZone.SpellZoneType.CUBOID) {
            world = zoneConfig.getString(zoneName + "." + "world");

            if (world == null || world.isEmpty()) {
                common.printDebugMessage("O2Spells.loadZoneConfig: world or cuboid zone " + zoneName + " with no world name set, ignored.", null, null, true);
                return;
            }
        }

        int[] area = {0, 0, 0, 0, 0, 0};

        if (type == SpellZone.SpellZoneType.CUBOID) {
            String areaString = zoneConfig.getString(zoneName + "." + "area");

            if (areaString == null || areaString.isEmpty()) {
                common.printDebugMessage("O2Spells.loadZoneConfig: cuboid zone " + zoneName + " with no area coordinates set, ignored", null, null, true);
                return;
            }

            area = Cuboid.parseArea(areaString);
            if (area == null) {
                common.printDebugMessage("O2Spells.loadZoneConfig: zone " + zoneName + " has invalid area " + areaString, null, null, true);
                return;
            }
        }

        ArrayList<O2SpellType> allowed = getSpellsForZone(zoneName, SpellZone.allowedList);
        ArrayList<O2SpellType> disallowed = getSpellsForZone(zoneName, SpellZone.disallowList);

        SpellZone zone = new SpellZone(zoneName, world, type, area, allowed, disallowed);
        spellZones.add(zone);
        p.getLogger().info("Added zone type " + type + " " + zoneName);
    }

    /**
     * Get the spell for a specific list for a zone
     *
     * @param zoneName the name of the zone
     * @param listName the name of the list
     * @return the spells for that zone list
     */
    @NotNull
    private ArrayList<O2SpellType> getSpellsForZone(@NotNull String zoneName, @NotNull String listName) {
        ArrayList<O2SpellType> spellList = new ArrayList<>();

        common.printDebugMessage(listName + ":", null, null, false);

        for (String spell : zoneConfig.getStringList(zoneName + "." + listName)) {
            O2SpellType spellType = O2SpellType.spellTypeFromString(spell.toUpperCase());
            if (spellType != null && isLoaded(spellType)) {
                common.printDebugMessage(" - " + spellType, null, null, false);

                spellList.add(spellType);
            }
            else if (spellType == null)
                common.printDebugMessage("invalid spell " + spell, null, null, false);
            else if (!isLoaded(spellType))
                common.printDebugMessage(spell + " not loaded", null, null, false);
        }

        return spellList;
    }

    /**
     * Check if a spell is allowed based on zone config
     *
     * @param location  the location of the spell
     * @param spellType the spell type to check
     * @return true if spell is allowed, false otherwise
     */
    public boolean isSpellTypeAllowed(@NotNull Location location, @NotNull O2SpellType spellType) {
        if (!isLoaded(spellType))
            return false;

        // first check global allow lists
        if (!globalAllowedSpells.isEmpty())
            return globalAllowedSpells.contains(spellType);

        // check world permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.WORLD)
                continue;

            World world = location.getWorld();
            if (world == null) {
                common.printDebugMessage("O2Spells.isSpellTypeAllowed: null world on spell location", null, null, true);
                return false;
            }

            if (world.getName().equalsIgnoreCase(zone.zoneWorldName) && !zone.allowedSpells.isEmpty())
                return zone.allowedSpells.contains(spellType);
        }

        // check world guard zone permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.WORLD_GUARD)
                continue;

            if (Ollivanders2.worldGuardO2.isLocationInRegionByName(zone.zoneName, location) && !zone.allowedSpells.isEmpty())
                return zone.allowedSpells.contains(spellType);
        }

        // check cuboid zone permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.CUBOID)
                continue;

            if (zone.cuboid.isInside(location) && !zone.allowedSpells.isEmpty())
                return zone.allowedSpells.contains(spellType);
        }

        if (isExplicitlyDisallowed(location, spellType))
            return false;

        return true;
    }

    /**
     * Determine if this spell is explicitly disallowed
     *
     * @param location  the location to check
     * @param spellType the spell type
     * @return true if the spell is explicitly disallowed at this location, false otherwise
     */
    private boolean isExplicitlyDisallowed(@NotNull Location location, @NotNull O2SpellType spellType) {
        // first check global disallow lists
        if (globalDisallowedSpells.contains(spellType))
            return true;

        // check world permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.WORLD)
                continue;

            World world = location.getWorld();
            if (world == null) {
                common.printDebugMessage("O2Spells.isSpellTypeAllowed: null world on spell location", null, null, true);
                return true;
            }

            if (world.getName().equalsIgnoreCase(zone.zoneWorldName) && zone.disallowedSpells.contains(spellType))
                return true;
        }

        // check world guard zone permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.WORLD_GUARD)
                continue;

            if (Ollivanders2.worldGuardO2.isLocationInRegionByName(zone.zoneName, location) && zone.disallowedSpells.contains(spellType))
                return true;
        }

        // check cuboid zone permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.CUBOID)
                continue;

            if (zone.cuboid.isInside(location) && zone.disallowedSpells.contains(spellType))
                return true;
        }

        return false;
    }

    /**
     * Add the spell and increment cast count
     *
     * @param player the player who cast the spell
     * @param spell  the spell cast
     */
    public void addSpell(@NotNull Player player, @NotNull O2Spell spell) {
        activeSpells.add(spell);

        p.incrementSpellCount(player, spell.spellType);

        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FAST_LEARNING))
            p.incrementSpellCount(player, spell.spellType);
    }

    /**
     * Get all the active spell projectiles
     *
     * @return a list of all active spell projectiles
     */
    @NotNull
    public List<O2Spell> getActiveSpells() {
        return new ArrayList<>(activeSpells);
    }

    /**
     * This creates the spell projectile.
     *
     * @param player the player that cast the spell
     * @param name   the name of the spell cast
     * @param wandC  the wand check value for the held wand
     * @return the spell that was created, or null if something failed
     */
    @Nullable
    public O2Spell createSpell(@NotNull Player player, @NotNull O2SpellType name, double wandC) {
        common.printDebugMessage("OllivandersListener.createSpellProjectile: enter", null, null, false);

        //spells go here, using any of the three types of magic
        String spellClass = "net.pottercraft.ollivanders2.spell." + name;

        Constructor<?> c;
        try {
            c = Class.forName(spellClass).getConstructor(Ollivanders2.class, Player.class, Double.class);
        }
        catch (Exception e) {
            common.printDebugMessage("OllivandersListener.createSpellProjectile: exception creating spell constructor", e, null, true);
            return null;
        }

        O2Spell spell;

        try {
            spell = (O2Spell) c.newInstance(p, player, wandC);
        }
        catch (Exception e) {
            common.printDebugMessage("OllivandersListener.createSpellProjectile: exception creating spell", e, null, true);
            return null;
        }

        return spell;
    }
}

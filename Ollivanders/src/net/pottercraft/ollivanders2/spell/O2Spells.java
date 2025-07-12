package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.common.Cuboid;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages all spells
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
     * Load spell data on plugin start
     */
    public void onEnable() {
        // load enabled spells
        for (O2SpellType spellType : O2SpellType.values()) {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libsDisguisesSpells.contains(spellType))
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
}

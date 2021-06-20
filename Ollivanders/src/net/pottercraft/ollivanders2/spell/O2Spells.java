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
 *
 * @author Azami7
 * @since 2.2.8
 */
public class O2Spells
{
    /**
     * Common functions
     */
    private final Ollivanders2Common common;

    /**
     * List of loaded spells
     */
    final private Map<String, O2SpellType> O2SpellMap = new HashMap<>();

    /**
     * Wandless spells
     */
    public static final List<O2SpellType> wandlessSpells = new ArrayList<>()
    {{
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
    private ArrayList<SpellZone> spellZones = new ArrayList<>();

    private final static String allowedList = "allowed-spells";
    private final static String disallowList = "disallowed-spells";
    private final static String globalZoneName = "global";
    private final static String none = "NONE";
    private final static String all = "ALL";

    /**
     * Type of spell zones that can be defined
     */
    private enum SpellZoneType
    {
        CUBOID,
        WORLD,
        WORLD_GUARD;
    }

    /**
     * A zone that spells can be explicitly allowed and disallowed for
     */
    private class SpellZone
    {
        String zoneName;
        SpellZoneType zoneType;
        final Cuboid cuboid;
        String zoneWorldName;

        ArrayList<O2SpellType> disallowedSpells;
        ArrayList<O2SpellType> allowedSpells;

        /**
         * Constructor
         *
         * @param name the name of this zone
         * @param world the name of the world this zone is in
         * @param type the type of zone
         * @param area the area bounds for this zone if a cuboid, this should be opposite corners
         * @param allowed a list of allowed spells, if set, this will take precedence over a disallow list
         * @param disallowed a list of disallowed spells
         */
        SpellZone (@NotNull String name, @NotNull String world, @NotNull O2Spells.SpellZoneType type, int[] area, @NotNull ArrayList<O2SpellType> allowed, @NotNull ArrayList<O2SpellType> disallowed)
        {
            zoneName = name;
            zoneType = type;
            zoneWorldName = world;

            if (type == SpellZoneType.CUBOID)
                cuboid = new Cuboid(zoneWorldName, area);
            else
            {
                int[] emptyArea = {0, 0, 0, 0, 0, 0};
                cuboid = new Cuboid(zoneWorldName, emptyArea);
            }

            disallowedSpells = disallowed;
            allowedSpells = allowed;
        }
    }

    /**
     * Constructor
     *
     * @param plugin a callback to the MC plugin
     */
    public O2Spells(@NotNull Ollivanders2 plugin)
    {
        common = new Ollivanders2Common(plugin);

        for (O2SpellType spellType : O2SpellType.values())
        {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libsDisguisesSpells.contains(spellType))
                continue;

            O2SpellMap.put(spellType.getSpellName().toLowerCase(), spellType);
        }

        loadZoneConfig(plugin);
    }

    /**
     * Get an O2Spell object from its type.
     *
     * @param spellType the type of spell to get
     * @return the O2Spell object, if it could be created, or null otherwise
     */
    @Nullable
    private O2Spell getSpellFromType(@NotNull O2SpellType spellType)
    {
        O2Spell spell;

        Class<?> spellClass = spellType.getClassName();

        try
        {
            spell = (O2Spell) spellClass.getConstructor().newInstance();
        }
        catch (Exception exception)
        {
            common.printDebugMessage("Exception trying to create new instance of " + spellType.toString(), exception, null, true);
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
    public O2SpellType getSpellTypeByName(@NotNull String name)
    {
        return O2SpellMap.get(name.toLowerCase());
    }

    /**
     * Verify this spell type is loaded. A spell may not be loaded if it depends on something such as LibsDisguises and that
     * dependency plugin does not exist.
     *
     * @param spellType the spell type to check
     * @return true if this spell type is loaded, false otherwise
     */
    public boolean isLoaded(@NotNull O2SpellType spellType)
    {
        return O2SpellMap.containsValue(spellType);
    }

    /**
     * Load the zone config for spells
     *
     * @param p a callback to the plugin
     */
    public void loadZoneConfig (@NotNull Ollivanders2 p)
    {
        zoneConfig = p.getConfig().getConfigurationSection("zones");

        if (zoneConfig == null)
            return;

        for (String zone : zoneConfig.getKeys(false))
        {
            if (zone.equalsIgnoreCase(globalZoneName))
            {
                common.printDebugMessage("Loading global zone config:", null, null, false);

                globalAllowedSpells = getSpellsForZone(globalZoneName, allowedList);
                globalDisallowedSpells = getSpellsForZone(globalZoneName, disallowList);
            }
            else
            {
                common.printDebugMessage("Loading zone config for " + zone + ":", null, null, false);

                loadZoneConfig(zone, p);
            }
        }
    }

    /**
     * Load zone config for allowed and disallowed spells
     *
     * @param zoneName the name of the zone
     * @param p a callback to the plugin
     */
    private void loadZoneConfig (@NotNull String zoneName, @NotNull Ollivanders2 p)
    {
        String typeString = zoneConfig.getString(zoneName + "." + "type");
        if (typeString == null || typeString.length() < 1)
            return;

        SpellZoneType type;
        try
        {
            type = SpellZoneType.valueOf(typeString.toUpperCase());
        }
        catch (Exception e)
        {
            common.printDebugMessage("O2Spells.loadZoneConfig: zone " + zoneName + " has invalid type " + typeString, null, null, true);
            return;
        }

        String world = "";
        if (type == SpellZoneType.WORLD || type == SpellZoneType.CUBOID)
        {
            world = zoneConfig.getString(zoneName + "." + "world");

            if (world == null || world.length() < 1)
            {
                common.printDebugMessage("O2Spells.loadZoneConfig: world or cuboid zone " + zoneName + " with no world name set, ignored.", null, null, true);
                return;
            }
        }

        int[] area = {0, 0, 0, 0, 0, 0};

        if (type == SpellZoneType.CUBOID)
        {
            String areaString = zoneConfig.getString(zoneName + "." + "area");

            if (areaString == null || areaString.length() < 1)
            {
                common.printDebugMessage("O2Spells.loadZoneConfig: cuboid zone " + zoneName + " with no area coordinates set, ignored", null, null, true);
                return;
            }

            area = Cuboid.parseArea(areaString);
            if (area == null)
            {
                common.printDebugMessage("O2Spells.loadZoneConfig: zone " + zoneName + " has invalid area " + areaString, null, null, true);
                return;
            }
        }

        ArrayList<O2SpellType> allowed = getSpellsForZone(zoneName, allowedList);
        ArrayList<O2SpellType> disallowed = getSpellsForZone(zoneName, disallowList);

        SpellZone zone = new SpellZone(zoneName, world, type, area, allowed, disallowed);
        spellZones.add(zone);
        p.getLogger().info("Added zone type " + type.toString() + " " + zoneName);
    }

    /**
     * Get the spell for a specific list for a zone
     *
     * @param zoneName the name of the zone
     * @param list the name of the list
     * @return the spells for that zone list
     */
    @NotNull
    private ArrayList<O2SpellType> getSpellsForZone (@NotNull String zoneName, @NotNull String list)
    {
        ArrayList<O2SpellType> spellList = new ArrayList<>();

        common.printDebugMessage(list + ":", null, null, false);

        for (String spell : zoneConfig.getStringList(zoneName + "." + list))
        {
            O2SpellType spellType = O2SpellType.spellTypeFromString(spell.toUpperCase());
            if (spellType != null && isLoaded(spellType))
            {
                common.printDebugMessage(" - " + spellType.toString(), null, null, false);

                spellList.add(spellType);
            }
        }

        return spellList;
    }

    /**
     * Check if a spell is allowed based on zone config
     *
     * @param location the location of the spell
     * @param spellType the spell type to check
     * @return true if spell is allowed, false otherwise
     */
    public boolean isSpellTypeAllowed (@NotNull Location location, @NotNull O2SpellType spellType)
    {
        if (!isLoaded(spellType))
            return false;

        // first check global allow lists
        if (globalAllowedSpells.size() > 0)
        {
            if (globalAllowedSpells.contains(spellType))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        // check world permissions
        for (SpellZone zone : spellZones)
        {
            if (zone.zoneType != SpellZoneType.WORLD)
                continue;

            World world = location.getWorld();
            if (world == null)
            {
                common.printDebugMessage("O2Spells.isSpellTypeAllowed: null world on spell location", null, null, true);
                return false;
            }

            if (world.getName().equalsIgnoreCase(zone.zoneWorldName) && zone.allowedSpells.size() > 0)
            {
                if (zone.allowedSpells.contains(spellType))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        // check world guard zone permissions
        for (SpellZone zone : spellZones)
        {
            if (zone.zoneType != SpellZoneType.WORLD_GUARD)
                continue;

            if (Ollivanders2.worldGuardO2.isLocationInRegionByName(zone.zoneName, location) && zone.allowedSpells.size() > 0)
            {
                if (zone.allowedSpells.contains(spellType))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        // check cuboid zone permissions
        for (SpellZone zone : spellZones)
        {
            if (zone.zoneType != SpellZoneType.CUBOID)
                continue;

            if (zone.cuboid.isInside(location, common) && zone.allowedSpells.size() > 0)
            {
                if (zone.allowedSpells.contains(spellType))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        if (isExplicitlyDisallowed(location, spellType))
        {
            return false;
        }

        return true;
    }

    /**
     * Determine if this spell is explicitly disallowed
     *
     * @param location the location to check
     * @param spellType the spell type
     * @return true if the spell is explicitly disallowed at this location, false otherwise
     */
    private boolean isExplicitlyDisallowed (@NotNull Location location, @NotNull O2SpellType spellType)
    {
        // first check global disallow lists
        if (globalDisallowedSpells.contains(spellType))
        {
            return true;
        }

        // check world permissions
        for (SpellZone zone : spellZones)
        {
            if (zone.zoneType != SpellZoneType.WORLD)
                continue;

            World world = location.getWorld();
            if (world == null)
            {
                common.printDebugMessage("O2Spells.isSpellTypeAllowed: null world on spell location", null, null, true);
                return true;
            }

            if (world.getName().equalsIgnoreCase(zone.zoneWorldName) && zone.disallowedSpells.contains(spellType))
            {
                return true;
            }
        }

        // check world guard zone permissions
        for (SpellZone zone : spellZones)
        {
            if (zone.zoneType != SpellZoneType.WORLD_GUARD)
                continue;

            if (Ollivanders2.worldGuardO2.isLocationInRegionByName(zone.zoneName, location) && zone.disallowedSpells.contains(spellType))
            {
                return true;
            }
        }

        // check cuboid zone permissions
        for (SpellZone zone : spellZones)
        {
            if (zone.zoneType != SpellZoneType.CUBOID)
                continue;

            if (zone.cuboid.isInside(location, common) && zone.disallowedSpells.contains(spellType))
            {
                return true;
            }
        }

        return false;
    }
}

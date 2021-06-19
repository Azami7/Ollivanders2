package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.common.Cuboid;
import org.bukkit.Location;
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

    private enum SpellZoneType
    {
        CUBOID,
        WORLD,
        WORLD_GUARD;
    }

    /**
     * A zone that spells can be explicitly allowed and disallowed
     */
    private class SpellZone
    {
        String zoneName;
        SpellZoneType zoneType;
        final Cuboid cuboid;

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

            if (type == SpellZoneType.CUBOID)
                cuboid = new Cuboid(world, area);
            else
            {
                int[] emptyArea = {0, 0, 0, 0, 0, 0};
                cuboid = new Cuboid(world, emptyArea);
            }

            disallowedSpells = disallowed;
            allowedSpells = allowed;
        }

        /**
         * Is the spell allowed in this zone
         *
         * @param spellType the spell to check
         * @return true if allowed, false otherwise
         */
        public boolean isAllowed (@NotNull O2SpellType spellType, @NotNull Location location)
        {
            // is this a world guard zone and the location is in a WG rehion with this name
            // OR this is a cuboid area and the location is inside the cuboid
            if ((zoneType == SpellZoneType.WORLD && location.getWorld().getName().equalsIgnoreCase(zoneName))
                    || (zoneType == SpellZoneType.WORLD_GUARD && !Ollivanders2.worldGuardO2.isLocationInRegionByName(zoneName, location))
                    || (zoneType == SpellZoneType.CUBOID) && !cuboid.isInside(location))
            {
                common.printDebugMessage("O2Spells.isAllowed: location is not in zone " + zoneName, null, null, false);
                return false;
            }

            common.printDebugMessage("O2Spells.isAllowed: location is in zone " + zoneName, null, null, false);

            // check allowed list
            if (allowedSpells.size() > 0)
                return allowedSpells.contains(spellType);

            // check disallowed list
            return !(disallowedSpells.contains(spellType));
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
            if (zone.equalsIgnoreCase("global"))
            {
                loadGlobalZoneConfig(p);
            }
            else
            {
                loadZoneConfig(zone, p);
            }
        }
    }

    /**
     * Load global zone spell allow/disallow lists
     *
     * @param p a callback to the plugin
     */
    private void loadGlobalZoneConfig (@NotNull Ollivanders2 p)
    {
        String global = "global";

        // check allowed first since globalAllowedSpells has precedence over globalDisallowedSpells
        globalAllowedSpells = getSpellsForZone(global, allowedList);
        if (globalAllowedSpells.size() > 0)
        {
            p.getLogger().info("Setting globally allowed spells: ");
            for (O2SpellType spellType : globalAllowedSpells)
            {
                p.getLogger().info("  - " + spellType.toString());
            }

            return;
        }

        globalDisallowedSpells = getSpellsForZone(global, disallowList);
        if (globalDisallowedSpells.size() > 0)
        {
            p.getLogger().info("Setting globally disallowed spells: ");
            for (O2SpellType spellType : globalDisallowedSpells)
            {
                p.getLogger().info("  - " + spellType.toString());
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
            return;
        }

        String world = "";
        if (type == SpellZoneType.WORLD || type == SpellZoneType.CUBOID)
        {
            world = zoneConfig.getString(zoneName + "." + "world");

            if (world == null || world.length() < 1)
            {
                common.printDebugMessage("O2Spells.loadZoneConfig: world or cuboid zone " + zoneName + " with no world name set, ignored.", null, null, false);
                return;
            }
        }

        int[] area = {0, 0, 0, 0, 0, 0};

        if (type == SpellZoneType.CUBOID)
        {
            String areaString = zoneConfig.getString(zoneName + "." + "area");

            if (areaString == null || areaString.length() < 1)
            {
                common.printDebugMessage("O2Spells.loadZoneConfig: cuboid zone " + zoneName + " with no area coordinates set, ignored", null, null, false);
                return;
            }

            area = Cuboid.parseArea(areaString);
            if (area == null)
                return;
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

        for (String spell : zoneConfig.getStringList(zoneName + "." + list))
        {
            O2SpellType spellType = O2SpellType.spellTypeFromString(spell.toUpperCase());
            if (spellType != null && isLoaded(spellType))
            {
                spellList.add(spellType);
            }
        }

        return spellList;
    }

    /**
     * Check if a spell is allowed based on zone config
     *
     * @param spellType the spell type to check
     * @param location the location of the spell
     * @return true if spell is allowed, false otherwise
     */
    public boolean checkSpellZoneConfig (@NotNull O2SpellType spellType, @NotNull Location location)
    {
        if (!isLoaded(spellType))
            return false;



        return true;
    }

    /**
     * Check if a spell is allowed based on zone config
     *
     * @param spellname the name of the spell
     * @param location the location of the spell
     * @return true if spell is allowed, false otherwise
     */
    public boolean checkSpellZoneConfig (@NotNull String spellname, @NotNull Location location)
    {
        O2SpellType spellType = O2SpellType.spellTypeFromString(spellname);

        if (spellType != null)
        {
            return checkSpellZoneConfig(spellType, location);
        }
        else
            return true;
    }
}

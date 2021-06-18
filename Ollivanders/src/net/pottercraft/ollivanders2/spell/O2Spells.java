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
     * Spell allowed/disallowed zones
     */
    private ConfigurationSection zoneConfig;
    private ArrayList<O2SpellType> globalDisallowedSpells = new ArrayList<>();
    private ArrayList<O2SpellType> globalAllowedSpells = new ArrayList<>();

    private enum spellZoneType
    {
        WORLD_GUARD,
        CUBOID;
    }

    private class SpellZone
    {
        spellZoneType zoneType;
        Cuboid cuboid = null;

        ArrayList<O2SpellType> disallowedSpells = new ArrayList<>();
        ArrayList<O2SpellType> allowedSpells = new ArrayList<>();

        SpellZone (@NotNull spellZoneType type, int[] area, @NotNull ArrayList<O2SpellType> allowed, @NotNull ArrayList<O2SpellType> disallowed)
        {
            zoneType = type;

            if (type == spellZoneType.WORLD_GUARD)
                cuboid = new Cuboid(area);

            allowedSpells = new ArrayList<>(allowed);
            disallowedSpells = new ArrayList<>(disallowed);
        }

        /**
         * Is the spell allowed in this zone
         *
         * @param spellType the spell to check
         * @return true if allowed, false otherwise
         */
        public boolean isAllowed (O2SpellType spellType, Location location)
        {
            // is this location in this zone
            if (zoneType == spellZoneType.WORLD_GUARD)
            {

            }
            else
            {
                if (cuboid == null)
                    return true;


            }

            return true;
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

        loadGlobalZoneConfig(p);


    }

    private void loadGlobalZoneConfig (@NotNull Ollivanders2 p)
    {
        if (zoneConfig == null)
            return;

        for (String zone : zoneConfig.getKeys(false))
        {
            if (!zone.equalsIgnoreCase("default-world"))
                continue;

            // check allowed first since globalAllowedSpells has precedence over globalDisallowedSpells
            for (String spell : zoneConfig.getStringList(zone + ".allowed-spells"))
            {
                O2SpellType spellType = O2SpellType.spellTypeFromString(spell);
                if (spellType != null && isLoaded(spellType))
                    globalAllowedSpells.add(spellType);
            }

            if (globalAllowedSpells.size() > 0)
                return;

            for (String spell : zoneConfig.getStringList(zone + ".disallowed-spells"))
            {
                O2SpellType spellType = O2SpellType.spellTypeFromString(spell);
                if (spellType != null)
                    globalDisallowedSpells.add(spellType);
            }
        }
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

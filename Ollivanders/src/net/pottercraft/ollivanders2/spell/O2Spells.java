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
 * Central manager for all Ollivanders2 spells: registration, loading, per-tick upkeep of active projectiles, and
 * zone-based cast permissions.
 * <p>
 * A spell's cast permission is resolved by {@link #isSpellTypeAllowed(Location, O2SpellType)}: a global allow list,
 * if set, takes precedence; otherwise a matching zone's allow list (WORLD, WORLD_GUARD, or CUBOID) governs; finally
 * global and zone disallow lists can deny an otherwise-allowed spell.
 * </p>
 *
 * @author Azami7
 */
public class O2Spells {
    private final Ollivanders2 p;

    private final Ollivanders2Common common;

    final private List<O2Spell> activeSpells = new ArrayList<>();

    /**
     * Loaded spell types keyed by lowercased display name. Static, so only spells that survived {@link #onEnable()}
     * plugin-dependency filtering are present.
     */
    final static private Map<String, O2SpellType> O2SpellMap = new HashMap<>();

    /**
     * Spells that can be cast without a wand. Populated at startup; divination spells are added in {@link #onEnable()}.
     */
    public static final List<O2SpellType> wandlessSpells = new ArrayList<>() {{
        add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
    }};

    private ConfigurationSection zoneConfig;

    /**
     * Spells denied everywhere. A disallow list denies a spell that is not otherwise permitted by an allow list.
     */
    private ArrayList<O2SpellType> globalDisallowedSpells = new ArrayList<>();

    /**
     * Spells permitted everywhere. When non-empty, only these spells may be cast anywhere, overriding all other lists.
     */
    private ArrayList<O2SpellType> globalAllowedSpells = new ArrayList<>();

    /**
     * The configured per-zone allow/disallow rules.
     */
    final ArrayList<SpellZone> spellZones = new ArrayList<>();

    /**
     * @param plugin a callback to the MC plugin
     */
    public O2Spells(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);
    }

    /**
     * Initialize the spell subsystem at plugin startup. Only spells whose plugin dependencies are met are registered,
     * so a spell needing an absent plugin (e.g. LibsDisguises) is left unloaded.
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
     * Kill all active spells at plugin shutdown.
     */
    public void onDisable() {
        // kill all active spells
        for (O2Spell spell : activeSpells) {
            spell.kill();
        }
    }

    /**
     * Advance every active spell one game tick via {@link O2Spell#checkEffect()} and drop any that were killed.
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
     * Get all loaded spell types.
     *
     * @return a copy of the loaded spell type list
     */
    public static List<O2SpellType> getAllSpellTypes() {
        return new ArrayList<>(O2SpellMap.values());
    }

    /**
     * Get a spell type by its display name, matched case-insensitively.
     *
     * @param name the spell's display name
     * @return the matching spell type, or null if none is loaded under that name
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

        return !(isExplicitlyDisallowed(location, spellType));
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
     * Register a cast spell as active and increment the player's cast count for it. The count is incremented twice
     * when the player has the FAST_LEARNING effect.
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
     * Get the active spells.
     *
     * @return a copy of the active spell list
     */
    @NotNull
    public List<O2Spell> getActiveSpells() {
        return new ArrayList<>(activeSpells);
    }

    /**
     * Construct a spell instance of the given type. The returned spell is not yet active; register it with
     * {@link #addSpell(Player, O2Spell)} to run it.
     *
     * @param player the player that cast the spell
     * @param name   the spell type to create
     * @param wandC  the wand check value for the held wand
     * @return the new spell, or null if construction failed
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

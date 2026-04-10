package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.common.Cuboid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for restricting spells in a particular area of the world.
 * <p>
 * A spell zone defines an area — a cuboid, an entire Minecraft world, or a WorldGuard region —
 * along with the spells that are explicitly allowed or disallowed there. The zone itself is a
 * passive value object: actual enforcement of the allow/disallow rules lives in
 * {@link O2Spells#isSpellTypeAllowed(org.bukkit.Location, O2SpellType)}, which iterates the
 * configured zones and applies the allow list (which takes precedence) or disallow list to a
 * candidate spell cast.
 * </p>
 * <p>
 * Zones are loaded from the plugin's zones config at startup by {@link O2Spells}; see the
 * referenced wiki page for the config schema.
 * </p>
 *
 * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#zones">Ollivanders2 Wiki - Zone Configuration</a>
 */
public class SpellZone {
    /**
     * The kind of area a {@link SpellZone} covers.
     */
    public enum SpellZoneType {
        /**
         * A 3D bounding box defined by two opposite corner coordinates within a single world.
         */
        CUBOID,
        /**
         * An entire Minecraft world, identified by world name.
         */
        WORLD,
        /**
         * A WorldGuard region, identified by region name within a world.
         */
        WORLD_GUARD
    }

    /**
     * Config sub-key for the per-zone allowed-spells list.
     */
    final static String allowedList = "allowed-spells";

    /**
     * Config sub-key for the per-zone disallowed-spells list.
     */
    final static String disallowList = "disallowed-spells";

    /**
     * Reserved zone name for the global allow/disallow lists that apply across all worlds.
     */
    final static String globalZoneName = "global";

    /**
     * Sentinel value indicating an empty spell list in the zones config.
     */
    final static String none = "NONE";

    /**
     * Sentinel value indicating that the spell list should match every loaded spell type.
     */
    final static String all = "ALL";

    /**
     * The name of this zone, used as the lookup key in the zones config.
     */
    final String zoneName;

    /**
     * The kind of area this zone covers. See {@link SpellZoneType}.
     */
    final SpellZoneType zoneType;

    /**
     * The cuboid backing this zone. Only meaningful when {@link #zoneType} is
     * {@link SpellZoneType#CUBOID}; for other zone types this is a stub at (0,0,0,0,0,0)
     * and must not be queried for containment.
     */
    final Cuboid cuboid;

    /**
     * The name of the Minecraft world this zone is in.
     */
    final String zoneWorldName;

    /**
     * Spells that are explicitly disallowed in this zone. Stored as a defensive copy of the
     * caller-supplied list.
     */
    final List<O2SpellType> disallowedSpells;

    /**
     * Spells that are explicitly allowed in this zone. When non-empty, the consumer
     * ({@link O2Spells#isSpellTypeAllowed(org.bukkit.Location, O2SpellType)}) treats this as
     * the only allowed set in the zone — taking precedence over {@link #disallowedSpells}.
     * Stored as a defensive copy of the caller-supplied list.
     */
    final List<O2SpellType> allowedSpells;

    /**
     * Constructs a spell zone from values supplied by the zones config loader.
     * <p>
     * The {@code area} array is consumed only when {@code type} is {@link SpellZoneType#CUBOID};
     * for {@link SpellZoneType#WORLD} and {@link SpellZoneType#WORLD_GUARD} zones the cuboid
     * field is constructed as a stub and must not be queried. Both spell lists are stored as
     * defensive copies, so mutating the caller's lists after construction does not affect the
     * resulting zone.
     * </p>
     *
     * @param name       the name of this zone, used as the config-lookup key
     * @param world      the name of the Minecraft world this zone is in
     * @param type       the kind of area this zone covers
     * @param area       opposite-corner coordinates {@code [x1,y1,z1,x2,y2,z2]} for CUBOID
     *                   zones; ignored for WORLD and WORLD_GUARD zones
     * @param allowed    spells explicitly allowed in this zone; when non-empty, the consumer
     *                   ({@link O2Spells#isSpellTypeAllowed(org.bukkit.Location, O2SpellType)})
     *                   treats this as the only allowed set, taking precedence over
     *                   {@code disallowed}
     * @param disallowed spells explicitly disallowed in this zone
     */
    public SpellZone(@NotNull String name, @NotNull String world, @NotNull SpellZoneType type, int[] area, @NotNull ArrayList<O2SpellType> allowed, @NotNull ArrayList<O2SpellType> disallowed) {
        zoneName = name;
        zoneType = type;
        zoneWorldName = world;

        if (type == SpellZoneType.CUBOID)
            cuboid = new Cuboid(zoneWorldName, area);
        else {
            int[] emptyArea = {0, 0, 0, 0, 0, 0};
            cuboid = new Cuboid(zoneWorldName, emptyArea);
        }

        // defensive copies — caller-supplied lists must not be aliased
        disallowedSpells = new ArrayList<>(disallowed);
        allowedSpells = new ArrayList<>(allowed);
    }

    /**
     * Get the name of this zone.
     *
     * @return the zone name
     */
    @NotNull
    public String getZoneName() {
        return zoneName;
    }

    /**
     * Get the type of this zone.
     *
     * @return the zone type (CUBOID, WORLD, or WORLD_GUARD)
     */
    @NotNull
    public SpellZoneType getZoneType() {
        return zoneType;
    }

    /**
     * Get the name of the world this zone is in.
     *
     * @return the world name
     */
    @NotNull
    public String getZoneWorldName() {
        return zoneWorldName;
    }

    /**
     * Get the cuboid backing this zone. Only meaningful when {@link #getZoneType()} is CUBOID;
     * non-CUBOID zones return a stub cuboid that should not be queried for containment.
     *
     * @return the cuboid for this zone
     */
    @NotNull
    public Cuboid getCuboid() {
        return cuboid;
    }

    /**
     * Get a fresh defensive copy of the spells allowed in this zone. Mutating the returned
     * list does not affect the zone's stored state, and successive calls return distinct
     * list instances.
     *
     * @return a defensive copy of the allowed spells list
     */
    @NotNull
    public List<O2SpellType> getAllowedSpells() {
        return new ArrayList<>(allowedSpells);
    }

    /**
     * Get a fresh defensive copy of the spells disallowed in this zone. Mutating the returned
     * list does not affect the zone's stored state, and successive calls return distinct
     * list instances.
     *
     * @return a defensive copy of the disallowed spells list
     */
    @NotNull
    public List<O2SpellType> getDisallowedSpells() {
        return new ArrayList<>(disallowedSpells);
    }
}

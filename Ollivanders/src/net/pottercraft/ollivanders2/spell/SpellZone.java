package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.common.Cuboid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Allow/disallow spells in an area
 *
 * @see <a href = "https://github.com/Azami7/Ollivanders2/wiki/Configuration#zones">https://github.com/Azami7/Ollivanders2/wiki/Configuration#zones</a>
 */
public class SpellZone {
    /**
     * Type of spell zones that can be defined
     */
    public enum SpellZoneType {
        /**
         * bounding box
         */
        CUBOID,
        /**
         * entire world
         */
        WORLD,
        /**
         * world guard region
         */
        WORLD_GUARD;
    }

    /**
     * String keys for zone config
     */
    final static String allowedList = "allowed-spells";
    final static String disallowList = "disallowed-spells";
    final static String globalZoneName = "global";
    final static String none = "NONE";
    final static String all = "ALL";

    String zoneName;
    SpellZoneType zoneType;
    final Cuboid cuboid;
    String zoneWorldName;

    ArrayList<O2SpellType> disallowedSpells;
    ArrayList<O2SpellType> allowedSpells;

    /**
     * Constructor
     *
     * @param name       the name of this zone
     * @param world      the name of the world this zone is in
     * @param type       the type of zone
     * @param area       the area bounds for this zone if a cuboid, this should be opposite corners
     * @param allowed    a list of allowed spells, if set, this will take precedence over a disallow list
     * @param disallowed a list of disallowed spells
     */
    SpellZone(@NotNull String name, @NotNull String world, @NotNull SpellZoneType type, int[] area, @NotNull ArrayList<O2SpellType> allowed, @NotNull ArrayList<O2SpellType> disallowed) {
        zoneName = name;
        zoneType = type;
        zoneWorldName = world;

        if (type == SpellZoneType.CUBOID)
            cuboid = new Cuboid(zoneWorldName, area);
        else {
            int[] emptyArea = {0, 0, 0, 0, 0, 0};
            cuboid = new Cuboid(zoneWorldName, emptyArea);
        }

        disallowedSpells = disallowed;
        allowedSpells = allowed;
    }
}

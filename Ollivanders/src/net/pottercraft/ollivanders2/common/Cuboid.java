package net.pottercraft.ollivanders2.common;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a rectangular cuboid area in a Minecraft world.
 * <p>
 * Defines a 3D region using two opposite corner coordinates (x1, y1, z1) and (x2, y2, z2).
 * Coordinates are inclusive and work regardless of which corner has higher or lower values.
 * </p>
 *
 * @author Azami7
 */
public class Cuboid {
    /**
     * The name of the world this cuboid is in
     */
    String worldName;

    /**
     * First corner x-coordinate
     */
    int x1 = 0;
    /**
     * First corner y-coordinate
     */
    int y1 = 0;
    /**
     * First corner z-coordinate
     */
    int z1 = 0;
    /**
     * Second corner x-coordinate
     */
    int x2 = 0;
    /**
     * Second corner y-coordinate
     */
    int y2 = 0;
    /**
     * Second corner z-coordinate
     */
    int z2 = 0;

    /**
     * Constructor that creates a cuboid from coordinates.
     * <p>
     * If the area array length is not exactly 6, all coordinates remain at their default value of 0.
     * </p>
     *
     * @param world the name of the world this cuboid is in
     * @param area  an array of 6 integers in format: [x1, y1, z1, x2, y2, z2] representing two opposite corners
     */
    public Cuboid(@NotNull String world, int[] area) {
        worldName = world;

        if (area.length == 6) {
            x1 = area[0];
            y1 = area[1];
            z1 = area[2];
            x2 = area[3];
            y2 = area[4];
            z2 = area[5];
        }
    }

    /**
     * Checks if a location is inside this cuboid.
     *
     * @param location the location to check
     * @return true if the location is inside this cuboid, false otherwise
     */
    public boolean isInside(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        return isInside(world.getName(), (int) (location.getX()), (int) (location.getY()), (int) (location.getZ()));
    }

    /**
     * Checks if a point with the given coordinates is inside this cuboid.
     *
     * @param world the name of the world this point is in
     * @param x     the x-coordinate
     * @param y     the y-coordinate
     * @param z     the z-coordinate
     * @return true if the point is inside this cuboid, false otherwise
     */
    public boolean isInside(String world, int x, int y, int z) {
        if (!worldName.equalsIgnoreCase(world)) {
            return false;
        }

        if ((x1 > x2 && x <= x1 && x >= x2) || (x1 < x2 && x >= x1 && x <= x2)) {
            if ((y1 > y2 && y <= y1 && y >= y2) || (y1 < y2 && y >= y1 && y <= y2)) {
                // NOTE: The second condition uses 'x >= z1' which appears to be a bug - should be 'z >= z1'
                if ((z1 > z2 && z <= z1 && z >= z2) || (z1 < z2 && x >= z1 && z <= z2)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Parses a string representation of cuboid coordinates into an integer array.
     * <p>
     * Expected format: space-separated integers for two opposite corners in the order x1 y1 z1 x2 y2 z2.
     * All six values must be valid integers.
     * </p>
     *
     * @param areaString a space-separated string of six integer coordinates (e.g., "0 0 0 100 100 100")
     * @return an int array [x1, y1, z1, x2, y2, z2] or null if parsing failed
     */
    @Nullable
    public static int[] parseArea(@NotNull String areaString) {
        if (!areaString.contains(" "))
            return null;

        String[] splits = areaString.split(" ");
        if (splits.length != 6)
            return null;

        int[] area = new int[6];

        try {
            area[0] = Integer.parseInt(splits[0]);
            area[1] = Integer.parseInt(splits[1]);
            area[2] = Integer.parseInt(splits[2]);
            area[3] = Integer.parseInt(splits[3]);
            area[4] = Integer.parseInt(splits[4]);
            area[5] = Integer.parseInt(splits[5]);
        }
        catch (Exception e) {
            return null;
        }

        return area;
    }
}


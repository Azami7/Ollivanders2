package net.pottercraft.ollivanders2.common;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An Area Cuboid
 *
 * @author Azami7
 */
public class Cuboid
{
    String worldName;

    int x1 = 0;
    int y1 = 0;
    int z1 = 0;
    int x2 = 0;
    int y2 = 0;
    int z2 = 0;

    /**
     * Constructor
     *
     * @param world the name of the world this cuboid is in
     * @param area two opposite corner points of the Cuboid
     */
    public Cuboid (@NotNull String world, @NotNull int[] area)
    {
        worldName = world;

        if (area.length == 6)
        {
            x1 = area[0];
            y1 = area[1];
            z1 = area[2];
            x2 = area[3];
            y2 = area[4];
            z2 = area[5];
        }
    }

    /**
     * Is the location inside this Cuboid
     *
     * @param location the location to check
     * @return true if inside, false otherwise
     */
    public boolean isInside (@NotNull Location location, Ollivanders2Common common)
    {
        World world = location.getWorld();
        if (world == null)
        {
            common.printDebugMessage("Cuboid.isInside: World is null", null, null, true);
            return false;
        }

        return isInside(world.getName(), (int)(location.getX()), (int)(location.getY()), (int)(location.getZ()), common);
    }

    /**
     * Is the location point inside this Cuboid
     *
     * @param world the name of the world this point is in
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return true if inside, false otherwise
     */
    public boolean isInside (String world, int x, int y, int z, Ollivanders2Common common)
    {
        if (!worldName.equalsIgnoreCase(world))
        {
            return false;
        }

        if ((x1 > x2 && x <= x1 && x >= x2) || (x1 < x2 && x >= x1 && x <= x2))
        {
            if ((y1 > y2 && y <= y1 && y >= y2) || (y1 < y2 && y >= y1 && y <= y2))
            {
                if ((z1 > z2 && z <= z1 && z >= z2) || (z1 < z2 && x >= z1 && z <= z2))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Parse a cuboid area from a string
     *
     * @param areaString a string of two x, y, z coordinates in the format "0 0 0 0 0 0"
     * @return an int array of two x, y, z coordinates or null if parse failed
     */
    @Nullable
    public static int[] parseArea (@NotNull String areaString)
    {
        if (!areaString.contains(" "))
            return null;

        String[] splits = areaString.split(" ");
        if (splits.length != 6)
            return null;

        int[] area = new int[6];

        try
        {
            area[0] = Integer.parseInt(splits[0]);
            area[1] = Integer.parseInt(splits[1]);
            area[2] = Integer.parseInt(splits[2]);
            area[3] = Integer.parseInt(splits[3]);
            area[4] = Integer.parseInt(splits[4]);
            area[5] = Integer.parseInt(splits[5]);
        }
        catch (Exception e)
        {
            return null;
        }

        return area;
    }
}


package net.pottercraft.ollivanders2.common;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * An Area Cuboid
 *
 * @author Azami7
 */
public class Cuboid
{
    int x1 = 0;
    int y1 = 0;
    int z1 = 0;
    int x2 = 0;
    int y2 = 0;
    int z2 = 0;

    /**
     * Constructor
     *
     * @param area two opposite corner points of the Cuboid
     */
    public Cuboid (@NotNull int[] area)
    {
        if (area.length == 6)
        {
            int x1 = area[0];
            int y1 = area[1];
            int z1 = area[2];
            int x2 = area[3];
            int y2 = area[4];
            int z2 = area[5];
        }
    }

    /**
     * Is the location inside this Cuboid
     *
     * @param location the location to check
     * @return true if inside, false otherwise
     */
    public boolean isInside (@NotNull Location location)
    {
        return isInside((int)(location.getY()), (int)(location.getX()), (int)(location.getZ()));
    }

    /**
     * Is the location inside this Cuboid
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return true if inside, false otherwise
     */
    public boolean isInside (int x, int y, int z)
    {
        if ((x1 > x2 && x < x1 && x > x2) || (x1 < x2 && x > x1 && x < x2))
        {
            if ((y1 > y2 && y < y1 && y > y2) || (y1 < y2 && y > y1 && y < y2))
            {
                if ((z1 > z2 && z < z1 && z > z2) || (z1 < z2 && x > z1 && z < z2))
                    return true;
                else
                    return false;
            }
            else
                return false;
        }
        else
            return false;
    }
}


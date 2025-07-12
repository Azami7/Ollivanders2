package net.pottercraft.ollivanders2;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles all WorldGuard support for Ollivanders2.  If WorldGuard is not enabled on the server, all calls
 * will short circuit to allow actions.
 *
 * @author Azami7
 * @see <a href = "https://worldguard.enginehub.org/en/latest/developer/">https://worldguard.enginehub.org/en/latest/developer/</a>
 * @since 2.2.5
 */
public class Ollivanders2WorldGuard {
    private Ollivanders2Common common;
    private WorldGuardPlugin worldGuard;
    final private Ollivanders2 p;

    /**
     * Constructor.
     *
     * @param o2plugin a callback to the ollivanders plugin
     * @see <a href = "https://worldguard.enginehub.org/en/latest/developer/dependency/">https://worldguard.enginehub.org/en/latest/developer/dependency/</a>
     */
    public Ollivanders2WorldGuard(@NotNull Ollivanders2 o2plugin) {
        p = o2plugin;
        worldGuard = null;
        common = new Ollivanders2Common(p);

        Plugin wg = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (wg != null) {
            try {
                if (wg instanceof WorldGuardPlugin && wg.isEnabled()) {
                    if (wg.getDescription().getVersion().startsWith("7")) {
                        worldGuard = (WorldGuardPlugin) wg;
                    }
                }
            }
            catch (Exception e) {
                p.getLogger().info("Failed to get WorldGuard plugin, WorldGuard features will be disabled.");
            }
        }

        if (worldGuard == null)
            Ollivanders2.worldGuardEnabled = false;
    }

    /**
     * Test the state of a specific WorldGuard protection.
     *
     * @param player the player to check for
     * @param flag   the state flag for this region
     * @return true if the player can take the action, false otherwise
     */
    private boolean wgTestState(@NotNull Player player, @NotNull Location location, @NotNull StateFlag flag) {
        if (worldGuard == null) {
            return true;
        }

        ApplicableRegionSet regionSet = getWGRegionSet(location);
        if (regionSet != null && !regionSet.getRegions().isEmpty()) {
            StateFlag.State state = regionSet.queryState(worldGuard.wrapPlayer(player), flag);
            if (state == null) {
                return true;
            }

            return (state != StateFlag.State.DENY);
        }
        else
            return true;
    }

    /**
     * Get the regions in this location.
     *
     * @param location the locationto check
     * @return the set of regions for this location or null if there is no set.
     */
    @Nullable
    public ApplicableRegionSet getWGRegionSet(@NotNull Location location) {
        if (worldGuard == null)
            return null;

        World world = location.getWorld();
        if (world == null)
            return null;

        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();

        if (regionContainer == null) {
            common.printDebugMessage("Failed to get RegionContainer...", null, null, false);
            return null;
        }

        RegionQuery query = regionContainer.createQuery();
        if (query == null) {
            common.printDebugMessage("Failed to get RegionQuery...", null, null, false);
            return null;
        }

        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        com.sk89q.worldedit.util.Location weLocation = new com.sk89q.worldedit.util.Location(weWorld, location.getX(), location.getY(), location.getZ());

        return query.getApplicableRegions(weLocation);
    }

    /**
     * If WorldGuard is enabled, determine if player is allowed a specific permission.  This is done so that
     * Ollivanders2 actions do not complete partially, such as mob transfiguration spells, and then cannot
     * complete because WorldGuard is enabled.
     *
     * @param player   the player to check for
     * @param location the location to check (since it may not be where the player is)
     * @param flag     the flag to check
     * @return true if the player has this permission at this location, false otherwise
     */
    public boolean checkWGFlag(@NotNull Player player, @NotNull Location location, @NotNull StateFlag flag) {
        return wgTestState(player, location, flag);
    }

    /**
     * If WorldGuard is enabled, determine if the player has build permissions.  This is done so that
     * Ollivanders2 actions do not complete partially, such as object transformation spells, and then
     * cannot complete because WorldGuard is enabled.
     *
     * @param player   the player to check WG for
     * @param location the location to check (since it may not be where the player is)
     * @return true if the player has permissions to build at their location, false otherwise
     */
    public boolean checkWGBuild(@NotNull Player player, @NotNull Location location) {
        if (worldGuard == null)
            return true;

        ApplicableRegionSet regionSet = getWGRegionSet(location);
        if (regionSet == null)
            return true;

        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return regionSet.testState(wgPlayer, Flags.BUILD);
    }

    /**
     * Determine if a location is in a region by name
     *
     * @param regionName the name of the region to check
     * @param location   the location to check
     * @return true if it is inside a region with this name, false otherwise
     */
    public boolean isLocationInRegionByName(String regionName, Location location) {
        ApplicableRegionSet regionSet = getWGRegionSet(location);

        if (regionSet == null || regionSet.size() < 1)
            return false;

        for (ProtectedRegion region : regionSet.getRegions()) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }
}

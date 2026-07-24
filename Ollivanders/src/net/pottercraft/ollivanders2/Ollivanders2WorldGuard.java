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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles all WorldGuard support for Ollivanders2. When WorldGuard is not usable, permission checks short
 * circuit to allow the action; region-name lookups short circuit to "not in the region".
 *
 * @author Azami7
 * @see <a href="https://worldguard.enginehub.org/en/latest/developer/">https://worldguard.enginehub.org/en/latest/developer/</a>
 */
public class Ollivanders2WorldGuard {
    /**
     * The WorldGuard plugin, or null when it is absent or disabled - all checks short circuit when null.
     */
    private WorldGuardPlugin worldGuard;

    /**
     * Reference to the plugin for logging
     */
    final private Ollivanders2 p;

    /**
     * Locate the WorldGuard plugin and set {@link Ollivanders2#worldGuardEnabled} to whether it is present
     * and enabled.
     *
     * @param o2plugin reference to the plugin
     * @see <a href="https://worldguard.enginehub.org/en/latest/developer/dependency/">https://worldguard.enginehub.org/en/latest/developer/dependency/</a>
     */
    public Ollivanders2WorldGuard(@NotNull Ollivanders2 o2plugin) {
        p = o2plugin;
        worldGuard = null;

        Plugin wg = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (wg != null) {
            try {
                if (wg instanceof WorldGuardPlugin && wg.isEnabled()) {
                    worldGuard = (WorldGuardPlugin) wg;
                }

                Ollivanders2.worldGuardEnabled = true;
            }
            catch (Exception e) {
                p.getLogger().info("Failed to get WorldGuard plugin, WorldGuard features will be disabled.");

                Ollivanders2.worldGuardEnabled = false;
            }
        }

        if (worldGuard == null)
            Ollivanders2.worldGuardEnabled = false;
    }

    /**
     * Test a WorldGuard state flag for a player at a location. Allows the action unless a region at the
     * location explicitly denies the flag - no regions, an unset flag, or WorldGuard unavailable all allow.
     *
     * @param player   the player to check for
     * @param location the location to check
     * @param flag     the state flag to check
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
     * Get the WorldGuard regions at a location.
     *
     * @param location the location to check
     * @return the applicable region set (which may be empty), or null if WorldGuard is unavailable or the
     * location has no world
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
            Ollivanders2API.common.printDebugMessage("Failed to get RegionContainer...", null, null, false);
            return null;
        }

        RegionQuery query = regionContainer.createQuery();
        if (query == null) {
            Ollivanders2API.common.printDebugMessage("Failed to get RegionQuery...", null, null, false);
            return null;
        }

        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        com.sk89q.worldedit.util.Location weLocation = new com.sk89q.worldedit.util.Location(weWorld, location.getX(), location.getY(), location.getZ());

        return query.getApplicableRegions(weLocation);
    }

    /**
     * Determine if a player is allowed an action at a location, so actions can be permission-checked up front
     * rather than failing partway through, such as mid-transfiguration.
     *
     * @param player   the player to check for
     * @param location the location to check (since it may not be where the player is)
     * @param flag     the flag to check
     * @return true if the player has this permission at this location or WorldGuard is unavailable, false otherwise
     */
    public boolean checkWGFlag(@NotNull Player player, @NotNull Location location, @NotNull StateFlag flag) {
        return wgTestState(player, location, flag);
    }

    /**
     * Determine if a player has build permission at a location, so actions can be permission-checked up front
     * rather than failing partway through, such as mid-transfiguration.
     *
     * @param player   the player to check for
     * @param location the location to check (since it may not be where the player is)
     * @return true if the player can build at this location or WorldGuard is unavailable, false otherwise
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
     * Determine if a location is inside a named WorldGuard region.
     *
     * @param regionName the region id to check, case-insensitive
     * @param location   the location to check
     * @return true if a region with this id contains the location, false otherwise or if WorldGuard is unavailable
     */
    public boolean isLocationInRegionByName(@NotNull String regionName, @NotNull Location location) {
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

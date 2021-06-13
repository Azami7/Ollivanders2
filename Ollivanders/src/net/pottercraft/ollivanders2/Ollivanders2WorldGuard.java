package net.pottercraft.ollivanders2;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
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
 * Handles all WorldGuard support for Ollivanders2.  If WorldGuard is not enabled on the server, all calls
 * will short circuit to allow actions.
 *
 * @since 2.2.5
 * @link https://worldguard.enginehub.org/en/latest/developer/
 * @author Azami7
 */
public class Ollivanders2WorldGuard
{
   private WorldGuardPlugin worldGuard;
   final private Ollivanders2 p;

   /**
    * Constructor.
    *
    * @link https://worldguard.enginehub.org/en/latest/developer/dependency/
    */
   public Ollivanders2WorldGuard (@NotNull Ollivanders2 o2plugin)
   {
      p = o2plugin;
      worldGuard = null;

      Plugin wg = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

      if (wg != null)
      {
         try
         {
            if (wg instanceof WorldGuardPlugin && wg.isEnabled())
            {
               if (wg.getDescription().getVersion().startsWith("7"))
               {
                  worldGuard = (WorldGuardPlugin) wg;
               }
            }
         }
         catch (Exception e)
         {
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
    * @param flag the state flag for this region
    * @return true if the player can take the action, false otherwise
    */
   private boolean wgTestState (@NotNull Player player, @NotNull Location location, @NotNull StateFlag flag)
   {
      if (worldGuard == null)
      {
         return true;
      }

      ApplicableRegionSet regionSet = getWGRegionSet(location);
      if (regionSet != null && !regionSet.getRegions().isEmpty())
      {
         StateFlag.State state = regionSet.queryState(worldGuard.wrapPlayer(player), flag);
         if (state == null)
         {
            return true;
         }

         if (Ollivanders2.debug)
            p.getLogger().info("State of " + flag.toString() + " for " + player.getDisplayName() + " is " + state.toString());


         return (state != StateFlag.State.DENY);
      }
      else
      {
         if (Ollivanders2.debug)
            p.getLogger().info("No regions defined here.");

         return true;
      }
   }

   /**
    * Get the regions in this location.
    *
    * @param location the locationto check
    * @return the set of regions for this location or null if there is no set.
    */
   @Nullable
   public ApplicableRegionSet getWGRegionSet (@NotNull Location location)
   {
      if (worldGuard == null)
         return null;

      World world = location.getWorld();
      if (world == null)
         return null;

      RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();

      if (regionContainer == null)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Failed to get RegionContainer...");

         return null;
      }

      RegionQuery query = regionContainer.createQuery();
      if (query == null)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Failed to get RegionQuery...");

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
    * @param player the player to check for
    * @param location the location to check (since it may not be where the player is)
    * @return true if the player has this permission at this location, false otherwise
    */
   public boolean checkWGFlag (@NotNull Player player, @NotNull Location location, @NotNull StateFlag flag)
   {
      return wgTestState(player, location, flag);
   }

   /**
    * If WorldGuard is enabled, determine if the player has build permissions.  This is done so that
    * Ollivanders2 actions do not complete partially, such as object transformation spells, and then
    * cannot complete because WorldGuard is enabled.
    *
    * @link https://github.com/Azami7/Ollivanders2/issues/5
    *
    * @param player the player to check WG for
    * @param location the location to check (since it may not be where the player is)
    * @return true if the player has permissions to build at their location, false otherwise
    */
   public boolean checkWGBuild (@NotNull Player player, @NotNull Location location)
   {
      if (worldGuard == null)
         return true;

      ApplicableRegionSet regionSet = getWGRegionSet(location);
      if (regionSet == null)
         return true;

      LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
      return regionSet.testState(wgPlayer, Flags.BUILD);
   }
}

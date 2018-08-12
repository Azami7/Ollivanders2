package net.pottercraft.Ollivanders2;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.sk89q.worldedit.Vector;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

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
   private Ollivanders2 p;

   /**
    * Constructor.
    *
    * @link https://worldguard.enginehub.org/en/latest/developer/dependency/
    */
   public Ollivanders2WorldGuard (Ollivanders2 o2plugin)
   {
      p = o2plugin;

      Plugin wg = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

      if (wg != null)
      {
         try
         {
            if (wg instanceof WorldGuardPlugin && wg.isEnabled())
            {
               worldGuard = (WorldGuardPlugin) wg;
            }
         }
         catch (Exception e)
         {
            worldGuard = null;
         }
      }
      else
      {
         worldGuard = null;
      }
   }

   /**
    * Test the state of a specific WorldGuard protection.
    *
    * @param player the player to check for
    * @param flag the state flag for this region
    * @return true if the player can take the action, false otherwise
    */
   private boolean wgTestState (Player player, Location location, StateFlag flag)
   {
      if (worldGuard == null)
      {
         return true;
      }

      ApplicableRegionSet regionSet = getWGRegionSet(location);
      if (regionSet != null && !regionSet.getRegions().isEmpty())
      {
         StateFlag.State state = regionSet.queryState(worldGuard.wrapPlayer(player), flag);
         if (Ollivanders2.debug)
            p.getLogger().info("State of " + flag.toString() + " for " + player.getDisplayName() + " is " + state.toString());

         if (state == StateFlag.State.DENY)
            return false;
         else
            return true;
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
   private ApplicableRegionSet getWGRegionSet (Location location)
   {
      ApplicableRegionSet regionSet = null;

      if (worldGuard != null)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Getting region manager...");

         RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());

         if (regionManager != null)
         {
            if (Ollivanders2.debug)
               p.getLogger().info("Getting regions...");

            Vector locPt = toVector(location);
            regionSet = regionManager.getApplicableRegions(locPt);
         }
      }

      return regionSet;
   }

   /**
    * If WorldGuard is enabled, determine if this player has permissions to damage friendly mobs in this location.
    * This is done so that Ollivanders2 actions do not complete partially, such as mob transfiguration spells, and then
    * cannot complete because WorldGuard is enabled.
    *
    * @param player the player to check WG for
    * @param location the location to check (since it may not be where the player is)
    * @return true if the player is allowed to damage friendly mobs in this location, false otherwise.
    */
   public boolean checkWGFriendlyMobDamage (Player player, Location location)
   {
      return wgTestState(player, location, DefaultFlag.DAMAGE_ANIMALS);
   }

   /**
    * If WorldGuard is enabled, determine if PVP is allowed in this location. This is done so that Ollivanders2 actions
    * do not complete partially, such as mob transfiguration spells, and then cannot complete because WorldGuard is enabled.
    *
    * @param player the player to check WG for
    * @param location the location to check (since it may not be where the player is)
    * @return true if the player can damage friendly mobs at this location, false otherwise
    */
   public boolean checkWGPVP (Player player, Location location)
   {
      return wgTestState(player, location, DefaultFlag.PVP);
   }

   /**
    * If WorldGuard is enabled, determine if mobs can be spawned at a location.  This is done so that
    * Ollivanders2 actions do not complete partially, such as mob transfiguration spells, and then cannot
    * complete because WorldGuard is enabled.
    *
    * @param player the player to check for
    * @param location the location to check (since it may not be where the player is)
    * @return true if mobs can be spawned in this location, false otherwise
    */
   public boolean checkWGSpawn (Player player, Location location)
   {
      return wgTestState(player, location, DefaultFlag.MOB_SPAWNING);
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
   public boolean checkWGBuild (Player player, Location location)
   {
      if (worldGuard == null)
      {
         return true;
      }
      else
      {
         return worldGuard.canBuild(player, location);
      }
   }
}

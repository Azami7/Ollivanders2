package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hides all blocks within its area by sending out block changes.
 * Hides all players within its area. The code to hide players is located in OllivandersSchedule.invisPlayer()
 *
 * @author lownes
 */
public class REPELLO_MUGGLETON extends ShieldSpell implements StationarySpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public REPELLO_MUGGLETON (Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.REPELLO_MUGGLETON;
   }

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param pid the player who cast the spell
    * @param location the center location of the spell
    * @param type the type of this spell
    * @param radius the radius for this spell
    * @param duration the duration of the spell
    */
   public REPELLO_MUGGLETON (Ollivanders2 plugin, UUID pid, Location location, O2StationarySpellType type, Integer radius,
                             Integer duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.REPELLO_MUGGLETON;
   }

   @Override
   public void checkEffect ()
   {
      age();

      if (duration % Ollivanders2Common.ticksPerSecond == 0)
      {
         Material toMat = getBlock().getType();
         byte toDat = getBlock().getData();
         double viewDistance = Math.sqrt(2 * Math.pow(((Bukkit.getServer().getViewDistance() + 1) * 16), 2));
         for (Player player : getBlock().getWorld().getPlayers())
         {
            if (player.isPermissionSet("Ollivanders2.BYPASS"))
            {
               if (player.hasPermission("Ollivanders2.BYPASS"))
               {
                  continue;
               }
            }
            if (player.getLocation().distance(location) < viewDistance && !isInside(player.getLocation()))
            {
               for (Block block : Ollivanders2API.common.getBlocksInRadius(location, radius))
               {
                  player.sendBlockChange(block.getLocation(), toMat, toDat);
               }
            }
            else if (isInside(player.getLocation()))
            {
               for (Block block : Ollivanders2API.common.getBlocksInRadius(location, radius))
               {
                  player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
               }
            }
         }
      }
      if (duration <= 1)
      {
         for (Player player : getBlock().getWorld().getPlayers())
         {
            for (Block block : Ollivanders2API.common.getBlocksInRadius(location, radius))
            {
               player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
            }
         }
      }
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   @Override
   public Map<String, String> serializeSpellData ()
   {
      return new HashMap<>();
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData (Map<String, String> spellData) { }
}
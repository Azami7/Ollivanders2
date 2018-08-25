package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Hides all blocks within its area by sending out block changes.
 * Hides all players within its area. The code to hide players is located
 * in OllivandersSchedule.invisPlayer()
 *
 * @author lownes
 */
public class REPELLO_MUGGLETON extends StationarySpellObj implements StationarySpell
{
   public REPELLO_MUGGLETON (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(plugin, player, location, name, radius, duration);
   }

   public REPELLO_MUGGLETON (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                             Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration);

      deserializeSpellData(spellData);
   }

   @Override
   public void checkEffect ()
   {
      age();
      if (p.getConfig().getBoolean("muggletonBlockChange"))
      {
         if (duration % 20 == 0)
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
                  for (Block block : p.common.getBlocksInRadius(location, radius))
                  {
                     player.sendBlockChange(block.getLocation(), toMat, toDat);
                  }
               }
               else if (isInside(player.getLocation()))
               {
                  for (Block block : p.common.getBlocksInRadius(location, radius))
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
               for (Block block : p.common.getBlocksInRadius(location, radius))
               {
                  player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
               }
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
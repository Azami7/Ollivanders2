package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Stays in the fireplace and makes the floo network work
 *
 * @author lownes
 */
public class ALIQUAM_FLOO extends StationarySpellObj implements StationarySpell
{
   private String flooName;
   private int countDown = 0;

   private final String flooNameLabel = "name";

   public ALIQUAM_FLOO (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                        String flooName)
   {
      super(plugin, player, location, name, radius, duration);
      this.flooName = flooName;
   }

   public ALIQUAM_FLOO (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                        Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration);

      deserializeSpellData(spellData);
   }

   @Override
   public void checkEffect ()
   {
      Block block = location.getBlock();
      if (block.getType().isSolid())
      {
         kill();
      }
      if (countDown > 0)
      {
         location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);

         for (LivingEntity live : getCloseLivingEntities())
         {
            live.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 0), true);
         }
         countDown--;
      }
      if (block.getType() == Material.FIRE)
      {
         for (Item item : location.getWorld().getEntitiesByClass(Item.class))
         {
            ItemStack stack = item.getItemStack();
            if (item.getLocation().getBlock().equals(block))
            {
               if (stack.getType() == Material.getMaterial(p.getConfig().getString("flooPowder")))
               {
                  if (stack.hasItemMeta())
                  {
                     ItemMeta meta = stack.getItemMeta();
                     if (meta.hasLore())
                     {
                        List<String> lore = meta.getLore();
                        if (lore.contains("Glittery, silver powder"))
                        {
                           countDown += 20 * 60 * stack.getAmount();
                           item.remove();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public String getFlooName ()
   {
      return flooName;
   }

   /**
    * Is it acceping floo destinations?
    *
    * @return
    */
   public boolean isWorking ()
   {
      return countDown > 0;
   }

   /**
    * Stop the floo fireplace working after teleporting.
    */
   public void stopWorking ()
   {
      countDown = 0;
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   @Override
   public Map<String, String> serializeSpellData ()
   {
      Map<String, String> spellData = new HashMap<>();

      spellData.put(flooNameLabel, flooName);

      return spellData;
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData (Map<String, String> spellData)
   {
      for (Entry<String, String> e : spellData.entrySet())
      {
         if (e.getKey().equals(flooNameLabel))
         {
            flooName = e.getValue();
         }
      }
   }
}

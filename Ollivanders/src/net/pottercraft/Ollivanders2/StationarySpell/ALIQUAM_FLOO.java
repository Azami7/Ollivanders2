package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
   private final String flooName;
   private int countDown = 0;

   public ALIQUAM_FLOO (Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                        String flooName)
   {
      super(player, location, name, radius, duration);
      this.flooName = flooName;
   }

   @Override
   public void checkEffect (Ollivanders2 p)
   {
      Location loc = location.toLocation();
      Block block = loc.getBlock();
      if (block.getType().isSolid())
      {
         kill();
      }
      if (countDown > 0)
      {
         loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
         for (LivingEntity live : this.getLivingEntities())
         {
            live.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 0), true);
         }
         countDown--;
      }
      if (block.getType() == Material.FIRE)
      {
         for (Item item : loc.getWorld().getEntitiesByClass(Item.class))
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
}

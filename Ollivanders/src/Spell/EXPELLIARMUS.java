package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Disarms an entity of it's held item, flinging the item in the direction
 * of the caster with force determined by the spell level.
 *
 * @author lownes
 */
public class EXPELLIARMUS extends SpellProjectile implements Spell
{

   public EXPELLIARMUS (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }


   public void checkEffect ()
   {
      move();
      for (LivingEntity entity : getLivingEntities(1))
      {
         ItemStack itemInHand = entity.getEquipment().getItemInMainHand().clone();
         if (itemInHand.getType() != Material.AIR)
         {
            if (holdsWand(entity))
            {
               allyWand(itemInHand);
            }
            entity.getEquipment().setItemInMainHand(null);
            Item item = entity.getWorld().dropItem(entity.getEyeLocation(), itemInHand);
            item.setVelocity(player.getEyeLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(usesModifier / 10));
         }
         kill();
         return;
      }
   }

   /**
    * Does the player hold a wand item?
    *
    * @param entity - Player to check.
    * @return True if the player holds a wand. False if not.
    */
   public boolean holdsWand (LivingEntity entity)
   {
      ItemStack held;
      if (entity.getEquipment().getItemInMainHand() != null)
      {
         held = entity.getEquipment().getItemInMainHand();
         if (held.getType() == Material.STICK || held.getType() == Material.BLAZE_ROD)
         {
            List<String> lore = held.getItemMeta().getLore();
            if (lore.get(0).split(" and ").length == 2)
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
    * Modifies the ItemStack, allying it to the disarming player.
    *
    * @param wand - ItemStack that may be a wand.
    */
   private void allyWand (ItemStack wand)
   {
      ItemMeta wandMeta = wand.getItemMeta();
      List<String> wandLore = wandMeta.getLore();
      if (wandLore.size() == 1)
      {
         wandLore.add(player.getUniqueId().toString());
         wandMeta.setLore(wandLore);
         wand.setItemMeta(wandMeta);
      }
      else
      {
         wandLore.set(1, player.getUniqueId().toString());
      }
      wandMeta.setLore(wandLore);
      wand.setItemMeta(wandMeta);
   }
}
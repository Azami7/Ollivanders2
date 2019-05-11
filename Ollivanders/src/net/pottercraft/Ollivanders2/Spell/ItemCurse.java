package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Place a curse on a item.
 *
 * @author Azami7
 * @since 2.3
 */
public abstract class ItemCurse extends DarkArts
{
   protected String curseLabel;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ItemCurse ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ItemCurse (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      // world guard flags
      worldGuardFlags.add(DefaultFlag.ITEM_PICKUP);
      worldGuardFlags.add(DefaultFlag.ITEM_DROP);

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
   }

   /**
    * Add the curse effect to an item stack in the projectile's location
    */
   @Override
   protected void doCheckEffect ()
   {
      List<Item> items = getItems(1.5);

      if (items.size() > 0)
      {
         Item item = items.get(0);

         // if this is a wand, skip
         if (Ollivanders2API.common.isWand(item.getItemStack()))
         {
            kill();
            return;
         }

         // get item meta
         ItemStack stack = item.getItemStack().clone();
         int amount = stack.getAmount();
         ItemMeta meta = stack.getItemMeta();
         List<String> lore = new ArrayList<>();

         // set the lore on the item to indicate it is affected by Flagrante
         if (meta.hasLore())
         {
            lore = meta.getLore();
            for (int i = 0; i < lore.size(); i++)
            {
               if (lore.get(i).contains(curseLabel))
               {
                  String[] loreParts = lore.get(i).split(" ");
                  int magnitude = Integer.parseInt(loreParts[1]);
                  if (magnitude < usesModifier)
                  {
                     magnitude = (int) usesModifier;
                  }
                  lore.set(i, curseLabel + " " + magnitude);
               }
               else
               {
                  lore.add(curseLabel + " " + (int) usesModifier);
               }
            }
         }
         else
         {
            lore.add(curseLabel + " " + (int) usesModifier);
         }
         meta.setLore(lore);
         stack.setItemMeta(meta);
         stack.setAmount(1);
         if (amount > 1)
         {
            item.getItemStack().setAmount(amount - 1);
         }
         else
         {
            item.remove();
         }
         item.getWorld().dropItem(item.getLocation(), stack);

         kill();
         return;
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}

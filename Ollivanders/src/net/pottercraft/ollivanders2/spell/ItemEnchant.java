package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.EnchantedItems;
import net.pottercraft.ollivanders2.item.ItemEnchantmentType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Place an enchantment on a item.
 *
 * @author Azami7
 * @since 2.3
 */
public abstract class ItemEnchant extends O2Spell
{
   protected ItemEnchantmentType enchantmentType;

   int minMagnitude = 1;
   int maxMagnitude = 100;

   double strength = 1;
   int magnitude;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ItemEnchant()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ItemEnchant (@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.ITEM_PICKUP);
         worldGuardFlags.add(Flags.ITEM_DROP);
      }

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
   }

   @Override
   void doInitSpell ()
   {
      magnitude = (int) ((usesModifier / 4) * strength);

      if (magnitude < minMagnitude)
      {
         magnitude = minMagnitude;
      }
      else if (magnitude > maxMagnitude)
      {
         magnitude = maxMagnitude;
      }

      common.printDebugMessage("Magnitude for enchantment = " + magnitude, null, null, false);
   }

   /**
    * Add the curse effect to an item stack in the projectile's location
    */
   @Override
   protected void doCheckEffect ()
   {
      if (hasHitTarget())
      {
         kill();
         return;
      }

      List<Item> items = getItems(1.5);
      for (Item item : items)
      {
         // if this is a wand, skip it - wands are already enchanted
         if (Ollivanders2API.common.isWand(item.getItemStack()))
         {
            continue;
         }

         // if this item is already enchanted, skip it
         if (Ollivanders2API.getItems(p).enchantedItems.isEnchanted(item))
         {
            continue;
         }

         Item enchantedItem = enchantItem(item);
         if (enchantedItem != null)
         {
            Ollivanders2API.getItems(p).enchantedItems.addEnchantedItem(enchantedItem);
         }

         stopProjectile();
         kill();

         break;
      }
   }

   @Nullable
   private Item enchantItem (@NotNull Item item)
   {
      common.printDebugMessage("Enchanting item " + item.getName(), null, null, false);

      // clone the item stack
      ItemStack enchantedItemStack = item.getItemStack().clone();

      // get item meta
      ItemMeta stackMeta = enchantedItemStack.getItemMeta();
      if (stackMeta == null)
      {
         common.printDebugMessage("ItemCurse.doCheckEffect: item meta is null", null, null, true);
         kill();
         return null;
      }

      // create new item stack of the 1 enchanted item
      ItemMeta enchantedMeta = newItemMeta(stackMeta);
      enchantedItemStack.setItemMeta(enchantedMeta);
      enchantedItemStack.setAmount(1);

      // update original itemStack to remove 1 item, if it had more than one, or remove the original item if there is only 1
      if (item.getItemStack().getAmount() > 1)
      {
         item.getItemStack().setAmount(item.getItemStack().getAmount() - 1);
      }
      else
      {
         item.remove();
      }

      // drop enchanted item in World
      return item.getWorld().dropItem(item.getLocation(), enchantedItemStack);
   }

   /**
    * Create new ItemMeta that includes the enchantment.
    *
    * @param itemMeta the ItemMeta for the item to enchany
    * @return the new ItemMeta with enchantment data added
    */
   @Nullable
   private ItemMeta newItemMeta(@NotNull ItemMeta itemMeta)
   {
      List<String> itemLore;

      itemLore = itemMeta.getLore();

      if (itemLore != null)
      {
         for (int i = 0; i < itemLore.size(); i++)
         {
            if (itemLore.get(i).contains(enchantmentType.getName()))
            {
               String[] loreParts = itemLore.get(i).split(" ");
               int curMagnitude = Integer.parseInt(loreParts[1]);
               if (magnitude < curMagnitude)
               {
                  magnitude = curMagnitude;
               }

               itemLore.set(i, enchantmentType.getName() + " " + magnitude);
            }
         }
      }

      if (itemLore == null)
         itemLore = new ArrayList<>();

      if (itemLore.size() < 1)
         itemLore.add(enchantmentType.getName() + " " + magnitude);

      itemMeta.setLore(itemLore);

      return itemMeta;
   }

   /**
    * Do the enchantment effect for an enchanted item
    *
    * @param target 
    * @param item
    */
   public static void doEnchantment (Player target, Item item)
   {

   }
}

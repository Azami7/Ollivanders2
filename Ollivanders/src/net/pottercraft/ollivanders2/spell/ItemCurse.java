package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Place a curse on a item.
 *
 * @author Azami7
 * @since 2.3
 */
public abstract class ItemCurse extends O2Spell
{
   protected String curseLabel;

   final int minMagnitude = 1;
   final int maxMagnitude = 100;

   double strength = 1;
   int magnitude;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ItemCurse()
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
   public ItemCurse(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
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

      // set to an empty string to protect against NPEs
      curseLabel = "";
   }

   @Override
   void doInitSpell ()
   {
      magnitude = (int) ((usesModifier / 4) * strength);

      if (magnitude < minMagnitude)
      {
         magnitude = minMagnitude;
      } else if (magnitude > maxMagnitude)
      {
         magnitude = maxMagnitude;
      }
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

      if (items.size() > 0)
      {
         Item item = items.get(0);

         // if this is a wand, skip
         if (Ollivanders2API.common.isWand(item.getItemStack()))
         {
            stopProjectile();
            return;
         }

         // get item meta
         ItemStack stack = item.getItemStack().clone();
         int amount = stack.getAmount();
         ItemMeta stackMeta = stack.getItemMeta();
         if (stackMeta == null)
         {
            common.printDebugMessage("ItemCurse.doCheckEffect: item meta is null", null, null, true);
            kill();
            return;
         }

         ItemMeta meta = newItemMeta(stackMeta);

         // mark target hit
         stopProjectile();

         // create new stack
         stack.setItemMeta(meta);
         stack.setAmount(1);

         // update original itemStack
         if (amount > 1)
         {
            item.getItemStack().setAmount(amount - 1);
         }
         else
         {
            item.remove();
         }

         // drop cursed item
         item.getWorld().dropItem(item.getLocation(), stack);
      }
   }

   /**
    * Create new ItemMeta that includes the curse.
    *
    * @param itemMeta the ItemMeta for the item to curse
    * @return the new ItemMeta with curse data added
    */
   private ItemMeta newItemMeta(@NotNull ItemMeta itemMeta)
   {
      List<String> itemLore;

      itemLore = itemMeta.getLore();

      if (itemLore != null)
      {
         for (int i = 0; i < itemLore.size(); i++)
         {
            if (itemLore.get(i).contains(curseLabel))
            {
               String[] loreParts = itemLore.get(i).split(" ");
               int curMagnitude = Integer.parseInt(loreParts[1]);
               if (magnitude < curMagnitude)
               {
                  magnitude = curMagnitude;
               }

               itemLore.set(i, curseLabel + " " + magnitude);
            }
         }
      }

      if (itemLore == null)
         itemLore = new ArrayList<>();

      if (itemLore.size() < 1)
         itemLore.add(curseLabel + " " + magnitude);

      itemMeta.setLore(itemLore);

      return itemMeta;
   }
}

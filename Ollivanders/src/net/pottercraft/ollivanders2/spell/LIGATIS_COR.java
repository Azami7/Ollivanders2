package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2WandCoreType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * If a coreless wand is near enough a core material, makes a wand of the wand wood type and core type. Itemstack amount
 * is 1 regardless of how many items were in either starting stack.
 *
 * @author lownes
 * @author Azami7
 */
public final class LIGATIS_COR extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LIGATIS_COR()
   {
      super();

      spellType = O2SpellType.LIGATIS_COR;
      branch = O2MagicBranch.CHARMS;

      text = "Ligatis Cor will bind a coreless wand to a core material. Make sure the two items are near each other when this spell is cast. You can only use this on one coreless wand and one core material at a time.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LIGATIS_COR (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LIGATIS_COR;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.ITEM_PICKUP);
         worldGuardFlags.add(Flags.ITEM_DROP);
      }
   }

   @Override
   protected void doCheckEffect ()
   {
      List<Item> items = super.getItems(1.5);

      for (Item item : items)
      {
         ItemMeta meta = item.getItemStack().getItemMeta();

         // look for any coreless wands by item meta
         if (meta.hasLore())
         {
            List<String> lore = meta.getLore();

            for (String l : lore)
            {
               if (l.contains(FRANGE_LIGNEA.corelessWandLabel))
               {
                  Location loc = item.getLocation();

                  // create the wand
                  ItemStack wand = createWand(item);

                  // spawn in to the world
                  item.getWorld().dropItem(loc, wand);

                  kill();
                  return;
               }
            }
         }

         kill();
         return;
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }

   /**
    * Create the wand from the material and core.
    *
    * @param corelessWand
    * @return
    */
   private ItemStack createWand(Item corelessWand)
   {
      ItemStack wand = null;

      ItemMeta meta = corelessWand.getItemStack().getItemMeta();

      // pick a random wood type
      ArrayList<String> woodTypes = O2WandCoreType.getAllCoresByName();

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % woodTypes.size());
      String wood = woodTypes.get(rand);

      // determine core based on nearby materials
      List<Entity> entities = corelessWand.getNearbyEntities(2, 2, 2);
      for (Entity coreItem : entities)
      {
         if (coreItem instanceof Item)
         {
            Material coreMaterial = ((Item) coreItem).getItemStack().getType();

            if (O2WandCoreType.getAllCoresByMaterial().contains(coreMaterial))
            {
               String core = O2WandCoreType.getWandCoreTypeByMaterial(coreMaterial).getLabel();

               Ollivanders2API.common.makeWands(wood, core, 1);

               // use up the wand materials
               corelessWand.remove();
               coreItem.remove();
            }
         }
      }

      return wand;
   }
}
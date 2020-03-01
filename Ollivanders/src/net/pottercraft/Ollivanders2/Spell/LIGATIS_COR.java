package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.Player.O2WandCoreType;
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
      worldGuardFlags.add(DefaultFlag.ITEM_PICKUP);
      worldGuardFlags.add(DefaultFlag.ITEM_DROP);
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
      List<String> lore = meta.getLore();

      // pick a random wood type
      ArrayList<String> woodTypes = O2WandCoreType.getAllCoresByName();

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % woodTypes.size());
      String woodType = woodTypes.get(rand);

      // determine core based on nearby materials
      List<Entity> entities = corelessWand.getNearbyEntities(2, 2, 2);
      for (Entity core : entities)
      {
         if (core instanceof Item)
         {
            Material material = ((Item) core).getItemStack().getType();

            if (O2WandCoreType.getAllCoresByMaterial().contains(material))
            {
               String newlore = meta.getLore().get(0);

               newlore = newlore.concat(" and ");
               newlore = newlore.concat(O2WandCoreType.getWandCoreTypeByMaterial(material).getLabel());

               meta.setDisplayName("Wand");
               List<String> loreL = new ArrayList<>();
               loreL.add(newlore);
               meta.setLore(loreL);

               wand = corelessWand.getItemStack();
               wand.setAmount(1);
               wand.setItemMeta(meta);

               // use up the wand materials
               corelessWand.remove();
               core.remove();
            }
         }
      }

      return wand;
   }
}
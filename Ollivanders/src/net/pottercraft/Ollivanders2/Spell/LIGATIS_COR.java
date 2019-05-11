package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mysql.fabric.xmlrpc.base.Array;
import net.pottercraft.Ollivanders2.Ollivanders2;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.Player.O2WandCoreType;
import net.pottercraft.Ollivanders2.Player.O2WandWoodType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * If a coreless wand is near enough a core material, makes a wand of
 * the wand wood type and core type. Itemstack amount is 1 regardless of
 * how many items were in either starting stack.
 *
 * @author lownes
 * @author Azami7
 */
public final class LIGATIS_COR extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LIGATIS_COR ()
   {
      super();

      spellType = O2SpellType.LIGATIS_COR;
      text = "Ligatis cor will bind a coreless wand to a core material.  Make sure the two items are near each other when this spell is cast. You can only use this on one coreless wand and one core material at a time.";
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
      setUsesModifier();
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
                  createWand(item);

                  kill();
                  return;
               }
            }
         }

         if (corM.hasLore())
         {
            ArrayList<String> wandWood = O2WandWoodType.getAllWoodsByName();
            if (wandWood.contains(corM.getLore().get(0)))
            {
               List<Entity> entities = item.getNearbyEntities(2, 2, 2);
               for (Entity e : entities)
               {
                  if (e instanceof Item)
                  {
                     Item e2 = (Item) e;
                     Material mat = e2.getItemStack().getType();

                     if (Arrays.asList(O2WandCoreType.getAllCoresByName()).contains(mat))
                     {
                        String lore = corM.getLore().get(0);
                        lore = lore.concat(" and ");
                        lore = lore.concat(wandWood.get(mat));
                        corM.setDisplayName("Wand");
                        List<String> loreL = new ArrayList<>();
                        loreL.add(lore);
                        corM.setLore(loreL);
                        ItemStack coreStack = item.getItemStack();
                        coreStack.setAmount(1);
                        coreStack.setItemMeta(corM);
                        item.setItemStack(coreStack);
                        e2.remove();
                     }
                  }
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

   private ItemStack createWand (Item item)
   {
      ItemStack wand = null;

      ItemMeta meta = item.getItemStack().getItemMeta();
      List<String> lore = meta.getLore();

      // pick a random wood type
      ArrayList<String> woodTypes = O2WandCoreType.getAllCoresByName();

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % woodTypes.size());
      String woodType = woodTypes.get(rand);

      // determine core based on nearby materials

      return wand;
   }
}
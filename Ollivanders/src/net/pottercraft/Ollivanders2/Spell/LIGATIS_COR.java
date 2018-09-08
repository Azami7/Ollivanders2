package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pottercraft.Ollivanders2.Ollivanders2;

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

      text = "Ligatis cor will bind a coreless wand to a core material.  Make sure the two items are near each other when this spell is cast. You can only use this on one coreless wand and one core material at a time.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LIGATIS_COR (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<Item> items = super.getItems(1);
      for (Item item : items)
      {
         ItemMeta corM = item.getItemStack().getItemMeta();
         String[] woodTypes = {"Oak", "Spruce", "Birch", "Jungle"};
         if (corM.hasLore())
         {
            if (Arrays.asList(woodTypes).contains(corM.getLore().get(0)))
            {
               List<Entity> entities = item.getNearbyEntities(2, 2, 2);
               for (Entity e : entities)
               {
                  if (e instanceof Item)
                  {
                     Item e2 = (Item) e;
                     Material mat = e2.getItemStack().getType();
                     Material[] cores = {Material.SPIDER_EYE, Material.ROTTEN_FLESH, Material.BONE, Material.SULPHUR};
                     if (Arrays.asList(cores).contains(mat))
                     {
                        Map<Material, String> matMap = new HashMap<Material, String>();
                        matMap.put(Material.SPIDER_EYE, "Spider Eye");
                        matMap.put(Material.ROTTEN_FLESH, "Rotten Flesh");
                        matMap.put(Material.BONE, "Bone");
                        matMap.put(Material.SULPHUR, "Gunpowder");
                        String lore = corM.getLore().get(0);
                        lore = lore.concat(" and ");
                        lore = lore.concat(matMap.get(mat));
                        corM.setDisplayName("Wand");
                        List<String> loreL = new ArrayList<String>();
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
      }
   }
}
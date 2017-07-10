package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.OPlayer;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Reduces any spell effects on an item
 *
 * @author lownes
 */
public class FINITE_INCANTATEM extends SpellProjectile implements Spell
{
   public FINITE_INCANTATEM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         if (live instanceof Player)
         {
            Player ply = (Player) live;
            OPlayer op = p.getOPlayer(ply);
            for (OEffect effect : op.getEffects())
            {
               effect.age((int) (usesModifier * 1200));
            }
            kill();
            return;
         }
      }
      for (Item item : getItems(1))
      {
         ItemStack stack = item.getItemStack();
         ItemMeta meta = stack.getItemMeta();
         if (meta.hasLore())
         {
            List<String> lore = meta.getLore();
            ArrayList<String> newLore = new ArrayList<String>();
            for (int i = 0; i < lore.size(); i++)
            {
               String string = lore.get(i);
               if (string.contains("Geminio "))
               {
                  String[] loreParts = lore.get(i).split(" ");
                  int magnitude = Integer.parseInt(loreParts[1]);
                  magnitude -= (int) usesModifier;
                  if (magnitude > 0)
                  {
                     newLore.add("Geminio " + magnitude);
                  }
               }
               else if (string.contains("Flagrante "))
               {
                  String[] loreParts = lore.get(i).split(" ");
                  int magnitude = Integer.parseInt(loreParts[1]);
                  magnitude -= (int) usesModifier;
                  if (magnitude > 0)
                  {
                     newLore.add("Flagrante " + magnitude);
                  }
               }
               else if (string.contains("Portkey "))
               {
                  //remove the portkey by not adding it to newLore
               }
               else
               {
                  newLore.add(lore.get(i));
               }
            }
            meta.setLore(newLore);
            stack.setItemMeta(meta);
            item.setItemStack(stack);
         }
         kill();
      }
   }
}
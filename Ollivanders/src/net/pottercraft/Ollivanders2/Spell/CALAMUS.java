package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/29/17. Imported from iarepandemonium/Ollivanders.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class CALAMUS extends Transfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CALAMUS ()
   {
      super();

      text = "Turns sticks in to arrows.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public CALAMUS(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect()
   {
      move();
      List<Item> items = super.getItems(1);
      for (Item item : items){
         Material mat = item.getItemStack().getType();
         if (mat == Material.STICK)
         {
            int amount = item.getItemStack().getAmount();
            Location loc = item.getLocation();
            ItemStack drop = new ItemStack(Material.ARROW);
            drop.setAmount(amount);
            loc.getWorld().dropItem(loc, drop);
            item.remove();
         }
      }
   }
}

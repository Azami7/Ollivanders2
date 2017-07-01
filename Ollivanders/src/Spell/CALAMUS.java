package Spell;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/29/17. Imported from iarepandemonium/Ollivanders.
 *
 * @author lownes
 */
public class CALAMUS extends SpellProjectile implements Spell
{
   public CALAMUS(Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
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

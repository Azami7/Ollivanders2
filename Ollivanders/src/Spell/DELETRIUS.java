package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * Deletes an item entity.
 *
 * @author lownes
 */
public class DELETRIUS extends SpellProjectile implements Spell
{

   public DELETRIUS (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<Item> items = getItems(1);
      for (Item item : items)
      {
         item.remove();
         kill();
         return;
      }
   }
}
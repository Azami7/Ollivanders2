package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * Spell which will grab the targeted item and pull it toward you
 * with a force determined by your level in the spell.
 *
 * @author lownes
 */
public class ACCIO extends SpellProjectile implements Spell
{

   public ACCIO (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<Item> items = getItems(1);
      for (Item item : items)
      {
         item.setVelocity(player.getEyeLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(usesModifier / 10));
         kill();
         return;
      }
   }
}
package Spell;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Shoots target high into air.
 *
 * @author lownes
 */
public class ALARTE_ASCENDARE extends SpellProjectile implements Spell
{

   public ALARTE_ASCENDARE (Ollivanders2 plugin, Player player, Spells name,
                            Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      double up = usesModifier * 0.4;
      if (up > 4)
      {
         up = 4;
      }
      Vector vec = new Vector(0, up, 0);
      for (LivingEntity lentity : getLivingEntities(1))
      {
         lentity.setVelocity(lentity.getVelocity().add(vec));
         kill();
         return;
      }
      for (Item item : getItems(1))
      {
         item.setVelocity(item.getVelocity().add(vec));
         kill();
         return;
      }
   }

}
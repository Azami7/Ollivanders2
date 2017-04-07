package Spell;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Transfigures a rotten flesh into inferi
 *
 * @author lownes
 */
public class MORTUOS_SUSCITATE extends Transfiguration implements Spell
{

   public MORTUOS_SUSCITATE (Ollivanders2 plugin, Player player, Spells name,
                             Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      if (!hasTransfigured())
      {
         move();
         for (Item item : getItems(1))
         {
            if (item.getItemStack().getType() == Material.ROTTEN_FLESH)
            {
               Zombie inferi = (Zombie) transfigureEntity(item, EntityType.ZOMBIE, null);
               inferi.setCustomName("Inferius");
            }
         }
      }
      else
      {
         if (lifeTicks > 160)
         {
            endTransfigure();
         }
         else
         {
            lifeTicks++;
         }
      }
   }

}

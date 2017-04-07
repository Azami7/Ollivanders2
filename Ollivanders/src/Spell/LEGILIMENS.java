package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Open the target LivingEntity's inventory
 *
 * @author lownes
 */
public class LEGILIMENS extends SpellProjectile implements Spell
{

   public LEGILIMENS (Ollivanders2 plugin, Player player, Spells name,
                      Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         if (live instanceof Player)
         {
            Player target = (Player) live;
            double experience = p.getOPlayer(target).getSpellCount().get(Spells.LEGILIMENS);
            if (usesModifier > experience)
            {
               player.openInventory(target.getInventory());
            }
            kill();
            return;
         }
      }
   }

}

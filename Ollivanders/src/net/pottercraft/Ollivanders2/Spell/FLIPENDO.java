package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Throws another player away from the caster. Twice as powerful as depulso.
 *
 * @author lownes
 */
public final class FLIPENDO extends Charms
{
   public FLIPENDO ()
   {
      super();

      flavorText.add("");
   }

   public FLIPENDO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> living = this.getLivingEntities(1);
      for (LivingEntity live : living)
      {
         if (live instanceof Player)
         {
            live.setVelocity(player.getLocation().getDirection().normalize().multiply(usesModifier / 10));
            kill();
            return;
         }
      }
   }
}
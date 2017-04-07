package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Give a player the ability to fly
 *
 * @author lownes
 */
public class VENTO_FOLIO extends SpellProjectile implements Spell
{

   public VENTO_FOLIO (Ollivanders2 plugin, Player player, Spells name,
                       Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<LivingEntity> living = getLivingEntities(1);
      for (LivingEntity live : living)
      {
         if (live instanceof Player)
         {
            Player ply = (Player) live;
            OPlayer oply = p.getOPlayer(ply);
            int dur = (int) (usesModifier * 1200);
            oply.addEffect(new Effect.VENTO_FOLIO(player, Effects.VENTO_FOLIO, dur));
            p.setOPlayer(ply, oply);
            kill();
            return;
         }
      }
   }

}

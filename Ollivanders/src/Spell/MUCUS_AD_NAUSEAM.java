package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Adds a MUCUS_AD_NAUSEAM oeffect to the player
 *
 * @author lownes
 */
public class MUCUS_AD_NAUSEAM extends SpellProjectile implements Spell
{
   public MUCUS_AD_NAUSEAM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

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
            oply.addEffect(new Effect.MUCUS_AD_NAUSEAM(player, Effects.MUCUS_AD_NAUSEAM, dur));
            p.setOPlayer(ply, oply);
            kill();
         }
      }
   }
}
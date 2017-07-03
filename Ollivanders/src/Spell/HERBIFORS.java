package Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OPlayer;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Gives target player up to a dozen flowers, depending on the spell experience.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class HERBIFORS extends SpellProjectile implements Spell
{
   private int maxFlowers;
   public HERBIFORS(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      if (usesModifier > 120)
         maxFlowers = 12;
      else
         maxFlowers = (int)usesModifier / 10;
   }

   public void checkEffect()
   {
      move();
      List<LivingEntity> living = getLivingEntities(1);
      for (LivingEntity live : living)
      {
         if (live instanceof Player)
         {
            Player ply = (Player)live;
            OPlayer oply = p.getOPlayer(ply);
            oply.addEffect(new Effect.HERBIFORS(player, Effects.HERBIFORS, 1, maxFlowers));
            p.setOPlayer(ply, oply);
            kill();
         }
      }
   }
}

package Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.OPlayer;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to a Creature.
 *
 * @author Azami7
 */
public abstract class IncarnatioSuper extends Transfiguration implements Spell
{
   OEffect effect;

   public IncarnatioSuper(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
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
            effect.duration = (int)(usesModifier*1200);
            oply.addEffect(effect);
            p.setOPlayer(ply, oply);
            kill();
         }
      }
   }
}

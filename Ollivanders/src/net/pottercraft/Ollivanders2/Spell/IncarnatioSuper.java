package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to a Creature.
 *
 * @author Azami7
 */
public abstract class IncarnatioSuper extends Transfiguration
{
   OEffect effect;

   public IncarnatioSuper () {}

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

package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Blinds and slows the target entity for a duration depending on
 * the spell's level.
 *
 * @author lownes
 */
public class STUPEFY extends SpellProjectile implements Spell
{

   public STUPEFY (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }


   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(1);
      if (entities.size() > 0)
      {
         LivingEntity entity = entities.get(0);
         int modifier = (int) usesModifier;
         PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, modifier * 20, modifier);
         PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, modifier * 20, modifier);
         entity.addPotionEffect(blindness);
         entity.addPotionEffect(slowness);
         kill = true;
      }
   }
}
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
 * Slows any living entity by an amount and time depending on the player's
 * spell level.
 *
 * @author lownes
 */
public class IMPEDIMENTA extends SpellProjectile implements Spell
{
   public IMPEDIMENTA (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(1);
      for (LivingEntity entity : entities)
      {
         int modifier = (int) usesModifier;
         PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, modifier * 20, modifier);
         entity.addPotionEffect(slow);
         kill();
      }
   }
}
package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Gives a target blindness
 *
 * @author lownes
 */
public class OBSCURO extends SpellProjectile implements Spell
{
   public OBSCURO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, (int) (usesModifier * 1200), 0);
         live.addPotionEffect(blind);
         kill();
         return;
      }
   }
}

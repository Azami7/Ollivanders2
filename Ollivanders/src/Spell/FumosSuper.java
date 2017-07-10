package Spell;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Causes blindness in a radius
 *
 * @author Azami7
 */
public abstract class FumosSuper extends SpellProjectile implements Spell
{
   int strength = 1;

   public FumosSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      moveEffect = Effect.SMOKE;
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         for (LivingEntity live : getLivingEntities(usesModifier))
         {
            PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, (int) (usesModifier * 20 * strength), 0);
            live.addPotionEffect(blind);
         }
      }
   }
}

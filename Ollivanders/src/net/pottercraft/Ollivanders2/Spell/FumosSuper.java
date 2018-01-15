package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Causes blindness in a radius
 *
 * @author Azami7
 */
public abstract class FumosSuper extends Charms
{
   int strength = 1;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FumosSuper ()
   {
      super();
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public FumosSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
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

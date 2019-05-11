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
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FumosSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      // material black list
      materialBlackList.add(Material.WATER);
      materialBlackList.add(Material.LAVA);
      materialBlackList.add(Material.FIRE);
   }

   /**
    * Blind targets in a radius of the spell location
    */
   @Override
   public void checkEffect ()
   {
      for (LivingEntity living : getLivingEntities(usesModifier))
      {
         if (living.getUniqueId() == player.getUniqueId())
            continue;

         PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, (int) (usesModifier * 20 * strength), 0);
         living.addPotionEffect(blind);
      }

      kill();
   }
}

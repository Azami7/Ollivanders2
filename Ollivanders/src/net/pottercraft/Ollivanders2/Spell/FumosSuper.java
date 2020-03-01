package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Causes blindness in a radius
 *
 * @author Azami7
 */
public abstract class FumosSuper extends AddPotionEffectInRadius
{
   int minRadius = 5;
   int maxRadius = 20;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FumosSuper()
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

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);

      effectTypes.add(PotionEffectType.BLINDNESS);
      strengthModifier = 1;
      minDurationInSeconds = 15;
      maxDurationInSeconds = 120;

      durationInSeconds = minDurationInSeconds;
      radius = minRadius;
   }

   /**
    * Initialize the parts of the spell that are based on experience, the player, etc. and not on class
    * constants.
    */
   @Override
   void doInitSpell ()
   {
      radius = (int) usesModifier / 10;
      if (radius < minDurationInSeconds)
      {
         radius = minRadius;
      }
      else if (radius > maxRadius)
      {
         radius = maxRadius;
      }

      durationInSeconds = (int) usesModifier;
      if (durationInSeconds < minDurationInSeconds)
      {
         durationInSeconds = minDurationInSeconds;
      }
      else if (durationInSeconds > maxDurationInSeconds)
      {
         durationInSeconds = maxDurationInSeconds;
      }
   }
}

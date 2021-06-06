package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Remove a potion effect for all entities in a radius of the caster
 *
 * @author Azami7
 */
public class RemovePotionEffectInRadius extends RemovePotionEffect
{
   /**
    * Radius of the spell from the caster.
    */
   int radius = 5;

   /**
    * Whether the spell targets the caster
    */
   boolean targetSelf = false;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public RemovePotionEffectInRadius()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public RemovePotionEffectInRadius(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   /**
    * Remove the potion effect from the caster.
    */
   @Override
   public void checkEffect ()
   {
      if (!checkSpellAllowed())
      {
         kill();
         return;
      }

      if (targetSelf)
      {
         removePotionEffects(player);
      }

      affectRadius(radius, true);

      kill();
   }
}

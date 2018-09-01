package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Improve the player's ability to remember information.
 *
 */
public class MEMORY_POTION extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param player the player this effect acts on
    */
   public MEMORY_POTION (Ollivanders2 plugin, O2EffectType effect, int duration, Player player)
   {
      super(plugin, effect, duration, player);
   }

   /**
    * Age this effect each game tick.
    */
   @Override
   public void checkEffect ()
   {
      age(1);
   }
}

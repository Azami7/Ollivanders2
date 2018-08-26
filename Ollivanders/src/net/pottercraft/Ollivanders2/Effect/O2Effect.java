package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * An effect is either a temporary or semi-permanent alteration of an O2Player. O2EffectType cannot
 * permanently change the O2Player but some effects may remain until a specific removal action/effect
 * is taken.
 *
 * @author Azami7
 */
public abstract class O2Effect
{
   Ollivanders2 p;
   public O2EffectType name;
   public int duration;
   public boolean kill;
   Player target;

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect to add to this player
    * @param duration the length this effect should remain
    * @param player the player this effect acts on
    */
   public O2Effect (Ollivanders2 plugin, O2EffectType effect, int duration, Player player)
   {
      p = plugin;

      this.duration = duration;
      name = effect;
      kill = false;
      target = player;
   }

   /**
    * Ages the effect.
    *
    * @param i the amount to age this effect
    */
   public void age (int i)
   {
      duration -= i;
      if (duration < 0)
      {
         kill();
      }
   }

   /**
    * This kills the effect.
    */
   public void kill ()
   {
      kill = true;
   }

   /**
    * This is the effect's action. age() must be called in this if you want the effect to age and die eventually.
    */
   public void checkEffect () { }
}

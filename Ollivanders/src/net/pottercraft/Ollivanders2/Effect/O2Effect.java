package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * An effect is either a temporary or semi-permanent alteration of an O2Player. O2EffectType cannot
 * permanently change the O2Player but some effects may remain until a specific removal action/effect
 * is taken.
 *
 * @author Azami7
 */
public abstract class O2Effect
{
   public O2EffectType effectType;
   public Integer duration;

   protected Ollivanders2 p;
   protected boolean kill;
   protected boolean permanent = false;
   protected UUID targetID;

   /**
    * Constructor. If you change this method signature, be sure to update all reflection code that uses it.
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect to add to this player
    * @param duration the length this effect should remain
    * @param pid the player this effect acts on
    */
   public O2Effect (Ollivanders2 plugin, O2EffectType effect, Integer duration, UUID pid)
   {
      p = plugin;

      this.duration = duration;
      effectType = effect;
      kill = false;
      targetID = pid;
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

   /**
    * Is this effect permanent.
    *
    * @return true if the effect is permanent, false if it is not
    */
   public boolean isPermanent ()
   {
      return permanent;
   }

   public boolean isKilled ()
   {
      return kill;
   }
}
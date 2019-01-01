package net.pottercraft.Ollivanders2.Effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;

/**
 * An effect is either a temporary or semi-permanent alteration of an O2Player. O2EffectType cannot
 * permanently change the O2Player but some effects may remain until a specific removal action/effect
 * is taken.
 *
 * @author Azami7
 */
public abstract class O2Effect
{
   /**
    * The type this effect is. Set to babbling as a safe default.
    */
   public O2EffectType effectType = O2EffectType.BABBLING;

   /**
    * The number of game ticks this effect lasts. 24000 ticks is one MC day and should be ~20 real minutes.
    */
   public Integer duration;

   /**
    * A callback to the MC plugin
    */
   protected Ollivanders2 p;

   /**
    * Whether this effect should be killed next upkeep.
    */
   protected boolean kill;

   /**
    * Whether this effect is permanent. Permanent effects do not age and can only be removed by explicitly killing them.
    */
   protected boolean permanent = false;

   /**
    * The id of the player this effect affects.
    */
   protected UUID targetID;

   /**
    * The output to be shown for information spells like Informous, if this effect can be detected in this way. This string is the predicate of
    * a sentence that starts with the player's name and should not include ending punctuation. Example: "feels unnaturally tired"
    */
   protected String informousText;

   /**
    * The output to be shown for mind-reading spells like Legilimens, if this effect can be detected in this way. This string is the predicate of
    * a sentence that starts with the player's name and should not include ending punctuation. Example: "feels aggressive"
    */
   protected String legilimensText;

   /**
    * The output to be shown for prophecies that use this effect. This should be in future tense.
    */
   protected ArrayList<String> divinationText = new ArrayList<>();

   /**
    * Constructor. If you change this method signature, be sure to update all reflection code that uses it.
    *
    * @param plugin a callback to the MC plugin
    * @param duration the length this effect should remain
    * @param pid the player this effect acts on
    */
   public O2Effect (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      p = plugin;

      this.duration = duration;
      kill = false;
      targetID = pid;

      informousText = null;
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
    * Override default permanent setting for an effect.
    *
    * @param perm true if this is permanent, false otherwise
    */
   public void setPermanent (boolean perm)
   {
      permanent = perm;
   }

   /**
    * This kills the effect.
    */
   public void kill ()
   {
      kill = true;
   }

   /**
    * Get the id of the player affected by this effect.
    *
    * @return the id of the player
    */
   public UUID getTargetID ()
   {
      UUID pid = new UUID(targetID.getMostSignificantBits(), targetID.getLeastSignificantBits());

      return pid;
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

   /**
    * Has this effect been killed.
    *
    * @return true if it is killed, false otherwise
    */
   public boolean isKilled ()
   {
      return kill;
   }

   /**
    * Get the text to be used for this effect in a prophecy
    *
    * @return a random divination text for this effect
    */
   public String getDivinationText ()
   {
      if (divinationText.size() < 1)
      {
         return "will be affected by an unseen affliction";
      }
      else
      {
         int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % divinationText.size());
         return divinationText.get(rand);
      }
   }
}
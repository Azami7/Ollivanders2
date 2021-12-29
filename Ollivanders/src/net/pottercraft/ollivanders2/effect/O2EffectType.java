package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;

/**
 * All effects
 *
 * @author Azami7
 * @version Ollivanders2
 */
public enum O2EffectType
{
   AGGRESSION(AGGRESSION.class, Ollivanders2Common.MagicLevel.OWL),
   ANIMAGUS_EFFECT(ANIMAGUS_EFFECT.class, Ollivanders2Common.MagicLevel.EXPERT),
   ANIMAGUS_INCANTATION(ANIMAGUS_INCANTATION.class, Ollivanders2Common.MagicLevel.EXPERT),
   AWAKE(AWAKE.class, Ollivanders2Common.MagicLevel.BEGINNER),
   AWAKE_ANTIDOTE_LESSER(AWAKE_ANTIDOTE_LESSER.class, Ollivanders2Common.MagicLevel.BEGINNER),
   BABBLING(BABBLING.class, Ollivanders2Common.MagicLevel.BEGINNER),
   BABBLING_ANTIDOTE_LESSER (BABBLING_ANTIDOTE_LESSER.class, Ollivanders2Common.MagicLevel.BEGINNER),
   BLINDNESS(BLINDNESS.class, Ollivanders2Common.MagicLevel.OWL),
   BURNING(BURNING.class, Ollivanders2Common.MagicLevel.NEWT),
   CONFUSION(CONFUSION.class, Ollivanders2Common.MagicLevel.OWL),
   FAST_LEARNING(FAST_LEARNING.class, Ollivanders2Common.MagicLevel.NEWT),
   FLYING(FLYING.class, Ollivanders2Common.MagicLevel.EXPERT),
   HARM(HARM.class, Ollivanders2Common.MagicLevel.OWL),
   HARM_ANTIDOTE_LESSER(HARM_ANTIDOTE_LESSER.class, Ollivanders2Common.MagicLevel.OWL),
   HEAL(HEAL.class, Ollivanders2Common.MagicLevel.OWL),
   HEALTH_BOOST(HEALTH_BOOST.class, Ollivanders2Common.MagicLevel.NEWT),
   HUNGER(HUNGER.class, Ollivanders2Common.MagicLevel.BEGINNER),
   HIGHER_SKILL(HIGHER_SKILL.class, Ollivanders2Common.MagicLevel.NEWT),
   IMMOBILIZE(IMMOBILIZE.class, Ollivanders2Common.MagicLevel.BEGINNER),
   IMPROVED_BOOK_LEARNING(IMPROVED_BOOK_LEARNING.class, Ollivanders2Common.MagicLevel.NEWT),
   LUCK(LUCK.class, Ollivanders2Common.MagicLevel.BEGINNER),
   LYCANTHROPY(LYCANTHROPY.class, Ollivanders2Common.MagicLevel.EXPERT),
   LYCANTHROPY_SPEECH(LYCANTHROPY_SPEECH.class, Ollivanders2Common.MagicLevel.EXPERT),
   LYCANTHROPY_RELIEF(LYCANTHROPY_RELIEF.class, Ollivanders2Common.MagicLevel.EXPERT),
   MUCUS(MUCUS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   MUTED_SPEECH(MUTED_SPEECH.class, Ollivanders2Common.MagicLevel.OWL),
   NIGHT_VISION(NIGHT_VISION.class, Ollivanders2Common.MagicLevel.BEGINNER),
   POISON(POISON.class, Ollivanders2Common.MagicLevel.OWL),
   POISON_ANTIDOTE_LESSER(POISON_ANTIDOTE_LESSER.class, Ollivanders2Common.MagicLevel.OWL),
   SLEEP_SPEECH(SLEEP_SPEECH.class, Ollivanders2Common.MagicLevel.BEGINNER),
   SLEEPING(SLEEPING.class, Ollivanders2Common.MagicLevel.BEGINNER),
   SPEED(SPEED.class, Ollivanders2Common.MagicLevel.BEGINNER),
   SPEED_SPEEDIER(SPEED_SPEEDIER.class, Ollivanders2Common.MagicLevel.OWL),
   SPEED_SPEEDIEST(SPEED_SPEEDIEST.class, Ollivanders2Common.MagicLevel.NEWT),
   SLOWNESS (SLOWNESS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   SUSPENSION (SUSPENSION.class, Ollivanders2Common.MagicLevel.OWL),
   UNLUCK (UNLUCK.class, Ollivanders2Common.MagicLevel.BEGINNER),
   UNLUCK_ANTIDOTE_LESSER (UNLUCK_ANTIDOTE_LESSER.class, Ollivanders2Common.MagicLevel.BEGINNER),
   WATER_BREATHING(WATER_BREATHING.class, Ollivanders2Common.MagicLevel.NEWT),
   WEAKNESS (WEAKNESS.class, Ollivanders2Common.MagicLevel.OWL),
   WEALTH (WEALTH.class, Ollivanders2Common.MagicLevel.OWL),
   ;

   /**
    * The class of effect to create
    */
   final private Class<?> className;

   /**
    * The level of magic this effect is, for use in counter-spells and antidotes
    */
   final private Ollivanders2Common.MagicLevel level;

   /**
    * Is this effect type enabled
    */
   boolean enabled = true;

   /**
    * Enum constructor.
    *
    * @param className the name of the effect class this type represents.
    * @param level the level of this effect
    */
   O2EffectType(@NotNull Class<?> className, @NotNull Ollivanders2Common.MagicLevel level)
   {
      this.className = className;
      this.level = level;
   }

   /**
    * Get the class name for the O2Effect class that this type represents.
    *
    * @return the effect class
    */
   @NotNull
   public Class<?> getClassName()
   {
      return className;
   }

   /**
    * Get the level of this effect.
    *
    * @return the level of this effect
    */
   @NotNull
   public Ollivanders2Common.MagicLevel getLevel()
   {
      return level;
   }

   /**
    * Is this effect currently enabled
    *
    * @return
    */
   public boolean isEnabled()
   {
      return enabled;
   }

   /**
    * Set whether this effect is currently enabled
    *
    * @param enabled
    * @return
    */
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }
}
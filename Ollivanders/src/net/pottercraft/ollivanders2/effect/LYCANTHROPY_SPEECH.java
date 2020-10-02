package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Change the the player's speech to dog sounds.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class LYCANTHROPY_SPEECH extends BABBLING
{
   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public LYCANTHROPY_SPEECH(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.LYCANTHROPY_SPEECH;

      dictionary = new ArrayList<String>()
      {{
         add("§oHOOOOOOWLLLLLL");
         add("§obark bark bark bark");
         add("§ogrowl");
         add("§osnarl");
      }};

      permanent = true;
      maxWords = 3;
   }
}
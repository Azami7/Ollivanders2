package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Sleep speech makes the player say "sleeping" sounds rather than talk.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class SLEEP_SPEECH extends BABBLING
{
   public SLEEP_SPEECH (Ollivanders2 plugin, O2EffectType effect, Integer duration, UUID pid)
   {
      super(plugin, effect, duration, pid);

      dictionary = new ArrayList<String>() {{
         add("§ozzzzzzzz");
         add("§osnore");
         add("§oincoherent mumbling");
         add("§ozzzz zzz zzzzzz");
      }};

      permanent = true;
      maxWords = 2;
   }
}

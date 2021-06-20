package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Causes a player to chat nonsense. Any Babbling effects must also be added to onPlayerChat in Ollivanders2Listener.
 *
 * @since 2.2.7
 * @author Azami7
 */
public class BABBLING extends O2Effect
{
   public ArrayList<String> dictionary = new ArrayList<String>() {{
      add("mimble");
      add("wimble");
      add("oddment");
      add("tweak");
      add("nitwit");
      add("blubber");
      add("buzzlepop");
      add("ackamarackus");
      add("crodsquinkled");
      add("fuddle");
      add("woozle");
      add("hibber-gibber");
      add("tootle");
      add("tarradiddle");
      add("skittles");
      add("schmegeggy");
      add("bletherskate");
      add("flimflam");
      add("flapdoodle");
      add("razzmatazz");
      add("linsey-woolsey");
      add("pussel-skwonk");
      add("slithy toves");
      add("twas brillig");
      add("rannygazoo");
      add("quatsch");
      add("jiggery-pokery");
      add("bandersnatch");
      add("snicker-snack");
      add("peskipiksi");
      add("pesternomi");
      add("piddle");
      add("clatfart");
      add("phonus-bolonus");
      add("jabberwock");
      add("nugament");
      add("narrischkeit");
      add("mumbo-jumbo");
      add("flummadiddle");
      add("me me big boy");
   }};

   int maxWords = 5;

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public BABBLING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.BABBLING;
      informousText = "is unable to speak clearly";

      divinationText.add("shall be afflicted in the mind");
      divinationText.add("shall lose their mind to insanity");
      divinationText.add("will begin to speak in tongues");
      divinationText.add("will be possessed by a demon spirit");
   }

   /**
    * Age this effect each game tick.
    */
   @Override
   public void checkEffect ()
   {
      if (!permanent)
      {
         age(1);
      }
   }

   /**
    * Get nonsense text to replace what the user actually typed.
    *
    * @return a string of nonsense words from 1-5 words in length
    */
   private String getNonsense ()
   {
      StringBuilder nonsense = new StringBuilder();

      int numWords = Math.abs(Ollivanders2Common.random.nextInt() % maxWords) + 1;

      for (int i = 0; i < numWords; i++)
      {
         String random = dictionary.get(Math.abs(Ollivanders2Common.random.nextInt()) % dictionary.size());

         if (nonsense.length() > 0)
            nonsense.append(" ");

         nonsense.append(random);
      }

      return nonsense.toString();
   }

   /**
    * Change a player's chat to babbling nonsense.
    *
    * @param event the player chat event
    */
   public void doBabblingEffect(@NotNull AsyncPlayerChatEvent event)
   {
      String message = event.getMessage();
      String newMessage = getNonsense();

      event.setMessage(newMessage);

      if (Ollivanders2.debug)
         p.getLogger().info("Changed " + event.getPlayer().getDisplayName() + "'s chat from \"" + message + "\" to \"" + newMessage + "\"");
   }
}
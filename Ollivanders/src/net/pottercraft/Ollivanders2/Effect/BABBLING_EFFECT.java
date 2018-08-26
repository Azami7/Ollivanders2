package net.pottercraft.Ollivanders2.Effect;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Causes a player to chat nonsense.
 *
 * @since 2.2.7
 * @author Azami7
 */
public class BABBLING_EFFECT extends O2Effect
{
   public final ArrayList<String> dictionary = new ArrayList<String>() {{
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
   }};

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect on the player
    * @param duration the duration of this effect
    * @param player the player this effect acts on
    */
   public BABBLING_EFFECT (Ollivanders2 plugin, O2EffectType effect, int duration, Player player)
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

   /**
    * Get nonsense text to replace what the user actually typed.
    *
    * @return a string of nonsense words from 1-5 words in length
    */
   private String getNonsense ()
   {
      String nonsense = "";
      int numWords = Math.abs(Ollivanders2.random.nextInt() % 5) + 1;

      for (int i = 0; i < numWords; i++)
      {
         String random = dictionary.get(Math.abs(Ollivanders2.random.nextInt()) % dictionary.size());

         if (nonsense.length() > 0)
            nonsense = nonsense + " ";

         nonsense = nonsense + random;
      }

      return nonsense;
   }

   /**
    * Change a player's chat to babbling nonsense.
    *
    * @param event the player chat event
    */
   public void doBabblingEffect (AsyncPlayerChatEvent event)
   {
      String message = event.getMessage();
      String newMessage = getNonsense();

      event.setMessage(newMessage);

      if (Ollivanders2.debug)
         p.getLogger().info("Changed " + event.getPlayer().getDisplayName() + "'s chat from \"" + message + "\" to \"" + newMessage + "\"");
   }
}

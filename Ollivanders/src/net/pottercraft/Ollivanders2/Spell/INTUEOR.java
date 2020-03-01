package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Divination.O2DivinationType;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Crystal-gazing was the art of looking into a crystal ball in order to try to gain insight into the future events.
 * https://harrypotter.fandom.com/wiki/Crystal-gazing
 *
 * @author Azami7
 * @since
 */
public class INTUEOR extends Divination
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INTUEOR ()
   {
      super();

      spellType = O2SpellType.INTUEOR;
      divinationType = O2DivinationType.CRYSTAL_BALL;
      branch = O2MagicBranch.DIVINATION;

      flavorText = new ArrayList<String>()
      {{
         add("\"‘I have been crystal-gazing, Headmaster,’ said Professor Trelawney, in her mistiest, most faraway voice, ‘and to my astonishment, I saw myself abandoning my solitary luncheon and coming to join you. Who am I to refuse the prompting of fate?'\"");
         add("\"If you must know, Minerva, I have seen that poor Professor Lupin will not be with us for very long. He seems aware, himself, that his time is short. He positively fled when I offered to crystal-gaze for him.\" -Sybill Trelawney");
         add("\"Crystal-gazing is a particularly refined art. I do not expect any of you to See when first you peer into the Orb’s infinite depths.\" -Sybill Trelawney");
         add("\"‘Would anyone like me to help them interpret the shadowy portents within their Orb?’ she murmured over the clinking of her bangles. ‘I don’t need help,’ Ron whispered. ‘It’s obvious what this means. There’s going to be loads of fog tonight.’\"");
         add("\"‘There is something here!’ Professor Trelawney whispered, lowering her face to the ball, so that it was reflected twice in her huge glasses. ‘Something moving… but what is it?'\"");
      }};

      text = "Through careful interpretation of the visions seen within a crystal ball, a seer can predict the future.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INTUEOR (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INTUEOR;
      divinationType = O2DivinationType.CRYSTAL_BALL;
      branch = O2MagicBranch.DIVINATION;

      facingBlock = Material.GLASS;
      facingBlockString = "a crystal ball";

      initSpell();
   }
}

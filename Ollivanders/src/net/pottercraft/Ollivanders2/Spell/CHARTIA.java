package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Divination.O2DivinationType;
import net.pottercraft.Ollivanders2.Item.O2ItemType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Cartomancy is the art of reading cards to gain insight into future events.
 * http://harrypotter.wikia.com/wiki/Cartomancy
 *
 * @author Azami7
 * @since 2.2.9
 */
public final class CHARTIA extends Divination
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CHARTIA ()
   {
      super();

      spellType = O2SpellType.CHARTIA;
      divinationType = O2DivinationType.CARTOMANCY;

      flavorText = new ArrayList<String>()
      {{
         add("\"‘Two of spades: conflict,’ she murmured, as she passed the place where Harry crouched, hidden. ‘Seven of spades: an ill omen. Ten of spades: violence. Knave of spades: a dark young man, possibly troubled, one who dislikes the questioner –‘\"");
         add("\"Harry proceeded through deserted corridors, though he had to step hastily behind a statue when Professor Trelawney appeared round a corner, muttering to herself as she shuffled a pack of dirty-looking playing cards, reading them as she walked.\"");
      }};

      text = "A true seer may gain insight in to future events by accurately reading playing cards.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public CHARTIA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.CHARTIA;
      divinationType = O2DivinationType.CARTOMANCY;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      itemHeld = O2ItemType.PLAYING_CARDS;
      itemHeldString = "playing cards";
   }
}

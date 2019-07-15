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
public final class CARTOMANCIE extends Divination
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CARTOMANCIE ()
   {
      super();

      spellType = O2SpellType.CARTOMANCIE;
      divinationType = O2DivinationType.CARTOMANCY_TAROT;

      flavorText = new ArrayList<String>()
      {{
         add("\"If Dumbledore chooses to ignore the warnings the cards show - again and again, no matter how I lay them out – the lightning-struck tower. Calamity. Disaster. Coming nearer all the time.’\" -Sybill Trelawney");
      }};

      text = "Divination by reading tarot cards will reveal future events to those who possess the inner eye.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public CARTOMANCIE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.CARTOMANCIE;
      divinationType = O2DivinationType.CARTOMANCY_TAROT;

      initSpell();

      itemHeld = O2ItemType.TAROT_CARDS;
      itemHeldString = "tarot cards";
   }
}

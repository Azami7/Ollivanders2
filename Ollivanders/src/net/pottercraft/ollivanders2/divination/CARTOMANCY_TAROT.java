package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Cartomancy is the art of reading cards to gain insight into future events.
 * http://harrypotter.wikia.com/wiki/Cartomancy
 *
 * @author Azami7
 * @since 2.2.9
 */
public class CARTOMANCY_TAROT extends O2Divination
{
   public CARTOMANCY_TAROT(@NotNull Ollivanders2 plugin, @NotNull Player pro, @NotNull Player tar, int exp)
   {
      super(plugin, pro, tar, exp);

      divintationType = O2DivinationType.CARTOMANCY_TAROT;
      maxAccuracy = 35;

      prophecyPrefix.add("The cards have revaled that");
      prophecyPrefix.add("The reading of the cards says that");
      prophecyPrefix.add("The Lightning-Struck Tower is revealed,");
      prophecyPrefix.add("The Star is revealed,");
      prophecyPrefix.add("The High Priestess is revealed,");
      prophecyPrefix.add("The Hanged Man is revealed,");
      prophecyPrefix.add("Judgement is revealed,");
      prophecyPrefix.add("The Emperor is revealed,");
      prophecyPrefix.add("Wheel of Fortune is revealed,");
      prophecyPrefix.add("The Chariot is revealed,");
      prophecyPrefix.add("Death is revealed,");
      prophecyPrefix.add("The Hierophant is revealed,");
      prophecyPrefix.add("The Hanged Man is revealed,");
      prophecyPrefix.add("The Moon is revealed,");
      prophecyPrefix.add("The Empress is revealed,");
      prophecyPrefix.add("The Devil is revealed,");
      prophecyPrefix.add("The Fool is revealed,");
   }
}

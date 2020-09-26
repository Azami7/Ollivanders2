package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Ovomancy is a type of divination that involves cracking open eggs and observing which way the yolks fall.
 * https://harrypotter.fandom.com/wiki/Ovomancy
 *
 * @author Azami7
 * @since 2.2.9
 */
public class OVOMANCY extends O2Divination
{
   public OVOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player pro, @NotNull Player tar, int exp)
   {
      super(plugin, pro, tar, exp);

      divintationType = O2DivinationType.OVOMANCY;
      maxAccuracy = 40;

      prophecyPrefix.add("The shape of the egg whites means that");
      prophecyPrefix.add("Through the teachings of Orpheus, it is foretold that");
      prophecyPrefix.add("The omen of the egg reveals that");
      prophecyPrefix.add("Egg whites and yolks take form,");
      prophecyPrefix.add("The egg forms the shape of a bell,");
      prophecyPrefix.add("The egg forms the shape of a snake,");
      prophecyPrefix.add("The egg takes the shape of a boat,");
   }
}

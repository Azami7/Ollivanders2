package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Crystal-gazing was the art of looking into a crystal ball in order to try to gain insight into the future events.
 * https://harrypotter.fandom.com/wiki/Crystal-gazing
 *
 * @author Azami7
 * @since 2.2.9
 */
public class CRYSTAL_BALL extends O2Divination
{
   public CRYSTAL_BALL (Ollivanders2 plugin, Player pro, Player tar, Integer exp)
   {
      super(plugin, pro, tar, exp);

      divintationType = O2DivinationType.CRYSTAL_BALL;
      maxAccuracy = 30;

      prophecyPrefix.add("The crystal ball has revealed that");
      prophecyPrefix.add("The clairvoyant vibrations of the orb show that");
      prophecyPrefix.add("The shadowy portents have been read,");
      prophecyPrefix.add("The orb tells the future,");
   }
}

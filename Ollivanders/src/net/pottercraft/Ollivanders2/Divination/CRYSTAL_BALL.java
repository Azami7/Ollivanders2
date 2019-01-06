package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

public class CRYSTAL_BALL extends O2Divination
{
   public CRYSTAL_BALL (Ollivanders2 plugin, Player pro, Player tar, Integer exp)
   {
      super(plugin, pro, tar, exp);

      divintationType = O2DivinationType.CRYSTAL_BALL;
      maxAccuracy = 30;

      prophecyPrefix.add("Gazing in to the crystal ball, it is revealed that");
      prophecyPrefix.add("The clairvoyant vibrations of the orb show that");
      prophecyPrefix.add("The shadowy portents of the orb say that");
   }
}

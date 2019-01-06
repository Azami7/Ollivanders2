package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

public class ASTROLOGY extends O2Divination
{
   public ASTROLOGY (Ollivanders2 plugin, Player pro, Player tar, Integer exp)
   {
      super(plugin, pro, tar, exp);

      divintationType = O2DivinationType.ASTROLOGY;
      maxAccuracy = 20;

      prophecyPrefix.add("Because of the influence of Mars,");
      prophecyPrefix.add("Due to the influence of Mercury,");
      prophecyPrefix.add("From the influence of Venus,");
      prophecyPrefix.add("Due to the influence of Jupiter,");
      prophecyPrefix.add("Because of the influence of Saturn,");
      prophecyPrefix.add("Due to the influence of the Moon,");
      prophecyPrefix.add("The angle of Saturn and Venus means that");
      prophecyPrefix.add("The angle of Mercury and Mars means");
      prophecyPrefix.add("The angle of Jupiter and the Moon predicts that");
      prophecyPrefix.add("The angle of the Moon and Saturn reveals that");
      prophecyPrefix.add("Mars in the 6th house fortells that");
      prophecyPrefix.add("Mercury in the 3rd house fortells that");
      prophecyPrefix.add("The Moon in the 7th house will cause");
      prophecyPrefix.add("Saturn in the 5th house indicates that");
      prophecyPrefix.add("Jupiter in the 4th house predicts that");
      prophecyPrefix.add("Because of the position of the Moon in their birth chart,");
      prophecyPrefix.add("The position of Mercury in their birth chart reveals that");
      prophecyPrefix.add("Due to the position of Venus in their birth chart predicts that");
      prophecyPrefix.add("From the position of Mars in their birth chart indicates that");
      prophecyPrefix.add("The position of Jupiter in their birth chart assures that");
      prophecyPrefix.add("The position of Saturn in their birth chart means");
   }
}

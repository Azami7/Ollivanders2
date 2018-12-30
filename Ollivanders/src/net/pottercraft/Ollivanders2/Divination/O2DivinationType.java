package net.pottercraft.Ollivanders2.Divination;

/**
 * All divination methods
 *
 * @author Azami7
 * @since 2.2.9
 */
public enum O2DivinationType
{
   ASTROLOGY(net.pottercraft.Ollivanders2.Divination.ASTROLOGY.class, 0, 25),
   //CARTOMANCY,
   //CATOPTROMANCY,
   CENTAUR_ASTROLOGY(net.pottercraft.Ollivanders2.Divination.ASTROLOGY.class, 0, 99);
   //CHINESE_FORTUNE_STICKS,
   //CRYSTAL_BALL,
   //DREAM_INTERPRETATION,
   //FIRE_OMEN,
   //HEPTOMOLOGY,
   //ICHTHYOMANCY,
   //MYOMANCY,
   //PALMISTRY,
   //ORNITHOMANCY,
   //OVOMANCY,
   //TESSOMANCY,
   //XYLOMANCY;

   private Class className;

   int minAccuracy;
   int maxAccuracy;

   O2DivinationType (Class c, int min, int max)
   {
      className = c;

      minAccuracy = min;
      maxAccuracy = max;
   }
}

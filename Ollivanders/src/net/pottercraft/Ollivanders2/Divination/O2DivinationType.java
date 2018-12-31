package net.pottercraft.Ollivanders2.Divination;

/**
 * All divination methods
 *
 * @author Azami7
 * @since 2.2.9
 */
public enum O2DivinationType
{
   ASTROLOGY(net.pottercraft.Ollivanders2.Divination.ASTROLOGY.class);
   //CARTOMANCY,
   //CATOPTROMANCY,
   //CENTAUR_ASTROLOGY
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

   O2DivinationType (Class c)
   {
      className = c;
   }

   public Class getClassName ()
   {
      return className;
   }
}

package net.pottercraft.Ollivanders2.Divination;

/**
 * All divination methods
 *
 * @author Azami7
 * @since 2.2.9
 */
public enum O2DivinationType
{
   ASTROLOGY(ASTROLOGY.class),
   //CARTOMANCY,
   //CATOPTROMANCY,
   CENTAUR_DIVINATION(CENTAUR_DIVINATION.class),
   //CHINESE_FORTUNE_STICKS,
   CRYSTAL_BALL(CRYSTAL_BALL.class),
   //DREAM_INTERPRETATION,
   //FIRE_OMEN,
   //HEPTOMOLOGY,
   //ICHTHYOMANCY,
   //MYOMANCY,
   //PALMISTRY,
   //ORNITHOMANCY,
   OVOMANCY(OVOMANCY.class),
   //TESSOMANCY,
   //XYLOMANCY,
   ;

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

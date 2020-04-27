package net.pottercraft.ollivanders2.divination;

/**
 * All divination methods
 *
 * @author Azami7
 * @since 2.2.9
 */
public enum O2DivinationType
{
   ASTROLOGY(net.pottercraft.ollivanders2.divination.ASTROLOGY.class),
   CARTOMANCY(net.pottercraft.ollivanders2.divination.CARTOMANCY.class),
   CARTOMANCY_TAROT(net.pottercraft.ollivanders2.divination.CARTOMANCY_TAROT.class),
   //CATOPTROMANCY,
   CENTAUR_DIVINATION(net.pottercraft.ollivanders2.divination.CENTAUR_DIVINATION.class),
   //CHINESE_FORTUNE_STICKS,
   CRYSTAL_BALL(net.pottercraft.ollivanders2.divination.CRYSTAL_BALL.class),
   //DREAM_INTERPRETATION,
   //FIRE_OMEN,
   //HEPTOMOLOGY,
   //ICHTHYOMANCY,
   //MYOMANCY,
   //PALMISTRY,
   //ORNITHOMANCY,
   OVOMANCY(net.pottercraft.ollivanders2.divination.OVOMANCY.class),
   TASSEOMANCY(TASSEOMANCY.class),
   //XYLOMANCY,
   ;

   private Class className;

   O2DivinationType(Class c)
   {
      className = c;
   }

   public Class getClassName ()
   {
      return className;
   }
}

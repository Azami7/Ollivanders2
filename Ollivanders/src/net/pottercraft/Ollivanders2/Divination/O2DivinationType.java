package net.pottercraft.Ollivanders2.Divination;

/**
 * All divination methods
 *
 * @author Azami7
 * @since 2.2.9
 */
public enum O2DivinationType
{
   ASTROLOGY(net.pottercraft.Ollivanders2.Divination.ASTROLOGY.class),
   CARTOMANCY(net.pottercraft.Ollivanders2.Divination.CARTOMANCY.class),
   CARTOMANCY_TAROT(net.pottercraft.Ollivanders2.Divination.CARTOMANCY_TAROT.class),
   //CATOPTROMANCY,
   CENTAUR_DIVINATION(net.pottercraft.Ollivanders2.Divination.CENTAUR_DIVINATION.class),
   //CHINESE_FORTUNE_STICKS,
   CRYSTAL_BALL(net.pottercraft.Ollivanders2.Divination.CRYSTAL_BALL.class),
   //DREAM_INTERPRETATION,
   //FIRE_OMEN,
   //HEPTOMOLOGY,
   //ICHTHYOMANCY,
   //MYOMANCY,
   //PALMISTRY,
   //ORNITHOMANCY,
   OVOMANCY(net.pottercraft.Ollivanders2.Divination.OVOMANCY.class),
   TASSEOMANCY(TASSEOMANCY.class),
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

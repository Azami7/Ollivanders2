package net.pottercraft.Ollivanders2.Potion;

/**
 * Enum of all potions types.
 *
 * @author Azami7
 * @since 2.2.7
 */
public enum O2PotionType
{
   ANIMAGUS_POTION (net.pottercraft.Ollivanders2.Potion.ANIMAGUS_POTION.class),
   ANTIDOTE_POTION (net.pottercraft.Ollivanders2.Potion.ANTIDOTE_POTION.class),
   BABBLING_BEVERAGE (net.pottercraft.Ollivanders2.Potion.BABBLING_BEVERAGE.class),
   BARUFFIOS_BRAIN_ELIXIR (net.pottercraft.Ollivanders2.Potion.BARUFFIOS_BRAIN_ELIXIR.class),
   DRAUGHT_OF_LIVING_DEATH (net.pottercraft.Ollivanders2.Potion.DRAUGHT_OF_LIVING_DEATH.class),
   FORGETFULLNESS_POTION (net.pottercraft.Ollivanders2.Potion.FORGETFULLNESS_POTION.class),
   HERBICIDE_POTION (net.pottercraft.Ollivanders2.Potion.HERBICIDE_POTION.class),
   MEMORY_POTION (net.pottercraft.Ollivanders2.Potion.MEMORY_POTION.class),
   REGENERATION_POTION (net.pottercraft.Ollivanders2.Potion.REGENERATION_POTION.class),
   SLEEPING_DRAUGHT (net.pottercraft.Ollivanders2.Potion.SLEEPING_DRAUGHT.class),
   WIDEYE_POTION (net.pottercraft.Ollivanders2.Potion.WIDEYE_POTION.class),
   WIGGENWELD_POTION (net.pottercraft.Ollivanders2.Potion.WIGGENWELD_POTION.class),
   WIT_SHARPENING_POTION (net.pottercraft.Ollivanders2.Potion.WIT_SHARPENING_POTION.class),
   WOLFSBANE_POTION (net.pottercraft.Ollivanders2.Potion.WOLFSBANE_POTION.class);

   /**
    * The class of the potion this type creates
    */
   Class className;

   /**
    * Constructor.
    *
    * @param className the class for this potion type
    */
   O2PotionType (Class className)
   {
      this.className = className;
   }

   /**
    * Get the class for this potion type
    *
    * @return the class name for this type of potion.
    */
   public Class getClassName()
   {
      return className;
   }
}
package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2API;

/**
 * Enum of all potions types.
 *
 * @author Azami7
 * @since 2.2.7
 */
public enum O2PotionType
{
   ANIMAGUS_POTION(net.pottercraft.ollivanders2.potion.ANIMAGUS_POTION.class),
   BABBLING_BEVERAGE(net.pottercraft.ollivanders2.potion.BABBLING_BEVERAGE.class),
   BARUFFIOS_BRAIN_ELIXIR(net.pottercraft.ollivanders2.potion.BARUFFIOS_BRAIN_ELIXIR.class),
   COMMON_ANTIDOTE_POTION(net.pottercraft.ollivanders2.potion.COMMON_ANTIDOTE_POTION.class),
   CURE_FOR_BOILS(net.pottercraft.ollivanders2.potion.CURE_FOR_BOILS.class),
   DRAUGHT_OF_LIVING_DEATH(net.pottercraft.ollivanders2.potion.DRAUGHT_OF_LIVING_DEATH.class),
   FORGETFULLNESS_POTION(net.pottercraft.ollivanders2.potion.FORGETFULLNESS_POTION.class),
   HERBICIDE_POTION(net.pottercraft.ollivanders2.potion.HERBICIDE_POTION.class),
   MEMORY_POTION(net.pottercraft.ollivanders2.potion.MEMORY_POTION.class),
   REGENERATION_POTION(net.pottercraft.ollivanders2.potion.REGENERATION_POTION.class),
   SLEEPING_DRAUGHT(net.pottercraft.ollivanders2.potion.SLEEPING_DRAUGHT.class),
   WIDEYE_POTION(net.pottercraft.ollivanders2.potion.WIDEYE_POTION.class),
   WIGGENWELD_POTION(net.pottercraft.ollivanders2.potion.WIGGENWELD_POTION.class),
   WIT_SHARPENING_POTION(net.pottercraft.ollivanders2.potion.WIT_SHARPENING_POTION.class),
   WOLFSBANE_POTION(net.pottercraft.ollivanders2.potion.WOLFSBANE_POTION.class);

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

   /**
    * Get the name for this potion type.
    *
    * @return the spell name for this potion type.
    */
   public String getPotionName ()
   {
      String potionTypeString = this.toString().toLowerCase();

      return Ollivanders2API.common.firstLetterCapitalize(potionTypeString.replace("_", " "));
   }

   /**
    * Get a O2PotionType enum from a string. This should be used as the opposite of toString() on the enum.
    *
    * @param potionString the name of the spell type, ex. "AQUA_ERUCTO"
    * @return the potion type
    */
   public static O2PotionType potionTypeFromString (String potionString)
   {
      O2PotionType potionType = null;

      try
      {
         potionType = O2PotionType.valueOf(potionString);
      }
      catch (Exception e) { }

      return potionType;
   }
}
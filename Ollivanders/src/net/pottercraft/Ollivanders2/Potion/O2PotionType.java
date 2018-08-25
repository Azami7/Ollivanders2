package net.pottercraft.Ollivanders2.Potion;

public enum O2PotionType
{
   ANIMAGUS_POTION (net.pottercraft.Ollivanders2.Potion.ANIMAGUS_POTION.class),
   ANTIDOTE_POTION (net.pottercraft.Ollivanders2.Potion.ANTIDOTE_POTION.class),
   BABBLING_BEVERAGE (net.pottercraft.Ollivanders2.Potion.BABBLING_BEVERAGE.class),
   BARUFFIOS_BRAIN_ELIXIR (net.pottercraft.Ollivanders2.Potion.BARUFFIOS_BRAIN_ELIXIR.class),
   FORGETFULLNESS_POTION (net.pottercraft.Ollivanders2.Potion.FORGETFULLNESS_POTION.class),
   HERBICIDE_POTION (net.pottercraft.Ollivanders2.Potion.HERBICIDE_POTION.class),
   MEMORY_POTION (net.pottercraft.Ollivanders2.Potion.MEMORY_POTION.class),
   REGENERATION_POTION (net.pottercraft.Ollivanders2.Potion.REGENERATION_POTION.class),
   WIT_SHARPENING_POTION (net.pottercraft.Ollivanders2.Potion.WIT_SHARPENING_POTION.class),
   WOLFSBANE_POTION (net.pottercraft.Ollivanders2.Potion.WOLFSBANE_POTION.class);

   Class className;

   O2PotionType (Class className)
   {
      this.className = className;
   }

   public Class getClassName()
   {
      return className;
   }
}

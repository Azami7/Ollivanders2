package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents allowable stationary spells.
 */
public enum O2StationarySpellType
{
   ALIQUAM_FLOO(ALIQUAM_FLOO.class, Ollivanders2Common.MagicLevel.EXPERT),
   COLLOPORTUS(COLLOPORTUS.class, Ollivanders2Common.MagicLevel.EXPERT), // colloportus can only be undone with alohomora so we level to max to ensure other spells cannot undo it
   HARMONIA_NECTERE_PASSUS(HARMONIA_NECTERE_PASSUS.class, Ollivanders2Common.MagicLevel.EXPERT),
   HORCRUX(HORCRUX.class, Ollivanders2Common.MagicLevel.EXPERT),
   MOLLIARE(MOLLIARE.class, Ollivanders2Common.MagicLevel.OWL),
   MUFFLIATO(MUFFLIATO.class, Ollivanders2Common.MagicLevel.NEWT),
   NULLUM_APPAREBIT(NULLUM_APPAREBIT.class, Ollivanders2Common.MagicLevel.EXPERT),
   NULLUM_EVANESCUNT(NULLUM_EVANESCUNT.class, Ollivanders2Common.MagicLevel.EXPERT),
   PROTEGO(PROTEGO.class, Ollivanders2Common.MagicLevel.NEWT),
   PROTEGO_HORRIBILIS(PROTEGO_HORRIBILIS.class, Ollivanders2Common.MagicLevel.EXPERT),
   PROTEGO_MAXIMA(PROTEGO_MAXIMA.class, Ollivanders2Common.MagicLevel.EXPERT),
   PROTEGO_TOTALUM(PROTEGO_TOTALUM.class, Ollivanders2Common.MagicLevel.EXPERT),
   REPELLO_MUGGLETON(REPELLO_MUGGLETON.class, Ollivanders2Common.MagicLevel.NEWT);

   /**
    * The class of stationary spell this creates
    */
   private final Class<?> className;

   /**
    * The level of this stationary spell, for use with counter-spells
    */
   private final Ollivanders2Common.MagicLevel level;

   /**
    * Constructor
    *
    * @param className the class this type represents
    * @param level the level of this stationary spell
    */
   O2StationarySpellType(@NotNull Class<?> className, @NotNull Ollivanders2Common.MagicLevel level)
   {
      this.className = className;
      this.level = level;
   }

   /**
    * Get the class for this stationary spell
    *
    * @return the classname for this spell
    */
   @NotNull
   public Class<?> getClassName()
   {
      return className;
   }

   /**
    * Get the level of magic for this spell, for counter-spells
    *
    * @return the level of this spell
    */
   @NotNull
   public Ollivanders2Common.MagicLevel getLevel()
   {
      return level;
   }

   /**
    * Get the spell by name
    *
    * @param name the name of the spell
    * @return the spell type or null if not found
    */
   @Nullable
   public static O2StationarySpellType getStationarySpellTypeFromString(@NotNull String name)
   {
      O2StationarySpellType spellType = null;

      try
      {
         spellType = O2StationarySpellType.valueOf(name);
      }
      catch (Exception e)
      {
         // do nothing, this is expected if they send a string that is not a valid spell
      }

      return spellType;
   }

   /**
    * Get the spell name for this spell type.
    *
    * @return the spell name for this spell type.
    */
   @NotNull
   public String getSpellName ()
   {
      String spellTypeString = this.toString().toLowerCase();

      return Ollivanders2API.common.firstLetterCapitalize(spellTypeString.replace("_", " "));
   }
}
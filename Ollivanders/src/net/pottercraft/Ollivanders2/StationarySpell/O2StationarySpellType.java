package net.pottercraft.Ollivanders2.StationarySpell;

/**
 * Represents allowable stationary spells.
 */
public enum O2StationarySpellType
{
   ALIQUAM_FLOO (net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO.class),
   COLLOPORTUS (net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS.class),
   HARMONIA_NECTERE_PASSUS (net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS.class),
   HORCRUX (net.pottercraft.Ollivanders2.StationarySpell.HORCRUX.class),
   MOLLIARE (net.pottercraft.Ollivanders2.StationarySpell.MOLLIARE.class),
   MUFFLIATO (net.pottercraft.Ollivanders2.StationarySpell.MUFFLIATO.class),
   NULLUM_APPAREBIT (net.pottercraft.Ollivanders2.StationarySpell.NULLUM_APPAREBIT.class),
   NULLUM_EVANESCUNT (net.pottercraft.Ollivanders2.StationarySpell.NULLUM_EVANESCUNT.class),
   PRAEPANDO (net.pottercraft.Ollivanders2.StationarySpell.PRAEPANDO.class),
   PROTEGO (net.pottercraft.Ollivanders2.StationarySpell.PROTEGO.class),
   PROTEGO_HORRIBILIS (net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_HORRIBILIS.class),
   PROTEGO_MAXIMA (net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_MAXIMA.class),
   PROTEGO_TOTALUM (net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_TOTALUM.class),
   REPELLO_MUGGLETON (net.pottercraft.Ollivanders2.StationarySpell.REPELLO_MUGGLETON.class);

   private final Class className;

   O2StationarySpellType (Class className)
   {
      this.className = className;
   }

   public Class getClassName ()
   {
      return className;
   }

   public static O2StationarySpellType getStationarySpellTypeFromString (String name)
   {
      O2StationarySpellType spellType = null;

      try
      {
         spellType = O2StationarySpellType.valueOf(name);
      }
      catch (Exception e) { }

      return spellType;
   }
}
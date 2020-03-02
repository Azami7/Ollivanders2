package net.pottercraft.ollivanders2.stationaryspell;

/**
 * Represents allowable stationary spells.
 */
public enum O2StationarySpellType
{
   ALIQUAM_FLOO(net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO.class),
   COLLOPORTUS(net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS.class),
   HARMONIA_NECTERE_PASSUS(net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS.class),
   HORCRUX(net.pottercraft.ollivanders2.stationaryspell.HORCRUX.class),
   MOLLIARE(net.pottercraft.ollivanders2.stationaryspell.MOLLIARE.class),
   MUFFLIATO(net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO.class),
   NULLUM_APPAREBIT(net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT.class),
   NULLUM_EVANESCUNT(net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT.class),
   PRAEPANDO(net.pottercraft.ollivanders2.stationaryspell.PRAEPANDO.class),
   PROTEGO(net.pottercraft.ollivanders2.stationaryspell.PROTEGO.class),
   PROTEGO_HORRIBILIS(net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS.class),
   PROTEGO_MAXIMA(net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA.class),
   PROTEGO_TOTALUM(net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.class),
   REPELLO_MUGGLETON(net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON.class);

   private final Class className;

   O2StationarySpellType(Class className)
   {
      this.className = className;
   }

   public Class getClassName()
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
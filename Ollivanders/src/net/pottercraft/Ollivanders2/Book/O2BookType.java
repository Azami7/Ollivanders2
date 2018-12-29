package net.pottercraft.Ollivanders2.Book;

/**
 * All Ollivanders2 book types.
 *
 * @since 2.2.4
 * @author Azami7
 */
public enum O2BookType
{
   A_BEGINNERS_GUIDE_TO_TRANSFIGURATION (net.pottercraft.Ollivanders2.Book.A_BEGINNERS_GUIDE_TO_TRANSFIGURATION.class),
   ACHIEVEMENTS_IN_CHARMING (net.pottercraft.Ollivanders2.Book.ACHIEVEMENTS_IN_CHARMING.class),
   ADVANCED_FIREWORKS (net.pottercraft.Ollivanders2.Book.ADVANCED_FIREWORKS.class),
   ADVANCED_POTION_MAKING (net.pottercraft.Ollivanders2.Book.ADVANCED_POTION_MAKING.class),
   ADVANCED_TRANSFIGURATION (net.pottercraft.Ollivanders2.Book.ADVANCED_TRANSFIGURATION.class),
   BASIC_FIREWORKS (net.pottercraft.Ollivanders2.Book.BASIC_FIREWORKS.class),
   BASIC_HEXES (net.pottercraft.Ollivanders2.Book.BASIC_HEXES.class),
   BREAK_WITH_A_BANSHEE (net.pottercraft.Ollivanders2.Book.BREAK_WITH_A_BANSHEE.class),
   CHADWICKS_CHARMS_VOLUME_1 (CHADWICKS_CHARMS_VOLUME_1.class),
   CONFRONTING_THE_FACELESS (net.pottercraft.Ollivanders2.Book.CONFRONTING_THE_FACELESS.class),
   CURSES_AND_COUNTERCURSES (net.pottercraft.Ollivanders2.Book.CURSES_AND_COUNTERCURSES.class),
   DE_MEDICINA_PRAECEPTA(net.pottercraft.Ollivanders2.Book.DE_MEDICINA_PRAECEPTA.class),
   ESSENTIAL_DARK_ARTS (net.pottercraft.Ollivanders2.Book.ESSENTIAL_DARK_ARTS.class),
   EXTREME_INCANTATIONS (net.pottercraft.Ollivanders2.Book.EXTREME_INCANTATIONS.class),
   //FANTASTIC_BEASTS (net.pottercraft.Ollivanders2.Book.FANTASTIC_BEASTS.class),
   FOR_THE_GREATER_GOOD (net.pottercraft.Ollivanders2.Book.FOR_THE_GREATER_GOOD.class),
   GADDING_WITH_GHOULS (net.pottercraft.Ollivanders2.Book.GADDING_WITH_GHOULS.class),
   HARMONIOUS_CONNECTIONS (net.pottercraft.Ollivanders2.Book.HARMONIOUS_CONNECTIONS.class),
   HOLIDAYS_WITH_HAGS (net.pottercraft.Ollivanders2.Book.HOLIDAYS_WITH_HAGS.class),
   INTERMEDIATE_TRANSFIGURATION (net.pottercraft.Ollivanders2.Book.INTERMEDIATE_TRANSFIGURATION.class),
   JINXES_FOR_THE_JINXED (net.pottercraft.Ollivanders2.Book.JINXES_FOR_THE_JINXED.class),
   MAGICAL_DRAFTS_AND_POTIONS (net.pottercraft.Ollivanders2.Book.MAGICAL_DRAFTS_AND_POTIONS.class),
   MAGICK_MOSTE_EVILE (net.pottercraft.Ollivanders2.Book.MAGICK_MOSTE_EVILE.class),
   MODERN_MAGICAL_TRANSPORTATION (net.pottercraft.Ollivanders2.Book.MODERN_MAGICAL_TRANSPORTATION.class),
   MOSTE_POTENTE_POTIONS (net.pottercraft.Ollivanders2.Book.MOSTE_POTENTE_POTIONS.class),
   NUMEROLOGY_AND_GRAMMATICA (net.pottercraft.Ollivanders2.Book.NUMEROLOGY_AND_GRAMMATICA.class),
   POTION_OPUSCULE (net.pottercraft.Ollivanders2.Book.POTION_OPUSCULE.class),
   PRACTICAL_DEFENSIVE_MAGIC (net.pottercraft.Ollivanders2.Book.PRACTICAL_DEFENSIVE_MAGIC.class),
   QUINTESSENCE_A_QUEST (net.pottercraft.Ollivanders2.Book.QUINTESSENCE_A_QUEST.class),
   SECRETS_OF_THE_DARKEST_ART (net.pottercraft.Ollivanders2.Book.SECRETS_OF_THE_DARKEST_ART.class),
   SECRETS_OF_WANDLORE (net.pottercraft.Ollivanders2.Book.SECRETS_OF_WANDLORE.class),
   STANDARD_BOOK_OF_SPELLS_GRADE_1 (net.pottercraft.Ollivanders2.Book.STANDARD_BOOK_OF_SPELLS_GRADE_1.class),
   STANDARD_BOOK_OF_SPELLS_GRADE_2 (net.pottercraft.Ollivanders2.Book.STANDARD_BOOK_OF_SPELLS_GRADE_2.class),
   STANDARD_BOOK_OF_SPELLS_GRADE_3 (net.pottercraft.Ollivanders2.Book.STANDARD_BOOK_OF_SPELLS_GRADE_3.class),
   STANDARD_BOOK_OF_SPELLS_GRADE_4 (net.pottercraft.Ollivanders2.Book.STANDARD_BOOK_OF_SPELLS_GRADE_4.class),
   STANDARD_BOOK_OF_SPELLS_GRADE_5 (net.pottercraft.Ollivanders2.Book.STANDARD_BOOK_OF_SPELLS_GRADE_5.class),
   STANDARD_BOOK_OF_SPELLS_GRADE_6 (net.pottercraft.Ollivanders2.Book.STANDARD_BOOK_OF_SPELLS_GRADE_6.class),
   STANDARD_BOOK_OF_SPELLS_GRADE_7 (net.pottercraft.Ollivanders2.Book.STANDARD_BOOK_OF_SPELLS_GRADE_7.class),
   THE_DARK_FORCES (net.pottercraft.Ollivanders2.Book.THE_DARK_FORCES.class),
   THE_HEALERS_HELPMATE (net.pottercraft.Ollivanders2.Book.THE_HEALERS_HELPMATE.class),
   TRAVELS_WITH_TROLLS (net.pottercraft.Ollivanders2.Book.TRAVELS_WITH_TROLLS.class),
   VOYAGES_WITH_VAMPIRES (net.pottercraft.Ollivanders2.Book.VOYAGES_WITH_VAMPIRES.class),
   WANDERINGS_WITH_WEREWOLVES (net.pottercraft.Ollivanders2.Book.WANDERINGS_WITH_WEREWOLVES.class),
   WORLD_OF_COLOR (net.pottercraft.Ollivanders2.Book.WORLD_OF_COLOR.class),
   YEAR_WITH_A_YETI (net.pottercraft.Ollivanders2.Book.YEAR_WITH_A_YETI.class);

   private Class className;

   O2BookType (Class className)
   {
      this.className = className;
   }

   public Class getClassName()
   {
      return className;
   }
}

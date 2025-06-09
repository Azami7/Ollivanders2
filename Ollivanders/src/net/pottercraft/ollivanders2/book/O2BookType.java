package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * All Ollivanders2 book types.
 *
 * @author Azami7
 * @since 2.2.4
 */
public enum O2BookType {
    A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(A_BEGINNERS_GUIDE_TO_TRANSFIGURATION.class, "A Beginner's Guide to Transfiguration", "Beginners Transfiguration", "Emeric Switch", O2MagicBranch.TRANSFIGURATION),
    ACHIEVEMENTS_IN_CHARMING(ACHIEVEMENTS_IN_CHARMING.class, "Achievements in Charming", "Achievements in Charming", "Unknown", O2MagicBranch.CHARMS),
    ADVANCED_FIREWORKS(ADVANCED_FIREWORKS.class, "Advanced Fireworks for Fun and Profit", "Advanced Fireworks", "George Weasley", O2MagicBranch.CHARMS),
    ADVANCED_POTION_MAKING(ADVANCED_POTION_MAKING.class, "Advanced Potion Making", "Advanced Potion Making", "Libatius Borage", O2MagicBranch.POTIONS),
    ADVANCED_TRANSFIGURATION(ADVANCED_TRANSFIGURATION.class, "A Guide to Advanced Transfiguration", "Advanced Transfiguration", "Unknown", O2MagicBranch.TRANSFIGURATION),
    BASIC_FIREWORKS(BASIC_FIREWORKS.class, "Basic Fireworks", "Basic Fireworks", "George Weasley", O2MagicBranch.CHARMS),
    BASIC_HEXES(BASIC_HEXES.class, "Basic Hexes for the Busy and Vexed", "Basic Hexes", "Unknown", O2MagicBranch.DARK_ARTS),
    // BOOK_OF_POTIONS (BOOK_OF_POTIONS.class, "Book Of Potions", "Book Of Potions", "Zygmunt Budge", O2MagicBranch.POTIONS),
    BREAK_WITH_A_BANSHEE(BREAK_WITH_A_BANSHEE.class, "Break With A Banshee", "Break With A Banshee", "Gilderoy Lockhart", O2MagicBranch.DARK_ARTS),
    CHADWICKS_CHARMS_VOLUME_1(CHADWICKS_CHARMS_VOLUME_1.class, "Chadwick's Charms Volume 1", "Chadwicks Charms Volume 1", "Chadwick Boot", O2MagicBranch.CHARMS),
    CHARMING_COLORS(CHARMING_COLORS.class, "Charming Colors", "Charming Colors", "Nymphadora Tonks", O2MagicBranch.CHARMS),
    CONFRONTING_THE_FACELESS(CONFRONTING_THE_FACELESS.class, "Confronting the Faceless", "Confronting the Faceless", "Unknown", O2MagicBranch.DARK_ARTS),
    CURSES_AND_COUNTERCURSES(CURSES_AND_COUNTERCURSES.class, "Curses and Counter-Curses", "Curses and Counter-Curses", "Professor Vindictus Viridian", O2MagicBranch.DARK_ARTS),
    DE_MEDICINA_PRAECEPTA(DE_MEDICINA_PRAECEPTA.class, "De Medicina Praecepta", "De Medicina Praecepta", "Quintus Serenus Sammonicus", O2MagicBranch.HEALING),
    DEFENSE_AGAINST_THE_DARK_ARTS(DEFENSE_AGAINST_THE_DARK_ARTS.class, "Defence Against the Dark Arts", "Defence Against Dark Arts", "Galatea Merrythought", O2MagicBranch.DARK_ARTS),
    ESSENTIAL_DARK_ARTS(ESSENTIAL_DARK_ARTS.class, "The Essential Defence Against the Dark Arts", "Essential Dark Arts Defence", "Arsenius Jigger", O2MagicBranch.DARK_ARTS),
    EXTREME_INCANTATIONS(EXTREME_INCANTATIONS.class, "Extreme Incantations", "Extreme Incantations", "Violeta Stitch", O2MagicBranch.CHARMS),
    //FANTASTIC_BEASTS (FANTASTIC_BEASTS.class, "Fantastic Beasts and Where to Find Them", "Fantastic Beasts", "Newt Scamander", O2MagicBranch.CARE_OF_MAGICAL_CREATURES;),
    FOR_THE_GREATER_GOOD(FOR_THE_GREATER_GOOD.class, "For The Greater Good", "For The Greater Good", "Gellert Grindelwald", O2MagicBranch.DARK_ARTS),
    GADDING_WITH_GHOULS(GADDING_WITH_GHOULS.class, "Gadding with Ghouls", "Gadding with Ghouls", "Gilderoy Lockhart", O2MagicBranch.DARK_ARTS),
    HARMONIOUS_CONNECTIONS(HARMONIOUS_CONNECTIONS.class, "Harmonious Connections", "Harmonious Connections", "Unknown", O2MagicBranch.CHARMS),
    HOLIDAYS_WITH_HAGS(HOLIDAYS_WITH_HAGS.class, "Holidays with Hags", "Holidays with Hags", "Gilderoy Lockhart", O2MagicBranch.DARK_ARTS),
    INTERMEDIATE_TRANSFIGURATION(INTERMEDIATE_TRANSFIGURATION.class, "Intermediate Transfiguration", "Intermediate Transfiguration", "Unknown", O2MagicBranch.TRANSFIGURATION),
    JINXES_FOR_THE_JINXED(JINXES_FOR_THE_JINXED.class, "Jinxes for the Jinxed", "Jinxes for the Jinxed", "Unknown", O2MagicBranch.DARK_ARTS),
    MAGICAL_DRAFTS_AND_POTIONS(MAGICAL_DRAFTS_AND_POTIONS.class, "Magical Drafts and Potions", "Magical Drafts and Potions", "Arsenius Jigger", O2MagicBranch.POTIONS),
    MAGICK_MOSTE_EVILE(MAGICK_MOSTE_EVILE.class, "Magick Moste Evile", "Magick Moste Evile", "Godelot", O2MagicBranch.DARK_ARTS),
    MODERN_MAGICAL_TRANSPORTATION(MODERN_MAGICAL_TRANSPORTATION.class, "Modern Magical Transportation", "Magical Transportation", "Azami7", O2MagicBranch.CHARMS),
    MOSTE_POTENTE_POTIONS(MOSTE_POTENTE_POTIONS.class, "Moste Potente Potions", "Moste Potente Potions", "Phineas Bourne", O2MagicBranch.POTIONS),
    NUMEROLOGY_AND_GRAMMATICA(NUMEROLOGY_AND_GRAMMATICA.class, "Numerology and Grammatica", "Numerology and Grammatica", "Unknown", O2MagicBranch.ARITHMANCY),
    OMENS_ORACLES_AND_THE_GOAT(OMENS_ORACLES_AND_THE_GOAT.class, "Omens, Oracles & the Goat", "Omens, Oracles & the Goat", "Bathilda Bagshot", O2MagicBranch.DIVINATION),
    POTION_OPUSCULE(POTION_OPUSCULE.class, "Potion Opuscule", "Potion Opuscule", "Arsenius Jigger", O2MagicBranch.POTIONS),
    PRACTICAL_DEFENSIVE_MAGIC(PRACTICAL_DEFENSIVE_MAGIC.class, "Practical Defensive Magic Volume One", "Practical Defensive Magic 1", "Unknown", O2MagicBranch.DARK_ARTS),
    QUINTESSENCE_A_QUEST(QUINTESSENCE_A_QUEST.class, "Quintessence: A Quest", "Quintessence", "Unknown", O2MagicBranch.CHARMS),
    SECRETS_OF_THE_DARKEST_ART(SECRETS_OF_THE_DARKEST_ART.class, "Secrets of the Darkest Art", "Darkest Art", "Owle Bullock", O2MagicBranch.DARK_ARTS),
    SECRETS_OF_WANDLORE(SECRETS_OF_WANDLORE.class, "Secrets of Wandlore", "Secrets of Wandlore", "Geraint Ollivander", O2MagicBranch.CHARMS),
    STANDARD_BOOK_OF_SPELLS_GRADE_1(STANDARD_BOOK_OF_SPELLS_GRADE_1.class, "Standard Book of Spells Grade 1", "Standard Book of Spells Grade 1", "Miranda Goshawk", O2MagicBranch.CHARMS),
    STANDARD_BOOK_OF_SPELLS_GRADE_2(STANDARD_BOOK_OF_SPELLS_GRADE_2.class, "Standard Book of Spells Grade 2", "Standard Book of Spells Grade 2", "Miranda Goshawk", O2MagicBranch.CHARMS),
    STANDARD_BOOK_OF_SPELLS_GRADE_3(STANDARD_BOOK_OF_SPELLS_GRADE_3.class, "Standard Book of Spells Grade 3", "Standard Book of Spells Grade 3", "Miranda Goshawk", O2MagicBranch.CHARMS),
    STANDARD_BOOK_OF_SPELLS_GRADE_4(STANDARD_BOOK_OF_SPELLS_GRADE_4.class, "Standard Book of Spells Grade 4", "Standard Book of Spells Grade 4", "Miranda Goshawk", O2MagicBranch.CHARMS),
    STANDARD_BOOK_OF_SPELLS_GRADE_5(STANDARD_BOOK_OF_SPELLS_GRADE_5.class, "Standard Book of Spells Grade 5", "Standard Book of Spells Grade 5", "Miranda Goshawk", O2MagicBranch.CHARMS),
    STANDARD_BOOK_OF_SPELLS_GRADE_6(STANDARD_BOOK_OF_SPELLS_GRADE_6.class, "Standard Book of Spells Grade 6", "Standard Book of Spells Grade 6", "Miranda Goshawk", O2MagicBranch.CHARMS),
    STANDARD_BOOK_OF_SPELLS_GRADE_7(STANDARD_BOOK_OF_SPELLS_GRADE_7.class, "Standard Book of Spells Grade 7", "Standard Book of Spells Grade 7", "Miranda Goshawk", O2MagicBranch.CHARMS),
    TETRABIBLIOS(TETRABIBLIOS.class, "Tetrabilios", "Tetrabilios", "Ptolemy", O2MagicBranch.DIVINATION),
    THE_DARK_FORCES(THE_DARK_FORCES.class, "The Dark Forces: A Guide to Self-Protection", "The Dark Forces", "Quentin Trimble", O2MagicBranch.DARK_ARTS),
    THE_HEALERS_HELPMATE(THE_HEALERS_HELPMATE.class, "The Healer's Helpmate", "The Healers Helpmate", "H. Pollingtonious", O2MagicBranch.HEALING),
    TRAVELS_WITH_TROLLS(TRAVELS_WITH_TROLLS.class, "Traveling with Trolls", "Traveling with Trolls", "Gilderoy Lockhart", O2MagicBranch.DARK_ARTS),
    UNFOGGING_THE_FUTURE(UNFOGGING_THE_FUTURE.class, "Unfogging the Future", "Unfogging the Future", "Cassandra Vablatsky", O2MagicBranch.DIVINATION),
    VOYAGES_WITH_VAMPIRES(VOYAGES_WITH_VAMPIRES.class, "Voyages with Vampires", "Voyages with Vampires", "Gilderoy Lockhart", O2MagicBranch.DARK_ARTS),
    WANDERINGS_WITH_WEREWOLVES(WANDERINGS_WITH_WEREWOLVES.class, "Wanderings with Werewolves", "Wanderings with Werewolves", "Gilderoy Lockhart", O2MagicBranch.DARK_ARTS),
    YEAR_WITH_A_YETI(YEAR_WITH_A_YETI.class, "Year with a Yeti", "Year with a Yeti", "Gilderoy Lockhart", O2MagicBranch.DARK_ARTS),
    ;

    /**
     * The implementing class for the book contents
     */
    private final Class<?> className;

    /**
     * The book author
     */
    private final String author;

    /**
     * The full book title
     */
    private String title;
    final static String titleLabel = "_title";

    /**
     * The book title for item lore, cannot be more than 32 characters or it will appear blank.
     */
    private String shortTitle;
    final static String shortTitleLabel = "_shortTitle";

    /**
     * The branch of magic this book covers
     */
    private final O2MagicBranch branch;

    /**
     * Constructor
     *
     * @param className the name of the class this type represents
     */
    O2BookType(@NotNull Class<?> className, @NotNull String title, @NotNull String shortTitle, @NotNull String author, @NotNull O2MagicBranch branch) {
        this.className = className;
        this.title = title;

        if (shortTitle.length() > 32)
            this.shortTitle = shortTitle.substring(0, 31);
        else
            this.shortTitle = shortTitle;

        this.author = author;
        this.branch = branch;
    }

    /**
     * Get the class for this type
     *
     * @return the class name
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Return the short title for a book. This is the display name title for the book item.
     *
     * @param p the Ollivanders2 plugin
     * @return the short title of the book
     */
    public String getShortTitle(@NotNull Ollivanders2 p) {
        // first check to see if it has been set with config
        if (Ollivanders2.useTranslations && p.getConfig().isSet(this.toString() + shortTitleLabel)) {
            String s = p.getConfig().getString(this.toString() + shortTitleLabel);
            if (s != null && s.length() > 0)
                return s;
        }

        return shortTitle;
    }

    /**
     * Return the title for a book. This is the display name title for the book item.
     *
     * @param p the Ollivanders2 plugin
     * @return the title of the book
     */
    public String getTitle(@NotNull Ollivanders2 p) {
        // first check to see if it has been set with config
        String identifier = this.toString() + titleLabel;

        if (Ollivanders2.useTranslations && p.getConfig().isSet(identifier)) {
            String t = p.getConfig().getString(identifier);

            if (t != null && t.length() > 0)
                return t;
        }

        return title;
    }

    /**
     * Return the author for a book. This is the display name title for the book item.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Return the branch of magic this book is.
     *
     * @return the branch of magic for this book
     */
    public O2MagicBranch getBranch() {
        return branch;
    }
}

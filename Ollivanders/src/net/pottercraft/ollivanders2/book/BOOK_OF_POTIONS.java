package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.jetbrains.annotations.NotNull;

/**
 * The Book of Potions is a book concerned with Potion-making, written by wizard Zygmunt Budge. Like with Miranda
 * Goshawk's Book of Spells, this potions book has the ability to conjure utensils with which the reader can brew the
 * various potions included.
 * <p>
 * http://harrypotter.wikia.com/wiki/Book_of_Potions
 *
 * @author Azami7
 * @since 2.2.7
 */
public class BOOK_OF_POTIONS extends O2Book {
    public BOOK_OF_POTIONS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        // bookType = O2BookType.BOOK_OF_POTIONS;

        openingPage = "You, young potioneer, hold in your hands my masterpiece. With it, my place in history is assured. -Zygmunt Budge";

        // todo polyjuice potion - https://harrypotter.fandom.com/wiki/Polyjuice_Potion
        // todo Beautification Potion - https://harrypotter.fandom.com/wiki/Beautification_Potion
        potions.add(O2PotionType.CURE_FOR_BOILS);
        // todo doxycide - https://harrypotter.fandom.com/wiki/Doxycide
        // todo felix felicis - https://harrypotter.fandom.com/wiki/Felix_Felicis
        // todo Laughing Potion - ame effect as the tickling charm - https://harrypotter.fandom.com/wiki/Laughing_Potion
        // todo swelling solution - https://harrypotter.fandom.com/wiki/Swelling_Solution - must be different than engorgio
        // todo sleeping potion - https://harrypotter.fandom.com/wiki/Sleeping_Draught
    }
}

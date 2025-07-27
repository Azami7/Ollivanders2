package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Advanced Potion-Making is a book written by Libatius Borage. As the title implies this book contains advanced recipes
 * and various other topics related to potion-making. This textbook has been used for decades in the education of young
 * witches and wizards.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.potion.DRAUGHT_OF_LIVING_DEATH}<br>
 * {@link net.pottercraft.ollivanders2.potion.MEMORY_POTION}
 * </p>
 *
 * @see <a href = "http://harrypotter.wikia.com/wiki/Advanced_Potion-Making">http://harrypotter.wikia.com/wiki/Advanced_Potion-Making</a>
 * @author Azami7
 * @since 2.2.7
 */
public class ADVANCED_POTION_MAKING extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public ADVANCED_POTION_MAKING(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.ADVANCED_POTION_MAKING;

        // 5th year
        // todo draught of peace - https://harrypotter.fandom.com/wiki/Draught_of_Peace
        // todo invigoration draught - speed potion effect - https://harrypotter.fandom.com/wiki/Invigoration_Draught
        // todo everlasting elixir - https://harrypotter.fandom.com/wiki/Everlasting_Elixirs
        // todo befuddlement draught - slowness potion effect - https://harrypotter.fandom.com/wiki/Befuddlement_Draught
        // todo blindness potion effect
        // todo occulua potion - reverses blindness - https://harrypotter.fandom.com/wiki/Oculus_Potion (cures conjunctivitis curse)
        // todo jump boost potion effect
        // todo wither potion effect
        // todo Hiccoughing Solution
        // todo stronger strengthening solution
        // todo lingering potions

        // 6th year
        // todo elixir to induce euphornia - https://harrypotter.fandom.com/wiki/Elixir_to_Induce_Euphoria
        // todo Amortentia - https://harrypotter.fandom.com/wiki/Amortentia
        // todo love potion antidote
        potions.add(O2PotionType.DRAUGHT_OF_LIVING_DEATH);
        // todo pink poison - poison potion effect - https://harrypotter.fandom.com/wiki/Garish_pink_blended_poison
        // todo antidote for pink poison - https://harrypotter.fandom.com/wiki/Antidote_to_garish_pink_blended_poison
        // todo regerminating potion - restore dead plants - https://harrypotter.fandom.com/wiki/Regerminating_Potion
        potions.add(O2PotionType.MEMORY_POTION);
        // todo Scintillation Solution - glowing potion effect - https://harrypotter.fandom.com/wiki/Scintillation_Solution
        // todo dolphins grace potion effect
        // todo exploding potion - https://harrypotter.fandom.com/wiki/Exploding_Potion

        // 7th year
        // todo mandrake restorative draught - https://harrypotter.fandom.com/wiki/Mandrake_Restorative_Draught
        // todo stronger healing potion - instance health potion effect
        // todo instant damage potion effect
        // todo potion to force animagus to turn back to human form
        // todo wolfsbane
        // todo shrinking solution, snape's version - https://harrypotter.fandom.com/wiki/Shrinking_Solution
        // todo water breathing potion
    }
}

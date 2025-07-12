package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.jetbrains.annotations.NotNull;

/**
 * Potion Opuscule is an elementary book on potions by Jigger.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.potion.BABBLING_BEVERAGE}<br>
 * {@link net.pottercraft.ollivanders2.potion.FORGETFULLNESS_POTION}
 * </p>
 *
 * @see <a href = "http://harrypotter.wikia.com/wiki/Potion_Opuscule">http://harrypotter.wikia.com/wiki/Potion_Opuscule</a>
 * @author Azami7
 * @since 2.2.7
 */
public class POTION_OPUSCULE extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public POTION_OPUSCULE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.POTION_OPUSCULE;

        potions.add(O2PotionType.BABBLING_BEVERAGE);
        potions.add(O2PotionType.FORGETFULLNESS_POTION);
        // todo hunger potion effect
        // todo mining fatigue potion effect - https://harrypotter.fandom.com/wiki/Fatiguing_Fusion
        // todo weaving potion effect
        // todo wind charged potion effect
    }
}

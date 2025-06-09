package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.jetbrains.annotations.NotNull;

/**
 * Potion Opuscule is an elementary book on potions by Jigger.
 * <p>
 * http://harrypotter.wikia.com/wiki/Potion_Opuscule
 *
 * @author Azami7
 * @since 2.2.7
 */
public class POTION_OPUSCULE extends O2Book {
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

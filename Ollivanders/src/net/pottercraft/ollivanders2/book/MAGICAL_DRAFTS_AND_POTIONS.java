package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.jetbrains.annotations.NotNull;

/**
 * Magical Drafts and Potions - OWL potions book
 * <p>
 * {@link net.pottercraft.ollivanders2.potion.COMMON_ANTIDOTE_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.FORGETFULLNESS_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.HERBICIDE_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.CURE_FOR_BOILS}<br>
 * {@link net.pottercraft.ollivanders2.potion.OCULUS_FELIS}<br>
 * {@link net.pottercraft.ollivanders2.potion.WIGGENWELD_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.ICE_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.STRENGTHENING_SOLUTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.WIDEYE_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.SLEEPING_DRAUGHT}<br>
 * {@link net.pottercraft.ollivanders2.potion.WIT_SHARPENING_POTION}
 * </p>
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Magical_Drafts_and_Potions">https://harrypotter.fandom.com/wiki/Magical_Drafts_and_Potions</a>
 * @since 2.2.7
 */
public class MAGICAL_DRAFTS_AND_POTIONS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public MAGICAL_DRAFTS_AND_POTIONS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.MAGICAL_DRAFTS_AND_POTIONS;

        // 1st year
        potions.add(O2PotionType.COMMON_ANTIDOTE_POTION);
        potions.add(O2PotionType.FORGETFULLNESS_POTION);
        potions.add(O2PotionType.HERBICIDE_POTION);
        potions.add(O2PotionType.CURE_FOR_BOILS);
        potions.add(O2PotionType.OCULUS_FELIS);

        // 2nd year
        potions.add(O2PotionType.WIGGENWELD_POTION);
        potions.add(O2PotionType.ICE_POTION);
        potions.add(O2PotionType.STRENGTHENING_SOLUTION);
        // todo weakness potion - weakness potion effect
        // todo swelling solution - https://harrypotter.fandom.com/wiki/Swelling_Solution - must be different than engorgio
        // todo shrinking solution - https://harrypotter.fandom.com/wiki/Shrinking_Solution - must be different than reducio
        // todo hunger potion effect
        // todo saturation potion effect

        // 3rd year
        potions.add(O2PotionType.WIDEYE_POTION);
        // todo Confusing Concoctio - nausea potion effect - https://harrypotter.fandom.com/wiki/Confusing_Concoction
        // todo antidote to uncommon poisons - https://harrypotter.fandom.com/wiki/Antidote_to_Uncommon_Poisons
        potions.add(O2PotionType.SLEEPING_DRAUGHT);
        // todo doxycide - https://harrypotter.fandom.com/wiki/Doxycide
        // todo absorbtion potion effect
        // todo darkness potion effect
        // todo slow-falling potion effect

        // 4th year
        potions.add(O2PotionType.WIT_SHARPENING_POTION);
        // todo calming draught
        // todo pepperup potion - https://harrypotter.fandom.com/wiki/Pepperup_Potion
        // todo ageing potion
        // todo Weedosoros poison - https://harrypotter.fandom.com/wiki/Weedosoros
        // todo skele-gro - regeneration potion effect
        // todo health boost potion effect
        // todo resistance potion effect
        // todo oculus potion - https://harrypotter.fandom.com/wiki/Oculus_Potion
        // todo splash potions
    }
}

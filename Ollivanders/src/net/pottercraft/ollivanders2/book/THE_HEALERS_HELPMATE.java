package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Healer's Helpmate
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.AGUAMENTI}<br>
 * {@link net.pottercraft.ollivanders2.spell.BRACKIUM_EMENDO}<br>
 * {@link net.pottercraft.ollivanders2.spell.EPISKEY}<br>
 * {@link net.pottercraft.ollivanders2.potion.COMMON_ANTIDOTE_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.WIDEYE_POTION}<br>
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/The_Healer's_Helpmate">https://harrypotter.fandom.com/wiki/The_Healer's_Helpmate</a>
 * @author Azami7
 * @since 2.2.4
 */
public class THE_HEALERS_HELPMATE extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public THE_HEALERS_HELPMATE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.THE_HEALERS_HELPMATE;

        spells.add(O2SpellType.AGUAMENTI);
        spells.add(O2SpellType.BRACKIUM_EMENDO);
        spells.add(O2SpellType.EPISKEY);
        potions.add(O2PotionType.COMMON_ANTIDOTE_POTION);
        // todo wound cleaning potion
        // todo pepperup potion
        potions.add(O2PotionType.WIDEYE_POTION);
        // todo burn healing paste
    }
}

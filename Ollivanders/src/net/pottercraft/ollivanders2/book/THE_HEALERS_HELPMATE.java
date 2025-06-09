package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Healer's Helpmate
 *
 * @link https://harrypotter.fandom.com/wiki/The_Healer's_Helpmate
 * @author Azami7
 * @since 2.2.4
 */
public class THE_HEALERS_HELPMATE extends O2Book {
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

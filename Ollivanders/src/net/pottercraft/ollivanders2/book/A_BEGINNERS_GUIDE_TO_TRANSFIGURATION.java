package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * A Beginner's Guide to Transfiguration - 1-3 year transfiguration book
 * <p>
 * https://harrypotter.fandom.com/wiki/A_Beginner's_Guide_to_Transfiguration
 */
public class A_BEGINNERS_GUIDE_TO_TRANSFIGURATION extends O2Book
{
    public A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(@NotNull Ollivanders2 plugin)
    {
        super(plugin);

        bookType = O2BookType.A_BEGINNERS_GUIDE_TO_TRANSFIGURATION;

        spells.add(O2SpellType.DURO);
        spells.add(O2SpellType.FATUUS_AURUM);
        spells.add(O2SpellType.CALAMUS);
        spells.add(O2SpellType.DEPRIMO);
        spells.add(O2SpellType.MULTICORFORS);
        spells.add(O2SpellType.VERA_VERTO);
        spells.add(O2SpellType.SNUFFLIFORS);
        // 8
        // 9
        // 10
        // 11
        // 12
    }
}

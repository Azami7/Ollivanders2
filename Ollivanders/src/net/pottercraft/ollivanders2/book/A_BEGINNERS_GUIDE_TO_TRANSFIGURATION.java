package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * A Beginner's Guide to Transfiguration - 1st & 2nd year transfiguration book
 * <p>
 * https://harrypotter.fandom.com/wiki/A_Beginner's_Guide_to_Transfiguration
 */
public class A_BEGINNERS_GUIDE_TO_TRANSFIGURATION extends O2Book
{
    public A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(@NotNull Ollivanders2 plugin)
    {
        super(plugin);

        bookType = O2BookType.A_BEGINNERS_GUIDE_TO_TRANSFIGURATION;

        // todo acus fieri (match to needle) - https://harrypotter.fandom.com/wiki/Match_to_needle_spell
        // todo permurate (switching spell) - https://harrypotter.fandom.com/wiki/Switching_Spell
        // todo arculam (mouse to snuffbox) - https://harrypotter.fandom.com/wiki/Mice_to_Snuffboxes
        // todo beetle into button - https://harrypotter.fandom.com/wiki/Beetle_into_Button
        // todo amphoram - snail to teapot
        spells.add(O2SpellType.CALAMUS);

        spells.add(O2SpellType.VERA_VERTO);
        spells.add(O2SpellType.LAGOMORPHA);
        spells.add(O2SpellType.REPARIFARGE);
        spells.add(O2SpellType.MULTICORFORS);
        spells.add(O2SpellType.SNUFFLIFORS);
    }
}

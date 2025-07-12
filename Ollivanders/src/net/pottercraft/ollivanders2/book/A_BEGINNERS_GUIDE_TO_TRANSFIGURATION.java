package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * A Beginner's Guide to Transfiguration - 1st and 2nd year transfiguration book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.CALAMUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.PERMURATE}<br>
 * {@link net.pottercraft.ollivanders2.spell.LEPUS_SACCULUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.SCARABAEUS_FIBULUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.RANACULUS_AMPHORAM}<br>
 * {@link net.pottercraft.ollivanders2.spell.VERA_VERTO}<br>
 * {@link net.pottercraft.ollivanders2.spell.LAGOMORPHA}<br>
 * {@link net.pottercraft.ollivanders2.spell.REPARIFARGE}<br>
 * {@link net.pottercraft.ollivanders2.spell.MULTICORFORS}<br>
 * {@link net.pottercraft.ollivanders2.spell.SNUFFLIFORS}<br>
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/A_Beginner's_Guide_to_Transfiguration">https://harrypotter.fandom.com/wiki/A_Beginner's_Guide_to_Transfiguration</a>
 */
public class A_BEGINNERS_GUIDE_TO_TRANSFIGURATION extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public A_BEGINNERS_GUIDE_TO_TRANSFIGURATION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.A_BEGINNERS_GUIDE_TO_TRANSFIGURATION;

        // year 1
        spells.add(O2SpellType.CALAMUS);
        spells.add(O2SpellType.PERMURATE);
        spells.add(O2SpellType.LEPUS_SACCULUM);
        spells.add(O2SpellType.SCARABAEUS_FIBULUM);
        spells.add(O2SpellType.RANACULUS_AMPHORAM);

        // year 2
        spells.add(O2SpellType.VERA_VERTO);
        spells.add(O2SpellType.LAGOMORPHA);
        spells.add(O2SpellType.REPARIFARGE);
        spells.add(O2SpellType.MULTICORFORS);
        spells.add(O2SpellType.SNUFFLIFORS);
    }
}

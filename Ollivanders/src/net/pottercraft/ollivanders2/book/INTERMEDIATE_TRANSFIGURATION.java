package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Intermediate Transfiguration - an O.W.L level (3rd - 5th year) transfiguration book
 * <p>
 * Topics:<br>
 * Animal Transfiguration<br>
 * Self-Transformation<br>
 * Vanishing<br>
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Intermediate_Transfiguration
 */
public class INTERMEDIATE_TRANSFIGURATION extends O2Book {
    public INTERMEDIATE_TRANSFIGURATION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.INTERMEDIATE_TRANSFIGURATION;

        // 3rd year
        spells.add(O2SpellType.DURO);
        // todo version of duro that "hardens" tools
        spells.add(O2SpellType.LAGOMORPHA);
        // todo teapot to tortoise
        // todo felifors (cats in to cauldrons)
        // todo owl in to opera glasses - https://harrypotter.fandom.com/wiki/Owl_to_Opera_Glasses
        spells.add(O2SpellType.AVIFORS);

        // 4th year
        spells.add(O2SpellType.EVANESCO);
        spells.add(O2SpellType.LAPIFORS);
        spells.add(O2SpellType.COLOVARIA);
        spells.add(O2SpellType.DUCKLIFORS);
        spells.add(O2SpellType.EQUUSIFORS);
        // todo Orchideous - https://github.com/Azami7/Ollivanders2/issues/56

        // 5th year
        spells.add(O2SpellType.PIERTOTUM_LOCOMOTOR);
        // todo inanimatus conjurus - https://harrypotter.fandom.com/wiki/Inanimatus_Conjurus_Spell
        // todo gobstone in to skunk - https://harrypotter.fandom.com/wiki/Gobstone_to_Skunk
        // todo armadillo in to pillow - https://harrypotter.fandom.com/wiki/Armadillo_to_Pillow
        // todo cauldron in to badger - https://harrypotter.fandom.com/wiki/Cauldron_to_badger_spell
        spells.add(O2SpellType.DELETRIUS);
    }
}

package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Chadwick's Charms - O.W.L level charms book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.ASCENDIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.CRESCERE_PROTEGAT}<br>
 * {@link net.pottercraft.ollivanders2.spell.HORREAT_PROTEGAT}<br>
 * {@link net.pottercraft.ollivanders2.spell.MOV_FOTIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.FINESTRA}<br>
 * {@link net.pottercraft.ollivanders2.spell.FATUUS_AURUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.ABERTO}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Chadwick's_Charms">https://harrypotter.fandom.com/wiki/Chadwick's_Charms</a>
 */
public class CHADWICKS_CHARMS_VOLUME_1 extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public CHADWICKS_CHARMS_VOLUME_1(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.CHADWICKS_CHARMS_VOLUME_1;

        spells.add(O2SpellType.ASCENDIO);
        spells.add(O2SpellType.CRESCERE_PROTEGAT);
        spells.add(O2SpellType.HORREAT_PROTEGAT);
        spells.add(O2SpellType.MOV_FOTIA);
        spells.add(O2SpellType.FINESTRA);
        spells.add(O2SpellType.FATUUS_AURUM);
        spells.add(O2SpellType.ABERTO);
    }
}

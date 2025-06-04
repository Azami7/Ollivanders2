package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * 2nd Century Roman healing text.
 *
 * @author Azami7
 * @sinze 2.2.9
 */
public class DE_MEDICINA_PRAECEPTA extends O2Book {
    public DE_MEDICINA_PRAECEPTA(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.DE_MEDICINA_PRAECEPTA;

        openingPage = "Phoebus, protect this health-giving song, which I composed and let this manifest favour be an attendant to the art you discovered.";

        spells.add(O2SpellType.REPARIFORS);
        //Abracadabra
        //Mithridates Antidotum - a powerful antidote to poison
    }
}

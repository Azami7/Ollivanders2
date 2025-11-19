package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * 2nd Century Roman healing text.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.REPARIFORS}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://en.wikipedia.org/wiki/Serenus_Sammonicus">https://en.wikipedia.org/wiki/Serenus_Sammonicus</a>
 */
public class DE_MEDICINA_PRAECEPTA extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public DE_MEDICINA_PRAECEPTA(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.DE_MEDICINA_PRAECEPTA;

        openingPage = "Phoebus, protect this health-giving song, which I composed and let this manifest favour be an attendant to the art you discovered.";

        spells.add(O2SpellType.REPARIFORS);
        //todo Abracadabra - a cure for fever and ague
        //todo Mithridates Antidotum - a powerful antidote to poison
    }
}

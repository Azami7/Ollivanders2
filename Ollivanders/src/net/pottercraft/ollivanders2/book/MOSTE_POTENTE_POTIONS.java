package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.jetbrains.annotations.NotNull;

/**
 * Moste Potente Potions is a book by Phineas Bourne that gives instructions on how to brew advanced potions.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.potion.BARUFFIOS_BRAIN_ELIXIR}<br>
 * {@link net.pottercraft.ollivanders2.potion.WOLFSBANE_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.REGENERATION_POTION}<br>
 * {@link net.pottercraft.ollivanders2.potion.ANIMAGUS_POTION}
 * </p>
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Moste_Potente_Potions">http://harrypotter.wikia.com/wiki/Moste_Potente_Potions</a>
 */
public class MOSTE_POTENTE_POTIONS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public MOSTE_POTENTE_POTIONS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.MOSTE_POTENTE_POTIONS;

        // todo felix felicis - https://harrypotter.fandom.com/wiki/Felix_Felicis
        // todo polyjuice potion - https://harrypotter.fandom.com/wiki/Polyjuice_Potion
        // todo slow acting venoms - https://harrypotter.fandom.com/wiki/Slow-acting_venom
        // todo invisibility potion effect
        // todo levitation potion effect
        // todo mopsus potion - https://harrypotter.fandom.com/wiki/Mopsus_Potion
        potions.add(O2PotionType.BARUFFIOS_BRAIN_ELIXIR);
        potions.add(O2PotionType.WOLFSBANE_POTION);
        potions.add(O2PotionType.REGENERATION_POTION);
        potions.add(O2PotionType.ANIMAGUS_POTION);

        // potions not in use by pottercraft
        // todo bad omen potion effect
        // todo infested potion effect
        // todo oozing potion effect
        // todo raid open potion effect
        // todo trial omen potion effect
        // todo unluck potion effect
        // todo hero of the village potion effect
        // todo focus potion - https://harrypotter.fandom.com/wiki/Focus_Potion
        // todo maxima potion - https://harrypotter.fandom.com/wiki/Maxima_Potion
        // todo another spell boost potion - https://harrypotter.fandom.com/wiki/Exstimulo_Potion
        // todo restoration potion - https://harrypotter.fandom.com/wiki/Restoration_Potion
    }
}

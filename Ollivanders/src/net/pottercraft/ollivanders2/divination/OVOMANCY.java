package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Ovomancy divination — interpreting the patterns of cracked-egg yolks and whites, with a maximum accuracy of 40%.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Ovomancy">Harry Potter Wiki - Ovomancy</a>
 */
public class OVOMANCY extends O2Divination {
    /**
     * Create an ovomancy divination prophecy about the target.
     *
     * @param plugin     a callback to the plugin
     * @param prophet    the player performing the divination
     * @param target     the player the prophecy is about
     * @param experience the prophet's experience level with this divination
     */
    public OVOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        divinationType = O2DivinationType.OVOMANCY;
        maxAccuracy = 40;

        prophecyPrefix.add("The shape of the egg whites means that");
        prophecyPrefix.add("Through the teachings of Orpheus, it is foretold that");
        prophecyPrefix.add("The omen of the egg reveals that");
        prophecyPrefix.add("Egg whites and yolks take form,");
        prophecyPrefix.add("The egg forms the shape of a bell,");
        prophecyPrefix.add("The egg forms the shape of a snake,");
        prophecyPrefix.add("The egg takes the shape of a boat,");
    }
}

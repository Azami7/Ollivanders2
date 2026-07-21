package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Tassomancy divination — reading the tea leaves left in a cup, with a maximum accuracy of 20% (the lowest of any
 * divination method).
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Tessomancy">Harry Potter Wiki - Tessomancy</a>
 */
public class TASSOMANCY extends O2Divination {
    /**
     * Create a tassomancy divination prophecy about the target.
     *
     * @param plugin     a callback to the plugin
     * @param prophet    the player performing the divination
     * @param target     the player the prophecy is about
     * @param experience the prophet's experience level with this divination
     */
    public TASSOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        divinationType = O2DivinationType.TASSOMANCY;
        maxAccuracy = 20;

        prophecyPrefix.add("The falcon ... a deadly enemy,");
        prophecyPrefix.add("The club ... an attack,");
        prophecyPrefix.add("The skull ... danger,");
        prophecyPrefix.add("The Grim ... death,");
        prophecyPrefix.add("The ring ... confusion,");
        prophecyPrefix.add("The snake ... enmity,");
        prophecyPrefix.add("The acorn ... good fortune,");
        prophecyPrefix.add("The mountain ... a hindrance,");
        prophecyPrefix.add("The cross ... trials and suffering,");
        prophecyPrefix.add("The sun ... great happiness,");
    }
}

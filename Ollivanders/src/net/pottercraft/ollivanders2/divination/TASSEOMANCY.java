package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Tasseomancy is the art of reading tea leaves to predict events in the future.
 * <p>Reference: http://harrypotter.wikia.com/wiki/Tessomancy</p>
 *
 * @author Azami7
 * @since 2.2.9
 */
public class TASSEOMANCY extends O2Divination {
    /**
     * Constructor
     *
     * @param plugin     a callback to the plugin
     * @param prophet    the player making the prophecy
     * @param target     the target of the prophecy
     * @param experience the experience level of the prophet
     */
    public TASSEOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        divinationType = O2DivinationType.TASSEOMANCY;
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

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
    public TASSEOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player pro, @NotNull Player tar, int exp) {
        super(plugin, pro, tar, exp);

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

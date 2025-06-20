package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Crystal-gazing was the art of looking into a crystal ball in order to try to gain insight into the future events.
 *
 * <p>Reference: https://harrypotter.fandom.com/wiki/Crystal-gazing</p>
 *
 * @author Azami7
 * @since 2.2.9
 */
public class CRYSTAL_BALL extends O2Divination {
    public CRYSTAL_BALL(@NotNull Ollivanders2 plugin, @NotNull Player pro, @NotNull Player tar, int exp) {
        super(plugin, pro, tar, exp);

        divinationType = O2DivinationType.CRYSTAL_BALL;
        maxAccuracy = 30;

        prophecyPrefix.add("The crystal ball has revealed that");
        prophecyPrefix.add("The clairvoyant vibrations of the orb show that");
        prophecyPrefix.add("The shadowy portents have been read,");
        prophecyPrefix.add("The orb tells the future,");
    }
}

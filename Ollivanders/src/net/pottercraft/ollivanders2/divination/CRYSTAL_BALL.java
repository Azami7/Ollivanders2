package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Crystal ball divination — scrying and clairvoyant vision, with a maximum accuracy of 30%.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Crystal-gazing">Harry Potter Wiki - Crystal-gazing</a>
 */
public class CRYSTAL_BALL extends O2Divination {
    /**
     * Create a crystal ball divination prophecy about the target.
     *
     * @param plugin     a callback to the plugin
     * @param prophet    the player performing the divination
     * @param target     the player the prophecy is about
     * @param experience the prophet's experience level with this divination
     */
    public CRYSTAL_BALL(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        divinationType = O2DivinationType.CRYSTAL_BALL;
        maxAccuracy = 30;

        prophecyPrefix.add("The crystal ball has revealed that");
        prophecyPrefix.add("The clairvoyant vibrations of the orb show that");
        prophecyPrefix.add("The shadowy portents have been read,");
        prophecyPrefix.add("The orb tells the future,");
    }
}

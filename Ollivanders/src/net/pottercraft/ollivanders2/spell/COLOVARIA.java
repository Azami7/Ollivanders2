package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.common.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Colour Change Charm that recolors a sheep or colorable block to a random color.
 *
 * <p>Unlike the specific-color variants (e.g., {@link COLOVARIA_VERMICULO}), this spell selects a random
 * dyeable color each time it is cast.</p>
 *
 * @author lownes
 * @author Azami7
 * @see ChangeColorable
 * @see <a href="https://harrypotter.fandom.com/wiki/Colour_Change_Charm">Harry Potter Wiki - Colour Change Charm</a>
 */
public final class COLOVARIA extends ChangeColorable {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public COLOVARIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.COLOVARIA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Colour Change Charm");
            add("[...] he wished he had not mixed up the incantations for Colour Change and Growth Charms, so that the rat he was supposed to be turning orange swelled shockingly and was the size of a badger before Harry could rectify his mistake.");
        }};

        text = "Changes color of sheep and colorable blocks to another color.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public COLOVARIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.COLOVARIA;
        branch = O2MagicBranch.CHARMS;

        color = O2Color.getRandomDyeableColor();

        initSpell();
    }
}
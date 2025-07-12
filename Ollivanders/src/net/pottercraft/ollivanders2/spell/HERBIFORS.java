package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * This spell places a flower on the target player's head.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 * @see <a href = "https://harrypotter.fandom.com/wiki/Herbifors">https://harrypotter.fandom.com/wiki/Herbifors</a>
 */
public final class HERBIFORS extends GaleatiSuper {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public HERBIFORS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.HERBIFORS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Flower-Hair Spell");
        }};

        text = "Puts a flower on the target player's head.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public HERBIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.HERBIFORS;
        branch = O2MagicBranch.CHARMS;

        materialType = Material.DANDELION;

        initSpell();
    }
}

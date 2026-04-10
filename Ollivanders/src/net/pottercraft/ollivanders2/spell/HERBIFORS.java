package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Places a flower ({@link org.bukkit.Material#DANDELION}) on the target player's head.
 *
 * @author lownes
 * @author Azami7
 * @see Galeati
 * @see <a href="https://harrypotter.fandom.com/wiki/Herbifors">Herbifors Spell</a>
 */
public final class HERBIFORS extends Galeati {
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

        helmetType = Material.DANDELION;

        initSpell();
    }
}

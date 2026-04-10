package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Encases the target player's head in a randomly selected melon or pumpkin block.
 *
 * @author lownes
 * @author Azami7
 * @see Galeati
 * @see <a href="https://harrypotter.fandom.com/wiki/Pumpkin-Head_Jinx">Pumpkin-Head Jinx</a>
 */
public final class MELOFORS extends Galeati {
    /**
     * The pool of melon/pumpkin materials that the spell randomly selects from when cast.
     */
    static Material[] melons = {
            Material.MELON,
            Material.JACK_O_LANTERN,
            Material.PUMPKIN,
    };

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MELOFORS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.MELOFORS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("Harry overheard one second-year girl assuring another that Fudge was now lying in St Mungo’s with a pumpkin for a head.\"");
            add("The Melon-Head Spell");
        }};

        text = "Melofors places a melon on the target player's head.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MELOFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.MELOFORS;
        branch = O2MagicBranch.CHARMS;

        helmetType = melons[Math.abs(Ollivanders2Common.random.nextInt() % melons.length)];

        initSpell();
    }
}
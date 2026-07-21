package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Red Sparks charm: shoots red sparks with no damage. By wizarding tradition an emergency signal to call for help.
 * Base for the damaging {@link VERMILLIOUS_DUO} and {@link VERMILLIOUS_TRIA} variants.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Red_Sparks">Harry Potter Wiki - Red Sparks</a>
 */
public class VERMILLIOUS extends Sparks {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERMILLIOUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERMILLIOUS;

        flavorText = new ArrayList<>() {{
            add("Red Sparks Charm");
            add("\"Vermillious, otherwise known as Red Sparks, is an excellent spell for emergencies.If you're ever in danger, fire Red Sparks into the air to call for help.\" -Filius Flitwick");
        }};

        text = "Shoots red sparks out of the caster's wand.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VERMILLIOUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERMILLIOUS;
        moveEffectData = Material.RED_STAINED_GLASS;
        damageModifier = 0;

        initSpell();
    }
}

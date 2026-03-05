package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Red Sparks charm that emits red projectiles for signaling help in emergencies.
 *
 * <p>VERMILLIOUS is a non-damaging charm that shoots red sparks from the caster's wand.
 * According to wizarding tradition, it is used as an emergency signal to call for help when
 * in danger. Like VERDIMILLIOUS, it serves as a foundation for more powerful variants.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Visual Effect: RED_STAINED_GLASS projectile trail</li>
 * <li>Damage: None (damageModifier = 0)</li>
 * <li>Purpose: Emergency signal charm or foundation for spell variants</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Red_Sparks">Red Sparks on Harry Potter Wiki</a>
 * @since 2.21
 */
public class VERMILLIOUS extends Sparks {
    /**
     * Default constructor for use in generating spell book text.  Do not use to cast the spell.
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
     * Constructor for casting VERMILLIOUS spells.
     *
     * <p>Initializes the spell with RED_STAINED_GLASS visual effect and no damage capability.
     * Like VERDIMILLIOUS, this serves as a foundation for enhanced variants.</p>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand)
     */
    public VERMILLIOUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERMILLIOUS;
        moveEffectData = Material.RED_STAINED_GLASS;
        damageModifier = 0;

        initSpell();
    }
}

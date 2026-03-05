package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The basic Green Sparks charm that emits visual projectiles without damage.
 *
 * <p>VERDIMILLIOUS is a non-damaging charm that shoots green sparks from the caster's wand.
 * It serves as a foundation for more powerful variants like VERDIMILLIOUS_DUO, which add
 * curse detection and other effects while retaining the green spark visual.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Visual Effect: GREEN_STAINED_GLASS projectile trail</li>
 * <li>Damage: None (damageModifier = 0)</li>
 * <li>Purpose: Pure visual charm or foundation for spell variants</li>
 * </ul>
 *
 * @author Azami7
 * @see VERDIMILLIOUS_DUO for the enhanced variant with curse detection
 * @since 2.21
 */
public final class VERDIMILLIOUS extends Sparks {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERDIMILLIOUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERDIMILLIOUS;

        flavorText = new ArrayList<>() {{
            add("Green Sparks Charm");
        }};

        text = "Shoots green sparks out of the caster's wand.";
    }

    /**
     * Constructor for casting VERDIMILLIOUS spells.
     *
     * <p>Initializes the spell with GREEN_STAINED_GLASS visual effect and no damage capability.
     * Subclasses can override this to add functionality (e.g., VERDIMILLIOUS_DUO adds curse detection).</p>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand)
     */
    public VERDIMILLIOUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERDIMILLIOUS;
        moveEffectData = Material.GREEN_STAINED_GLASS;
        damageModifier = 0;

        initSpell();
    }
}

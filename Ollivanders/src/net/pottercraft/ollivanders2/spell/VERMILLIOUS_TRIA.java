package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The most powerful Red Sparks charm with the highest damage and large effect radius.
 *
 * <p>VERMILLIOUS_TRIA is the strongest variant of the Red Sparks charms. It shoots red sparks
 * from the caster's wand and deals the most damage of all red sparks variants to nearby entities
 * upon impact.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Visual Effect: RED_STAINED_GLASS projectile trail</li>
 * <li>Damage: Enabled with 0.25 modifier (scales with player skill)</li>
 * <li>Radius: 4 blocks for entity detection</li>
 * <li>Purpose: Most powerful damaging red sparks charm</li>
 * </ul>
 *
 * @author Azami7
 * @see VERMILLIOUS for the basic non-damaging variant
 * @see VERMILLIOUS_DUO for the intermediate variant
 * @since 2.21
 */
public class VERMILLIOUS_TRIA extends Sparks {
    /**
     * Default constructor for use in generating spell book text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERMILLIOUS_TRIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERMILLIOUS_TRIA;

        text = "The strongest Red Sparks Charm which shoots red sparks from the caster's wand and damages entities.";
    }

    /**
     * Constructor for casting VERMILLIOUS_TRIA spells.
     *
     * <p>Initializes the spell with RED_STAINED_GLASS visual effect, damage enabled,
     * 0.25 damage modifier (highest of all red sparks variants), and a 4-block entity detection radius.</p>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand)
     */
    public VERMILLIOUS_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERMILLIOUS_TRIA;
        moveEffectData = Material.RED_STAINED_GLASS;
        doDamage = true;
        damageModifier = 0.25;
        radius = 4;

        initSpell();
    }
}

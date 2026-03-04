package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The enhanced Red Sparks charm that damages entities with a large effect radius.
 *
 * <p>VERMILLIOUS_DUO is a more powerful variant of VERMILLIOUS that shoots red sparks
 * from the caster's wand and deals damage to nearby entities upon impact.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Visual Effect: RED_STAINED_GLASS projectile trail</li>
 * <li>Damage: Enabled with 0.125 modifier (scales with player skill)</li>
 * <li>Radius: 4 blocks for entity detection</li>
 * <li>Purpose: Damaging charm with larger effect radius</li>
 * </ul>
 *
 * @author Azami7
 * @see VERMILLIOUS for the basic non-damaging variant
 * @since 2.21
 */
public class VERMILLIOUS_DUO extends Sparks {
    /**
     * Default constructor for use in generating spell book text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERMILLIOUS_DUO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERMILLIOUS_DUO;

        text = "A stronger Red Sparks Charm that shoots red sparks from the caster's wand and damages entities.";
    }

    /**
     * Constructor for casting VERMILLIOUS_DUO spells.
     *
     * <p>Initializes the spell with RED_STAINED_GLASS visual effect, damage enabled,
     * 0.125 damage modifier, and a 4-block entity detection radius.</p>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand)
     */
    public VERMILLIOUS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERMILLIOUS_DUO;
        moveEffectData = Material.RED_STAINED_GLASS;
        doDamage = true;
        damageModifier = 0.125;
        radius = 4;

        initSpell();
    }
}

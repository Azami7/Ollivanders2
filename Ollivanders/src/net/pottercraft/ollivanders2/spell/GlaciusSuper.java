package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for Glacius spells that freeze blocks.
 *
 * <p>Glacius spells cause intense cold in a radius from the impact point, transforming liquids
 * and fire into frozen or solidified forms. The radius and duration of the freezing effect scale
 * with the caster's spell skill level.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target materials:</strong> WATER, LAVA, ICE</li>
 * <li><strong>Transformations:</strong>
 *   <ul>
 *   <li>WATER → ICE (freezes water)</li>
 *   <li>LAVA → OBSIDIAN (solidifies lava)</li>
 *   <li>ICE → PACKED_ICE (further freezes ice)</li>
 *   </ul>
 * </li>
 * <li><strong>Duration:</strong> Temporary; affected blocks revert after spell duration expires</li>
 * </ul>
 *
 * <p>This is an abstract base class; subclasses must implement specific Glacius variants
 * (e.g., GLACIUS, GLACIUS_DUO) with their own radius and duration configurations.</p>
 *
 * @author Azami7
 */
public abstract class GlaciusSuper extends BlockTransfiguration {
    /**
     * Default constructor for spell text generation and documentation.
     *
     * <p>Used only for generating spell descriptions in spell books and UI displays.
     * <strong>Do not use this constructor to cast the spell.</strong> Use the
     * three-argument constructor instead.</p>
     *
     * <p>Initializes spell metadata including branch (CHARMS). Subclasses provide spell type
     * and flavor text.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public GlaciusSuper(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Constructor for casting Glacius spells.
     *
     * <p>Initializes GlaciusSuper with player context, wand information, and base spell configuration:</p>
     * <ul>
     * <li>Target materials: WATER, LAVA, ICE (extinguishables and liquids)</li>
     * <li>Transformations: Water → Ice, Lava → Obsidian, Ice → Packed Ice</li>
     * <li>Duration: Temporary; freezing effects revert after spell duration expires</li>
     * <li>WorldGuard: Requires BUILD permission (if enabled)</li>
     * </ul>
     *
     * <p>Subclasses must set radius configuration and duration modifiers specific to their
     * Glacius variant. Subclasses should call initSpell() after configuration.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public GlaciusSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        branch = O2MagicBranch.CHARMS;

        permanent = false;

        transfigurationMap.put(Material.WATER, Material.ICE);
        transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
        transfigurationMap.put(Material.ICE, Material.PACKED_ICE);

        // materials that can be targeted by this spell
        materialAllowList.add(Material.WATER);
        materialAllowList.add(Material.LAVA);
        materialAllowList.add(Material.ICE);

        projectilePassThrough.removeAll(materialAllowList);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);
    }
}

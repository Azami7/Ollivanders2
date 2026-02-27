package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Aqua Eructo Charm that shoots water to permanently extinguish fires.
 *
 * <p>AQUA_ERUCTO is a water-jet spell that extinguishes fire by converting flames into air or
 * converting lava into obsidian. A jet of water erupts from the wand tip, striking at the target
 * location. This spell is particularly useful for combating magical and natural fires.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target materials:</strong> FIRE, SOUL_FIRE, LAVA, CAMPFIRE, SOUL_CAMPFIRE</li>
 * <li><strong>Transformations:</strong>
 *   <ul>
 *   <li>FIRE → AIR (extinguishes fire)</li>
 *   <li>SOUL_FIRE → AIR (extinguishes soul fire)</li>
 *   <li>LAVA → OBSIDIAN (solidifies lava)</li>
 *   <li>CAMPFIRE → OAK_LOG (extinguishes campfire)</li>
 *   <li>SOUL_CAMPFIRE → OAK_LOG (extinguishes soul campfire)</li>
 *   </ul>
 * </li>
 * <li><strong>Radius:</strong> 1 block (single-target spell)</li>
 * <li><strong>Duration:</strong> Permanent; extinguished fire does not reignite</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * <li><strong>Visual Effect:</strong> Blue ice particle effect for water jet</li>
 * </ul>
 *
 * <p>AQUA_ERUCTO is a precision spell designed for extinguishing fires without affecting
 * surrounding blocks. Unlike most transfiguration spells, the effects are permanent—extinguished
 * fires will not revert to their original state.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Aqua_Eructo">Aqua Eructo Charm on Harry Potter Wiki</a>
 */
public final class AQUA_ERUCTO extends BlockTransfiguration {
    /**
     * Minimum effect radius for AQUA_ERUCTO (1 block).
     *
     * <p>AQUA_ERUCTO is a precision single-target spell with fixed radius of 1 block.</p>
     */
    private static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius for AQUA_ERUCTO (1 block).
     *
     * <p>AQUA_ERUCTO is a precision single-target spell with fixed radius of 1 block.</p>
     */
    private static final int maxRadiusConfig = 1;

    /**
     * Default constructor for spell text generation and documentation.
     *
     * <p>Used only for generating spell descriptions in spell books and UI displays.
     * <strong>Do not use this constructor to cast the spell.</strong> Use the
     * three-argument constructor instead.</p>
     *
     * <p>Initializes spell metadata including name, branch (CHARMS), and flavor text
     * describing the Aqua Eructo water-jet spell.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public AQUA_ERUCTO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AQUA_ERUCTO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Aqua Eructo Charm");
            add("\"Very good. You'll need to use Aqua Eructo to put out the fires.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
        }};

        text = "Shoots a jet of water from your wand tip to extinguish a fire.";
    }

    /**
     * Constructor for casting AQUA_ERUCTO spells.
     *
     * <p>Initializes AQUA_ERUCTO with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Radius: Fixed 1 block (precision single-target spell)</li>
     * <li>Target materials: FIRE, SOUL_FIRE, LAVA, CAMPFIRE, SOUL_CAMPFIRE</li>
     * <li>Transformations: Fire variants → Air/Logs, Lava → Obsidian</li>
     * <li>Duration: Permanent; extinguished fire does not reignite</li>
     * <li>Visual Effect: Blue ice particle stream (water jet)</li>
     * <li>WorldGuard: Requires BUILD permission (if enabled)</li>
     * </ul>
     *
     * <p>AQUA_ERUCTO is a precision spell designed for single-target fire extinguishing. Unlike
     * most transfiguration spells, the effects are permanent and will not revert.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public AQUA_ERUCTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AQUA_ERUCTO;
        branch = O2MagicBranch.CHARMS;

        permanent = true;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        successMessage = "A fire is doused by the water.";
        failureMessage = "Nothing seems to happen.";

        moveEffectData = Material.BLUE_ICE;

        // materials that can be transfigured by this spell
        materialAllowList.add(Material.LAVA);
        materialAllowList.add(Material.FIRE);
        materialAllowList.add(Material.CAMPFIRE);
        materialAllowList.add(Material.SOUL_FIRE);

        // make sure none of these are on the pass-through list
        projectilePassThrough.removeAll(materialAllowList);

        // the map of what each material transfigures in to for this spell
        transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
        transfigurationMap.put(Material.FIRE, Material.AIR);
        transfigurationMap.put(Material.CAMPFIRE, Material.OAK_LOG);
        transfigurationMap.put(Material.SOUL_CAMPFIRE, Material.OAK_LOG);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}
package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Extinguishing Charm that permanently eliminates fire blocks.
 *
 * <p>PYROSVESTIRAS is a block transfiguration spell that extinguishes fires in a targeted area.
 * It converts all types of fire blocks to non-flammable alternatives: standard fire and soul fire
 * become air, while campfires are converted to oak logs. This spell is particularly useful for
 * stopping the spread of fire and creating firebreaks.</p>
 *
 * <p>Spell behavior:</p>
 * <ul>
 * <li><strong>Target:</strong> FIRE, SOUL_FIRE, CAMPFIRE, and SOUL_CAMPFIRE only</li>
 * <li><strong>Effect:</strong> Converts fire blocks to air or logs depending on type</li>
 * <li><strong>Radius:</strong> 1-10 blocks, scaled by player skill (25% modifier)</li>
 * <li><strong>Duration:</strong> Permanent; extinguished fire does not reignite</li>
 * <li><strong>Success Rate:</strong> 100% (deterministic)</li>
 * </ul>
 *
 * <p>This spell is particularly useful for dragonologists and those dealing with magical fires.
 * Unlike temporary transfigurations, fire extinguished by PYROSVESTIRAS remains extinguished
 * permanently and will not revert to its original state.</p>
 *
 * @author Azami7
 */
public class PYROSVESTIRAS extends BlockTransfiguration {
    /**
     * Minimum effect radius for PYROSVESTIRAS (1 block).
     *
     * <p>The spell's radius is clamped to not go below this value, ensuring a minimum
     * area of effect even at low skill levels.</p>
     */
    private static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius for PYROSVESTIRAS (10 blocks).
     *
     * <p>The spell's radius is clamped to not exceed this value, establishing an upper
     * bound on the area of effect even at maximum skill levels.</p>
     */
    private static final int maxRadiusConfig = 10;

    /**
     * Default constructor for spell text generation and documentation.
     *
     * <p>Used only for generating spell descriptions in spell books and UI displays.
     * <strong>Do not use this constructor to cast the spell.</strong> Use the
     * three-argument constructor instead.</p>
     *
     * <p>Initializes spell metadata including name, branch (CHARMS), and flavor text
     * describing the Extinguishing Charm.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public PYROSVESTIRAS(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
        spellType = O2SpellType.PYROSVESTIRAS;

        flavorText = new ArrayList<>() {{
            add("A charm that extinguishes fires. Most commonly employed by Dragonologists.");
            add("The Extinguishing Charm");
        }};

        text = "A spell that extinguishes fire and soul fire.";
    }

    /**
     * Constructor for casting PYROSVESTIRAS spells.
     *
     * <p>Initializes PYROSVESTIRAS with player context, wand information, and spell-specific configuration:</p>
     * <ul>
     * <li>Effect radius: 1-10 blocks (25% skill modifier)</li>
     * <li>Target materials: FIRE, SOUL_FIRE, CAMPFIRE, and SOUL_CAMPFIRE only</li>
     * <li>Effect: Converts fire blocks to air (fire) or oak logs (campfire)</li>
     * <li>Duration: Permanent; extinguished fire does not reignite</li>
     * <li>WorldGuard: Requires BUILD permission (if enabled)</li>
     * </ul>
     *
     * <p>Fire extinguished by PYROSVESTIRAS remains permanently extinguished and will not
     * revert to its original state, unlike most other transfiguration spells.</p>
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public PYROSVESTIRAS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.PYROSVESTIRAS;
        branch = O2MagicBranch.CHARMS;

        successMessage = "A fire is doused by the water.";
        failureMessage = "Nothing seems to happen.";

        permanent = true;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        effectRadiusModifier = 0.25; // 25% of usesModifier

        // allow list only fire blocks
        materialAllowList.add(Material.FIRE);
        materialAllowList.add(Material.SOUL_FIRE);
        materialAllowList.add(Material.CAMPFIRE);
        materialAllowList.add(Material.SOUL_CAMPFIRE);

        // do not pass through fire blocks
        projectilePassThrough.removeAll(materialAllowList);

        // what type blocks transfigure in to for this spell
        transfigurationMap.put(Material.FIRE, Material.AIR);
        transfigurationMap.put(Material.SOUL_FIRE, Material.AIR);
        transfigurationMap.put(Material.CAMPFIRE, Material.OAK_LOG);
        transfigurationMap.put(Material.SOUL_CAMPFIRE, Material.OAK_LOG);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Calculates and sets the effect radius based on player skill level.
     *
     * <p>Called from {@link BlockTransfiguration#transfigure()} on each tick to dynamically
     * adjust the radius based on the caster's spell skill. The radius is calculated as:
     * <code>radius = usesModifier / 10</code></p>
     *
     * <p>The calculated radius is clamped to [minEffectRadius, maxEffectRadius].</p>
     *
     * <p>Note: PYROSVESTIRAS does not use an effectRadiusModifier; the formula is always
     * (usesModifier / 10).</p>
     */
    @Override
    protected void setEffectRadius() {
        effectRadius = (int) (usesModifier / 10);

        if (effectRadius < minEffectRadius)
            effectRadius = minEffectRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }
}

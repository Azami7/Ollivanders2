package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.WATER_BREATHING;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Carcerem Aquaticum - The Water Orb Spell.
 *
 * <p>CARCEREM_AQUATICUM traps the target player in a protective orb of non-flowing water, immobilizing them
 * while preventing drowning damage. The spell expands the player's bounding box by 1 block in all directions
 * and fills all air blocks within that region with water, preventing movement while allowing the player to
 * breathe safely through the WATER_BREATHING effect. The spell only affects players at normal or reduced
 * size (scale ≤ 1.0).</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Only targets players with scale attribute ≤ 1.0</li>
 * <li>Expands player's bounding box by 1 block in all directions</li>
 * <li>Converts all air blocks within the expanded region to non-flowing water</li>
 * <li>Preserves any non-air blocks that exist within the region</li>
 * <li>Applies WATER_BREATHING effect to prevent drowning during immobilization</li>
 * <li>Automatically reverts all water blocks after effect duration expires</li>
 * <li>Uses partial immobilization (allows rotation but prevents movement)</li>
 * <li>Minimum effect duration: 2 minutes (ensures water breathing lasts through cleanup)</li>
 * </ul>
 *
 * <p>Reference: <a href="https://harrypotter.fandom.com/wiki/Orb_of_Water">Harry Potter Wiki - Orb of Water</a></p>
 */
public class CARCEREM_AQUATICUM extends ImmobilizePlayer {
    /**
     * Setting to 2 minutes, which is the min duration for the WATER_BREATHING effect
     */
    private static final int minEffectDurationConfig = 2 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CARCEREM_AQUATICUM(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CARCEREM_AQUATICUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Water Orb Spell");
            add("\"The water in the pool rose up and covered Voldemort like a cocoon of molten glass.\"");
        }};

        text = "Creates an orb of water that surrounds the target player and immobilizes them.";
    }

    /**
     * Constructor for casting the spell.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using (correctness factor)
     */
    public CARCEREM_AQUATICUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        moveEffectData = Material.BLUE_ICE;

        spellType = O2SpellType.CARCEREM_AQUATICUM;
        branch = O2MagicBranch.CHARMS;

        fullImmobilize = false;
        minEffectDuration = minEffectDurationConfig;
        imprison = true;
        imprisonMaterial = Material.WATER;
        prisonIsShell = false;

        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Determine if a player can be targeted by this spell.
     *
     * <p>Only players with a scale attribute of 1.0 or lower can be targeted. Oversized players cannot
     * be trapped in the water orb due to the expanded bounding box being too small to contain them.</p>
     *
     * @param target the player to validate as a potential target
     * @return true if the player's scale is ≤ 1.0, false if oversized (or if scale attribute is null)
     */
    boolean canTarget(Player target) {
        if (!Ollivanders2.testMode) {
            AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);

            if (scaleAttribute == null || scaleAttribute.getBaseValue() > 1.0) {
                common.printDebugMessage("CarceremAquaticum.canTarget: player scale > 1.0", null, null, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Apply a WATER_BREATHING effect to prevent the target from drowning inside the water orb.
     *
     * <p>The water breathing effect lasts 10 ticks longer than the immobilization effect to ensure
     * it persists through cleanup when the water blocks are reverted.</p>
     *
     * @param target the immobilized player
     */
    @Override
    void addAdditionalEffects(Player target) {
        // add water breathing with just over duration time to make sure it doesn't expire before we clean up the water blocks
        WATER_BREATHING waterBreathing = new WATER_BREATHING(p, effectDuration + 10, false, target.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(waterBreathing);
    }
}

package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Blue sparks charm that damages entities and spawns an area effect cloud on impact.
 *
 * <p>AZURILLIOUS shoots blue sparks from the caster's wand. Upon hitting a target entity,
 * it deals damage and spawns a lingering AreaEffectCloud at the target's location.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Visual Effect: BLUE_STAINED_GLASS projectile trail</li>
 * <li>Damage: Enabled (damageModifier = 0, base 1 damage)</li>
 * <li>Radius: 4 blocks for entity detection</li>
 * <li>On Hit: Spawns a 2-block radius cloud lasting 5 seconds (100 ticks)</li>
 * </ul>
 *
 * @author Azami7
 */
public class AZURILLIOUS extends Sparks {
    /**
     * Default constructor for use in generating spell book text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AZURILLIOUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AZURILLIOUS;

        flavorText = new ArrayList<>() {{
            add("Blue Sparks Charm");
        }};

        text = "Shoots blue sparks from the caster's wand and can .";
    }

    /**
     * Constructor for casting AZURILLIOUS spells.
     *
     * <p>Initializes the spell with BLUE_STAINED_GLASS visual effect, damage enabled,
     * and a 4-block entity detection radius.</p>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand)
     */
    public AZURILLIOUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.AZURILLIOUS;
        moveEffectData = Material.BLUE_STAINED_GLASS;
        radius = 4;
        doDamage = true;

        initSpell();
    }

    /**
     * Spawns a blue-tinted AreaEffectCloud at the target's location on impact.
     *
     * <p>Called by {@link Sparks#doCheckEffect()} when the spell hits an entity. The cloud:</p>
     * <ul>
     * <li>Radius: 2.0 blocks</li>
     * <li>Duration: 100 ticks (5 seconds)</li>
     * <li>Does not shrink on use or over time</li>
     * <li>Reapplies effects every 20 ticks (1 second)</li>
     * </ul>
     *
     * @param target the entity that was hit by the spell
     */
    @Override
    void doOtherEffects(@NotNull LivingEntity target) {
        World world = target.getWorld();
        Location loc = target.getLocation();

        // Spawn an area effect cloud at the target's location
        AreaEffectCloud cloud = (AreaEffectCloud) world.spawnEntity(loc, EntityType.AREA_EFFECT_CLOUD);

        // Visual configuration
        cloud.setParticle(Particle.CLOUD);
        cloud.setColor(Color.fromRGB(0, 100, 255)); // blue tint if using colored particle
        cloud.setRadius(2.0f);          // size of the cloud
        cloud.setRadiusOnUse(0.0f);     // don't shrink when it applies effects
        cloud.setRadiusPerTick(0.0f);   // don't grow/shrink over time
        cloud.setDuration(100);         // lasts 5 seconds (100 ticks)
        cloud.setReapplicationDelay(20); // reapply effects every 1 second
    }
}

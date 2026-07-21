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
 * Blue Sparks charm: shoots blue sparks that damage the entity hit and spawn a lingering area effect cloud at it.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Blue_Sparks">Blue Sparks</a>
 */
public class AZURILLIOUS extends Sparks {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AZURILLIOUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AZURILLIOUS;

        flavorText = new ArrayList<>() {{
            add("Blue Sparks Charm");
            add("\"I'm in a colourful mood, class, so today we'll be expanding our palette a bit. I'm sure you all remember how to conjure Red Sparks with Vermillious, yes? Well, I say it's about time we move beyond red and learn to cast Blue Sparks. The theory may be familiar, but I'd still appreciate your full attention.\" -Professor Flitwick");
        }};

        text = "A minor dueling spell that shoots blue sparks from the caster's wand that can linger and harm nearby entities.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
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
     * Spawn a lingering blue area effect cloud at the target's location on impact.
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

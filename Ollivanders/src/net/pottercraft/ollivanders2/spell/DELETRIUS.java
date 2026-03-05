package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Eradication Spell that removes lingering potion effect clouds.
 *
 * <p>When cast, the spell launches a projectile that removes any area effect clouds it encounters.
 * The spell scans a 4-block radius for cloud entities and destroys them upon impact.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Detection: Scans 4 blocks around the projectile for AreaEffectCloud entities</li>
 * <li>Effect: Removes all detected clouds from the world</li>
 * <li>Termination: Kills itself after removing the first cloud found</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Eradication_Spell">Eradication Spell</a>
 */
public final class DELETRIUS extends O2Spell {
    private static final double effectRadius = 4;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DELETRIUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.DELETRIUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Eradication Spell");
            add("'Deletrius!' Mr Diggory shouted, and the smoky skull vanished in a wisp of smoke.");
        }};

        text = "Removes lingering potion effect clouds.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DELETRIUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DELETRIUS;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.ITEM_PICKUP);

        initSpell();
    }

    /**
     * Scans for and removes area effect clouds within the effect radius.
     *
     * <p>Each tick, this method checks the projectile's current location for AreaEffectCloud
     * entities within a 4-block radius. Any clouds found are removed from the world.
     * After removing the first cloud, the spell kills itself to stop execution.</p>
     *
     * <p>If the spell has already hit a block target (hasHitTarget), it immediately
     * terminates without scanning for clouds.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
            kill();

        // look for area effect clouds
        List<Entity> nearbyEntities = getNearbyEntities(effectRadius);
        for (Entity entity : nearbyEntities) {
            if (entity.getType() == EntityType.AREA_EFFECT_CLOUD) {
                entity.remove();

                if (!isKilled())
                    kill(); // stop the spell
            }
        }
    }
}
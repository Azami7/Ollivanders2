package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Levitation Charm. Levitates a nearby item and lets the caster move it through the air while sneaking.
 *
 * <p>The spell projectile travels outward searching for a dropped item. When an item is found and
 * the caster is sneaking, the item's gravity is disabled so it can float to follow the caster's gaze
 * direction. When the caster stops sneaking, gravity is restored and the spell ends.</p>
 *
 * <p>If the projectile finds an item but the caster is not sneaking, the spell fails.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Levitation_Charm">https://harrypotter.fandom.com/wiki/Levitation_Charm</a>
 */
public final class WINGARDIUM_LEVIOSA extends O2Spell {
    /**
     * Whether the spell has acquired an item and is actively moving it.
     */
    boolean moving = false;

    /**
     * How long the caster can levitate the item, based on their skill
     */
    int moveTicks = 0;

    /**
     * The item being levitated.
     */
    Item target = null;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public WINGARDIUM_LEVIOSA(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
        spellType = O2SpellType.WINGARDIUM_LEVIOSA;

        flavorText = new ArrayList<>() {{
            add("The Levitation Charm");
            add("You're saying it wrong ...It's Wing-gar-dium Levi-o-sa, make the 'gar' nice and long.\" -Hermione Granger");
            add("The Levitation Charm is one of the first spells learnt by any young witch or wizard.  With the charm a witch or wizard can make things fly with the flick of a wand.");
        }};

        text = "Levitates items and allows you to move them while crouching.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public WINGARDIUM_LEVIOSA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.WINGARDIUM_LEVIOSA;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_PICKUP);
            worldGuardFlags.add(Flags.ITEM_DROP);
        }

        initSpell();
    }

    /**
     * Search for a nearby item to levitate, or move the already-levitated item.
     *
     * <p>Before an item is acquired: while the projectile is in flight it searches for nearby items. If one is
     * found and the caster is sneaking, its gravity is disabled and levitation begins; if the caster is not
     * sneaking, the spell fails. If the projectile stops without finding an item, the spell ends.</p>
     *
     * <p>Once levitating: each tick the item follows the caster's gaze while the caster keeps sneaking, for up to
     * {@link #calculateMoveTicks()} ticks. The spell ends when the caster stops sneaking or that time runs out,
     * restoring the item's gravity.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (moving) { // we have targeted an item to move
            if (caster.isSneaking()) {
                // calculate the point along the caster's gaze at the item's current distance
                Location eyeLoc = caster.getEyeLocation();
                double distance = eyeLoc.distance(target.getLocation());
                Location gazeLoc = eyeLoc.add(eyeLoc.getDirection().multiply(distance));

                // set velocity toward the gaze point, scaled for smooth movement
                Vector velocity = gazeLoc.toVector().subtract(target.getLocation().toVector()).multiply(0.3);
                target.setVelocity(velocity);
                moveTicks = moveTicks - 1;

                if (moveTicks <= 0) // caster cannot hold the item up any longer
                    kill();
            }
            else { // caster stopped sneaking, end spell
                kill();
            }
        }
        else { // we have not yet targeted an item to move
            List<Item> nearbyItems = getNearbyItems(defaultRadius);

            if (nearbyItems.isEmpty()) {
                if (hasHitBlock()) // we failed to find a target and the projectile has stopped
                    kill();
            }
            else {
                target = nearbyItems.getFirst();

                if (caster.isSneaking()) {
                    moving = true;
                    target.setGravity(false); // turn off gravity so this item no longer falls
                    moveTicks = calculateMoveTicks();
                }
                else {
                    sendFailureMessage();
                    kill();
                }
            }
        }
    }

    /**
     * Calculate the number of ticks the player can levitate the item for based on their skill level
     *
     * @return the number of ticks the item can levitate
     */
    public int calculateMoveTicks() {
        return (int)((usesModifier / 10) * Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Turn back on gravity for the target item
     */
    @Override
    protected void revert() {
        moving = false;

        if (target != null)
            target.setGravity(true);
    }

    /**
     * Get the remaining ticks the caster can keep the item levitated.
     *
     * @return the remaining levitation time in ticks
     */
    public int getMoveTicks() {
        return moveTicks;
    }

    /**
     * Get whether the spell has acquired an item and is actively moving it.
     *
     * @return true if an item is being levitated
     */
    public boolean isMoving() {
        return moving;
    }
}
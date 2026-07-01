package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Dividing Charm, which carves a temporary path of air through water, lava, or fire.
 * <p>
 * When the projectile strikes a target material (water, lava, fire, or soul fire) it begins carving a level path
 * forward. The cast direction is flattened to horizontal at the block it hit - so a cast aimed slightly downward
 * from eye height still runs the tunnel along the surface it struck rather than diving into the ground. Each
 * throttled segment clears a width-by-depth cross-section of target material to air and advances one block along
 * that horizontal direction, building a tunnel whose width, depth, and length scale with the caster's skill.
 * </p>
 * <p>
 * While the path is held open the spell registers as a {@link Listener} to cancel {@link BlockFromToEvent}s that
 * would flow water or lava back into the cleared blocks. After a skill-scaled duration the spell restores every
 * cleared block to its original material and unregisters its listener.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Partis_Temporus">Harry Potter Wiki - Partis Temporus</a>
 */
public final class PARTIS_TEMPORUS extends O2Spell implements Listener {
    /**
     * The maximum depth, in blocks above and below the path center, any cast can carve.
     */
    private static final int maxDepth = 5;

    /**
     * The minimum depth, in blocks above and below the path center, any cast carves.
     */
    private static final int minDepth = 1;

    /**
     * The minimum length, in segments, any carved path can be.
     */
    private static final int minLength = 5;

    /**
     * The maximum length, in segments, any carved path can be.
     */
    private static final int maxLength = 20;

    /**
     * The minimum duration, in ticks, the path is held open before it reverts.
     */
    private static final int minDuration = 30 * Ollivanders2Common.ticksPerSecond;

    /**
     * The maximum duration, in ticks, the path is held open before it reverts.
     */
    private static final int maxDuration = 5 * Ollivanders2Common.ticksPerMinute;

    /**
     * The cleared blocks, keyed by their block-aligned location and mapped to their original material so they can be
     * restored on revert and recognized when cancelling flow into the path. Keying by {@link Location} (value-based
     * equality) rather than {@link Block} keeps the lookup correct regardless of block-instance identity.
     */
    private final HashMap<Location, Material> changedBlocks = new HashMap<>();

    /**
     * The width of the carved path's cross-section, set from the caster's skill in {@link #doInitSpell()}.
     */
    private int width;

    /**
     * The vertical reach above and below the path center that each segment carves, set in {@link #doInitSpell()}.
     */
    private int depth;

    /**
     * The number of path segments still to carve, set in {@link #doInitSpell()} and decremented as the path is built.
     */
    private int length;

    /**
     * The remaining ticks to hold the path open, set in {@link #doInitSpell()} and counted down once carving is done.
     */
    private int duration;

    /**
     * The materials this spell carves through and the only materials the projectile may stop on.
     */
    private static final ArrayList<Material> targetMaterials = new ArrayList<>() {{
        add(Material.WATER);
        add(Material.LAVA);
        add(Material.FIRE);
        add(Material.SOUL_FIRE);
    }};

    /**
     * The block at the leading edge of the path, advanced one block along the cast vector after each carved segment.
     */
    Block center = null;

    /**
     * The fractional position of the leading edge, accumulated along {@link #pathVector} so diagonal casts (whose
     * per-axis components are each less than a full block) still step forward one whole block per carve. The current
     * {@link #center} block is derived from this.
     */
    private Location pathLocation = null;

    /**
     * The horizontal (flattened, y = 0) direction the path is carved in, derived from the cast vector once the first
     * block is hit. Flattening keeps the tunnel level with the block it struck rather than diving toward the ground
     * from the caster's eye-height cast angle.
     */
    private Vector pathVector = null;

    /**
     * The number of ticks between carved segments, throttling carving to two segments per second.
     */
    private static final int maxCooldown = Ollivanders2Common.ticksPerSecond / 2;

    /**
     * Ticks remaining before the next segment is carved, used to throttle carving to two segments per second.
     */
    private int cooldown = maxCooldown;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PARTIS_TEMPORUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PARTIS_TEMPORUS;
        branch = O2MagicBranch.CHARMS;

        text = "Partis Temporus will make a temporary path of air through fire or water.";
        flavorText = new ArrayList<>() {{
            add("The Dividing Charm");
        }};
    }

    /**
     * Casting constructor. Makes the target materials the only blocks the projectile stops on and the only blocks
     * it may carve, registers the WorldGuard BUILD flag, initializes the projectile, and registers this spell as a
     * listener so it can hold the path open against flowing liquids.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PARTIS_TEMPORUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PARTIS_TEMPORUS;
        branch = O2MagicBranch.CHARMS;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        // add target materials to the allow list
        materialAllowList.addAll(targetMaterials);
        // make sure the target materials are not pass through
        projectilePassThrough.removeAll(targetMaterials);

        initSpell();
        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Set the path's width, depth, length, and hold duration from the caster's skill.
     * <p>
     * All four scale with {@code usesModifier} and are clamped: width steps up to 3 at higher skill, depth runs from
     * {@link #minDepth} to {@link #maxDepth}, length runs from {@link #minLength} to {@link #maxLength}, and the hold
     * duration runs from {@link #minDuration} to {@link #maxDuration}.
     * </p>
     */
    @Override
    void doInitSpell() {
        if (usesModifier >= O2Spell.spellMasteryLevel)
            width = 3;
        else if (usesModifier >= (int)(O2Spell.spellMasteryLevel / 2))
            width = 2;
        else
            width = 1;

        length = (int)Math.ceil(usesModifier / 5); // at 100 this will be 20
        if (length < minLength)
            length = minLength;
        else if (length > maxLength)
            length = maxLength;

        depth = (int)Math.ceil(usesModifier / 20); // at 100 this will be 5
        if (depth < 1)
            depth = minDepth;
        else if (depth > maxDepth)
            depth = maxDepth;

        duration = (int)usesModifier * Ollivanders2Common.ticksPerSecond * 3; // at 100 this will be 5 minutes
        if (duration < minDuration)
            duration = minDuration;
        else if (duration > maxDuration)
            duration = maxDuration;
    }

    /**
     * Carve one throttled path segment, then hold the path open until its duration expires.
     * <p>
     * Does nothing until the projectile stops on a target material. On the first segment the cast direction is
     * flattened to horizontal (see {@link #pathVector}) so the tunnel runs level with the block that was struck.
     * While length remains, each throttle interval clears the cross-section at the current {@link #center}, advances
     * the center one block along the horizontal direction, and decrements the remaining length. A purely vertical
     * cast, having no horizontal direction, clears only the block it hit. Once the path is fully carved, counts down
     * the hold duration and ends the spell when it reaches zero, which restores the cleared blocks via {@link #revert()}.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        if (length > 0) { // we are not done clearing the path
            // use cooldown to slow partis down to process 2 segments per second
            if (cooldown > 0) {
                cooldown = cooldown - 1;
                return;
            }

            if (center == null) {
                center = getTargetBlock();

                if (center != null) {
                    // start the fractional accumulator at the impact block; clone so advancing it never mutates the
                    // block's own location (Block.getLocation() may hand back a shared instance)
                    pathLocation = center.getLocation().clone();

                    // flatten the cast vector to horizontal so the path runs level with the block it hit rather than
                    // diving into the ground from the caster's eye-height cast angle
                    pathVector = new Vector(vector.getX(), 0, vector.getZ());
                    if (pathVector.lengthSquared() > 0)
                        pathVector.normalize();
                }
            }

            if (center == null) { // only happens if hasHitBlock is false, which can't happen here but not having this causes a linting error
                kill();
                return;
            }

            // clear the blocks in this location within the depth and width
            clearBlocks();

            // reset cooldown
            cooldown = maxCooldown;

            if (pathVector.lengthSquared() == 0) {
                // a purely vertical cast has no horizontal direction to extend the path along; hold the one block cleared
                length = 0;
            }
            else {
                // step forward exactly one block along the flattened vector: keep adding it until the accumulated
                // position crosses into a new block, so any horizontal direction advances a whole block per carve
                Block nextBlock = center;
                while (nextBlock.equals(center)) {
                    pathLocation.add(pathVector);
                    nextBlock = pathLocation.getBlock();
                }
                center = nextBlock;

                // decrement length remaining
                length = length - 1;
            }
        }
        else { // we are done clearing the path
            // burn down the duration
            duration = duration - 1;
            if (duration <= 0)
                kill();
        }
    }

    /**
     * Clear the path cross-section centered on {@link #center}.
     *
     * <p>Clears each horizontal plane from {@link #depth} blocks below the center up to {@link #depth} blocks above
     * it, so the path is tall enough to move through.</p>
     */
    void clearBlocks() {
        Location centerLocation = center.getLocation();

        // clear blocks up and down
        for (int y = 0; y < depth; y++) {
            clearBlocksInPlane(new Location(centerLocation.getWorld(), centerLocation.getX(), centerLocation.getY() + y, centerLocation.getZ()));
            clearBlocksInPlane(new Location(centerLocation.getWorld(), centerLocation.getX(), centerLocation.getY() - y, centerLocation.getZ()));
        }
    }

    /**
     * Clear a single horizontal plane of the path, {@link #width} blocks out from the center in each direction.
     *
     * @param planeCenter the center of the plane to clear
     */
    void clearBlocksInPlane(Location planeCenter) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                clearBlock(new Location(planeCenter.getWorld(), planeCenter.getX() + x, planeCenter.getY(), planeCenter.getZ() + z));
                clearBlock(new Location(planeCenter.getWorld(), planeCenter.getX() + x, planeCenter.getY(), planeCenter.getZ() - z));
                clearBlock(new Location(planeCenter.getWorld(), planeCenter.getX() - x, planeCenter.getY(), planeCenter.getZ() + z));
                clearBlock(new Location(planeCenter.getWorld(), planeCenter.getX() - x, planeCenter.getY(), planeCenter.getZ() - z));
            }
        }
    }

    /**
     * Clear a single block if it is a target material, recording its original material for later restoration.
     *
     * <p>Blocks already cleared by this cast and blocks that are not target materials are left unchanged.</p>
     *
     * @param location the location of the block to clear
     */
    void clearBlock(@NotNull Location location) {
        Block block = location.getBlock();
        // clone so the stored key is an independent snapshot (Block.getLocation() may hand back a shared, mutable
        // instance, which would corrupt the map if it were later moved)
        Location blockLocation = block.getLocation().clone();
        if (changedBlocks.containsKey(blockLocation))
            return;

        Material type = block.getType();

        if (targetMaterials.contains(type)) {
            changedBlocks.put(blockLocation, type);
            block.setType(Material.AIR);
        }
    }

    /**
     * Prevent water or lava flowing in to the cleared path.
     *
     * @param event the water or lava flow event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFromToEvent(BlockFromToEvent event) {
        if (changedBlocks.containsKey(event.getToBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    /**
     * Restore the path and stop holding it open.
     *
     * <p>Unregisters this spell's flow-cancelling listener first - so it no longer blocks liquid flow into these
     * locations - then restores every cleared block to its original material.</p>
     */
    @Override
    protected void revert() {
        HandlerList.unregisterAll(this);

        for (Location location : changedBlocks.keySet()) {
            Material originalMaterial = changedBlocks.get(location);
            location.getBlock().setType(originalMaterial);
        }
    }

    /**
     * Get the number of path segments still to be carved by this cast. Set by {@link #doInitSpell()} from the
     * caster's skill and decremented as the path is carved, so read it before the spell ticks to its effect.
     *
     * @return the remaining path length, in segments
     */
    public int getLength() {
        return length;
    }

    /**
     * Get the width, in blocks, of the carved path's cross-section. Set by {@link #doInitSpell()} from the caster's
     * skill.
     *
     * @return the path width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the vertical reach, in blocks above and below the path center, that each segment carves. Set by
     * {@link #doInitSpell()} from the caster's skill.
     *
     * @return the path depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Get the duration, in ticks, the carved path is held open before it is reverted. Set by {@link #doInitSpell()}
     * from the caster's skill and counted down once carving is complete, so read it before the spell ticks to its
     * effect.
     *
     * @return the remaining hold duration, in ticks
     */
    public int getDuration() {
        return duration;
    }
}
package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens a door, trapdoor, or gate for a duration that scales with the caster's skill.
 * <p>
 * Dissendium targets any {@link Openable} block — vanilla doors, trapdoors, and fence gates —
 * and holds it open for {@link #openTime} ticks before automatically closing it. The duration
 * is computed from the caster's {@code usesModifier} in {@link #doInitSpell()} and clamped to
 * the {@link #minOpenTime}/{@link #maxOpenTime} range.
 * </p>
 * <p>
 * If the target block is locked by a {@link COLLOPORTUS} stationary spell, Dissendium fails
 * silently and is killed without opening the block. If the block is broken or replaced during
 * the open window, {@link #revert()} bails out defensively rather than throwing.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/One-Eyed_Witch_Spell">Harry Potter Wiki - One-Eyed Witch Spell</a>
 */
public final class DISSENDIUM extends O2Spell {
    /**
     * Maximum number of ticks the target block stays open. Caps the duration computed from
     * {@code usesModifier} in {@link #doInitSpell()}. Currently 60 seconds.
     */
    public final static int maxOpenTime = Ollivanders2Common.ticksPerSecond * 60;

    /**
     * Minimum number of ticks the target block stays open. Floors the duration computed from
     * {@code usesModifier} in {@link #doInitSpell()}. Currently 2 seconds.
     */
    public final static int minOpenTime = Ollivanders2Common.ticksPerSecond * 2;

    /**
     * Remaining ticks the door stays open. Computed in {@link #doInitSpell()}, then decremented
     * each tick once {@link #doorOpened} is set. When this reaches zero, the spell self-kills.
     */
    private int openTime;

    /**
     * True once {@link #openDoor()} has successfully flipped the target block to open. Used by
     * {@link #doCheckEffect()} to switch from "look for a target" mode to "count down then close"
     * mode, and by {@link #revert()} to know whether there's anything to undo.
     */
    private boolean doorOpened;

    /**
     * The target block this spell opened. Captured in {@link #openDoor()} so {@link #revert()}
     * can re-close the same block when the spell is killed, even if the projectile's
     * {@code location} has since drifted.
     */
    private Block doorBlock;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     * <p>
     * Populates {@link #flavorText} with the canon "Opening Charm" excerpt and sets the
     * descriptive {@link #text}. Does not configure cast-time state such as the
     * {@code materialAllowList} or WorldGuard flags — the casting constructor handles that.
     * </p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DISSENDIUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.DISSENDIUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Opening Charm");
            add("At once, the statue's hump opened wide enough to admit a fairly thin person.");
        }};

        text = "Dissendium will open a door, trapdoor, or gate for a few seconds. To open a door, aim at the bottom half.";
    }

    /**
     * Calculate how long the door will stay open based on the caster's skill.
     * <p>
     * {@link #openTime} is set to {@code (usesModifier / 3)} seconds (converted to ticks),
     * then clamped to the {@link #minOpenTime}/{@link #maxOpenTime} range. Higher skill
     * therefore produces a longer open duration, up to the configured ceiling.
     * </p>
     */
    @Override
    void doInitSpell() {
        openTime = ((int) usesModifier / 3) * Ollivanders2Common.ticksPerSecond;

        if (openTime < minOpenTime)
            openTime = minOpenTime;
        else if (openTime > maxOpenTime)
            openTime = maxOpenTime;
    }

    /**
     * Constructor for casting Dissendium.
     * <p>
     * Restricts the spell's {@code materialAllowList} to door, trapdoor, and gate materials
     * (via {@link Ollivanders2Common#getDoors()}) so the projectile is killed by the parent
     * spell flow when it hits anything else. Adds the {@link Flags#USE} WorldGuard flag if
     * WorldGuard is enabled, then invokes {@link #initSpell()} which triggers
     * {@link #doInitSpell()} to compute the open duration.
     * </p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DISSENDIUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DISSENDIUM;
        branch = O2MagicBranch.CHARMS;

        doorOpened = false;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.USE);

        materialAllowList.addAll(Ollivanders2Common.getDoors());

        initSpell();
    }

    /**
     * Tick handler: advance the projectile, open the target on first hit, and close it once the
     * configured duration has elapsed.
     * <p>
     * If the projectile hasn't hit a block yet, returns. Once {@link #doorOpened} is set, decrements
     * {@link #openTime} each tick and calls {@link #kill()} when the
     * counter expires. On the first hit, validates that the target is {@link Openable}, kills
     * the spell if not, otherwise delegates to {@link #openDoor()}.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        // either count down the open timer, or attempt to open on first hit
        if (isDoorOpened()) {
            // count down the open time
            openTime = openTime - 1;

            // if the open time has expired, close the door and kill the spell
            if (openTime <= 0) {
                kill();
            }
        }
        else {
            common.printDebugMessage("DISSENDIUM.doCheckEffect: hit door block", null, null, false);
            Block target = getTargetBlock();
            if (target == null) {
                common.printDebugMessage("DISSENDIUM.doCheckEffect: target block is null", null, null, true);
                kill();
                return;
            }
            BlockData targetBlockData = target.getBlockData();

            // if the target is openable, open it
            if (!(targetBlockData instanceof Openable) || ((Openable)targetBlockData).isOpen()) {
                common.printDebugMessage("DISSENDIUM.doCheckEffect: target block is not openable or is already open", null, null, false);
                kill();
                return;
            }
            openDoor();

            // kill the spell if we did not open a door after hitting a target
            if (!isDoorOpened())
                kill();
        }
    }

    /**
     * Open the door, trapdoor, or gate at the target block.
     * <p>
     * Fails (and kills the spell) if the target is null, locked by an active
     * {@link COLLOPORTUS} stationary spell at the same location, or already open. On success,
     * stores the block in {@link #doorBlock}, sets {@link Openable#setOpen(boolean) open=true}
     * on its block data, and flips {@link #doorOpened} so {@link #doCheckEffect()} starts counting
     * down toward auto-close.
     * </p>
     */
    private void openDoor() {
        Block target = getTargetBlock();
        if (target == null) {
            common.printDebugMessage("DISSENDIUM.openDoor: target block is null", null, null, true);
            kill();
            return;
        }

        // check for colloportus spell locking this door
        Location targetLocation = target.getLocation();
        List<O2StationarySpell> spellsAtLocation = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(targetLocation);

        for (O2StationarySpell statSpell : spellsAtLocation) {
            if (statSpell instanceof COLLOPORTUS) {
                common.printDebugMessage("DISSENDIUM.openDoor: door is within a colloportus spell", null, null, false);
                kill();
                return;
            }
        }

        BlockData targetBlockData = target.getBlockData();

        common.printDebugMessage("DISSENDIUM.openDoor: opening door", null, null, false);
        doorBlock = target;
        ((Openable) targetBlockData).setOpen(true);
        doorBlock.setBlockData(targetBlockData);

        doorOpened = true;
    }

    /**
     * Close the door, trapdoor, or gate previously opened by this spell.
     * <p>
     * No-op if the spell never successfully opened a target. Defensively re-checks that the
     * stored {@link #doorBlock} is still {@link Openable} before casting — a player or other
     * spell may have replaced or destroyed the block during the open window. If the block is
     * no longer openable, kills the spell and returns without throwing.
     * </p>
     */
    @Override
    protected void revert() {
        if (!hasHitBlock() || !isDoorOpened()) // spell was killed before it hit a target block or never opened a target door
            return;

        BlockData doorData = doorBlock.getBlockData();
        if (!(doorData instanceof Openable)) {
            common.printDebugMessage("DISSENDIUM.revert: target block is no longer a door", null, null, false);
            return;
        }

        common.printDebugMessage("DISSENDIUM.revert: closing door", null, null, false);
        ((Openable)doorData).setOpen(false);
        doorBlock.setBlockData(doorData);

        doorOpened = false;
    }

    /**
     * Get the remaining ticks the door will stay open.
     *
     * @return the current value of {@link #openTime}; decrements each tick after the door is opened
     */
    public int getOpenTime() {
        return openTime;
    }

    /**
     * Check whether this spell has opened its target.
     *
     * @return true if {@link #openDoor()} has successfully opened the target block, false otherwise
     */
    public boolean isDoorOpened() {
        return doorOpened;
    }
}
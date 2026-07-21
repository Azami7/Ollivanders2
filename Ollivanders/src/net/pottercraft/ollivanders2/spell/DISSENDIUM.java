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
 * The Opening Charm: opens a targeted door, trapdoor, or fence gate for a skill-scaled duration, then closes it again.
 *
 * <p>A door locked by a {@link COLLOPORTUS} stationary spell cannot be opened — the cast fails silently.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/One-Eyed_Witch_Spell">Harry Potter Wiki - One-Eyed Witch Spell</a>
 */
public final class DISSENDIUM extends O2Spell {
    /**
     * Maximum ticks the target block stays open; caps the skill-derived duration set in {@link #doInitSpell()}.
     */
    public final static int maxOpenTime = Ollivanders2Common.ticksPerSecond * 60;

    /**
     * Minimum ticks the target block stays open; floors the skill-derived duration set in {@link #doInitSpell()}.
     */
    public final static int minOpenTime = Ollivanders2Common.ticksPerSecond * 2;

    /**
     * Remaining ticks the door stays open, counted down each tick once opened; the spell self-kills at zero.
     */
    private int openTime;

    /**
     * True once the target block has been flipped open; separates the "find a target" and "count down then close"
     * phases and tells {@link #revert()} whether there is anything to undo.
     */
    private boolean doorOpened;

    /**
     * The block this spell opened, captured so {@link #revert()} re-closes the same block even if the projectile's
     * location has since moved.
     */
    private Block doorBlock;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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
     * Set how long the door stays open from the caster's skill, limited to [{@link #minOpenTime},
     * {@link #maxOpenTime}].
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
     * Constructor.
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
     * On the first block hit, open the target if it is a closed {@link Openable} (otherwise end the spell); on
     * subsequent ticks, count down the open timer and end the spell when it expires.
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
     * Open the target door, trapdoor, or gate and mark it opened. Fails and kills the spell if the target is missing
     * or locked by an active {@link COLLOPORTUS} stationary spell at its location.
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
     * Close the door this spell opened. No-op if it never opened one. Re-checks that {@link #doorBlock} is still
     * {@link Openable} first, since another player or spell may have replaced the block during the open window.
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
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
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens a trapdoor.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/One-Eyed_Witch_Spell">https://harrypotter.fandom.com/wiki/One-Eyed_Witch_Spell</a>
 */
public final class DISSENDIUM extends O2Spell {
    private final static int maxOpenTime = Ollivanders2Common.ticksPerSecond * 60; // 1 minute
    private final static int minOpenTime = Ollivanders2Common.ticksPerSecond * 2; // 2 seconds

    private int openTime;
    private boolean isOpen;
    private Block trapDoorBlock;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
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

        text = "Dissendium will open a door or trapdoor for a few seconds. To open a door, aim at the bottom half.";
    }

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

        isOpen = false;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.USE);

        initSpell();
    }

    /**
     * Move the spell projectile until it hits a block, if that is a trapdoor, open it and keep it open until the max
     * duration of the spell is reached.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        // continue until the spell opens a trapdoor, hits another block type and is killed, or projectile expires
        if (isOpen) {
            // count down the open time
            openTime = openTime - 1;

            // if the open time has expired, close the trap door and kill the spell
            if (openTime <= 0) {
                closeTrapDoor();
                kill();
            }
        }
        else {
            Block target = getTargetBlock();
            if (target == null) {
                common.printDebugMessage("DISSENDIUM.doCheckEffect: target block is null", null, null, true);
                kill();
                return;
            }
            BlockData targetBlockData = target.getBlockData();

            // if the target is a trap door, open it
            if (targetBlockData instanceof TrapDoor)
                openTrapDoor();

            // kill the spell if we did not open a trap door after hitting a target
            if (!isOpen)
                kill();
        }
    }

    /**
     * Opens the trapdoor at target block
     */
    private void openTrapDoor() {
        Block target = getTargetBlock();
        if (target == null) {
            common.printDebugMessage("DISSENDIUM.openTrapDoor: target block is null", null, null, true);
            kill();
            return;
        }

        // check for colloportus spell locking this door
        Location targetLocation = target.getLocation();
        List<O2StationarySpell> spellsAtLocation = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(targetLocation);

        for (O2StationarySpell statSpell : spellsAtLocation) {
            if (statSpell instanceof COLLOPORTUS) {
                kill();
                return;
            }
        }

        BlockData targetBlockData = target.getBlockData();

        // check to see if the trap door is already open
        if (((TrapDoor) targetBlockData).isOpen()) {
            kill();
            return;
        }

        trapDoorBlock = target;
        ((TrapDoor) targetBlockData).setOpen(true);
        trapDoorBlock.setBlockData(targetBlockData);

        isOpen = true;
    }

    /**
     * Close the trap door
     */
    private void closeTrapDoor() {
        if (!isOpen)
            return;

        // close the trap door
        TrapDoor trapDoorData = (TrapDoor) trapDoorBlock.getBlockData();
        trapDoorData.setOpen(false);
        trapDoorBlock.setBlockData(trapDoorData);

        isOpen = false;
    }
}
package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Colloportus is a locking spell that locks doors, trapdoors, and chests so they cannot be opened or broken.
 * The spell is permanent and can only be removed by the Unlocking Spell, Alohomora.
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Locks doors, trapdoors, and chests at the target location</li>
 * <li>Prevents the target from being opened by any means</li>
 * <li>Prevents the target from being broken or destroyed</li>
 * <li>Permanent spell - duration and radius do not apply</li>
 * <li>Can only be removed with the Alohomora (unlocking) spell</li>
 * <li>Fixed radius (all locked objects are treated the same)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Locking_Spell">https://harrypotter.fandom.com/wiki/Locking_Spell</a>
 * @see net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS
 */
public final class COLLOPORTUS extends StationarySpell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public COLLOPORTUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.COLLOPORTUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Locking Spell.");
        }};

        text = "Locks doors, trapdoors, and chests so they cannot be opened or broken. This spell does not age and can only be removed with the Unlocking Spell, Alohomora.";
    }

    /**
     * Constructs a new COLLOPORTUS spell cast by a player.
     *
     * <p>Sets up the spell-specific parameters. Since COLLOPORTUS is a permanent locking spell,
     * it has a fixed radius (no duration/radius scaling). The spell requires a projectile to hit
     * a valid target (door, trapdoor, or chest) to succeed.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
     */
    public COLLOPORTUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.COLLOPORTUS;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.BUILD);
        }

        minRadius = maxRadius = net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS.minRadiusConfig;
        radiusModifier = 1;
        flairSize = 10;

        initSpell();
    }

    /**
     * Creates the colloportus stationary spell at the target location.
     *
     * <p>Validates that the target block is a door, trapdoor, or chest. If so, ensures the block
     * is closed before creating the lock. Returns null if the target block is invalid.</p>
     *
     * @return the stationary spell instance, or null if the target block is not a valid lockable object
     */
    @Override
    @Nullable
    protected O2StationarySpell createStationarySpell() {
        Block targetBlock = getTargetBlock();
        if (targetBlock == null) {
            common.printDebugMessage("COLLOPORTUS.doCheckEffect: from block is null", null, null, true);
            return null;
        }


        if (!Ollivanders2Common.isDoor(targetBlock) && !Ollivanders2Common.isChest(targetBlock)) {
            common.printDebugMessage("block is not a door, trapdoor, or chest", null, null, true);
            return null;
        }

        // make sure the door/trapdoor is closed, if a player is too close when they cast this they can sometimes inadvertently "hit" the door open when they flick their wand
        BlockData blockData = targetBlock.getBlockData();
        if (blockData instanceof Openable) {
            ((Openable) blockData).setOpen(false);
            targetBlock.setBlockData(blockData);
        }

        return new net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS(p, caster.getUniqueId(), location);
    }
}
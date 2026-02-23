package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Opening charm that unlocks and opens doors and trapdoors.
 *
 * <p>When cast at a door or trapdoor, the spell sets the block's Openable state to open. The spell fails if:</p>
 * <ul>
 * <li>The target block is not a door or trapdoor</li>
 * <li>The door is protected by a COLLOPORTUS stationary spell (magical lock)</li>
 * </ul>
 *
 * <p><strong>Note:</strong> Doors requiring continuous power to remain open (such as iron doors) will
 * open momentarily but close again immediately when the redstone power applies. This is Minecraft's
 * standard behavior, not a limitation of the spell.</p>
 *
 * <p><strong>World Guard:</strong> Requires the INTERACT flag when WorldGuard is enabled.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Opening_Charm">Opening Charm</a>
 * @since 2.21
 */
public class ABERTO extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ABERTO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ABERTO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Opening Charm");
        }};

        text = "Opens doors and trapdoors unless they are magically protected such as by a Locking Spell.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ABERTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ABERTO;
        branch = O2MagicBranch.CHARMS;

        // world guard
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.INTERACT);

        initSpell();
    }

    /**
     * Open the target door or trapdoor when the spell hits.
     *
     * <p>Validates that the target is a door, checks for COLLOPORTUS protection, and if all checks pass,
     * sets the door's Openable state to true. Sends a failure message if the target is not a door or if
     * a COLLOPORTUS stationary spell protects the door.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        kill();

        Block target = getTargetBlock();
        if (target == null)
            return;

        // is it a door?
        if (!Ollivanders2Common.isDoor(target)) {
            sendFailureMessage();
            return;
        }

        // is the door protected by colloportus?
        if (!Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(location, O2StationarySpellType.COLLOPORTUS).isEmpty()) {
            sendFailureMessage();
            return;
        }

        BlockData blockData = target.getBlockData();
        if (!(blockData instanceof Openable))
            return;

        ((Openable) blockData).setOpen(true);
        target.setBlockData(blockData);
    }
}

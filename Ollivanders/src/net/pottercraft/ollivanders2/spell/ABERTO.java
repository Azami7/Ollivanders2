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
 * Opening charm that opens a targeted door or trapdoor. Fails if the target is not a door or trapdoor, or if it is
 * protected by a COLLOPORTUS magical lock.
 * <p>
 * Doors that need continuous power to stay open (such as iron doors) open momentarily and then close again as the
 * redstone state reasserts — that is standard Minecraft behavior, not a limitation of the spell.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Opening_Charm">Harry Potter Wiki - Opening Charm</a>
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
     * Open the struck door or trapdoor. Sends a failure message if the target is not a door or is protected by a
     * COLLOPORTUS lock.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
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

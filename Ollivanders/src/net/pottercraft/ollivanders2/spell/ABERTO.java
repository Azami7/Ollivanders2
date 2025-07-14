package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Opening charm opens doors and trapdoors. It will not work on doors that require power to stay open like iron doors ...
 * well it does open them, they just close again so fast you cannot see it.
 *
 * @author Azami7
 * @since 2.21
 * @see <a href = "https://harrypotter.fandom.com/wiki/Opening_Charm">https://harrypotter.fandom.com/wiki/Opening_Charm</a>
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
     * Opens the target door or trapdoor. If the door is magically locked by something like Colloportus, the spell
     * will fail to open the door.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        kill();

        Block target = getTargetBlock();
        if (target == null)
            return;

        failureMessage = "Nothing seems to happen.";

        // is it a door?
        if (!Ollivanders2Common.isDoor(target)) {
            sendFailureMessage();
            return;
        }

        // is the door protected by colloportus?
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(target.getLocation())) {
            if (stationarySpell instanceof COLLOPORTUS) {
                sendFailureMessage();
                return;
            }
        }

        BlockData blockData = target.getBlockData();
        if (!(blockData instanceof Openable))
            return;

        ((Openable) blockData).setOpen(true);
        target.setBlockData(blockData);
    }
}

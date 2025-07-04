package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Box Blasting Charm
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Box_Blasting_Charm">https://harrypotter.fandom.com/wiki/Box_Blasting_Charm</a>
 * @since 2.21
 */
public class CISTERN_APERIO extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CISTERN_APERIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CISTERN_APERIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"Have a seat, my fine young students! Today we will be discussing a controversial spell known as Cistem Aperio! It is used to blast open a trunk, chest or other container.\" -Filius Flitwick");
            add("Box-Blasting Charm");
        }};

        text = "Opens doors and trapdoors";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CISTERN_APERIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CISTERN_APERIO;
        branch = O2MagicBranch.CHARMS;

        // world guard
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.INTERACT);

        initSpell();
    }

    /**
     * Knocks some or all of the contents of a chest or barrel out of the container.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        kill();

        Block target = getTargetBlock();
        if (target == null)
            return;

        BlockState blockState = target.getState();
        if (!(blockState instanceof Container)) {
            common.printDebugMessage("cistern aperio: target block not a container", null, null, false);
            return;
        }

        Inventory inventory = ((Container) blockState).getInventory();
        failureMessage = "The container shakes but nothing happens.";

        if (inventory.isEmpty()) {
            common.printDebugMessage("cistern aperio: container empty", null, null, false);
            sendFailureMessage();
            return;
        }

        ArrayList<ItemStack> itemsToRemove = new ArrayList<>();
        for (ItemStack item : inventory.getContents()) {
            // 0-99% chance of this item being dropped, based on usesModifier where 200 casts gives 99% chance
            if ((usesModifier / 2) > ((Math.abs(Ollivanders2Common.random.nextInt()) % 100) + 1))
                itemsToRemove.add(item);
        }

        if (itemsToRemove.isEmpty()) {
            sendFailureMessage();
            return;
        }

        for (ItemStack item : itemsToRemove) {
            // this shouldn't be null because spigot Inventory.getContents() is supposed to force @NotNull, but it seems fot paper this can be null
            if (item == null)
                continue;

            inventory.remove(item);
            target.getWorld().dropItemNaturally(target.getLocation(), item);
        }
    }
}

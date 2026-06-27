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
 * The Box-Blasting Charm, which blasts open a container and scatters some of its contents.
 * <p>
 * Cistem Aperio is cast as a projectile. When it strikes a container block (chest, barrel, hopper, shulker box,
 * etc.), each item in the container has an independent chance to be ejected, scaling up to 99% as the caster gains
 * experience with the spell. Ejected items are dropped into the world at the container's location.
 * </p>
 * <p>
 * The spell fails with a "shakes but nothing happens" message if the target container is empty or if no items
 * happen to be ejected, and does nothing at all if the target block is not a container.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Box_Blasting_Charm">Harry Potter Wiki - Box-Blasting Charm</a>
 */
public class CISTEM_APERIO extends O2Spell {
    /**
     * Failure message sent to the caster when the targeted container is empty or no items happen to be ejected.
     */
    public static final String containerShakesMessage = "The container shakes but nothing happens.";

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CISTEM_APERIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CISTEM_APERIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"Have a seat, my fine young students! Today we will be discussing a controversial spell known as Cistem Aperio! It is used to blast open a trunk, chest or other container.\" -Filius Flitwick");
            add("Box-Blasting Charm");
        }};

        text = "Blast open a chest or other container and cause items to fall out.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CISTEM_APERIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CISTEM_APERIO;
        branch = O2MagicBranch.CHARMS;

        // world guard
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.INTERACT);

        initSpell();
    }

    /**
     * Blast open the struck container, ejecting some of its contents.
     * <p>
     * Runs only once the projectile has hit a block, after which the spell is killed. If the struck block is not a
     * {@link Container} the spell does nothing further. Otherwise each item in the container is independently rolled
     * against a chance derived from the caster's {@code usesModifier} (up to 99%); a selected item is cleared from
     * its slot and dropped at the container. If the container is empty, or no item is ejected, the caster is sent
     * the failure message.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        kill();

        Block target = getTargetBlock();
        if (target == null)
            return;

        BlockState blockState = target.getState();
        if (!(blockState instanceof Container)) {
            common.printDebugMessage("cistem aperio: target block not a container", null, null, false);
            return;
        }

        Inventory inventory = ((Container) blockState).getInventory();
        failureMessage = containerShakesMessage;

        if (inventory.isEmpty()) {
            common.printDebugMessage("cistem aperio: container empty", null, null, false);
            sendFailureMessage();
            return;
        }

        // iterate by slot so we can clear the exact slot that was selected. inventory.remove(ItemStack) would
        // remove every slot holding an identical stack, destroying duplicates that were not selected.
        ItemStack[] contents = inventory.getContents();
        boolean droppedAny = false;

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            // getContents() can return null for empty slots, and on Paper this can be null even though
            // spigot's Inventory.getContents() is annotated @NotNull
            if (item == null)
                continue;

            // 0-99% chance of this item being dropped, based on usesModifier where 200 casts gives 99% chance.
            // in test mode a cast at or above spell mastery guarantees the eject so tests are deterministic, while
            // the random roll is still exercised at lower skill levels.
            boolean dropItem;
            if (Ollivanders2.testMode && usesModifier >= O2Spell.spellMasteryLevel)
                dropItem = true;
            else
                dropItem = (usesModifier / 2) > (Ollivanders2Common.random.nextInt(100) + 1);

            if (dropItem) {
                inventory.setItem(slot, null);
                target.getWorld().dropItemNaturally(target.getLocation(), item);
                droppedAny = true;
            }
        }

        if (!droppedAny)
            sendFailureMessage();
    }
}

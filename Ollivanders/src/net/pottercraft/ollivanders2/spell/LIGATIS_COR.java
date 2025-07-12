package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * If a coreless wand is near enough a core material, makes a wand of the wand wood type and core type. Itemstack amount
 * is 1 regardless of how many items were in either starting stack.
 * <p>
 * {@link FRANGE_LIGNEA}
 */
public final class LIGATIS_COR extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LIGATIS_COR(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LIGATIS_COR;
        branch = O2MagicBranch.CHARMS;

        text = "Ligatis Cor will bind a coreless wand to a core material. Make sure the two items are near each other when this spell is cast. You can only use this on one coreless wand and one core material at a time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LIGATIS_COR(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LIGATIS_COR;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_PICKUP);
            worldGuardFlags.add(Flags.ITEM_DROP);
        }

        initSpell();
    }

    /**
     * Look for any coreless wands near the projectile and try to make a wand.
     */
    @Override
    protected void doCheckEffect() {
        // projectile has stopped, kill the spell
        if (hasHitTarget()) {
            player.sendMessage(Ollivanders2.chatColor + "Spell failed to find a coreless wand.");
            kill();
            return;
        }

        // is there a wand nearby?
        Item baseWand = EntityCommon.getNearbyItemByType(location, O2ItemType.WAND, 1.5);
        if (baseWand == null)
            return;

        // is this wand already complete?
        if (Ollivanders2API.getItems().getWands().isWand(baseWand.getItemStack()))
            return;

        kill();
        createAndDropWand(baseWand);
    }

    /**
     * Create the wand from the material and core.
     *
     * @param baseWand a coreless wand
     */
    private void createAndDropWand(@NotNull Item baseWand) {
        ItemStack wand = null;

        // is there a core material nearby?
        Item coreItem = EntityCommon.getNearbyItemByMaterialList(baseWand.getLocation(), O2WandCoreType.getAllCoresByMaterial(), 2.0);
        if (coreItem != null) {
            O2WandCoreType coreType = O2WandCoreType.getWandCoreTypeByMaterial(coreItem.getItemStack().getType());
            if (coreType != null) {
                String core = coreType.getLabel();
                wand = Ollivanders2API.getItems().getWands().makeWandFromCoreless(baseWand.getItemStack(), core, 1);
            }
        }

        if (wand == null) {
            player.sendMessage(Ollivanders2.chatColor + "No wand cores items found.");
            return;
        }

        Location dropLocation = player.getLocation();
        if (dropLocation.getWorld() == null)
            return;

        int amount = baseWand.getItemStack().getAmount();

        // decrement the baseWand amount
        if (amount > 1)
            baseWand.getItemStack().setAmount(amount - 1);
        else
            baseWand.remove();

        // decrement the core material
        amount = coreItem.getItemStack().getAmount();
        if (amount > 1)
            coreItem.getItemStack().setAmount(amount - 1);
        else
            coreItem.remove();

        // drop the wand
        dropLocation.getWorld().dropItemNaturally(dropLocation, wand);
    }
}
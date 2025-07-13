package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2Item;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Place an enchantment on an item.
 */
public abstract class ItemEnchant extends O2Spell {
    /**
     * The type of enchantment
     */
    protected ItemEnchantmentType enchantmentType;

    /**
     * The list of item types that this can enchant, if limited. When empty, all item types can be enchanted
     */
    protected ArrayList<O2ItemType> itemTypeAllowlist = new ArrayList<>();

    /**
     * Minimum magnitude
     */
    int minMagnitude = 1;

    /**
     * Maximum magnitude
     */
    int maxMagnitude = 100;

    /**
     * Strength multiplier for this enchantment
     */
    double strength = 1;

    /**
     * Magnitude of this enchantment - this is the final value used to determine the enchantment effect
     */
    int magnitude;

    /**
     * The optional arguments for the enchantment
     */
    String args = "";

    /**
     * Whether the player holds the item to enchant it or casts a spell projectile at an item
     */
    boolean holdItemToEnchant = false;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ItemEnchant(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ItemEnchant(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_PICKUP);
            worldGuardFlags.add(Flags.ITEM_DROP);
        }

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);
    }

    /**
     * Set enchantment magnitude based on caster's skill
     */
    @Override
    void doInitSpell() {
        magnitude = (int) ((usesModifier / 4) * strength);

        if (magnitude < minMagnitude)
            magnitude = minMagnitude;
        else if (magnitude > maxMagnitude)
            magnitude = maxMagnitude;

        common.printDebugMessage("Magnitude for enchantment = " + magnitude, null, null, false);
    }

    /**
     * Override from O2Spell since some enchantments will not use a spell projectile but enchant an item in the player inv
     */
    @Override
    public void checkEffect() {
        if (holdItemToEnchant) {

        }
        else
            super.checkEffect();
    }

    /**
     * Add the curse effect to an item stack in the projectile's location
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget()) {
            kill();
            return;
        }

        List<Item> items = getItems(1.5);
        for (Item item : items) {
            // if this is a wand or an enchanted item, skip it, we cannot stack enchantments
            if (Ollivanders2API.getItems().getWands().isWand(item.getItemStack()) || (Ollivanders2API.getItems().enchantedItems.isEnchanted(item)))
                continue;

            // if this enchantment has an allowed list, check it
            if (!itemTypeAllowlist.isEmpty()) {
                O2ItemType itemType = O2Item.getItemType(item.getItemStack());
                if (itemType == null || !(itemTypeAllowlist.contains(itemType)))
                    continue;
            }

            Item enchantedItem = enchantItem(item);
            Ollivanders2API.getItems().enchantedItems.addEnchantedItem(enchantedItem, enchantmentType, magnitude, args);

            stopProjectile();
            kill();

            break;
        }
    }

    protected boolean canBeEnchanted(Item item) {
        if (Ollivanders2API.getItems().getWands().isWand(item.getItemStack()) || (Ollivanders2API.getItems().enchantedItems.isEnchanted(item)))
            return false;

        return true;
    }

    /**
     * Enchant the item.
     *
     * @param item the item to enchant
     * @return the enchanted item
     */
    @NotNull
    private Item enchantItem(@NotNull Item item) {
        // clone the item stack
        ItemStack enchantedItemStack = item.getItemStack().clone();

        enchantedItemStack.setAmount(1);

        // update original itemStack to remove 1 item, if it had more than one, or remove the original item if there is only 1
        if (item.getItemStack().getAmount() > 1)
            item.getItemStack().setAmount(item.getItemStack().getAmount() - 1);
        else
            item.remove();

        // drop enchanted item in World
        return item.getWorld().dropItem(item.getLocation(), enchantedItemStack);
    }
}

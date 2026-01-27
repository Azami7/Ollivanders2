package net.pottercraft.ollivanders2.item.wand;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage wands and wand functions
 */
public class O2Wands {
    /**
     * plugin callback
     */
    private final Ollivanders2 p;

    /**
     * common functions
     */
    private final Ollivanders2Common common;

    /**
     * Namespace keys for NBT tags
     */
    private final NamespacedKey wandWoodKey;
    private final NamespacedKey wandCoreKey;

    /**
     * Wand conjunction for wand lore
     */
    public static final String wandLoreConjunction = " and ";

    /**
     * Constructor
     *
     * @param plugin callback to the plugin
     */
    public O2Wands(Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);

        wandCoreKey = new NamespacedKey(p, "o2wand_core");
        wandWoodKey = new NamespacedKey(p, "o2wand_wood");
    }

    /**
     * Is this item stack a wand?
     *
     * @param itemstack stack to be checked
     * @return true if yes, false if no
     */
    public boolean isWand(@NotNull ItemStack itemstack) {
        common.printDebugMessage("isWand enter", null, null, false);

        // check elder wand
        if (O2ItemType.ELDER_WAND.isItemThisType(itemstack))
            return true;

        if ((O2ItemType.WAND.isItemThisType(itemstack))) {
            // check NBT wand
            if (checkNBT(itemstack))
                return true;
        }

        common.printDebugMessage("not a wand", null, null, false);
        return false;
    }

    /**
     * Check wand NBT
     *
     * @param itemstack the itemstack to check
     * @return true if this wand has a valid NBT, false otherwise
     */
    public boolean checkNBT(@NotNull ItemStack itemstack) {
        ItemMeta itemMeta = itemstack.getItemMeta();
        if (itemMeta == null)
            return false;

        // assume if something is set, this is good enough since players cannot set NBT this shouldn't be faked
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(wandWoodKey, PersistentDataType.STRING) && container.has(wandCoreKey, PersistentDataType.STRING);
    }

    /**
     * Make a wand lore string from a wood and core
     *
     * @param wood the wand wood type
     * @param core the wand core type
     * @return the wand lore string
     */
    @NotNull
    public String createLore(@NotNull String wood, @NotNull String core) {
        return wood + wandLoreConjunction + core;
    }

    /**
     * Is this ItemStack the player's destined wand?
     *
     * @param player player to check the stack against.
     * @param stack  ItemStack to be checked
     * @return true if yes, false if no
     */
    public boolean isDestinedWand(@NotNull Player player, @NotNull ItemStack stack) {
        O2Player o2Player = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());

        if (o2Player == null)
            return false;

        return isDestinedWand(o2Player, stack);
    }

    /**
     * Determine if a wand matches the player's destined wand type.
     *
     * @param player    the player to check
     * @param itemStack the wand to check
     * @return true if is a wand and matches the player's destined wand, false otherwise
     */
    public boolean isDestinedWand(@NotNull O2Player player, @NotNull ItemStack itemStack) {
        if (!isWand(itemStack))
            return false;

        String destinedWood = player.getDestinedWandWood();
        String destinedCore = player.getDestinedWandCore();

        // check NBT
        return checkCoreNBT(destinedCore, itemStack) && checkWoodNBT(destinedWood, itemStack);
    }

    /**
     * Does this core match what is set on the NBT for this wand?
     *
     * @param core      the core type
     * @param itemStack the item to check
     * @return true if it matches, false otherwise
     */
    public boolean checkCoreNBT(@NotNull String core, @NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!(container.has(wandCoreKey, PersistentDataType.STRING)))
            return false;

        String coreNBT = container.get(wandCoreKey, PersistentDataType.STRING);
        if (coreNBT == null)
            return false;

        return coreNBT.equalsIgnoreCase(core);
    }

    /**
     * Does this wood match what is set on the NBT for this wand?
     *
     * @param wood      the wood type
     * @param itemStack the item to check
     * @return true if it matches, false otherwise
     */
    public boolean checkWoodNBT(@NotNull String wood, @NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!(container.has(wandWoodKey, PersistentDataType.STRING)))
            return false;

        String woodNBT = container.get(wandWoodKey, PersistentDataType.STRING);
        if (woodNBT == null)
            return false;

        return woodNBT.equalsIgnoreCase(wood);
    }

    /**
     * Does the player hold a wand item in their primary hand?
     *
     * @param player player to check.
     * @return true if the player holds a wand, false otherwise
     */
    public boolean holdsWand(@NotNull Player player) {
        return holdsWand(player, EquipmentSlot.HAND);
    }

    /**
     * Does the player hold a wand item in their hand?
     *
     * @param player player to check.
     * @param hand   the equipment slot to check for this player
     * @return true if the player holds a wand, false otherwise
     */
    public boolean holdsWand(@NotNull Player player, @NotNull EquipmentSlot hand) {
        ItemStack held;
        if (hand == EquipmentSlot.HAND) {
            common.printDebugMessage("O2Wands.holdsWand: checking for wand in main hand", null, null, false);
            held = player.getInventory().getItemInMainHand();
        }
        else if (hand == EquipmentSlot.OFF_HAND) {
            common.printDebugMessage("O2Wands.holdsWand: checking for wand in off hand", null, null, false);
            held = player.getInventory().getItemInOffHand();
        }
        else {
            return false;
        }

        if (held.getType() == Material.AIR) {
            common.printDebugMessage("O2Wands.holdsWand: player not holding an item", null, null, false);
            return false;
        }

        return isWand(held);
    }

    /**
     * Get all the wands in the game.
     *
     * @return a list of all the wands
     */
    @NotNull
    public List<ItemStack> getAllWands() {
        ArrayList<ItemStack> wands = new ArrayList<>();

        for (String wood : O2WandWoodType.getAllWandWoodsByName()) {
            for (String core : O2WandCoreType.getAllWandCoreNames()) {
                wands.add(makeWand(wood, core, 1));
            }
        }

        return wands;
    }

    /**
     * Make an ItemStack of a specific wand type
     *
     * @param wood   the wand wood
     * @param core   the wand core
     * @param amount the number of wands to make
     * @return an ItemStack of wands or null if wood or core is not valid
     */
    @Nullable
    public ItemStack makeWand(@NotNull String wood, @NotNull String core, int amount) {
        if (amount < 1)
            amount = 1;

        if (!O2WandWoodType.getAllWandWoodsByName().contains(wood) || !O2WandCoreType.getAllWandCoreNames().contains(core))
            return null;

        List<String> lore = new ArrayList<>();
        ItemStack wand = Ollivanders2API.getItems().getItemByType(O2ItemType.WAND, 1);
        if (wand == null) {
            common.printDebugMessage("O2Wands.makeWand: wand O2Item type missing", null, null, true);
            return null;
        }

        ItemMeta meta = wand.getItemMeta();

        if (meta == null)
            return null;

        // set NBT
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(wandWoodKey, PersistentDataType.STRING, wood);
        container.set(wandCoreKey, PersistentDataType.STRING, core);

        // set the lore
        lore.add(createLore(wood, core));
        meta.setLore(lore);

        // make wands not look like sticks
        meta.addEnchant(Enchantment.LOYALTY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        wand.setItemMeta(meta);
        wand.setAmount(amount);

        return wand;
    }

    /**
     * Make a wand from a coreless wand and a wand core
     *
     * @param corelessWand the coreless wand
     * @param core         the wand core
     * @param amount       the amount to make
     * @return an item stack of the wand type, null if an error occurred
     */
    @Nullable
    public ItemStack makeWandFromCoreless(@NotNull ItemStack corelessWand, @NotNull O2WandCoreType core, int amount) {
        // determine the wand wood
        ItemMeta itemMeta = corelessWand.getItemMeta();
        if (itemMeta == null) {
            common.printDebugMessage("O2Wands.makeWandFromCoreless: item meta is null", null, null, false);
            return null;
        }

        String wood = null;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(wandWoodKey, PersistentDataType.STRING)) {
            wood = container.get(wandWoodKey, PersistentDataType.STRING);
            if (wood == null)
                wood = O2WandWoodType.getRandomWood();
        }

        if (wood == null)
            wood = O2WandWoodType.getRandomWood();

        return makeWand(wood, core.getLabel(), amount);
    }

    /**
     * Create a coreless wand of a specified wood type.
     *
     * @param woodType the wood type for the wand
     * @param amount   the amount
     * @return an item stack of this wand type, null if an error occurred
     */
    @Nullable
    public ItemStack createCorelessWand(@NotNull O2WandWoodType woodType, int amount) {
        ItemStack wands = new ItemStack(O2ItemType.WAND.getMaterial(), amount);
        ItemMeta itemMeta = wands.getItemMeta();
        if (itemMeta == null)
            return null;

        // set the NBT
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(wandWoodKey, PersistentDataType.STRING, woodType.getLabel());

        // set the lore
        ArrayList<String> lore = new ArrayList<>();
        lore.add(woodType.getLabel());

        itemMeta.setLore(lore);
        wands.setItemMeta(itemMeta);

        return wands;
    }

    /**
     * Give a player a random wand.
     *
     * @param player the player to give the wand to
     * @return true if successful
     */
    public boolean giveRandomWand(@NotNull Player player) {
        ItemStack wand = createRandomWand();
        if (wand == null)
            return false;

        List<ItemStack> kit = new ArrayList<>();
        kit.add(wand);

        O2PlayerCommon.givePlayerKit(player, kit);
        return true;
    }

    /**
     * Create a random wand
     *
     * @return a random wand or null if something went wrong
     */
    @Nullable
    public ItemStack createRandomWand() {
        ArrayList<String> woods = O2WandWoodType.getAllWandWoodsByName();
        ArrayList<String> cores = O2WandCoreType.getAllWandCoreNames();

        String wood = woods.get(Ollivanders2Common.random.nextInt(woods.size()));
        String core = cores.get(Ollivanders2Common.random.nextInt(cores.size()));

        return makeWand(wood, core, 1);
    }
}

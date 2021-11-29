package net.pottercraft.ollivanders2.item.wand;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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
 *
 * @author Azami7
 */
public class O2Wands
{
    Ollivanders2 p;
    Ollivanders2Common common;

    /**
     * Namespace keys for NBT tags
     */
    NamespacedKey wandWoodKey;
    NamespacedKey wandCoreKey;

    /**
     * Wand conjunction for wand lore
     */
    public static final String wandLoreConjunction = " and ";

    /**
     * Constructor
     *
     * @param plugin callback to the plugin
     */
    public O2Wands(Ollivanders2 plugin)
    {
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
    public boolean isWand(@NotNull ItemStack itemstack)
    {
        common.printDebugMessage("isWand enter", null, null, false);

        // check elder wand
        if (O2ItemType.ELDER_WAND.isItemThisType(itemstack))
            return true;

        if ((O2ItemType.WAND.isItemThisType(itemstack)))
        {
            // check NBT wand
            if (checkNBT(itemstack))
                return true;

            // check lore-based wand
            // TODO remove this when we remove lore-based items in the next major rev
            return checkLore(itemstack);
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
    public boolean checkNBT(@NotNull ItemStack itemstack)
    {
        ItemMeta itemMeta = itemstack.getItemMeta();
        if (itemMeta == null)
            return false;

        // check NBT
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(wandWoodKey, PersistentDataType.STRING) && container.has(wandCoreKey, PersistentDataType.STRING))
        {
            // assume if something is set, this is good enough since players cannot set NBT this shouldnt be faked
            return true;
        }

        return false;
    }

    /**
     * Check wand lore. This is needed for wands created before 2.6.5.
     *
     * @param itemStack the itemstack to check
     * @return true if the lore is correctly set for a wand, false otherwise
     */
    @Deprecated
    public boolean checkLore(@NotNull ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;

        List<String> lore = itemMeta.getLore();
        if (lore == null || lore.size() < 1)
            return false;

        return (lore.get(0).split(wandLoreConjunction).length == 2);
    }

    /**
     * Make a wand lore string from a wood and core
     *
     * @param wood the wand wood type
     * @param core the wand core type
     * @return the wand lore string
     */
    @NotNull
    public String createLore(@NotNull String wood, @NotNull String core)
    {
        return wood + wandLoreConjunction + core;
    }

    /**
     * Is this ItemStack the player's destined wand?
     *
     * @param player player to check the stack against.
     * @param stack  ItemStack to be checked
     * @return true if yes, false if no
     */
    public boolean isDestinedWand(@NotNull Player player, @NotNull ItemStack stack)
    {
        O2Player o2Player = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());

        if (o2Player == null)
            return false;

        return isDestinedWand(o2Player, stack);
    }

    /**
     * Determine if a wand matches the player's destined wand type.
     *
     * @param player the player to check
     * @param itemStack the wand to check
     * @return true if is a wand and it matches the player's destined wand, false otherwise
     */
    public boolean isDestinedWand(@NotNull O2Player player, @NotNull ItemStack itemStack)
    {
        if (!isWand(itemStack))
            return false;

        String destinedWood = player.getDestinedWandWood();
        String destinedCore = player.getDestinedWandCore();

        // check NBT
        if (checkCoreNBT(destinedCore, itemStack) && checkWoodNBT(destinedWood, itemStack))
            return true;

        // check Lore
        return matchesLore(destinedWood, destinedCore, itemStack);
    }

    /**
     * Does this core match what is set on the NBT for this wand?
     *
     * @param core the wood type
     * @param itemStack the item to check
     * @return true if it matches, false otherwise
     */
    public boolean checkCoreNBT(@NotNull String core, @NotNull ItemStack itemStack)
    {
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
     * @param wood the wood type
     * @param itemStack the item to check
     * @return true if it matches, false otherwise
     */
    public boolean checkWoodNBT(@NotNull String wood, @NotNull ItemStack itemStack)
    {
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
     * Does the lore for this itemstack match the wood and core specified
     *
     * @param core the core to match
     * @param wood the wood to match
     * @param itemStack the item to check
     * @return true if the lore matches this wood and core, false otherwise
     */
    public boolean matchesLore(@NotNull String wood, @NotNull String core, @NotNull ItemStack itemStack)
    {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return false;


        List<String> lore = itemStack.getItemMeta().getLore();
        if (lore == null)
            return false;

        String[] wandLore = lore.get(0).split(wandLoreConjunction);
        if (wandLore.length != 2)
            return false;

        if (!wood.equalsIgnoreCase(wandLore[0]) || !core.equalsIgnoreCase(wandLore[1]))
            return false;

        return true;
    }

    /**
     * Does the player hold a wand item in their primary hand?
     *
     * @param player player to check.
     * @return True if the player holds a wand. False if not or if player is null.
     */
    public boolean holdsWand(@NotNull Player player)
    {
        return holdsWand(player, EquipmentSlot.HAND);
    }

    /**
     * Does the player hold a wand item in their hand?
     *
     * @param player player to check.
     * @param hand   the equipment slot to check for this player
     * @return True if the player holds a wand. False if not or if player is null.
     * @since 2.2.7
     */
    public boolean holdsWand(@NotNull Player player, @NotNull EquipmentSlot hand)
    {
        ItemStack held;
        if (hand == EquipmentSlot.HAND)
        {
            common.printDebugMessage("O2PlayerCommon.holdsWand: checking for wand in main hand", null, null, false);
            held = player.getInventory().getItemInMainHand();
        }
        else if (hand == EquipmentSlot.OFF_HAND)
        {
            common.printDebugMessage("O2PlayerCommon.holdsWand: checking for wand in off hand", null, null, false);
            held = player.getInventory().getItemInOffHand();
        }
        else
        {
            return false;
        }

        if (held.getType() == Material.AIR)
        {
            common.printDebugMessage("O2PlayerCommon.holdsWand: player not holding an item", null, null, false);
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
    public List<ItemStack> getAllWands()
    {
        ArrayList<ItemStack> wands = new ArrayList<>();

        for (String wood : O2WandWoodType.getAllWoodsByName())
        {
            for (String core : O2WandCoreType.getAllCoresByName())
            {
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
     * @return an ItemStack of wands or null if an error
     */
    @Nullable
    public ItemStack makeWand(@NotNull String wood, @NotNull String core, int amount)
    {
        if (amount < 1)
            amount = 1;

        List<String> lore = new ArrayList<>();
        ItemStack wand = Ollivanders2API.getItems().getItemByType(O2ItemType.WAND, 1);
        if (wand == null)
            return null;

        ItemMeta meta = wand.getItemMeta();

        if (meta == null)
            return null;

        // set NBT
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(wandWoodKey, PersistentDataType.STRING, wood);
        container.set(wandCoreKey, PersistentDataType.STRING, core);

        // set the lore
        lore.add(wood + wandLoreConjunction + core);
        meta.setLore(lore);

        wand.setItemMeta(meta);
        wand.setAmount(amount);

        return wand;
    }

    /**
     * Make a wand from a coreless wand and a wand core
     *
     * @param corelessWand the coreless wand
     * @param core the wand core
     * @param amount the amount to make
     * @return an item stack of the wand type, null if an error occurred
     */
    @Nullable
    public ItemStack makeWandFromCoreless(@NotNull ItemStack corelessWand, @NotNull String core, int amount)
    {
        // determine the wand wood
        ItemMeta itemMeta = corelessWand.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(wandWoodKey, PersistentDataType.STRING))
        {
            String wood = container.get(wandWoodKey, PersistentDataType.STRING);
            if (wood == null)
                wood = O2WandWoodType.getRandomWoodByName();

            return makeWand(core, wood, amount);
        }

        return null;
    }

    /**
     * Create a coreless wand of a specied wood type.
     *
     * @param woodType the wood type for the wand
     * @param amount the amount
     * @return an item stack of this wand type, null if an error occurred
     */
    @Nullable
    public ItemStack createCorelessWand(@NotNull O2WandWoodType woodType, int amount)
    {
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
     */
    public boolean giveRandomWand(@NotNull Player player)
    {
        ItemStack wand = createRandomWand();
        if (wand == null)
            return false;

        List<ItemStack> kit = new ArrayList<>();
        kit.add(wand);

        Ollivanders2API.common.givePlayerKit(player, kit);
        return true;
    }

    /**
     * Create a random wand
     *
     * @return a random wand or null if something went wrong
     */
    @Nullable
    public ItemStack createRandomWand()
    {
        String wood = O2WandWoodType.getAllWoodsByName().get(Math.abs(Ollivanders2Common.random.nextInt() % O2WandWoodType.getAllWoodsByName().size()));
        String core = O2WandCoreType.getAllCoresByName().get(Math.abs(Ollivanders2Common.random.nextInt() % O2WandCoreType.getAllCoresByName().size()));

        return makeWand(wood, core, 1);
    }
}

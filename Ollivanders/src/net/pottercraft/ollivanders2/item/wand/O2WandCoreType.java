package net.pottercraft.ollivanders2.item.wand;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Wand cores
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Wand_core">https://harrypotter.fandom.com/wiki/Wand_core</a>
 */
public enum O2WandCoreType {
    /**
     * bone - legacy
     */
    BONE(O2ItemType.BONE, true),
    /**
     * dragon heartstring
     */
    DRAGON_HEARTSTRING(O2ItemType.DRAGON_HEARTSTRING, false),
    /**
     * Gunpowder - legacy
     */
    GUNPOWDER(O2ItemType.GUNPOWDER, true),
    /**
     * kelpie hair
     */
    KELPIE_HAIR(O2ItemType.KELPIE_HAIR, false),
    /**
     * phoenix feather
     */
    PHOENIX_FEATHER(O2ItemType.PHOENIX_FEATHER, false),
    /**
     * Rotten flesh - legacy
     */
    ROTTEN_FLESH(O2ItemType.ROTTEN_FLESH, true),
    /**
     * Spider Eye - legacy
     */
    SPIDER_EYE(O2ItemType.SPIDER_EYE, true),
    /**
     * unicorn hair
     */
    UNICORN_HAIR(O2ItemType.UNICORN_HAIR, true),
    /**
     * veela hair
     */
    VEELA_HAIR(O2ItemType.VEELA_HAIR, true),
    ;

    private final O2ItemType o2ItemType;
    private final boolean isLegacy;

    /**
     * Constructor
     *
     * @param item the core item type
     */
    O2WandCoreType(@NotNull O2ItemType item, boolean isLegacy) {
        o2ItemType = item;
        this.isLegacy = isLegacy;
    }

    /**
     * Get the O2ItemType for this wand core type
     *
     * @return the item type
     */
    @NotNull
    public O2ItemType getO2ItemType() {
        return o2ItemType;
    }

    /**
     * Get the label for this wand core type
     *
     * @return the label
     */
    @NotNull
    public String getLabel() {
        return o2ItemType.getName();
    }

    /**
     * Get the wand core type for this material.
     *
     * @param type the O2ItemType to check
     * @return the wand core type if found, null otherwise
     */
    @Nullable
    public static O2WandCoreType getWandCoreTypeByItemType(@NotNull O2ItemType type) {
        for (O2WandCoreType coreType : O2WandCoreType.values()) {
            if (coreType.o2ItemType == type)
                return coreType;
        }

        return null;
    }

    /**
     * Get a list of all the wand core types by name.
     *
     * @return the names of all wand cores as a list
     */
    @NotNull
    public static ArrayList<String> getAllCoresByName() {
        ArrayList<String> cores = new ArrayList<>();

        for (O2WandCoreType coreType : O2WandCoreType.values())
            cores.add(coreType.getLabel());

        return cores;
    }

    /**
     * Get a list of all the wand core types by O2ItemType type.
     *
     * @return the types of all wand cores as a list
     */
    @NotNull
    public static ArrayList<O2ItemType> getAllCoresByO2ItemType() {
        ArrayList<O2ItemType> cores = new ArrayList<>();

        for (O2WandCoreType coreType : O2WandCoreType.values())
            cores.add(coreType.getO2ItemType());

        return cores;
    }

    /**
     * Get a random wand core by name
     *
     * @return a random wand core name, will not send a legacy core
     */
    @NotNull
    public static String getRandomCoreByName() {
        O2WandCoreType[] cores = O2WandCoreType.values();
        O2WandCoreType core;
        
        do {
            int rand = Ollivanders2Common.random.nextInt(cores.length);
            core = cores[rand];
        } while (core.isLegacy);
        
        return core.getLabel();
    }

    /**
     * Set a player's destined wand core by seed.
     *
     * @param seed the seed to determine their destined core
     * @return the wand core
     */
    public static String setDestinedWandCore (int seed)
    {
        O2WandCoreType[] cores = O2WandCoreType.values();
        O2WandCoreType core;

        core = cores[seed % O2WandCoreType.getAllCoresByName().size()];

        // if the core would be a legacy core, pick one of the 3 common cores
        if (core.isLegacy) {
            int seed2 = seed % 3;
            if (seed2 == 0)
                core = UNICORN_HAIR;
            else if (seed2 == 1)
                core = KELPIE_HAIR;
            else
                core = DRAGON_HEARTSTRING;
        }

        return core.getLabel();
    }

    /**
     * Is this Item a wand core?
     *
     * @param item the Item to check
     * @return true if it is a wand core, false otherwise
     */
    public static boolean isWandCore(Item item) {
        return isWandCore(item.getItemStack());
    }

    /**
     * Is this ItemStack a wand core?
     *
     * @param itemStack the ItemStack to check
     * @return true if it is a wand core, false otherwise
     */
    public static boolean isWandCore(ItemStack itemStack) {
        O2ItemType itemType = Ollivanders2API.getItems().getItemTypeByItemStack(itemStack);

        if (itemType == null)
            return false;

        if (getWandCoreTypeByItemType(itemType) == null)
            return false;
        else
            return true;
    }
}

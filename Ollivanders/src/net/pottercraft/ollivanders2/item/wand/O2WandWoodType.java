package net.pottercraft.ollivanders2.item.wand;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Wand wood types
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Wand_wood">Wand wood on Harry Potter Wiki</a>
 */
public enum O2WandWoodType {
    /**
     * Acacia
     */
    ACACIA(Material.ACACIA_LOG, "Acacia"),
    /**
     * Birch
     */
    BIRCH(Material.BIRCH_LOG, "Birch"),
    /**
     * Cherry
     */
    CHERRY(Material.CHERRY_LOG, "Cherry"),
    /**
     * Oak
     */
    OAK(Material.OAK_LOG, "Oak"),
    /**
     * Spruce
     */
    SPRUCE(Material.SPRUCE_LOG, "Spruce");

    private final Material material;
    private final String label;

    /**
     * Constructor
     *
     * @param material the wood material for the wand
     * @param label    the label string for the wood material
     */
    O2WandWoodType(@NotNull Material material, @NotNull String label) {
        this.material = material;
        this.label = label;
    }

    /**
     * Get the material for this type
     *
     * @return the material
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * Get the label for this type
     *
     * @return the label
     */
    @NotNull
    public String getLabel() {
        return label;
    }

    /**
     * Get the wand wood type for this material.
     *
     * @param m the material to check
     * @return the wand wood type if found, null otherwise
     */
    @Nullable
    public static O2WandWoodType getWandWoodTypeByMaterial(@NotNull Material m) {
        for (O2WandWoodType woodType : O2WandWoodType.values()) {
            if (woodType.material == m)
                return woodType;
        }

        return null;
    }

    /**
     * Get the wand wood type matching the given name.
     * <p>
     * Name comparison is case-sensitive using {@link String#equals(Object)}.
     * </p>
     *
     * @param name the name to look up
     * @return the wand wood type if found, null otherwise
     */
    @Nullable
    public static O2WandWoodType getWandWoodTypeByName(@NotNull String name) {
        for (O2WandWoodType woodType : O2WandWoodType.values()) {
            if (woodType.getLabel().equals(name)) {
                return woodType;
            }
        }

        return null;
    }

    /**
     * Get a list of all the wand wood types by name.
     *
     * @return the names of all wand woods as a list
     */
    @NotNull
    public static ArrayList<String> getAllWandWoodsByName() {
        ArrayList<String> woods = new ArrayList<>();

        for (O2WandWoodType woodType : O2WandWoodType.values())
            woods.add(woodType.getLabel());

        return woods;
    }

    /**
     * Get a random wand wood by name
     *
     * @return a random wand wood name
     */
    @NotNull
    public static String getRandomWood() {
        O2WandWoodType[] woods = O2WandWoodType.values();
        return woods[Ollivanders2Common.random.nextInt(woods.length)].getLabel();
    }

    /**
     * Get a player's destined wand wood by seed.
     *
     * @param seed the seed to determine their destined wood
     * @return the wand wood name
     */
    public static String getWandWoodBySeed(int seed) {
        O2WandWoodType[] woods = O2WandWoodType.values();
        return woods[Math.abs(seed) % woods.length].getLabel();
    }

    /**
     * Is this ItemStack a wand wood?
     *
     * @param material the ItemStack to check
     * @return true if it is a wand wood, false otherwise
     */
    public static boolean isWandWood(Material material) {
       return getWandWoodTypeByMaterial(material) != null;
    }
}

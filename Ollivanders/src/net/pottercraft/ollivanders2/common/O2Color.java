package net.pottercraft.ollivanders2.common;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Color representation for Ollivanders2 supporting multiple Minecraft color formats.
 * <p>
 * Minecraft handles colors inconsistently across different contexts - chat messages use ChatColor and format codes (§),
 * items use Color, and blocks use DyeColor. This enum provides unified access to all color representations,
 * allowing seamless conversion between formats.
 * </p>
 * <p>
 * <strong>When to use each color representation:</strong>
 * <ul>
 * <li>{@link #getBukkitColor()} - For items and potions (Color class)</li>
 * <li>{@link #getChatColor()} - For chat messages in commands and player messages (ChatColor enum)</li>
 * <li>{@link #getChatColorCode()} - For text formatting in books and signs using color codes like "§c" for red</li>
 * <li>{@link #getDyeColor()} - For dyeable blocks like wool, concrete, and glass (DyeColor enum)</li>
 * </ul>
 *
 * @author Azami7
 */
public enum O2Color {
    /**
     * aqua - Color.AQUA, ChatColor.AQUA, "§b", DyeColor.LIGHT_BLUE
     */
    AQUA(Color.AQUA, ChatColor.AQUA, "§b", DyeColor.LIGHT_BLUE),
    /**
     * black - Color.BLACK, ChatColor.BLACK, "§0", DyeColor.BLACK
     */
    BLACK(Color.BLACK, ChatColor.BLACK, "§0", DyeColor.BLACK),
    /**
     * blue - Color.BLUE, ChatColor.BLUE, "§9", DyeColor.BLUE
     */
    BLUE(Color.BLUE, ChatColor.BLUE, "§9", DyeColor.BLUE),
    /**
     * brown - Color.ORANGE, ChatColor.GOLD, "§6", DyeColor.BROWN
     */
    BROWN(Color.ORANGE, ChatColor.GOLD, "§6", DyeColor.BROWN),
    /**
     * cyan - Color.TEAL, ChatColor.DARK_AQUA, "§3", DyeColor.CYAN
     */
    CYAN(Color.TEAL, ChatColor.DARK_AQUA, "§3", DyeColor.CYAN),
    /**
     * dark blue - Color.NAVY, ChatColor.DARK_BLUE, "§1", DyeColor.BLUE
     */
    DARK_BLUE(Color.NAVY, ChatColor.DARK_BLUE, "§1", DyeColor.BLUE),
    /**
     * dark gray - Color.GRAY, ChatColor.GRAY, "§8", DyeColor.GRAY
     */
    DARK_GRAY(Color.GRAY, ChatColor.GRAY, "§8", DyeColor.GRAY),
    /**
     * dark green - Color.GREEN, ChatColor.DARK_GREEN, "§2", DyeColor.GREEN
     */
    DARK_GREEN(Color.GREEN, ChatColor.DARK_GREEN, "§2", DyeColor.GREEN),
    /**
     * dark red - Color.RED, ChatColor.RED, "§4", DyeColor.RED
     */
    DARK_RED(Color.RED, ChatColor.RED, "§4", DyeColor.RED),
    /**
     * fuschia - Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PINK
     */
    FUCHSIA(Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PINK),
    /**
     * gold - Color.YELLOW, ChatColor.GOLD, "§6", DyeColor.YELLOW
     */
    GOLD(Color.YELLOW, ChatColor.GOLD, "§6", DyeColor.YELLOW),
    /**
     * gray - Color.GRAY, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY
     */
    GRAY(Color.GRAY, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY),
    /**
     * green - Color.GREEN, ChatColor.GREEN, "§a", DyeColor.GREEN
     */
    GREEN(Color.GREEN, ChatColor.GREEN, "§a", DyeColor.GREEN),
    /**
     * light blue - Color.TEAL, ChatColor.BLUE, "§9", DyeColor.LIGHT_BLUE
     */
    LIGHT_BLUE(Color.TEAL, ChatColor.BLUE, "§9", DyeColor.LIGHT_BLUE),
    /**
     * light gray - Color.SILVER, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY
     */
    LIGHT_GRAY(Color.SILVER, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY),
    /**
     * light purple - Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PURPLE
     */
    LIGHT_PURPLE(Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PURPLE),
    /**
     * lime - Color.LIME, ChatColor.GREEN, "§a", DyeColor.LIME
     */
    LIME(Color.LIME, ChatColor.GREEN, "§a", DyeColor.LIME),
    /**
     * magenta - Color.PURPLE, ChatColor.DARK_PURPLE, "§5", DyeColor.MAGENTA
     */
    MAGENTA(Color.PURPLE, ChatColor.DARK_PURPLE, "§5", DyeColor.MAGENTA),
    /**
     * maroon - Color.MAROON, ChatColor.DARK_RED, "§4", DyeColor.RED
     */
    MAROON(Color.MAROON, ChatColor.DARK_RED, "§4", DyeColor.RED),
    /**
     * navy - Color.NAVY, ChatColor.DARK_BLUE, "§1", DyeColor.BLUE
     */
    NAVY(Color.NAVY, ChatColor.DARK_BLUE, "§1", DyeColor.BLUE),
    /**
     * olive - Color.OLIVE, ChatColor.DARK_GREEN, "§2", DyeColor.GREEN
     */
    OLIVE(Color.OLIVE, ChatColor.DARK_GREEN, "§2", DyeColor.GREEN),
    /**
     * orange - Color.ORANGE, ChatColor.GOLD, "§6", DyeColor.ORANGE
     */
    ORANGE(Color.ORANGE, ChatColor.GOLD, "§6", DyeColor.ORANGE),
    /**
     * pink - Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PINK
     */
    PINK(Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PINK),
    /**
     * purple - Color.PURPLE, ChatColor.DARK_PURPLE, "§5", DyeColor.PURPLE
     */
    PURPLE(Color.PURPLE, ChatColor.DARK_PURPLE, "§5", DyeColor.PURPLE),
    /**
     * red - Color.RED, ChatColor.RED, "§c", DyeColor.RED
     */
    RED(Color.RED, ChatColor.RED, "§c", DyeColor.RED),
    /**
     * silver - Color.SILVER, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY
     */
    SILVER(Color.SILVER, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY),
    /**
     * teal - Color.TEAL, ChatColor.DARK_AQUA, "§3", DyeColor.CYAN
     */
    TEAL(Color.TEAL, ChatColor.DARK_AQUA, "§3", DyeColor.CYAN),
    /**
     * white - Color.WHITE, ChatColor.WHITE, "§f", DyeColor.WHITE
     */
    WHITE(Color.WHITE, ChatColor.WHITE, "§f", DyeColor.WHITE),
    /**
     * yellow - Color.YELLOW, ChatColor.YELLOW, "§e", DyeColor.YELLOW
     */
    YELLOW(Color.YELLOW, ChatColor.YELLOW, "§e", DyeColor.YELLOW),
    ;

    /**
     * Primary dye colors for common dyeable blocks.
     * Used by {@link #getRandomPrimaryDyeableColor()} to select from the six most common color variants.
     * Primarily used for blocks that support standard color variants.
     */
    final static O2Color[] primaryDyeableColors = new O2Color[]{RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE};

    /**
     * All dye colors supported by Minecraft dyeable blocks.
     * Used by {@link #getRandomDyeableColor()} to select from the complete range of available dye colors.
     * Includes all standard Minecraft dye colors that can be applied to wool, concrete, glass, and similar blocks.
     */
    final static O2Color[] dyeableColors = new O2Color[]{BLACK, BLUE, BROWN, CYAN, GRAY, GREEN, LIGHT_BLUE, LIGHT_GRAY, LIME, MAGENTA, ORANGE, PINK, PURPLE, RED, WHITE, YELLOW};

    /**
     * Bukkit-defined colors representing standard Minecraft colors.
     * Used by {@link #getBukkitColorByNumber(int)} to convert numeric color codes (0-15) to their corresponding O2Color.
     * These 16 colors match the legacy Minecraft color palette (corresponds to the old chat color codes).
     * Defaults to WHITE if a number outside the valid range is provided.
     */
    final static O2Color[] bukkitColors = new O2Color[]{AQUA, BLACK, BLUE, FUCHSIA, GRAY, GREEN, LIME, MAROON, NAVY, OLIVE, ORANGE, PURPLE, RED, SILVER, TEAL, WHITE};

    /**
     * Constructor
     *
     * @param color    the Color for this type
     * @param chat     the chat color for this type
     * @param chatCode the chat code to make this type
     * @param dye      the dye for this type
     */
    O2Color(@NotNull Color color, @NotNull ChatColor chat, @NotNull String chatCode, @NotNull DyeColor dye) {
        bukkitColor = color;
        chatColor = chat;
        chatColorCode = chatCode;
        dyeColor = dye;
    }

    /**
     * The Bukkit Color object for this color.
     * Used for item coloring (e.g., potion colors). Access via {@link #getBukkitColor()}.
     */
    final Color bukkitColor;

    /**
     * The ChatColor enum value for this color.
     * Used for chat messages and command outputs. Access via {@link #getChatColor()}.
     */
    final ChatColor chatColor;

    /**
     * The chat format code for this color (e.g., "§c" for red).
     * Used for text formatting in books, signs, and other text-based UI elements.
     * Format is the section symbol (§) followed by a hexadecimal character. Access via {@link #getChatColorCode()}.
     */
    final String chatColorCode;

    /**
     * The DyeColor enum value for this color.
     * Used for dyeable blocks like wool, concrete, glass, and banners. Access via {@link #getDyeColor()}.
     */
    final DyeColor dyeColor;

    /**
     * Get the Bukkit Color object for this color.
     * Used for coloring items such as potions, leather armor, and other colorable items.
     *
     * @return the Bukkit Color object
     */
    @NotNull
    public Color getBukkitColor() {
        return bukkitColor;
    }

    /**
     * Get the ChatColor enum value for this color.
     * Used for chat messages, command messages, and other player-facing text.
     *
     * @return the ChatColor enum value
     */
    @NotNull
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * Get the chat format code for this color.
     * Format is the section symbol (§) followed by a hexadecimal character (e.g., "§c" for red).
     * Used for formatting text in books, signs, and other text-based UI elements.
     *
     * @return the chat color code as a string
     */
    @NotNull
    public String getChatColorCode() {
        return chatColorCode;
    }

    /**
     * Get the DyeColor enum value for this color.
     * Used for dyeable blocks such as wool, concrete, glass, banners, candles, and shulker boxes.
     *
     * @return the DyeColor enum value
     */
    @NotNull
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    /**
     * Get the colored material variant for this color with the given base material name.
     * <p>
     * Minecraft colorable materials follow the naming convention: {@code COLOR_MATERIAL}
     * (e.g., "RED_WOOL", "BLUE_CONCRETE", "WHITE_GLASS"). This method constructs the full material
     * name by combining the DyeColor prefix with the provided base name.
     * </p>
     *
     * @param materialBaseName the base material name without color prefix (e.g., "WOOL", "CONCRETE", "GLASS")
     * @return the colored Material (e.g., Material.RED_WOOL), or null if the colored variant doesn't exist
     */
    @Nullable
    public Material getColoredMaterial(String materialBaseName) {
        // Concatenate the DyeColor enum with the base material name. DyeColor.toString() returns the enum name
        // (e.g., "RED", "BLUE") which creates the full material name (e.g., "RED_WOOL", "BLUE_CONCRETE").
        String materialName = dyeColor + "_" + materialBaseName;

        try {
            return Material.valueOf(materialName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the Bukkit color associated with a numeric color code.
     * <p>
     * Converts numeric color codes (0-15) to their corresponding O2Color from the legacy Minecraft color palette.
     * This is useful for working with older data formats or system that use numeric color indices.
     * </p>
     *
     * @param number a number between 0-15 corresponding to the legacy Minecraft color palette
     * @return the O2Color at the specified index, or WHITE if the number is out of range (negative or >= 16)
     */
    @NotNull
    public static O2Color getBukkitColorByNumber(int number) {
        if (number >= bukkitColors.length || number < 0) {
            return WHITE;
        }
        else {
            return bukkitColors[number];
        }
    }

    /**
     * Get a random primary dyeable color.
     * <p>
     * Returns one of the six primary dye colors: red, orange, yellow, green, blue, or purple.
     * Uses a random seed from {@link Ollivanders2Common#random}.
     * </p>
     *
     * @return a random O2Color from the primary dyeable colors array
     * @see #getRandomPrimaryDyeableColor(int)
     */
    @NotNull
    public static O2Color getRandomPrimaryDyeableColor() {
        int seed = Math.abs(Ollivanders2Common.random.nextInt());

        return getRandomPrimaryDyeableColor(seed);
    }

    /**
     * Get a primary dyeable color based on the provided seed.
     * <p>
     * Returns one of the six primary dye colors: red, orange, yellow, green, blue, or purple.
     * The seed is used with modulo arithmetic to select from the available colors.
     * </p>
     *
     * @param seed the base value to determine which color is selected (using modulo)
     * @return a primary dyeable color from the seed value
     */
    @NotNull
    public static O2Color getRandomPrimaryDyeableColor(int seed) {
        // Modulo 6 distributes the seed value evenly across the 6 primary colors (0-5)
        int rand = Math.abs(seed) % primaryDyeableColors.length;

        return primaryDyeableColors[rand];
    }

    /**
     * Get a random dyeable color.
     * <p>
     * Returns one of the 16 available dye colors that can be applied to dyeable blocks.
     * Uses a random seed from {@link Ollivanders2Common#random}.
     * </p>
     *
     * @return a random O2Color from the complete dyeable colors array
     * @see #getRandomDyeableColor(int)
     */
    @NotNull
    public static O2Color getRandomDyeableColor() {
        int seed = Math.abs(Ollivanders2Common.random.nextInt());

        return getRandomDyeableColor(seed);
    }

    /**
     * Get a dyeable color based on the provided seed.
     * <p>
     * Returns one of the 16 available dye colors that can be applied to dyeable blocks like wool, concrete, and glass.
     * The seed is used with modulo arithmetic to select from the available colors.
     * </p>
     *
     * @param seed the base value to determine which color is selected (using modulo)
     * @return a dyeable color from the seed value
     */
    @NotNull
    public static O2Color getRandomDyeableColor(int seed) {
        // Modulo 16 distributes the seed value evenly across the 16 available dye colors (0-15)
        int rand = Math.abs(seed) % dyeableColors.length;

        return dyeableColors[rand];
    }

    /**
     * Check whether a material can be dyed to different colors.
     * <p>
     * Colorable materials follow the naming pattern {@code COLOR_BASE} (e.g., "RED_WOOL", "WHITE_CONCRETE").
     * This method checks if the material name ends with one of the known dyeable base material suffixes.
     * </p>
     *
     * @param material the Material to check for colorability
     * @return true if the material is colorable (wool, carpet, concrete, glass, bed, candle, banner, shulker box),
     * false otherwise
     */
    public static boolean isColorable(@NotNull Material material) {
        // determine if a material is colorable
        String materialName = material.toString();

        if (materialName.endsWith("_WOOL") || materialName.endsWith("_CARPET") || materialName.endsWith("_CONCRETE")
                || materialName.endsWith("_CONCRETE_POWDER") || materialName.endsWith("_SHULKER_BOX")
                || materialName.endsWith("_STAINED_GLASS") || materialName.endsWith("_STAINED_GLASS_PANE")
                || materialName.endsWith("_BED") || materialName.endsWith("_CANDLE")
                || materialName.endsWith("_BANNER")) {
            return true;
        }

        return false;
    }

    /**
     * Change a colorable material to a new color.
     * <p>
     * Converts the material to its color variant using the provided O2Color. For example, converting
     * WOOL with color RED produces RED_WOOL. The material name pattern is {@code COLOR_BASE}.
     * </p>
     *
     * @param material the colorable Material to change the color of (e.g., WHITE_WOOL, BLUE_CONCRETE)
     * @param color    the O2Color to change the material to
     * @return the new colored Material (e.g., RED_WOOL), or the original material if:
     * - the material is not colorable, or
     * - the colored variant does not exist in Minecraft
     */
    public static Material changeColor(@NotNull Material material, @NotNull O2Color color) {
        String materialName = material.toString();

        // is this material colorable?
        if (!isColorable(material))
            return material;

        // get the base material where name pattern is COLOR_MATERIAL, ex. WHITE_WOOL
        // Use split with limit of 2 to only split at the FIRST underscore. This handles materials like
        // STAINED_GLASS_PANE correctly: splits into ["WHITE", "STAINED_GLASS_PANE"], not ["WHITE", "STAINED", "GLASS_PANE"]
        String[] materialNameParts = materialName.split("_", 2);
        if (materialNameParts.length != 2)
            return material;

        // get the new material color
        String materialBase = materialNameParts[1];
        Material newMaterialColor = color.getColoredMaterial(materialBase);

        // return back the original if we failed to get this color for the material
        if (newMaterialColor == null)
            return material;

        return newMaterialColor;
    }
}

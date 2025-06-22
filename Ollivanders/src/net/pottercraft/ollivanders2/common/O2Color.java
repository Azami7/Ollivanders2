package net.pottercraft.ollivanders2.common;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ollivanders2 colors - because MC doesn't handle colors consistently
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
     * Primary dye colors
     */
    final static O2Color[] primaryDyeableColors = new O2Color[]{RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE};

    /**
     * All dye colors
     */
    final static O2Color[] dyeableColors = new O2Color[]{BLACK, BLUE, BROWN, CYAN, GRAY, GREEN, LIGHT_BLUE, LIGHT_GRAY, LIME, MAGENTA, ORANGE, PINK, PURPLE, RED, WHITE, YELLOW};

    /**
     * Bukkit color names
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
     * The bukkit color name for this color type
     */
    final Color bukkitColor;

    /**
     * The chat color for this color type
     */
    final ChatColor chatColor;

    /**
     * The color code for this color type
     */
    final String chatColorCode;

    /**
     * The dye color for this color type
     */
    final DyeColor dyeColor;

    /**
     * Get the Bukkit Color type for this color
     * @return the Color for this type
     */
    @NotNull
    public Color getBukkitColor() {
        return bukkitColor;
    }

    /**
     * Get the ChatColor for this color
     *
     * @return the ChatColor for this type
     */
    @NotNull
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * Get the chat string code for this color
     *
     * @return the chat code for this type
     */
    @NotNull
    public String getChatColorCode() {
        return chatColorCode;
    }

    /**
     * get the DyeColor for this color
     *
     * @return the dye color for this type
     */
    @NotNull
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    /**
     * Get the colored material for this base material for this type
     *
     * @param materialBaseName the base material
     * @return the colored material type or null if not found
     */
    @Nullable
    public Material getColoredMaterial(String materialBaseName) {
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
     * Get the bukkit color associated with a number 0-15
     *
     * @param number a number between 0-15, numbers outside of this range will be set to WHITE
     * @return the bukkit color associated with this number
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
     * Get a random primary dyeable color - red, orange, yellow, green, blue, purple
     *
     * @return the dyeable color
     */
    @NotNull
    public static O2Color getRandomPrimaryDyeableColor() {
        int seed = Math.abs(Ollivanders2Common.random.nextInt());

        return getRandomPrimaryDyeableColor(seed);
    }

    /**
     * Get a random primary dyeable color - red, orange, yellow, green, blue, purple
     *
     * @param seed the base value that the percentile check will use
     * @return the dyeable color
     */
    @NotNull
    public static O2Color getRandomPrimaryDyeableColor(int seed) {
        int rand = Math.abs(seed) % primaryDyeableColors.length;

        return primaryDyeableColors[rand];
    }

    /**
     * Get a random dyeable color
     *
     * @return the dyeable color
     */
    @NotNull
    public static O2Color getRandomDyeableColor() {
        int seed = Math.abs(Ollivanders2Common.random.nextInt());

        return getRandomDyeableColor(seed);
    }

    /**
     * Get a random dyeable color
     *
     * @param seed the base value that the percentile check will use
     * @return the dyeable color
     */
    @NotNull
    public static O2Color getRandomDyeableColor(int seed) {
        int rand = Math.abs(seed) % dyeableColors.length;

        return dyeableColors[rand];
    }

    /**
     * Return true if a material is colorable, false if it is not.
     *
     * @param material the material to check
     * @return true if it is colorable, false if it is not
     */
    public static boolean isColorable(@NotNull Material material) {
        // determine if a material is colorable
        String materialName = material.toString();

        if (materialName.endsWith("_WOOL") || materialName.endsWith("_CARPET") || materialName.endsWith("_CONCRETE")
                || materialName.endsWith("_CONCRETE_POWDER") || materialName.endsWith("_SHULKER_BOX")
                || materialName.endsWith("_STAINED_GLASS") || materialName.endsWith("_STAINED_GLASS_PANE")
                || materialName.endsWith("_BED") || materialName.endsWith("_CANDLE")
                || materialName.endsWith("_BANNER")){
            return true;
        }

        return false;
    }

    /**
     * Change the color of a colorable material.
     *
     * @param material the material to change color of
     * @param color    the color to change the material to
     * @return a material that is the new color or the original material if the material was not colorable
     */
    public static Material changeColor(@NotNull Material material, @NotNull O2Color color) {
        String materialName = material.toString();

        // is this material colorable?
        if (!isColorable(material))
            return material;

        // get the base material where name pattern is COLOR_MATERIAL, ex. WHITE_WOOL
        String [] materialNameParts = materialName.split("_", 2);
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

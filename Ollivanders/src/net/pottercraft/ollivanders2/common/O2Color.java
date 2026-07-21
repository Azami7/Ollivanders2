package net.pottercraft.ollivanders2.common;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A single color mapped to each of Minecraft's inconsistent color representations, so callers can convert freely
 * between them: {@link Color} for items and potions, {@link ChatColor} for messages, a "§"-code string for book and
 * sign text, and {@link DyeColor} for dyeable blocks.
 *
 * @author Azami7
 */
public enum O2Color {
    AQUA(Color.AQUA, ChatColor.AQUA, "§b", DyeColor.LIGHT_BLUE),
    BLACK(Color.BLACK, ChatColor.BLACK, "§0", DyeColor.BLACK),
    BLUE(Color.BLUE, ChatColor.BLUE, "§9", DyeColor.BLUE),
    BROWN(Color.ORANGE, ChatColor.GOLD, "§6", DyeColor.BROWN),
    CYAN(Color.TEAL, ChatColor.DARK_AQUA, "§3", DyeColor.CYAN),
    DARK_BLUE(Color.NAVY, ChatColor.DARK_BLUE, "§1", DyeColor.BLUE),
    DARK_GRAY(Color.GRAY, ChatColor.GRAY, "§8", DyeColor.GRAY),
    DARK_GREEN(Color.GREEN, ChatColor.DARK_GREEN, "§2", DyeColor.GREEN),
    DARK_RED(Color.RED, ChatColor.RED, "§4", DyeColor.RED),
    FUCHSIA(Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PINK),
    GOLD(Color.YELLOW, ChatColor.GOLD, "§6", DyeColor.YELLOW),
    GRAY(Color.GRAY, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY),
    GREEN(Color.GREEN, ChatColor.GREEN, "§a", DyeColor.GREEN),
    LIGHT_BLUE(Color.TEAL, ChatColor.BLUE, "§9", DyeColor.LIGHT_BLUE),
    LIGHT_GRAY(Color.SILVER, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY),
    LIGHT_PURPLE(Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PURPLE),
    LIME(Color.LIME, ChatColor.GREEN, "§a", DyeColor.LIME),
    MAGENTA(Color.PURPLE, ChatColor.DARK_PURPLE, "§5", DyeColor.MAGENTA),
    MAROON(Color.MAROON, ChatColor.DARK_RED, "§4", DyeColor.RED),
    NAVY(Color.NAVY, ChatColor.DARK_BLUE, "§1", DyeColor.BLUE),
    OLIVE(Color.OLIVE, ChatColor.DARK_GREEN, "§2", DyeColor.GREEN),
    ORANGE(Color.ORANGE, ChatColor.GOLD, "§6", DyeColor.ORANGE),
    PINK(Color.FUCHSIA, ChatColor.LIGHT_PURPLE, "§d", DyeColor.PINK),
    PURPLE(Color.PURPLE, ChatColor.DARK_PURPLE, "§5", DyeColor.PURPLE),
    RED(Color.RED, ChatColor.RED, "§c", DyeColor.RED),
    SILVER(Color.SILVER, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY),
    TEAL(Color.TEAL, ChatColor.DARK_AQUA, "§3", DyeColor.CYAN),
    WHITE(Color.WHITE, ChatColor.WHITE, "§f", DyeColor.WHITE),
    YELLOW(Color.YELLOW, ChatColor.YELLOW, "§e", DyeColor.YELLOW),
    ;

    /**
     * The six colors {@link #getRandomPrimaryDyeableColor()} draws from.
     */
    final static O2Color[] primaryDyeableColors = new O2Color[]{RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE};

    /**
     * The sixteen dyeable-block colors {@link #getRandomDyeableColor()} draws from.
     */
    final static O2Color[] dyeableColors = new O2Color[]{BLACK, BLUE, BROWN, CYAN, GRAY, GREEN, LIGHT_BLUE, LIGHT_GRAY, LIME, MAGENTA, ORANGE, PINK, PURPLE, RED, WHITE, YELLOW};

    /**
     * The legacy 16-color Minecraft palette, indexed by the old numeric color codes; see
     * {@link #getBukkitColorByNumber(int)}.
     */
    final static O2Color[] bukkitColors = new O2Color[]{AQUA, BLACK, BLUE, FUCHSIA, GRAY, GREEN, LIME, MAROON, NAVY, OLIVE, ORANGE, PURPLE, RED, SILVER, TEAL, WHITE};

    /**
     * @param color    the item/potion color
     * @param chat     the chat color
     * @param chatCode the "§"-code chat format string
     * @param dye      the dyeable-block color
     */
    O2Color(@NotNull Color color, @NotNull ChatColor chat, @NotNull String chatCode, @NotNull DyeColor dye) {
        bukkitColor = color;
        chatColor = chat;
        chatColorCode = chatCode;
        dyeColor = dye;
    }

    final Color bukkitColor;

    final ChatColor chatColor;

    /**
     * The "§"-code chat format string for this color (section symbol plus a hex character, e.g. "§c").
     */
    final String chatColorCode;

    final DyeColor dyeColor;

    /**
     * Get the Bukkit color, for coloring items such as potions and leather armor.
     *
     * @return the Bukkit Color
     */
    @NotNull
    public Color getBukkitColor() {
        return bukkitColor;
    }

    /**
     * Get the chat color, for player-facing messages.
     *
     * @return the ChatColor
     */
    @NotNull
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * Get the "§"-code chat format string (e.g. "§c"), for text in books and signs.
     *
     * @return the chat color code
     */
    @NotNull
    public String getChatColorCode() {
        return chatColorCode;
    }

    /**
     * Get the dye color, for dyeable blocks such as wool, concrete, glass, and banners.
     *
     * @return the DyeColor
     */
    @NotNull
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    /**
     * Get the colored variant of a base material in this color, following Minecraft's {@code COLOR_BASE} naming
     * (e.g. this color's dye prefix + "WOOL" -&gt; "RED_WOOL").
     *
     * @param materialBaseName the base material name without a color prefix (e.g. "WOOL", "CONCRETE", "GLASS")
     * @return the colored Material, or null if no such variant exists
     */
    @Nullable
    public Material getColoredMaterial(String materialBaseName) {
        String materialName = dyeColor + "_" + materialBaseName;

        // a missing variant is an expected outcome (see the null contract), so swallow it rather than logging
        try {
            return Material.valueOf(materialName);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Map a legacy numeric color code to its color.
     *
     * @param number an index into the legacy 16-color palette
     * @return the matching color, or WHITE if the number is out of range (negative or &gt;= 16)
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
     * Get a random primary dyeable color (red, orange, yellow, green, blue, or purple).
     *
     * @return a random primary dyeable color
     * @see #getRandomPrimaryDyeableColor(int)
     */
    @NotNull
    public static O2Color getRandomPrimaryDyeableColor() {
        int seed = Math.abs(Ollivanders2Common.random.nextInt());

        return getRandomPrimaryDyeableColor(seed);
    }

    /**
     * Select a primary dyeable color from a seed, so callers can reproduce a choice from a stored value.
     *
     * @param seed any int; the color is chosen by its magnitude modulo the number of primary colors
     * @return the selected primary dyeable color
     */
    @NotNull
    public static O2Color getRandomPrimaryDyeableColor(int seed) {
        int rand = Math.abs(seed) % primaryDyeableColors.length;

        return primaryDyeableColors[rand];
    }

    /**
     * Get a random dyeable-block color from the full set of 16.
     *
     * @return a random dyeable color
     * @see #getRandomDyeableColor(int)
     */
    @NotNull
    public static O2Color getRandomDyeableColor() {
        int seed = Math.abs(Ollivanders2Common.random.nextInt());

        return getRandomDyeableColor(seed);
    }

    /**
     * Select a dyeable-block color from a seed, so callers can reproduce a choice from a stored value.
     *
     * @param seed any int; the color is chosen by its magnitude modulo the number of dyeable colors
     * @return the selected dyeable color
     */
    @NotNull
    public static O2Color getRandomDyeableColor(int seed) {
        int rand = Math.abs(seed) % dyeableColors.length;

        return dyeableColors[rand];
    }

    /**
     * Check whether a material has color variants (wool, carpet, concrete, glass, bed, candle, banner, shulker box,
     * terracotta, and similar), determined by matching known dyeable base-material name suffixes.
     *
     * @param material the material to check
     * @return true if the material is colorable
     */
    public static boolean isColorable(@NotNull Material material) {
        String materialName = material.toString();

        return (materialName.endsWith("_BANNER") || materialName.endsWith("_BED") || materialName.endsWith("_BUNDLE")
                || materialName.equals("BUNDLE") || materialName.endsWith("_CANDLE") || materialName.equals("CANDLE")
                || materialName.endsWith("_CANDLE_CAKE") || materialName.equals("CANDLE_CAKE")
                || materialName.endsWith("_CARPET") || materialName.endsWith("_CONCRETE")
                || materialName.endsWith("_CONCRETE_POWDER") || materialName.equals("GLASS")
                || materialName.equals("GLASS_PANE") || materialName.endsWith("_SHULKER_BOX")
                || materialName.equals("SHULKER_BOX") || materialName.endsWith("_STAINED_GLASS")
                || materialName.endsWith("_STAINED_GLASS_PANE") || materialName.endsWith("_TERRACOTTA")
                || materialName.equals("TERRACOTTA") || materialName.endsWith("_WOOL"));
    }

    /**
     * Recolor a material to the given color (e.g. WHITE_WOOL with RED becomes RED_WOOL). Returns the material
     * unchanged if it is not colorable or the target color has no such variant in Minecraft.
     *
     * @param material the material to recolor
     * @param color    the target color
     * @return the recolored material, or the original if it could not be recolored
     */
    public static Material changeColor(@NotNull Material material, @NotNull O2Color color) {
        String materialName = material.toString();

        if (!isColorable(material))
            return material;

        // uncolored materials whose names contain an underscore need the base spelled out here; otherwise the
        // first-underscore split below would mis-parse them (e.g. GLASS_PANE -> "GLASS"/"PANE", losing STAINED_GLASS_PANE)
        String materialBase;
        if (materialName.equals("GLASS"))
            materialBase = "STAINED_GLASS";
        else if (materialName.equals("GLASS_PANE"))
            materialBase = "STAINED_GLASS_PANE";
        else if (materialName.equals("SHULKER_BOX") || materialName.equals("CANDLE_CAKE"))
            materialBase = materialName;
        else if (materialName.contains("_")) {
            // split on the first underscore only, so the color prefix drops off but a multi-word base survives
            // intact (WHITE_STAINED_GLASS_PANE -> "STAINED_GLASS_PANE", not "STAINED")
            String[] materialNameParts = materialName.split("_", 2);
            if (materialNameParts.length != 2)
                return material;

            materialBase = materialNameParts[1];
        }
        else {
            materialBase = materialName;
        }

        Material newMaterialColor = color.getColoredMaterial(materialBase);

        if (newMaterialColor == null)
            return material;

        return newMaterialColor;
    }
}

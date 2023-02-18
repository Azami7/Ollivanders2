package net.pottercraft.ollivanders2;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Ollivanders2 colors - because MC doesn't handle colors consistently
 */
public enum O2Color {
	AQUA(Color.AQUA, ChatColor.AQUA, "§b", DyeColor.LIGHT_BLUE),
	BLACK(Color.BLACK, ChatColor.BLACK, "§0", DyeColor.BLACK), BLUE(Color.BLUE, ChatColor.BLUE, "§9", DyeColor.BLUE),
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
	RED(Color.RED, ChatColor.RED, "§c", DyeColor.RED), SILVER(Color.SILVER, ChatColor.GRAY, "§7", DyeColor.LIGHT_GRAY),
	TEAL(Color.TEAL, ChatColor.DARK_AQUA, "§3", DyeColor.CYAN),
	WHITE(Color.WHITE, ChatColor.WHITE, "§f", DyeColor.WHITE),
	YELLOW(Color.YELLOW, ChatColor.YELLOW, "§e", DyeColor.YELLOW),;

	final static O2Color[] primaryDyeableColors = new O2Color[] { RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE };
	final static O2Color[] dyeableColors = new O2Color[] { BLACK, BLUE, BROWN, CYAN, GRAY, GREEN, LIGHT_BLUE,
			LIGHT_GRAY, LIME, MAGENTA, ORANGE, PINK, PURPLE, RED, WHITE, YELLOW };
	final static O2Color[] bukkitColors = new O2Color[] { AQUA, BLACK, BLUE, FUCHSIA, GRAY, GREEN, LIME, MAROON, NAVY,
			OLIVE, ORANGE, PURPLE, RED, SILVER, TEAL, WHITE };

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

	final Color bukkitColor;
	final ChatColor chatColor;
	final String chatColorCode;
	final DyeColor dyeColor;

	/**
	 * @return the Color for this type
	 */
	@NotNull
	public Color getBukkitColor() {
		return bukkitColor;
	}

	/**
	 * @return the ChatColor for this type
	 */
	@NotNull
	public ChatColor getChatColor() {
		return chatColor;
	}

	/**
	 * @return the chat code for this type
	 */
	@NotNull
	public String getChatColorCode() {
		return chatColorCode;
	}

	/**
	 * @return the dye color for this type
	 */
	@NotNull
	public DyeColor getDyeColor() {
		return dyeColor;
	}

	/**
	 * @return the wool material for this type
	 */
	@NotNull
	public Material getWoolMaterial() {
		Material material = getColoredMaterial("WOOL");

		if (material == null) {
			material = Material.WHITE_WOOL;
		}

		return material;
	}

	/**
	 * @return the carpet material for this type
	 */
	@NotNull
	public Material getCarpetMaterial() {
		Material material = getColoredMaterial("CARPET");

		if (material == null) {
			material = Material.WHITE_CARPET;
		}

		return material;
	}

	/**
	 * @return the concrete material for this type
	 */
	@NotNull
	public Material getConcreteMaterial() {
		Material material = getColoredMaterial("CONCRETE");

		if (material == null) {
			material = Material.WHITE_CONCRETE;
		}

		return material;
	}

	/**
	 * @return the concrete powder material for this type
	 */
	@NotNull
	public Material getConcretePowderMaterial() {
		Material material = getColoredMaterial("CONCRETE_POWDER");

		if (material == null) {
			material = Material.WHITE_CONCRETE_POWDER;
		}

		return material;
	}

	/**
	 * @return the shulker box for this type
	 */
	@NotNull
	public Material getShulkerBoxMaterial() {
		Material material = getColoredMaterial("SHULKER_BOX");

		if (material == null) {
			material = Material.WHITE_SHULKER_BOX;
		}

		return material;
	}

	/**
	 * @return the stained glass for this type
	 */
	@NotNull
	public Material getStainedGlassMaterial() {
		Material material = getColoredMaterial("STAINED_GLASS");

		if (material == null) {
			material = Material.WHITE_STAINED_GLASS;
		}

		return material;
	}

	/**
	 * Get the colored material for this base material for this type
	 *
	 * @param materialBaseName the base material
	 * @return the colored material type or null if not found
	 */
	@Nullable
	public Material getColoredMaterial(String materialBaseName) {
		String materialName = dyeColor.toString() + "_" + materialBaseName;

		try {
			return Material.valueOf(materialName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get the bukkit color associated with a number 0-15
	 *
	 * @param number a number between 0-15, numbers outside of this range will be
	 *               set to WHITE
	 * @return the bukkit color associated with this number
	 */
	@NotNull
	public static O2Color getBukkitColorByNumber(int number) {
		if (number >= bukkitColors.length || number < 0) {
			return WHITE;
		} else {
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
				|| materialName.endsWith("_STAINED_GLASS")) {
			return true;
		}

		return false;
	}

	/**
	 * Change the color of a colorable material.
	 *
	 * @param material the material to change color of
	 * @param color    the color to change the material to
	 * @return a material that is the new color or the original material if the
	 *         material was not colorable
	 */
	public static Material changeColor(@NotNull Material material, @NotNull O2Color color) {
		String materialName = material.toString();
		Material newColor = material;

		if (materialName.endsWith("_WOOL")) {
			newColor = color.getWoolMaterial();
		} else if (materialName.endsWith("_CARPET")) {
			newColor = color.getCarpetMaterial();
		} else if (materialName.endsWith("_CONCRETE")) {
			newColor = color.getConcreteMaterial();
		} else if (materialName.endsWith("_CONCRETE_POWDER")) {
			newColor = color.getConcretePowderMaterial();
		} else if (materialName.endsWith("_SHULKER_BOX")) {
			newColor = color.getShulkerBoxMaterial();
		} else if (materialName.endsWith("_STAINED_GLASS")) {
			newColor = color.getStainedGlassMaterial();
		}

		return newColor;
	}
}

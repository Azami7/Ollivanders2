package net.pottercraft.ollivanders2.test.common;

import net.pottercraft.ollivanders2.common.O2Color;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for O2Color enum.
 *
 * Tests the various color conversion methods and utilities provided by the O2Color enum,
 * including Bukkit color, chat color, dye color conversions, material color operations,
 * and random color generation.
 */
public class O2ColorTest {

    /**
     * Test that getBukkitColor() returns the correct Bukkit Color for each O2Color enum value.
     */
    @Test
    void getBukkitColorTest() {
        assertEquals(Color.RED, O2Color.RED.getBukkitColor());
        assertEquals(Color.BLUE, O2Color.BLUE.getBukkitColor());
        assertEquals(Color.WHITE, O2Color.WHITE.getBukkitColor());
        assertEquals(Color.BLACK, O2Color.BLACK.getBukkitColor());
    }

    /**
     * Test that getChatColor() returns the correct ChatColor for each O2Color enum value.
     */
    @Test
    void getChatColorTest() {
        assertEquals(ChatColor.RED, O2Color.RED.getChatColor());
        assertEquals(ChatColor.BLUE, O2Color.BLUE.getChatColor());
        assertEquals(ChatColor.WHITE, O2Color.WHITE.getChatColor());
        assertEquals(ChatColor.BLACK, O2Color.BLACK.getChatColor());
    }

    /**
     * Test that getChatColorCode() returns the correct Minecraft color code string for each O2Color enum value.
     */
    @Test
    void getChatColorCodeTest() {
        assertEquals("§c", O2Color.RED.getChatColorCode());
        assertEquals("§9", O2Color.BLUE.getChatColorCode());
        assertEquals("§f", O2Color.WHITE.getChatColorCode());
        assertEquals("§0", O2Color.BLACK.getChatColorCode());
    }

    /**
     * Test that getDyeColor() returns the correct DyeColor for each O2Color enum value.
     */
    @Test
    void getDyeColorTest() {
        assertEquals(DyeColor.RED, O2Color.RED.getDyeColor());
        assertEquals(DyeColor.BLUE, O2Color.BLUE.getDyeColor());
        assertEquals(DyeColor.WHITE, O2Color.WHITE.getDyeColor());
        assertEquals(DyeColor.BLACK, O2Color.BLACK.getDyeColor());
    }

    /**
     * Test that getColoredMaterial() creates the correct colored material from a base material name.
     * Tests wool, carpet, and concrete materials.
     */
    @Test
    void getColoredMaterialTest() {
        Material wool = O2Color.RED.getColoredMaterial("WOOL");
        assertEquals(Material.RED_WOOL, wool);

        Material carpet = O2Color.BLUE.getColoredMaterial("CARPET");
        assertEquals(Material.BLUE_CARPET, carpet);

        Material concrete = O2Color.WHITE.getColoredMaterial("CONCRETE");
        assertEquals(Material.WHITE_CONCRETE, concrete);
    }

    /**
     * Test that getColoredMaterial() returns null when given an invalid material base name.
     */
    @Test
    void getColoredMaterialInvalidTest() {
        Material invalid = O2Color.RED.getColoredMaterial("INVALID_MATERIAL");
        assertNull(invalid, "getColoredMaterial should return null for invalid material base name");
    }

    /**
     * Test that getBukkitColorByNumber() returns valid colors for numbers in the range 0-15.
     */
    @Test
    void getBukkitColorByNumberTest() {
        // Test valid numbers
        O2Color color0 = O2Color.getBukkitColorByNumber(0);
        assertNotNull(color0);

        O2Color color5 = O2Color.getBukkitColorByNumber(5);
        assertNotNull(color5);

        O2Color color15 = O2Color.getBukkitColorByNumber(15);
        assertNotNull(color15);
    }

    /**
     * Test that getBukkitColorByNumber() returns WHITE for out-of-range numbers.
     */
    @Test
    void getBukkitColorByNumberOutOfRangeTest() {
        // Test out of range numbers
        assertEquals(O2Color.WHITE, O2Color.getBukkitColorByNumber(-1));
        assertEquals(O2Color.WHITE, O2Color.getBukkitColorByNumber(16));
        assertEquals(O2Color.WHITE, O2Color.getBukkitColorByNumber(100));
        assertEquals(O2Color.WHITE, O2Color.getBukkitColorByNumber(-100));
    }

    /**
     * Test that getRandomPrimaryDyeableColor() returns variety when called multiple times.
     */
    @Test
    void getRandomPrimaryDyeableColorTest() {
        Set<O2Color> colors = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            O2Color color = O2Color.getRandomPrimaryDyeableColor();
            assertNotNull(color, "getRandomPrimaryDyeableColor should not return null");
            colors.add(color);
        }

        assertTrue(colors.size() > 1, "Expected variety in random primary dyeable colors");
    }

    /**
     * Test that getRandomPrimaryDyeableColor() with the same seed returns the same color consistently,
     * and that different seeds produce variety.
     */
    @Test
    void getRandomPrimaryDyeableColorWithSeedTest() {
        // Same seed should produce same color
        O2Color color1 = O2Color.getRandomPrimaryDyeableColor(42);
        O2Color color2 = O2Color.getRandomPrimaryDyeableColor(42);
        assertSame(color1, color2, "Same seed should produce same color");

        // Different seeds should potentially produce different colors
        Set<O2Color> colors = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            O2Color color = O2Color.getRandomPrimaryDyeableColor(i);
            assertNotNull(color, "getRandomPrimaryDyeableColor should not return null");
            colors.add(color);
        }

        assertTrue(colors.size() > 1, "Expected variety in colors with different seeds");
    }

    /**
     * Test that getRandomDyeableColor() returns variety when called multiple times.
     */
    @Test
    void getRandomDyeableColorTest() {
        Set<O2Color> colors = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            O2Color color = O2Color.getRandomDyeableColor();
            assertNotNull(color, "getRandomDyeableColor should not return null");
            colors.add(color);
        }

        assertTrue(colors.size() > 1, "Expected variety in random dyeable colors");
    }

    /**
     * Test that getRandomDyeableColor() with the same seed returns the same color consistently,
     * and that different seeds produce variety.
     */
    @Test
    void getRandomDyeableColorWithSeedTest() {
        // Same seed should produce same color
        O2Color color1 = O2Color.getRandomDyeableColor(123);
        O2Color color2 = O2Color.getRandomDyeableColor(123);
        assertSame(color1, color2, "Same seed should produce same color");

        // Different seeds should potentially produce different colors
        Set<O2Color> colors = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            O2Color color = O2Color.getRandomDyeableColor(i);
            assertNotNull(color, "getRandomDyeableColor should not return null");
            colors.add(color);
        }

        assertTrue(colors.size() > 1, "Expected variety in colors with different seeds");
    }

    /**
     * Test that isColorable() correctly identifies wool materials as colorable.
     */
    @Test
    void isColorableWoolTest() {
        assertTrue(O2Color.isColorable(Material.RED_WOOL));
        assertTrue(O2Color.isColorable(Material.BLUE_WOOL));
        assertTrue(O2Color.isColorable(Material.WHITE_WOOL));
    }

    /**
     * Test that isColorable() correctly identifies carpet materials as colorable.
     */
    @Test
    void isColorableCarpetTest() {
        assertTrue(O2Color.isColorable(Material.RED_CARPET));
        assertTrue(O2Color.isColorable(Material.GREEN_CARPET));
    }

    /**
     * Test that isColorable() correctly identifies concrete and concrete powder materials as colorable.
     */
    @Test
    void isColorableConcreteTest() {
        assertTrue(O2Color.isColorable(Material.BLUE_CONCRETE));
        assertTrue(O2Color.isColorable(Material.YELLOW_CONCRETE_POWDER));
    }

    /**
     * Test that isColorable() correctly identifies shulker box materials as colorable.
     */
    @Test
    void isColorableShulkerBoxTest() {
        assertTrue(O2Color.isColorable(Material.RED_SHULKER_BOX));
        assertTrue(O2Color.isColorable(Material.BLUE_SHULKER_BOX));
    }

    /**
     * Test that isColorable() correctly identifies stained glass and stained glass pane materials as colorable.
     */
    @Test
    void isColorableGlassTest() {
        assertTrue(O2Color.isColorable(Material.RED_STAINED_GLASS));
        assertTrue(O2Color.isColorable(Material.BLUE_STAINED_GLASS_PANE));
    }

    /**
     * Test that isColorable() correctly identifies bed materials as colorable.
     */
    @Test
    void isColorableBedTest() {
        assertTrue(O2Color.isColorable(Material.RED_BED));
        assertTrue(O2Color.isColorable(Material.WHITE_BED));
    }

    /**
     * Test that isColorable() correctly identifies candle materials as colorable.
     */
    @Test
    void isColorableCandleTest() {
        assertTrue(O2Color.isColorable(Material.RED_CANDLE));
        assertTrue(O2Color.isColorable(Material.BLUE_CANDLE));
    }

    /**
     * Test that isColorable() correctly identifies banner materials as colorable.
     */
    @Test
    void isColorableBannerTest() {
        assertTrue(O2Color.isColorable(Material.RED_BANNER));
        assertTrue(O2Color.isColorable(Material.WHITE_BANNER));
    }

    /**
     * Test that isColorable() correctly identifies non-colorable materials.
     */
    @Test
    void isColorableNonColorableTest() {
        assertFalse(O2Color.isColorable(Material.STONE));
        assertFalse(O2Color.isColorable(Material.DIRT));
        assertFalse(O2Color.isColorable(Material.GLASS));
        assertFalse(O2Color.isColorable(Material.DIAMOND));
        assertFalse(O2Color.isColorable(Material.IRON_INGOT));
    }

    /**
     * Test that changeColor() correctly changes wool material colors.
     */
    @Test
    void changeColorWoolTest() {
        Material redWool = Material.RED_WOOL;
        Material blueWool = O2Color.changeColor(redWool, O2Color.BLUE);
        assertEquals(Material.BLUE_WOOL, blueWool);

        Material whiteWool = O2Color.changeColor(blueWool, O2Color.WHITE);
        assertEquals(Material.WHITE_WOOL, whiteWool);
    }

    /**
     * Test that changeColor() correctly changes carpet material colors.
     */
    @Test
    void changeColorCarpetTest() {
        Material greenCarpet = Material.GREEN_CARPET;
        Material yellowCarpet = O2Color.changeColor(greenCarpet, O2Color.YELLOW);
        assertEquals(Material.YELLOW_CARPET, yellowCarpet);
    }

    /**
     * Test that changeColor() correctly changes concrete material colors.
     */
    @Test
    void changeColorConcreteTest() {
        Material redConcrete = Material.RED_CONCRETE;
        Material blueConcrete = O2Color.changeColor(redConcrete, O2Color.BLUE);
        assertEquals(Material.BLUE_CONCRETE, blueConcrete);
    }

    /**
     * Test that changeColor() returns the original material when attempting to change
     * the color of non-colorable materials.
     */
    @Test
    void changeColorNonColorableTest() {
        Material stone = Material.STONE;
        Material result = O2Color.changeColor(stone, O2Color.RED);
        assertEquals(stone, result, "changeColor should return original material if not colorable");

        Material diamond = Material.DIAMOND;
        result = O2Color.changeColor(diamond, O2Color.BLUE);
        assertEquals(diamond, result, "changeColor should return original material if not colorable");
    }
}
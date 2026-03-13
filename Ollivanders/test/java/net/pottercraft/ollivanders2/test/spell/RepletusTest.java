package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.REPLETUS;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the REPLETUS refilling spell.
 *
 * <p>Provides comprehensive test coverage for the spell's core functionality:</p>
 * <ul>
 * <li><strong>Bucket Refilling:</strong> Verifies buckets are converted to water buckets</li>
 * <li><strong>Bottle Refilling:</strong> Verifies glass bottles are converted to water potions</li>
 * <li><strong>Invalid Item Handling:</strong> Validates spell behavior when holding unsupported items</li>
 * <li><strong>Spell Termination:</strong> Confirms spell is killed after refilling or failing</li>
 * </ul>
 */
public class RepletusTest extends O2SpellTestSuper {
    /**
     * Returns the spell type being tested.
     *
     * @return REPLETUS spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.REPLETUS;
    }

    /**
     * Tests spell construction. No special construction code needed for REPLETUS.
     */
    @Override
    @Test
    void spellConstructionTest() {
        // no special set up needed
    }

    /**
     * Tests bucket refilling.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>A bucket in the caster's off-hand is converted to a water bucket</li>
     * <li>The filled item is placed back in the off-hand</li>
     * <li>The spell is killed after refilling</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("Repletus");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        caster.getInventory().setItemInOffHand(new ItemStack(Material.BUCKET, 1));
        REPLETUS repletus = (REPLETUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(1);

        ItemStack heldItem = caster.getInventory().getItemInOffHand();
        assertNotNull(heldItem, "filled bucket not in player's off hand");
        assertEquals(Material.WATER_BUCKET, heldItem.getType(), "off hand item not expected type");
        assertTrue(repletus.isKilled(), "repletus not killed after filling bucket");
    }

    /**
     * Tests glass bottle refilling.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>A glass bottle in the caster's off hand is converted to a water potion</li>
     * <li>The potion has the correct potion type (water)</li>
     * <li>The filled item is placed back in the off hand</li>
     * <li>The spell is killed after refilling</li>
     * </ul>
     */
    @Test
    void bottleTest() {
        World testWorld = mockServer.addSimpleWorld("Repletus");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        caster.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));
        REPLETUS repletus = (REPLETUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(1);

        assertTrue(repletus.isKilled());
        ItemStack heldItem = caster.getInventory().getItemInOffHand();
        assertNotNull(heldItem, "filled bottle not in player's off hand");
        assertEquals(Material.POTION, heldItem.getType(), "off hand item not expected type");
        PotionMeta meta = (PotionMeta) heldItem.getItemMeta();
        assertNotNull(meta);
        assertEquals(PotionType.WATER, meta.getBasePotionType(), "bottle does not contain water");
    }

    /**
     * Tests spell behavior with unsupported items.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>The spell does not convert unsupported items (honey bottles)</li>
     * <li>The unsupported item remains in the caster's off hand unchanged</li>
     * <li>The spell is still killed after failing to find a valid target</li>
     * </ul>
     */
    @Test
    void invalidTargetTest() {
        World testWorld = mockServer.addSimpleWorld("Repletus");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        caster.getInventory().setItemInOffHand(new ItemStack(Material.HONEY_BOTTLE, 1));
        REPLETUS repletus = (REPLETUS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(1);

        assertTrue(repletus.isKilled(), "repletus not killed when invalid target detected");
        ItemStack heldItem = caster.getInventory().getItemInOffHand();
        assertNotNull(heldItem, "caster no longer holding item in off hand");
        assertEquals(Material.HONEY_BOTTLE, heldItem.getType(), "off hand item not expected type");
    }

    /**
     * Tests reversion. No revertible state for REPLETUS, so this test is empty.
     *
     * <p>REPLETUS makes no temporary changes that require reversion.</p>
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }
}

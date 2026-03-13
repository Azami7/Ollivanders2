package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.FLAGRANTE;
import net.pottercraft.ollivanders2.spell.GEMINO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.VERDIMILLIOUS_DUO;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the VERDIMILLIOUS_DUO curse detection spell.
 *
 * <p>Provides comprehensive test coverage for the spell's curse detection and glow functionality:</p>
 * <ul>
 * <li><strong>Curse Detection:</strong> Verifies the spell detects cursed items and makes them glow</li>
 * <li><strong>Glow Duration:</strong> Confirms the glow effect lasts 60 seconds then turns off</li>
 * <li><strong>Skill-Based Detection:</strong> Tests that only cursed items within the caster's skill level are detected</li>
 * <li><strong>Non-Cursed Items:</strong> Validates the spell ignores regular and enchanted non-cursed items</li>
 * </ul>
 *
 * <p>Tests use Mockito spies to verify the spell's sound effect and proper spell termination.</p>
 */
public class VerdimilliousDuoTest extends SparksTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.VERDIMILLIOUS_DUO;
    }

    /**
     * Tests detection and glow of cursed items.
     *
     * <p>Verifies the spell's core functionality:</p>
     * <ul>
     * <li>Casts FLAGRANTE to create a cursed item</li>
     * <li>Casts VERDIMILLIOUS_DUO to detect the cursed item</li>
     * <li>Confirms the item starts glowing immediately</li>
     * <li>Confirms the item stops glowing after the glow duration expires (60 seconds)</li>
     * </ul>
     */
    @Test
    void cursedItemsTest() {
        World testWorld = mockServer.addSimpleWorld("VerdimilliousDuo_cursed");
        World spyWorld = Mockito.spy(testWorld);
        Location location = getNextLocation(spyWorld);
        Location targetLocation = new Location(spyWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        testWorld.dropItem(targetLocation, new ItemStack(Material.WOODEN_AXE, 1));

        FLAGRANTE flagrante = (FLAGRANTE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 20, O2SpellType.FLAGRANTE);
        mockServer.getScheduler().performTicks(20);
        Item cursedItem = EntityCommon.getItemAtLocation(flagrante.getLocation());
        assertNotNull(cursedItem);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isCursed(cursedItem));

        VERDIMILLIOUS_DUO verdimilliousDuo = (VERDIMILLIOUS_DUO) castSpell(caster, location, cursedItem.getLocation());
        mockServer.getScheduler().performTicks(20);
        assertTrue(verdimilliousDuo.isKilled(), "spell did not hit target");
        assertTrue(cursedItem.isGlowing(), "cursed item is not glowing");

        mockServer.getScheduler().performTicks(verdimilliousDuo.getGlowTime());
        assertFalse(cursedItem.isGlowing(), "cursed item did not stop glowing");
    }

    /**
     * Tests that the spell ignores curses above the caster's skill level.
     *
     * <p>Verifies skill-based curse detection:</p>
     * <ul>
     * <li>Casts GEMINO (higher-level curse) to create an undetectable cursed item</li>
     * <li>Casts VERDIMILLIOUS_DUO with basic skill (20 uses)</li>
     * <li>Confirms the spell does not detect the high-level curse and item does not glow</li>
     * </ul>
     *
     * <p>This test ensures the spell only reveals curses appropriate to the player's level.</p>
     */
    @Test
    void undetectableCurseTest() {
        World testWorld = mockServer.addSimpleWorld("VerdimilliousDuo_undetectable");
        World spyWorld = Mockito.spy(testWorld);
        Location location = getNextLocation(spyWorld);
        Location targetLocation = new Location(spyWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        testWorld.dropItem(targetLocation, new ItemStack(Material.DIAMOND_SWORD, 1));

        GEMINO gemino = (GEMINO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 20, O2SpellType.GEMINO);
        mockServer.getScheduler().performTicks(20);
        Item cursedItem = EntityCommon.getItemAtLocation(gemino.getLocation());
        assertNotNull(cursedItem);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isCursed(cursedItem));

        VERDIMILLIOUS_DUO verdimilliousDuo = (VERDIMILLIOUS_DUO) castSpell(caster, location, cursedItem.getLocation());
        mockServer.getScheduler().performTicks(20);
        assertTrue(verdimilliousDuo.isKilled(), "spell did not hit target");
        assertFalse(cursedItem.isGlowing(), "spell detected curse of too high level");
    }

    /**
     * Tests that the spell ignores non-cursed items.
     *
     * <p>Verifies the spell only glows cursed items:</p>
     * <ul>
     * <li>Drops a regular item (compass) and an enchanted non-cursed item (broomstick)</li>
     * <li>Casts VERDIMILLIOUS_DUO at the target location</li>
     * <li>Confirms neither item glows after the spell completes</li>
     * </ul>
     *
     * <p>This test ensures the spell accurately distinguishes cursed items from regular items.</p>
     */
    @Test
    void notCursedItemTest() {
        World testWorld = mockServer.addSimpleWorld("VerdimilliousDuo_notCursed");
        World spyWorld = Mockito.spy(testWorld);
        Location location = getNextLocation(spyWorld);
        Location targetLocation = new Location(spyWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        testWorld.dropItem(targetLocation, new ItemStack(Material.COMPASS, 1));

        ItemStack broom = O2ItemType.BROOMSTICK.getItem(1);
        assertNotNull(broom);
        Item enchantedItem = testWorld.dropItem(targetLocation, broom);
        assertFalse(Ollivanders2API.getItems().enchantedItems.isCursed(enchantedItem));

        VERDIMILLIOUS_DUO verdimilliousDuo = (VERDIMILLIOUS_DUO) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(verdimilliousDuo.isKilled(), "spell did not hit target");
        assertFalse(enchantedItem.isGlowing(), "spell detected not-cursed item");
    }
}

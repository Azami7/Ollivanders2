package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.spell.LIGATIS_COR;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the LIGATIS_COR spell.
 *
 * <p>Verifies that the spell correctly binds a core material to a coreless wand to create a finished wand,
 * and handles failure cases such as missing items, non-coreless wands, and absent core materials.
 * Also tests that item stack amounts are correctly decremented when stacks have remainders.</p>
 */
public class LigatisCorTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LIGATIS_COR;
    }

    /**
     * Test {@link LIGATIS_COR}.
     *
     * <ul>
     *   <li>No nearby item: Spell fails with message</li>
     *   <li>Wand material item that is not a coreless wand: Spell fails, item not consumed</li>
     *   <li>Coreless wand with no core nearby: Spell fails, coreless wand not consumed</li>
     *   <li>Coreless wand with core nearby: Creates finished wand, both items consumed</li>
     *   <li>Stacks with remainders: Only one of each consumed, remainders preserved</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        Ollivanders2.debug = true;

        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);

        // no item found
        LIGATIS_COR ligatisCor = (LIGATIS_COR) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(ligatisCor.isKilled(), "spell not killed when projectile stopped");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message when no item found");
        TestCommon.clearMessageQueue(caster);

        // not a coreless wand, but wand material
        Item target = testWorld.dropItem(targetLocation, new ItemStack(O2ItemType.WAND.getMaterial()));
        ligatisCor = (LIGATIS_COR) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(ligatisCor.isKilled(), "spell not killed when projectile stopped");
        assertFalse(target.isDead(), "target item was removed unexpectedly");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message when target is not a coreless wand");
        TestCommon.clearMessageQueue(caster);
        target.remove();

        // coreless wand, no core nearby
        ItemStack corelessWandStack = Ollivanders2API.getItems().getWands().createCorelessWand(O2WandWoodType.BIRCH, 1);
        assertNotNull(corelessWandStack);
        target = testWorld.dropItem(targetLocation, corelessWandStack);
        ligatisCor = (LIGATIS_COR) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(ligatisCor.isKilled(), "spell not killed when projectile stopped");
        assertFalse(target.isDead(), "target item was removed unexpectedly");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message when no wand core nearby");
        TestCommon.clearMessageQueue(caster);

        // coreless wand
        ItemStack wandCoreStack = O2WandCoreType.DRAGON_HEARTSTRING.getO2ItemType().getItem(1);
        assertNotNull(wandCoreStack);
        Item wandCore = testWorld.dropItem(targetLocation, wandCoreStack);
        ligatisCor = (LIGATIS_COR) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(ligatisCor.isKilled(), "spell not killed when projectile stopped");
        message = caster.nextMessage();
        assertNull(message, "caster received message when success does not send a message");
        assertTrue(target.isDead(), "coreless wand item not removed");
        assertTrue(wandCore.isDead(), "wand core item not removed");
        List<Item> items = EntityCommon.getItemsInRadius(ligatisCor.getLocation(), 2);
        assertFalse(items.isEmpty(), "wand item not found");
        assertTrue(Ollivanders2API.getItems().getWands().isWand(items.getFirst().getItemStack()), "wand item is not a valid wand");

        // handle remainders
        location = getNextLocation(testWorld);
        targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        corelessWandStack = Ollivanders2API.getItems().getWands().createCorelessWand(O2WandWoodType.BIRCH, 3);
        assertNotNull(corelessWandStack);
        target = testWorld.dropItem(targetLocation, corelessWandStack);
        wandCoreStack = O2WandCoreType.DRAGON_HEARTSTRING.getO2ItemType().getItem(4);
        assertNotNull(wandCoreStack);
        wandCore = testWorld.dropItem(targetLocation, wandCoreStack);
        ligatisCor = (LIGATIS_COR) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(ligatisCor.isKilled());
        assertFalse(target.isDead(), "coreless wand item removed when there should be remainder");
        assertFalse(wandCore.isDead(), "wand core item removed when there should be remainder");
        assertEquals(2, target.getItemStack().getAmount(), "unexpected remainder amount for coreless wands");
        assertEquals(3, wandCore.getItemStack().getAmount(), "unexpected remainder amount for wand cores");
    }

    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}

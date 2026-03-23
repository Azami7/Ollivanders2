package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.PERMURATE;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link PERMURATE} spell (switching spell).
 *
 * <p>PERMURATE swaps the material types of two nearby items. Overrides doCheckEffectTest and
 * remainderItemsTest to test the two-item swap behavior including failure cases (invalid item,
 * missing second item, invalid second item, same-type items) and partial stack handling.</p>
 *
 * @author Azami7
 */
public class PermurateTest extends ItemToItemTransfigurationTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PERMURATE
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PERMURATE;
    }

    /**
     * Compasses are a non-unbreakable material that can be targeted by PERMURATE.
     *
     * @return Material.COMPASS
     */
    Material getValidItemType() {
        return Material.COMPASS;
    }

    /**
     * Barriers are an unbreakable material rejected by canTransfigure.
     *
     * @return Material.BARRIER
     */
    Material getInvalidItemType() {
        return Material.BARRIER;
    }

    /**
     * Test the PERMURATE spell effect through multiple scenarios.
     *
     * <p>Tests the following cases in sequence:</p>
     *
     * <ol>
     * <li>Invalid (unbreakable) item only - spell should fail</li>
     * <li>Single valid item with no second item - spell should fail</li>
     * <li>Valid item with invalid second item - spell should fail</li>
     * <li>Two items of the same material type - spell should fail</li>
     * <li>Two valid items of different types - spell should succeed and swap material types</li>
     * </ol>
     */
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        Item itemOne = testWorld.dropItem(targetLocation, new ItemStack(getInvalidItemType(), 1));
        PERMURATE permurate = (PERMURATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(permurate.isKilled(), "spell not killed when item was invalid type");
        assertFalse(permurate.isTransfigured(), "spell transfigured invalid item type");
        assertFalse(itemOne.isDead(), "item1 is dead when item was invalid type");
        itemOne.remove();

        Material typeOne = Material.WOODEN_AXE;
        itemOne = testWorld.dropItem(targetLocation, new ItemStack(Material.WOODEN_AXE, 1));
        permurate = (PERMURATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(permurate.isKilled(), "spell not killed when second item not present");
        assertFalse(permurate.isTransfigured(), "spell transfigured when second item not present");
        assertFalse(itemOne.isDead(), "item1 is dead when second item not present");

        Location target2Location = targetLocation.getBlock().getRelative(BlockFace.EAST).getLocation();
        Item itemTwo = testWorld.dropItem(target2Location, new ItemStack(getInvalidItemType(), 1));
        permurate = (PERMURATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(permurate.isKilled(), "spell not killed when item2 is invalid type");
        assertFalse(permurate.isTransfigured(), "spell transfigured when item2 is invalid type");
        assertFalse(itemOne.isDead(), "item1 is dead when item2 is invalid type");
        assertFalse(itemTwo.isDead(), "item2 is dead when item2 is invalid type");
        itemTwo.remove();

        itemTwo = testWorld.dropItem(target2Location, new ItemStack(typeOne, 1));
        permurate = (PERMURATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(permurate.isKilled(), "spell not killed when item2 is same type as item1");
        assertFalse(permurate.isTransfigured(), "spell transfigured when item2 is same type as item1");
        assertFalse(itemOne.isDead(), "item1 is dead when item2 is same type as item1");
        assertFalse(itemTwo.isDead(), "item2 is dead when item2 is same type as item1");
        itemTwo.remove();

        Material typeTwo = Material.DIAMOND_BOOTS;
        itemTwo = testWorld.dropItem(target2Location, new ItemStack(typeTwo, 1));
        permurate = (PERMURATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(permurate.isTransfigured(), "isTransfigured() returned false for 2 valid items");
        assertTrue(itemOne.isDead(), "item1 not removed");
        assertTrue(itemTwo.isDead(), "item2 not removed");
        Item transItemOne = EntityCommon.getItemAtLocation(targetLocation);
        assertNotNull(transItemOne, "Did not find switched item1 at expected location");
        Item transItemTwo = EntityCommon.getItemAtLocation(target2Location);
        assertNotNull(transItemTwo, "Did not find switched item2 at expected location");
        assertEquals(typeTwo, transItemOne.getItemStack().getType(), "Switched item1 not expected type");
        assertEquals(typeOne, transItemTwo.getItemStack().getType(), "Switched item2 not expected type");
    }

    /**
     * Test partial stack swapping where the two items have different stack sizes.
     *
     * <p>Verifies that only the swappable amount is changed (capped by the smaller stack),
     * the larger stack retains its remainder, and the smaller stack is fully consumed.</p>
     */
    @Test
    void remainderItemsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        Location target2Location = targetLocation.getBlock().getRelative(BlockFace.EAST).getLocation();
        PlayerMock caster = mockServer.addPlayer();

        Item item1 = testWorld.dropItem(targetLocation, new ItemStack(Material.BOOK, 10));
        Item item2 = testWorld.dropItem(target2Location, new ItemStack(Material.BOWL, 5));

        PERMURATE permurate = (PERMURATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(permurate.isTransfigured());
        assertFalse(item1.isDead(), "item1 removed when it should have remainder amount");
        assertTrue(item2.isDead(), "item2 not removed when it should have no remainder amount");
        assertEquals(5, item1.getItemStack().getAmount(), "unexpected remainder for item1");
        Item transItem2 = EntityCommon.getItemAtLocation(target2Location);
        assertNotNull(transItem2);
        assertEquals(5, transItem2.getItemStack().getAmount(), "unexpected amount for switched item2");
    }
}

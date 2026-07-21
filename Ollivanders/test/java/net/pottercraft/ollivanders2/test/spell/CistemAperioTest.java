package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.CISTEM_APERIO;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.CISTEM_APERIO}.
 * <p>
 * The per-item eject chance is normally a {@code usesModifier / 2} random roll. In test mode a cast at or above
 * {@link O2Spell#spellMasteryLevel} is guaranteed to eject, so these tests are deterministic: casting at mastery
 * ejects every item, while experience 0 can never satisfy the roll and ejects nothing.
 * </p>
 *
 * @author Azami7
 */
public class CistemAperioTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CISTEM_APERIO;
    }

    /**
     * Verify a maxed-skill cast empties every slot of a struck chest and drops every item into the world. Items are
     * seeded into specific slots so the two identical emerald stacks stay separate, proving duplicates are each
     * dropped rather than destroyed.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        Block chestBlock = targetLocation.getBlock();
        chestBlock.setType(Material.CHEST);

        // seed by slot so the two identical emerald stacks remain in separate slots (addItem would merge them)
        Inventory chestInventory = ((Chest) chestBlock.getState()).getInventory();
        chestInventory.setItem(0, new ItemStack(Material.DIAMOND, 5));
        chestInventory.setItem(1, new ItemStack(Material.EMERALD, 2));
        chestInventory.setItem(2, new ItemStack(Material.EMERALD, 2)); // identical duplicate of slot 1
        assertEquals(3, itemSlotCount(((Chest) chestBlock.getState()).getInventory()), "chest was not seeded with 3 stacks");

        CISTEM_APERIO cistemAperio = (CISTEM_APERIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(cistemAperio.isKilled(), "spell was not killed after blasting the container");
        assertTrue(((Chest) chestBlock.getState()).getInventory().isEmpty(), "chest still has contents after a full eject");

        // all three seeded stacks should have been dropped into the world - the duplicate emeralds must not be lost
        List<Item> droppedItems = TestCommon.getAllItems(testWorld);
        assertEquals(3, droppedItems.size(), "wrong number of item stacks dropped");
        int emeraldTotal = 0;
        for (Item item : droppedItems) {
            if (item.getItemStack().getType() == Material.EMERALD)
                emeraldTotal = emeraldTotal + item.getItemStack().getAmount();
        }
        assertEquals(4, emeraldTotal, "both duplicate emerald stacks were not preserved when dropped");
    }

    /**
     * Verify blasting an empty container sends the "container shakes" failure message, drops nothing, and kills the
     * spell.
     */
    @Test
    void emptyContainerTest() {
        World testWorld = mockServer.addSimpleWorld("CistemAperioEmpty");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        targetLocation.getBlock().setType(Material.CHEST);

        CISTEM_APERIO cistemAperio = (CISTEM_APERIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(cistemAperio.isKilled(), "spell was not killed against an empty container");
        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive the failure message for an empty container");
        assertTrue(message.contains(CISTEM_APERIO.containerShakesMessage), "caster did not receive the container-shakes message");
        assertTrue(TestCommon.getAllItems(testWorld).isEmpty(), "items were dropped from an empty container");
    }

    /**
     * Verify that at experience 0 (the roll can never pass) the chest keeps all its contents, nothing is dropped, and
     * the caster gets the "container shakes" failure message.
     */
    @Test
    void noEjectTest() {
        World testWorld = mockServer.addSimpleWorld("CistemAperioNoEject");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        Block chestBlock = targetLocation.getBlock();
        chestBlock.setType(Material.CHEST);
        ((Chest) chestBlock.getState()).getInventory().setItem(0, new ItemStack(Material.DIAMOND, 5));

        CISTEM_APERIO cistemAperio = (CISTEM_APERIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        mockServer.getScheduler().performTicks(20);

        assertTrue(cistemAperio.isKilled(), "spell was not killed after failing to eject");
        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not receive the failure message when nothing was ejected");
        assertTrue(message.contains(CISTEM_APERIO.containerShakesMessage), "caster did not receive the container-shakes message");
        assertEquals(1, itemSlotCount(((Chest) chestBlock.getState()).getInventory()), "chest contents changed when nothing should have been ejected");
        assertTrue(TestCommon.getAllItems(testWorld).isEmpty(), "items were dropped when nothing should have been ejected");
    }

    /**
     * Verify the spell is killed with no failure message and no dropped items when the target is not a container.
     */
    @Test
    void nonContainerTest() {
        World testWorld = mockServer.addSimpleWorld("CistemAperioNonContainer");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        targetLocation.getBlock().setType(Material.OAK_PLANKS);

        CISTEM_APERIO cistemAperio = (CISTEM_APERIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(cistemAperio.isKilled(), "spell was not killed against a non-container block");
        assertNull(caster.nextMessage(), "caster received a message for a non-container target");
        assertTrue(TestCommon.getAllItems(testWorld).isEmpty(), "items were dropped for a non-container target");
    }

    /**
     * CISTEM_APERIO makes no temporary changes that require reversion.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }

    /**
     * Counts the number of non-empty slots in an inventory.
     *
     * @param inventory the inventory whose filled slots to count
     * @return the number of slots containing an item
     */
    private int itemSlotCount(@NotNull Inventory inventory) {
        int count = 0;

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getAmount() > 0)
                count = count + 1;
        }

        return count;
    }
}

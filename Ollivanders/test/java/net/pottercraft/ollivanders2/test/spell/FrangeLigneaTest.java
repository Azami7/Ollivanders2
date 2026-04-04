package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.FRANGE_LIGNEA;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
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
 * Tests for the FRANGE_LIGNEA spell.
 *
 * <p>Verifies that the spell correctly converts wand-wood logs into coreless wands and rejects
 * invalid block types. Also tests that the number of coreless wands scales with caster experience.</p>
 */
public class FrangeLigneaTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FRANGE_LIGNEA;
    }

    @Override
    @Test
    void spellConstructionTest() {
    }

    /**
     * Verifies the spell handles different block types correctly and produces coreless wands from valid targets.
     * <ul>
     *   <li>Non-log block (STONE): Spell fails with message, no items dropped</li>
     *   <li>Non-natural wood (OAK_PLANKS): Spell fails with message, no items dropped</li>
     *   <li>Natural wood that is not a wand wood (MANGROVE_LOG): Spell fails with message, no items dropped</li>
     *   <li>Valid wand wood (OAK_LOG): Drops coreless wand, no failure message</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(Material.STONE);
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);

        // try with stone block
        FRANGE_LIGNEA frangeLignea = (FRANGE_LIGNEA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(frangeLignea.isKilled(), "spell not killed when block hit");
        assertTrue(EntityCommon.getItemsInRadius(targetLocation, 2).isEmpty(), "item found near target location when target was wrong type");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        TestCommon.clearMessageQueue(caster);

        // try with wood that isn't natural wood
        target.setType(Material.OAK_PLANKS);
        frangeLignea = (FRANGE_LIGNEA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(frangeLignea.isKilled(), "spell not killed when block hit");
        assertTrue(EntityCommon.getItemsInRadius(targetLocation, 2).isEmpty(), "item found near target location when target was wrong type");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        TestCommon.clearMessageQueue(caster);

        // try with wood that is natural wood but not a wand wood type
        target.setType(Material.MANGROVE_LOG);
        frangeLignea = (FRANGE_LIGNEA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(frangeLignea.isKilled(), "spell not killed when block hit");
        assertTrue(EntityCommon.getItemsInRadius(targetLocation, 2).isEmpty(), "item found near target location when target was wrong type");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        TestCommon.clearMessageQueue(caster);

        // try with wand wood
        O2WandWoodType woodType = O2WandWoodType.OAK;
        target.setType(woodType.getMaterial());
        frangeLignea = (FRANGE_LIGNEA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(frangeLignea.isKilled(), "spell not killed when block hit");
        message = caster.nextMessage();
        assertNull(message, "caster received message when success does not give message");
        List<Item> items = EntityCommon.getItemsInRadius(targetLocation, 2);
        assertFalse(items.isEmpty(), "No items found near target location");
        Item wand = items.getFirst();
        assertEquals(O2ItemType.WAND.getMaterial(), wand.getItemStack().getType(), "Item was not expected type");
        assertTrue(Ollivanders2API.getItems().getWands().isCorelessWand(wand.getItemStack()), "Item is not a coreless wand");
    }

    /**
     * Test that the number of coreless wands created scales with caster experience.
     * <p>
     * The amount is calculated as usesModifier * 0.1, clamped between 1 and maxAmount.
     * </p>
     * <ul>
     *   <li>Experience 1: Creates 1 coreless wand (minimum)</li>
     *   <li>Experience 50: Creates 5 coreless wands</li>
     *   <li>Experience 100: Creates 10 coreless wands</li>
     *   <li>Experience 200: Capped at maxAmount</li>
     * </ul>
     */
    @Test
    void numberOfWandsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(O2WandWoodType.ACACIA.getMaterial());
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);

        // experience <= 10 should create 1 coreless wand
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        mockServer.getScheduler().performTicks(20);
        List<Item> items = EntityCommon.getItemsInRadius(targetLocation, 2);
        assertFalse(items.isEmpty());
        Item wand = items.getFirst();
        assertEquals(O2ItemType.WAND.getMaterial(), wand.getItemStack().getType());
        assertEquals(1, wand.getItemStack().getAmount(), "unexpected number of coreless wands for experience 1");
        wand.remove();

        // experience 10-100, number of wands = experience / 10
        target.setType(O2WandWoodType.ACACIA.getMaterial());
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 50);
        mockServer.getScheduler().performTicks(20);
        items = EntityCommon.getItemsInRadius(targetLocation, 2);
        assertFalse(items.isEmpty());
        wand = items.getFirst();
        assertEquals(O2ItemType.WAND.getMaterial(), wand.getItemStack().getType());
        assertEquals(5, wand.getItemStack().getAmount(), "unexpected number of coreless wands for experience 50");
        wand.remove();

        target.setType(O2WandWoodType.ACACIA.getMaterial());
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 100);
        mockServer.getScheduler().performTicks(20);
        items = EntityCommon.getItemsInRadius(targetLocation, 2);
        assertFalse(items.isEmpty());
        wand = items.getFirst();
        assertEquals(O2ItemType.WAND.getMaterial(), wand.getItemStack().getType());
        assertEquals(10, wand.getItemStack().getAmount(), "unexpected number of coreless wands for experience 100");
        wand.remove();

        // number of wands cannot exceed maxAmount
        target.setType(O2WandWoodType.ACACIA.getMaterial());
        FRANGE_LIGNEA frangeLignea = (FRANGE_LIGNEA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 200);
        mockServer.getScheduler().performTicks(20);
        items = EntityCommon.getItemsInRadius(targetLocation, 2);
        assertFalse(items.isEmpty());
        wand = items.getFirst();
        assertEquals(O2ItemType.WAND.getMaterial(), wand.getItemStack().getType());
        assertEquals(frangeLignea.getMaxAmount(), wand.getItemStack().getAmount(), "unexpected number of coreless wands for experience 200");
        wand.remove();
    }

    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}

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
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.VERDIMILLIOUS_DUO}. Extends {@link SparksTest} for the
 * shared sparks tests.
 *
 * @author Azami7
 */
public class VerdimilliousDuoTest extends SparksTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.VERDIMILLIOUS_DUO;
    }

    /**
     * Verify the spell detects a FLAGRANTE-cursed item and makes it glow immediately, then the glow turns off after
     * {@link VERDIMILLIOUS_DUO#getGlowTime()}.
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
     * Verify a curse above the caster's skill level (a GEMINO curse cast at basic skill) is not detected, so the item
     * does not glow.
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
     * Verify the spell leaves a regular item (compass) and an enchanted-but-not-cursed item (broomstick) un-glowing.
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

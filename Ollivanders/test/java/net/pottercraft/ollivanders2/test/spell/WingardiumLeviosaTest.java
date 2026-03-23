package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.WINGARDIUM_LEVIOSA;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WingardiumLeviosaTest extends O2SpellTestSuper {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.WINGARDIUM_LEVIOSA;
    }

    @Override @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);

        WINGARDIUM_LEVIOSA wingardiumLeviosa = (WINGARDIUM_LEVIOSA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(wingardiumLeviosa.isKilled(), "spell not killed when no target found");
        assertFalse(wingardiumLeviosa.isMoving(), "spell set to isMoving when no target found");

        Item target = testWorld.dropItem(targetLocation, new ItemStack(Material.IRON_HELMET, 1));
        wingardiumLeviosa = (WINGARDIUM_LEVIOSA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(wingardiumLeviosa.isKilled(), "spell not killed when valid target hit but player not sneaking");
        assertFalse(wingardiumLeviosa.isMoving(), "spell set to isMoving when valid target hit but player no sneaking");

        caster.setSneaking(true);
        wingardiumLeviosa = (WINGARDIUM_LEVIOSA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(wingardiumLeviosa.isKilled(), "spell killed when valid target hit and player sneaking");
        assertTrue(wingardiumLeviosa.isMoving(), "spell not set to isMoving when valid target hit and player sneaking");
        assertFalse(target.hasGravity(), "gravity not disabled for target");

        // test moving the item - look up, item should get upward velocity
        Location newLoc = caster.getLocation().clone();
        newLoc.setPitch(-45); // look up
        caster.setLocation(newLoc);
        mockServer.getScheduler().performTicks(1);

        assertFalse(wingardiumLeviosa.isKilled(), "spell should still be active while sneaking");
        assertTrue(wingardiumLeviosa.isMoving(), "spell should still be moving");
        assertTrue(target.getVelocity().getY() > 0, "item should have upward velocity when caster looks up");

        // look down, item should get downward velocity
        newLoc = caster.getLocation().clone();
        newLoc.setPitch(45); // look down
        caster.setLocation(newLoc);
        mockServer.getScheduler().performTicks(1);

        assertFalse(wingardiumLeviosa.isKilled(), "spell should still be active while sneaking");
        assertTrue(target.getVelocity().getY() < 0, "item should have downward velocity when caster looks down");
    }

    @Test
    void moveTicksTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        WINGARDIUM_LEVIOSA wingardiumLeviosa = (WINGARDIUM_LEVIOSA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertEquals(0, wingardiumLeviosa.calculateMoveTicks(), "unexpected moveTicks for skill level 0");

        wingardiumLeviosa = (WINGARDIUM_LEVIOSA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 50);
        assertEquals(5 * Ollivanders2Common.ticksPerSecond, wingardiumLeviosa.calculateMoveTicks(), "unexpected moveTicks for skill level 50");
    }

    @Override @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        Item target = testWorld.dropItem(targetLocation, new ItemStack(Material.KNOWLEDGE_BOOK, 1));
        caster.setSneaking(true);

        WINGARDIUM_LEVIOSA wingardiumLeviosa = (WINGARDIUM_LEVIOSA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(wingardiumLeviosa.isMoving());
        assertFalse(target.hasGravity());

        caster.setSneaking(false);
        mockServer.getScheduler().performTicks(2);
        assertTrue(wingardiumLeviosa.isKilled(), "spell not killed when player stopped sneaking");
        assertTrue(target.hasGravity(), "gravity not re-enabled for target item when spell killed");
        target.remove();

        target = testWorld.dropItem(targetLocation, new ItemStack(Material.LADDER, 1));
        caster.setSneaking(true);
        wingardiumLeviosa = (WINGARDIUM_LEVIOSA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(wingardiumLeviosa.isMoving());
        assertFalse(target.hasGravity());

        int moveTicks = wingardiumLeviosa.getMoveTicks();
        mockServer.getScheduler().performTicks(moveTicks - 1);
        assertTrue(wingardiumLeviosa.isMoving());
        assertFalse(target.hasGravity());

        mockServer.getScheduler().performTicks(2);
        assertTrue(wingardiumLeviosa.isKilled(), "spell not killed when moveTicks reached 0");
        assertTrue(target.hasGravity(), "gravity not re-enabled for target item when moveTicks reached 0");
    }
}

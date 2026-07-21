package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.Year;
import net.pottercraft.ollivanders2.spell.ARRESTO_MOMENTUM;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.ARRESTO_MOMENTUM}.
 */
public class ArrestoMomentumTest extends O2SpellTestSuper {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ARRESTO_MOMENTUM;
    }

    /**
     * Verify the spell falls back to the nearest item and slows it when no valid living entity is nearby.
     */
    @Override @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("Arresto");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        Item item = testWorld.dropItem(targetLocation, new ItemStack(Material.ARROW, 1));
        int yVelocity = 5;
        item.setVelocity(new Vector(0, yVelocity, 0));

        ARRESTO_MOMENTUM arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(arrestoMomentum.hasHitBlock());
        assertTrue(item.getVelocity().getY() < yVelocity, "item velocity did not decrease, y-velocity was " + yVelocity + ", now " + item.getVelocity().getY());
    }

    /**
     * Verify that with years enabled only a Year 5+ caster can slow a living entity; below Year 5 the spell targets
     * an item instead.
     */
    @Test
    void livingEntityTest() {
        World testWorld = mockServer.addSimpleWorld("Arresto");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Ollivanders2.useYears = true;
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2p);
        o2p.setYear(Year.YEAR_3);

        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);
        int yVelocity = 5;
        target.setVelocity(new Vector(0, yVelocity, 0));

        ARRESTO_MOMENTUM arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        // spell should fail because the caster year is not >= 5th
        assertTrue(arrestoMomentum.hasHitBlock());
        assertEquals(5, target.getVelocity().getY(), "target velocity decreased, y-velocity was " + yVelocity + ", now " + target.getVelocity().getY());

        o2p.setYear(Year.YEAR_5);
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        // spell should pass because the caster year is 5th
        assertTrue(arrestoMomentum.hasHitBlock());
        assertTrue(target.getVelocity().getY() < yVelocity, "target velocity did not decrease, y-velocity was " + yVelocity + ", now " + target.getVelocity().getY());
    }

    /**
     * Verify entities taller than 2 blocks (e.g. an iron golem) are skipped and keep their velocity.
     */
    @Test
    void oversizedEntityTest() {
        World testWorld = mockServer.addSimpleWorld("Arresto");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2p);
        o2p.setYear(Year.YEAR_5); // in case useYears is on (due to test parallelization)

        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        Entity ironGolem = testWorld.spawnEntity(targetLocation, EntityType.IRON_GOLEM);
        int yVelocity = 5;
        ironGolem.setVelocity(new Vector(0, yVelocity, 0));

        ARRESTO_MOMENTUM arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(arrestoMomentum.hasHitBlock());
        assertEquals(5, ironGolem.getVelocity().getY(), "Iron golem velocity changed");
    }

    /**
     * Verify the velocity multiplier shrinks (stronger slowing) as experience rises, across all five tiers from
     * 0.6x at low skill to 0.2x above 100.
     */
    @Test
    void velocityMultiplierTest() {
        World testWorld = mockServer.addSimpleWorld("Arresto");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        Item item = testWorld.dropItem(targetLocation, new ItemStack(Material.ARROW, 1));
        int yVelocity = 10;
        item.setVelocity(new Vector(0, yVelocity, 0));

        ARRESTO_MOMENTUM arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 10);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitBlock());
        assertEquals(6, item.getVelocity().getY(), "Velocity not changed to expected value for 10 experience");

        item.setVelocity(new Vector(0, yVelocity, 0));
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 30);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitBlock());
        assertEquals(5, item.getVelocity().getY(), "Velocity not changed to expected value for 30 experience");

        item.setVelocity(new Vector(0, yVelocity, 0));
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 60);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitBlock());
        assertEquals(4, item.getVelocity().getY(), "Velocity not changed to expected value for 60 experience");

        item.setVelocity(new Vector(0, yVelocity, 0));
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 80);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitBlock());
        assertEquals(3, item.getVelocity().getY(), "Velocity not changed to expected value for 80 experience");

        item.setVelocity(new Vector(0, yVelocity, 0));
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 101);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitBlock());
        assertEquals(2, item.getVelocity().getY(), "Velocity not changed to expected value for 101 experience");
    }

    /**
     * No-op: ARRESTO_MOMENTUM tracks no changes to revert (velocity changes are not undone).
     */
    @Override @Test
    void revertTest() {
        // arresto momentum has no revert tasks
    }
}

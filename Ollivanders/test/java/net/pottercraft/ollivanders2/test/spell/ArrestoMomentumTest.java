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
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the ARRESTO_MOMENTUM spell.
 *
 * <p>ARRESTO_MOMENTUM slows down the velocity of items or living entities based on the caster's
 * spell experience level. The spell prioritizes living entities (with size and year restrictions)
 * over items, falling back to items if no valid living entity is found.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Item targeting and velocity reduction</li>
 * <li>Living entity targeting with year-level restrictions</li>
 * <li>Oversized entity rejection (bounding box height > 2 blocks)</li>
 * <li>Velocity multiplier scaling based on spell experience</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.spell.ARRESTO_MOMENTUM for the spell implementation
 * @see O2SpellTestSuper for inherited test framework
 */
@Isolated
public class ArrestoMomentumTest extends O2SpellTestSuper {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.ARRESTO_MOMENTUM
     */
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ARRESTO_MOMENTUM;
    }

    /**
     * Test spell construction and initial configuration.
     *
     * <p>ARRESTO_MOMENTUM has no spell-specific construction requirements beyond those tested
     * in the base class.</p>
     */
    @Override @Test
    void spellConstructionTest() {
        // arresto momentum has no spell-specific settings
    }

    /**
     * Test that the spell targets and slows items.
     *
     * <p>Verifies that when no valid living entity is nearby, the spell falls back to targeting
     * the nearest item and reduces its velocity according to the spell's multiplier.</p>
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

        assertTrue(arrestoMomentum.hasHitTarget());
        assertTrue(item.getVelocity().getY() < yVelocity, "item velocity did not decrease, y-velocity was " + yVelocity + ", now " + item.getVelocity().getY());
    }

    /**
     * Test that living entity targeting respects year-level restrictions.
     *
     * <p>Verifies that when years are enabled, only casters at Year 5 or higher can target living
     * entities. Below Year 5, the spell should skip living entities and target items instead. Casters
     * at Year 5 and above can successfully slow living entities.</p>
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
        assertTrue(arrestoMomentum.hasHitTarget());
        assertEquals(5, target.getVelocity().getY(), "target velocity decreased, y-velocity was " + yVelocity + ", now " + target.getVelocity().getY());

        o2p.setYear(Year.YEAR_5);
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        // spell should pass because the caster year is 5th
        assertTrue(arrestoMomentum.hasHitTarget());
        assertTrue(target.getVelocity().getY() < yVelocity, "target velocity did not decrease, y-velocity was " + yVelocity + ", now " + target.getVelocity().getY());
    }

    /**
     * Test that oversized entities are not targeted.
     *
     * <p>Verifies that entities with bounding box height > 2 blocks (like Iron Golems) are skipped
     * and their velocity remains unchanged. The spell will only target normal-sized entities.</p>
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

        assertTrue(arrestoMomentum.hasHitTarget());
        assertEquals(5, ironGolem.getVelocity().getY(), "Iron golem velocity changed");
    }

    /**
     * Test that velocity multiplier scales correctly with spell experience.
     *
     * <p>Verifies that the velocity reduction multiplier decreases (stronger slowing) as spell
     * experience increases. Tests all five multiplier tiers: experience 10 (0.6x), 30 (0.5x), 60 (0.4x),
     * 80 (0.3x), and 101+ (0.2x).</p>
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
        assertTrue(arrestoMomentum.hasHitTarget());
        assertEquals(6, item.getVelocity().getY(), "Velocity not changed to expected value for 10 experience");

        item.setVelocity(new Vector(0, yVelocity, 0));
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 30);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitTarget());
        assertEquals(5, item.getVelocity().getY(), "Velocity not changed to expected value for 30 experience");

        item.setVelocity(new Vector(0, yVelocity, 0));
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 60);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitTarget());
        assertEquals(4, item.getVelocity().getY(), "Velocity not changed to expected value for 60 experience");

        item.setVelocity(new Vector(0, yVelocity, 0));
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 80);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitTarget());
        assertEquals(3, item.getVelocity().getY(), "Velocity not changed to expected value for 80 experience");

        item.setVelocity(new Vector(0, yVelocity, 0));
        arrestoMomentum = (ARRESTO_MOMENTUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 101);
        mockServer.getScheduler().performTicks(20);
        assertTrue(arrestoMomentum.hasHitTarget());
        assertEquals(2, item.getVelocity().getY(), "Velocity not changed to expected value for 101 experience");
    }

    /**
     * Test revert functionality.
     *
     * <p>ARRESTO_MOMENTUM has no revert actions since velocity changes are not tracked
     * for automatic cleanup.</p>
     */
    @Override @Test
    void revertTest() {
        // arresto momentum has no revert tasks
    }
}

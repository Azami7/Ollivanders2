package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.Knockback;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link Knockback} spells, covering invalid-target rejection, self-targeting, no-target-found,
 * and velocity direction (vertical/horizontal, push/pull, single and multiple targets). Subclasses supply the valid
 * and invalid entity types.
 *
 * @author Azami7
 */
public abstract class KnockbackTest extends O2SpellTestSuper {
    /**
     * @return an entity type this spell cannot target, or null if all types are valid
     */
    @Nullable
    abstract EntityType getInvalidEntityType();

    /**
     * @return an entity type this spell can target
     */
    @NotNull
    abstract EntityType getValidEntityType();

    /**
     * Verify the spell stops without applying velocity when it hits an invalid entity type (non-self spells only).
     */
    @Override
    @Test
    void doCheckEffectTest() {
        // test for invalid entity case, other cases in other tests
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3); // create a base for the entity to stand on

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel); // cast with high experience to ensure >0 velocity magnitude
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback) spell;

        if (!knockbackSpell.isTargetsSelf()) { // caster will always be selected so no need to test wrong entity type
            EntityType invalidType = getInvalidEntityType();
            if (invalidType != null) { // if null, there are no invalid entity types
                testWorld.spawnEntity(targetLocation, invalidType);

                mockServer.getScheduler().performTicks(12);
                assertTrue(knockbackSpell.isKilled(), "spell projectile did not stop");
                assertEquals(new Vector(0, 0, 0), knockbackSpell.getVelocity(), "velocity set on invalid entity");
            }
        }
    }

    /**
     * Verify a self-targeting spell immediately ends and applies velocity to the caster.
     */
    @Test
    void doCheckEffectTargetsSelf() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel); // cast with high experience to ensure >0 velocity magnitude
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback) spell;

        if (knockbackSpell.isTargetsSelf()) {
            mockServer.getScheduler().performTicks(1);
            assertTrue(knockbackSpell.isKilled(), "projectile not immediately killed when spell is targets self");

            mockServer.getScheduler().performTicks(1);
            assertNotEquals(new Vector(0, 0, 0), knockbackSpell.getVelocity(), "velocity not applied to caster for targets self");
        }
    }

    /**
     * Verify the spell is killed with no message when its projectile hits a block without finding a target.
     */
    @Test
    void doCheckEffectNoTargetFoundTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // spell exits with no failure message if it fails to find any entities
        targetLocation.getBlock().setType(Material.STONE);
        O2Spell spell = castSpell(caster, location, targetLocation);
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback) spell;

        if (!knockbackSpell.isTargetsSelf()) {
            Ollivanders2API.getSpells().addSpell(caster, spell);
            mockServer.getScheduler().performTicks(20);
            assertTrue(spell.isKilled(), "spell not killed when it hit a stone block");
            assertNull(caster.nextMessage(), "caster received message on failure");
        }
    }

    /**
     * Verify the velocity applied to a single target has the right sign and axis: Y for vertical, X for horizontal,
     * positive for push and negative for pull.
     */
    @Test
    void doCheckEffectVelocityTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3); // create a base for the entity to stand on

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback) spell;

        Entity target;
        if (!knockbackSpell.isTargetsSelf()) {
            EntityType validType = getValidEntityType();

            if (validType == EntityType.ITEM)
                target = testWorld.dropItem(targetLocation, new ItemStack(Material.WOODEN_AXE, 1));
            else
                target = testWorld.spawnEntity(targetLocation, validType);

            mockServer.getScheduler().performTicks(10); // do 10 ticks to get the projectile to the target
        }
        else
            target = caster;

        mockServer.getScheduler().performTicks(3);
        Vector velocity = knockbackSpell.getVelocity();

        if (knockbackSpell.isVertical()) {
            if (knockbackSpell.isPull())
                assertTrue(velocity.getY() < 0, "velocity not set to -Y for vertical pull, Y velocity = " + velocity.getY());
            else
                assertTrue(velocity.getY() > 0, "velocity not set to +Y for vertical push, Y velocity = " + velocity.getY());
        }
        else {
            if (knockbackSpell.isPull())
                assertTrue(velocity.getX() < 0, "velocity not set to -X for horizontal pull, X velocity = " + velocity.getX()); // we know it will be in X because location and targetLocation only differ in X
            else
                assertTrue(velocity.getX() > 0, "velocity not set to +X for horizontal push, X velocity = " + velocity.getX());
        }

        assertEquals(velocity, target.getVelocity(), "velocity not set on target");
    }

    /**
     * Verify a multi-target spell applies the same correctly-directed velocity to every entity in range.
     */
    @Test
    void doCheckEffectTargetMultipleTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3); // create a base for the entities to stand on

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback) spell;

        if (knockbackSpell.isTargetsMultiple()) {
            EntityType validType = getValidEntityType();

            Entity target1;
            Entity target2;
            Entity target3;

            // spawn
            if (validType == EntityType.ITEM) {
                target1 = testWorld.dropItem(targetLocation, new ItemStack(Material.WOODEN_AXE, 1));
                target2 = testWorld.dropItem(targetLocation.getBlock().getRelative(BlockFace.NORTH).getLocation(), new ItemStack(Material.WOODEN_AXE, 1));
                target3 = testWorld.dropItem(targetLocation.getBlock().getRelative(BlockFace.EAST).getLocation(), new ItemStack(Material.WOODEN_AXE, 1));
            }
            else {
                target1 = testWorld.spawnEntity(targetLocation, validType);
                target2 = testWorld.spawnEntity(targetLocation.getBlock().getRelative(BlockFace.SOUTH).getLocation(), validType);
                target3 = testWorld.spawnEntity(targetLocation.getBlock().getRelative(BlockFace.WEST).getLocation(), validType);
            }

            mockServer.getScheduler().performTicks(20);

            Vector velocity = knockbackSpell.getVelocity();

            if (knockbackSpell.isVertical()) {
                if (knockbackSpell.isPull())
                    assertTrue(velocity.getY() < 0, "velocity not set to -Y for vertical pull, Y velocity = " + velocity.getY());
                else
                    assertTrue(velocity.getY() > 0, "velocity not set to +Y for vertical push, Y velocity = " + velocity.getY());
            }
            else {
                if (knockbackSpell.isPull())
                    assertTrue(velocity.getX() < 0, "velocity not set to -X for horizontal pull, X velocity = " + velocity.getX()); // we know it will be in X because location and targetLocation only differ in X
                else
                    assertTrue(velocity.getX() > 0, "velocity not set to +X for horizontal push, X velocity = " + velocity.getX());
            }

            assertEquals(velocity, target1.getVelocity(), "target1 velocity not set");
            assertEquals(velocity, target2.getVelocity(), "target2 velocity not set");
            assertEquals(velocity, target3.getVelocity(), "target3 velocity not set");
        }
    }
}

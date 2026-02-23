package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.Knockback;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
 * Abstract base class for knockback spell unit tests.
 *
 * <p>Provides shared test infrastructure for testing Knockback spell implementations including:</p>
 * <ul>
 * <li>Invalid entity type filtering</li>
 * <li>Targets-self spell behavior</li>
 * <li>No target found scenarios</li>
 * <li>Velocity application and direction (vertical/horizontal, push/pull)</li>
 * </ul>
 *
 * <p>Subclasses must implement {@link #getInvalidEntityType()} and {@link #getValidEntityType()} to define
 * which entity types the spell can and cannot target.</p>
 *
 * @author Azami7
 */
public abstract class KnockbackTest extends O2SpellTestSuper {
    /**
     * Get an entity type that this spell cannot target.
     *
     * @return an invalid entity type, or null if all entity types are valid
     */
    @Nullable
    abstract EntityType getInvalidEntityType();

    /**
     * Get an entity type that this spell can target.
     *
     * @return a valid entity type
     */
    @NotNull
    abstract EntityType getValidEntityType();

    /**
     * Test that the spell rejects invalid entity types.
     *
     * <p>Verifies that when the spell targets an invalid entity type, no velocity is applied and the
     * projectile stops. This test is skipped for spells that target the caster (targetsSelf=true) or
     * those with no invalid entity types.</p>
     */
    @Override @Test
    void doCheckEffectTest() {
        // test for invalid entity case, other cases in other tests
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 40, 100);
        Location targetLocation = new Location(testWorld, 210, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3); // create a base for the entity to stand on

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel); // cast with high experience to ensure >0 velocity magnitude
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback)spell;

        if (!knockbackSpell.isTargetsSelf()) { // caster will always be selected so no need to test wrong entity type
            EntityType invalidType = getInvalidEntityType();
            if (invalidType != null) { // if null, there are no invalid entity types
                testWorld.spawnEntity(targetLocation, invalidType);

                mockServer.getScheduler().performTicks(12);
                assertTrue(knockbackSpell.isKilled(), "spell projectile did not stop");
                assertEquals(new Vector(0, 0, 0 ), knockbackSpell.getVelocity(), "velocity set on invalid entity");
            }
        }
    }

    /**
     * Test that invalid entity targets are not moved.
     *
     * <p>Verifies that when the spell targets an invalid entity type, the entity remains at its original
     * location and the projectile stops. This test is skipped for spells that target the caster (targetsSelf=true)
     * or those with no invalid entity types.</p>
     */
    @Test
    void doCheckEffectInvalidEntityTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 40, 100);
        Location targetLocation = new Location(testWorld, 310, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3); // create a base for the entity to stand on

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel); // cast with high experience to ensure >0 velocity magnitude
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback)spell;

        if (!knockbackSpell.isTargetsSelf()) { // caster will always be selected so no need to test wrong entity type
            EntityType invalidType = getInvalidEntityType();
            if (invalidType != null) { // if null, there are no invalid entity types
                Entity entity = testWorld.spawnEntity(targetLocation, invalidType);

                mockServer.getScheduler().performTicks(12);
                assertTrue(knockbackSpell.isKilled(), "spell projectile did not stop");
                assertEquals(targetLocation, entity.getLocation(), "invalid entity was moved by the spell");
            }
        }
    }

    /**
     * Test that self-targeting spells apply velocity to the caster.
     *
     * <p>Verifies that spells with targetsSelf=true immediately kill the projectile and apply velocity to
     * the caster. This test is skipped for spells that target other entities.</p>
     */
    @Test
    void doCheckEffectTargetsSelf() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 400, 40, 100);
        Location targetLocation = new Location(testWorld, 410, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel); // cast with high experience to ensure >0 velocity magnitude
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback)spell;

        if (knockbackSpell.isTargetsSelf()) {
            mockServer.getScheduler().performTicks(1);
            assertTrue(knockbackSpell.isKilled(), "projectile not immediately killed when spell is targets self");

            mockServer.getScheduler().performTicks(1);
            assertNotEquals(new Vector(0, 0, 0 ), knockbackSpell.getVelocity(), "velocity not applied to caster for targets self");
        }
    }

    /**
     * Test that the spell kills when no valid target is found.
     *
     * <p>Verifies that when the spell projectile hits a solid block (with no entity target), the spell is
     * killed and no message is sent to the caster. This test is skipped for spells that target the caster.</p>
     */
    @Test
    void doCheckEffectNoTargetFoundTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 500, 40, 100);
        Location targetLocation = new Location(testWorld, 510, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        // spell exits with no failure message if it fails to find any entities
        targetLocation.getBlock().setType(Material.STONE);
        O2Spell spell = castSpell(caster, location, targetLocation);
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback)spell;

        if (!knockbackSpell.isTargetsSelf()) {
            Ollivanders2API.getSpells().addSpell(caster, spell);
            mockServer.getScheduler().performTicks(20);
            assertTrue(spell.isKilled(), "spell not killed when it hit a stone block");
            assertNull(caster.nextMessage(), "caster received message on failure");
        }
    }

    /**
     * Test that velocity is applied in the correct direction.
     *
     * <p>Verifies that the spell applies velocity in the correct direction based on the spell's configuration:
     * for vertical spells, checks Y-axis velocity (positive for push, negative for pull);
     * for horizontal spells, checks X-axis velocity (positive for push, negative for pull).
     * This test is skipped for spells that target the caster.</p>
     */
    @Test
    void doCheckEffectVelocityTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 600, 40, 100);
        Location targetLocation = new Location(testWorld, 610, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3); // create a base for the entity to stand on

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        assertInstanceOf(Knockback.class, spell);
        Knockback knockbackSpell = (Knockback)spell;

        if (!knockbackSpell.isTargetsSelf()) {
            EntityType validType = getValidEntityType();
            if (validType == EntityType.ITEM)
                testWorld.dropItem(targetLocation, new ItemStack(Material.WOODEN_AXE, 1));
            else
                testWorld.spawnEntity(targetLocation, validType);

            mockServer.getScheduler().performTicks(10); // do 10 ticks to get the projectile to the target
        }

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
                assertTrue(velocity.getX() < 0, "velocity not set to -X for horizontal pull, Y velocity = " + velocity.getX()); // we know it will be in X because location and targetLocation only differ in X
            else
                assertTrue(velocity.getX() > 0, "velocity not set to +X for horizontal push, Y velocity = " + velocity.getX());
        }
    }
}

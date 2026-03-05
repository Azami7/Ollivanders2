package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.ChangeEntitySize;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base class for ENGORGIO and REDUCIO spell unit tests.
 *
 * <p>Provides shared test coverage for entity size-changing spells including:</p>
 * <ul>
 * <li>Ageable entity state change (baby/adult toggle)</li>
 * <li>Slime size change based on skill level</li>
 * <li>Hostile entity restriction at low vs. high skill</li>
 * <li>Multiple entity targeting up to the target limit</li>
 * </ul>
 *
 * @author Azami7
 */
abstract public class ChangeEntitySizeTest extends O2SpellTestSuper {
    @Override
    @Test
    void spellConstructionTest() {
        // no special set up code
    }

    /**
     * Tests that doCheckEffect() changes the age state of a peaceful Ageable entity.
     *
     * <p>Verifies the primary spell effect:</p>
     * <ul>
     * <li>ENGORGIO: a baby cow becomes an adult</li>
     * <li>REDUCIO: an adult cow becomes a baby</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("ChangeEntitySizeEffect_" + getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Cow cow = (Cow) testWorld.spawnEntity(targetLocation, EntityType.COW);
        ChangeEntitySize spell = (ChangeEntitySize) castSpell(caster, location, targetLocation);

        if (spell.isGrowing())
            cow.setBaby();
        else
            cow.setAdult();

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        if (spell.isGrowing())
            assertTrue(cow.isAdult(), "cow was not made adult");
        else
            assertFalse(cow.isAdult(), "cow was not made baby");
    }

    /**
     * Tests that doCheckEffect() changes the size of a Slime entity.
     *
     * <p>Verifies slime size scaling:</p>
     * <ul>
     * <li>ENGORGIO: a size-1 slime grows larger</li>
     * <li>REDUCIO: a size-3 slime shrinks smaller</li>
     * </ul>
     */
    @Test
    void slimeTest() {
        World testWorld = mockServer.addSimpleWorld("ChangeEntitySizeSlime_" + getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Slime slime = (Slime) testWorld.spawnEntity(targetLocation, EntityType.SLIME);
        ChangeEntitySize spell = (ChangeEntitySize) castSpell(caster, location, targetLocation);
        int initialSize = spell.isGrowing() ? 1 : 3;
        slime.setSize(initialSize);

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        if (spell.isGrowing())
            assertTrue(slime.getSize() > initialSize, "slime was not grown");
        else
            assertTrue(slime.getSize() < initialSize, "slime was not shrunk");
    }

    /**
     * Tests the hostile mob skill restriction in changeEntityAge().
     *
     * <p>Verifies that hostile Ageable entities (zombies) are:</p>
     * <ul>
     * <li>Not affected when the caster's skill level is below 100 (usesModifier &lt; 100)</li>
     * <li>Affected when the caster's skill level is 100 or above (usesModifier &ge; 100)</li>
     * </ul>
     */
    @Test
    void changeAgeTest() {
        World testWorld = mockServer.addSimpleWorld("ChangeEntitySizeAge_" + getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // hostile mob should NOT be affected at low skill (usesModifier = 50 < 100)
        Zombie zombie = (Zombie) testWorld.spawnEntity(targetLocation, EntityType.ZOMBIE);
        ChangeEntitySize spell = (ChangeEntitySize) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 50);
        if (spell.isGrowing())
            zombie.setBaby();
        else
            zombie.setAdult();

        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 50);
        mockServer.getScheduler().performTicks(20);

        if (spell.isGrowing())
            assertFalse(zombie.isAdult(), "hostile entity was affected at low skill");
        else
            assertTrue(zombie.isAdult(), "hostile entity was affected at low skill");

        // hostile mob SHOULD be affected at high skill (usesModifier = 1000 >= 100)
        Location location2 = getNextLocation(testWorld);
        Location targetLocation2 = new Location(testWorld, location2.getX() + 10, location2.getY(), location2.getZ());
        Zombie zombie2 = (Zombie) testWorld.spawnEntity(targetLocation2, EntityType.ZOMBIE);
        if (spell.isGrowing())
            zombie2.setBaby();
        else
            zombie2.setAdult();

        castSpell(caster, location2, targetLocation2, O2PlayerCommon.rightWand, 1000);
        mockServer.getScheduler().performTicks(20);

        if (spell.isGrowing())
            assertTrue(zombie2.isAdult(), "hostile entity was not affected at high skill");
        else
            assertFalse(zombie2.isAdult(), "hostile entity was not made baby at high skill");
    }

    /**
     * Tests that doCheckEffect() affects multiple entities up to the target limit.
     *
     * <p>With default experience (usesModifier = 20), targets = 2. Verifies that both
     * entities within range are changed by the spell.</p>
     */
    @Test
    void multipleTargetTest() {
        World testWorld = mockServer.addSimpleWorld("ChangeEntitySizeMulti_" + getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Cow cow1 = (Cow) testWorld.spawnEntity(targetLocation, EntityType.COW);
        Cow cow2 = (Cow) testWorld.spawnEntity(targetLocation, EntityType.COW);
        ChangeEntitySize spell = (ChangeEntitySize) castSpell(caster, location, targetLocation);
        if (spell.isGrowing()) {
            cow1.setBaby();
            cow2.setBaby();
        }
        else {
            cow1.setAdult();
            cow2.setAdult();
        }

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        if (spell.isGrowing()) {
            assertTrue(cow1.isAdult(), "first entity was not affected");
            assertTrue(cow2.isAdult(), "second entity was not affected");
        }
        else {
            assertFalse(cow1.isAdult(), "first entity was not affected");
            assertFalse(cow2.isAdult(), "second entity was not affected");
        }
    }

    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
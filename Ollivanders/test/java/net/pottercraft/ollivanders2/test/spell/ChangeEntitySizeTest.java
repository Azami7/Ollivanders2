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
 * Base test class for {@link ChangeEntitySize} spells (ENGORGIO, REDUCIO), covering baby/adult toggling of Ageable
 * creatures, Slime resizing, the hostile-mob skill restriction, and multi-target limits.
 *
 * @author Azami7
 */
abstract public class ChangeEntitySizeTest extends O2SpellTestSuper {
    /**
     * Verify the spell toggles a peaceful cow between baby and adult in the growing/shrinking direction.
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
     * Verify the spell grows or shrinks a Slime's size in the spell's direction.
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
     * Verify a hostile mob is unaffected below skill 100 but affected at skill 100 or above.
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
     * Verify the spell affects multiple entities in range up to its target limit.
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

    /**
     * No-op: size-change spells have no revert action.
     */
    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
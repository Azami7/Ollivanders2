package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.DELETRIUS}.
 *
 * @author Azami7
 */
public class DeletriusTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DELETRIUS;
    }

    /**
     * Verify DELETRIUS removes an area effect cloud within range and then kills itself.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Entity cloud = testWorld.spawnEntity(targetLocation, EntityType.AREA_EFFECT_CLOUD);

        O2Spell spell = castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(15);

        assertTrue(cloud.isDead(), "AreaEffectCloud was not removed by DELETRIUS");
        assertTrue(spell.isKilled(), "DELETRIUS spell was not killed after removing AreaEffectCloud");
    }

    /**
     * Verify DELETRIUS removes all area effect clouds within range, not just the first.
     */
    @Test
    void doCheckEffectRemovesMultipleCloudsTest() {
        World testWorld = mockServer.addSimpleWorld("DeletriusMultipleClouds");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Entity cloud1 = testWorld.spawnEntity(targetLocation, EntityType.AREA_EFFECT_CLOUD);
        Entity cloud2 = testWorld.spawnEntity(new Location(testWorld, targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ()), EntityType.AREA_EFFECT_CLOUD);

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(15);

        assertTrue(cloud1.isDead(), "First AreaEffectCloud was not removed by DELETRIUS");
        assertTrue(cloud2.isDead(), "Second AreaEffectCloud was not removed by DELETRIUS");
    }

    @Override
    @Test
    void revertTest() {
        // deletrius has no revert actions
    }
}
package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.MOV_FOTIA;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link MOV_FOTIA} spell, which creates purple firecracker fireworks.
 *
 * <p>Verifies the spell creates firework rockets at the impact location.</p>
 *
 * @author Azami7
 * @see MOV_FOTIA
 */
public class MovFotiaTest extends StationarySpellTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MOV_FOTIA;
    }

    /** {@inheritDoc} */
    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.TRANQUILLUS;
    }

    /** {@inheritDoc} */
    @Override
    void targetLocationSetup(Location location) {
    }

    /**
     * Verifies that the MOV_FOTIA spell creates a firework rocket at the impact location.
     */
    @Test
    void createStationarySpellTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        MOV_FOTIA movFotia = (MOV_FOTIA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(2);
        assertTrue(movFotia.isKilled());

        List<Entity> entities = EntityCommon.getNearbyEntitiesByType(location, 5, EntityType.FIREWORK_ROCKET);
        assertFalse(entities.isEmpty(), "firework not created");
    }
}

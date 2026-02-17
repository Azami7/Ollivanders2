package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.ACCIO;
import net.pottercraft.ollivanders2.spell.AVADA_KEDAVRA;
import net.pottercraft.ollivanders2.spell.FIENDFYRE;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link PROTEGO_HORRIBILIS} stationary spell.
 *
 * <p>Tests the protego horribilis shield spell, which blocks Dark Arts spells from entering the protected area
 * while allowing the Killing Curse (Avada Kedavra) and non-Dark Arts spells to pass through. Inherits common
 * spell tests from {@link O2StationarySpellTest} and provides spell-specific factory methods for test setup.</p>
 *
 * <p>The test verifies:
 * <ul>
 *   <li>Dark Arts spells are blocked when crossing into the protected area</li>
 *   <li>Avada Kedavra (Killing Curse) bypasses the shield</li>
 *   <li>Non-Dark Arts spells are allowed to cross the shield boundary</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 */
public class ProtegoHorribilisTest extends O2StationarySpellTest {
    /**
     * Gets the spell type being tested.
     *
     * @return {@link O2StationarySpellType#PROTEGO_HORRIBILIS}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.PROTEGO_HORRIBILIS;
    }

    /**
     * Creates a PROTEGO_HORRIBILIS spell instance for testing.
     *
     * <p>Constructs a new protego horribilis spell at the specified location with the minimum radius and duration values.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new PROTEGO_HORRIBILIS spell instance (not null)
     */
    @Override
    PROTEGO_HORRIBILIS createStationarySpell(Player caster, Location location) {
        return new PROTEGO_HORRIBILIS(testPlugin, caster.getUniqueId(), location, PROTEGO_HORRIBILIS.minRadiusConfig, PROTEGO_HORRIBILIS.minDurationConfig);
    }

    /**
     * Tests upkeep behavior (skipped - covered by base class tests).
     *
     * <p>The upkeep method only performs aging, which is already tested comprehensively by the inherited
     * ageAndKillTest() from the base test class.</p>
     */
    @Override
    @Test
    void upkeepTest() {
        // upkeep just calls age(), which is tested by ageAndKillTest()
    }

    /**
     * Tests spell projectile blocking behavior at the shield boundary.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Dark Arts spells (Fiendfyre) are cancelled when moving from outside to inside the protected area</li>
     *   <li>Avada Kedavra (Killing Curse) is allowed to cross the shield boundary</li>
     *   <li>Non-Dark Arts spells (Accio) are allowed to cross the shield boundary</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnSpellProjectileMoveEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_HORRIBILIS.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();

        PROTEGO_HORRIBILIS protegoHorribilis = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoHorribilis);
        mockServer.getScheduler().performTicks(20);

        caster.setLocation(location);
        player.setLocation(outsideLocation);
        assertFalse(protegoHorribilis.isLocationInside(player.getLocation()));

        // prevent dark arts spell from crossing spell boundary
        OllivandersSpellProjectileMoveEvent event = new OllivandersSpellProjectileMoveEvent(player, new FIENDFYRE(testPlugin, player, 1.0), outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "dark arts spell was not cancelled crossing inside spell area");

        // allow Avada Kadavra
        event = new OllivandersSpellProjectileMoveEvent(player, new AVADA_KEDAVRA(testPlugin, player, 1.0), outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "prevented avada kedavra from crossing spell boundary");

        // allow non-dark-arts spells
        event = new OllivandersSpellProjectileMoveEvent(player, new ACCIO(testPlugin, player, 1.0), outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "prevented accio from crossing spell boundary");
    }
}

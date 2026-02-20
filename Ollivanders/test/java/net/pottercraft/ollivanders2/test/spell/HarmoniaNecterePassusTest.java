package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HarmoniaNecterePassusTest extends O2SpellTestSuper {
    /**
     * Test spell-specific set up such as anything in doInitSpell, pass through blocks, move effect data,
     * target material allow and block lists, world guard flags, projectile radius, and any spell-specific settings
     */
    @Override @Test
    void spellConstructionTest() {
        // harmonia only sets world guard flags, which we do not have tests for because we do not have a WorldGuard mock
    }

    /**
     * Test the specific spell functionality
     */
    @Override @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world1");
        Location location1 = new Location(testWorld, 200, 40, 100);
        Location location2 = new Location(testWorld, 220, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        net.pottercraft.ollivanders2.test.stationaryspell.HarmoniaNecterePassusTest.createCabinet(location1, location2);
        net.pottercraft.ollivanders2.test.stationaryspell.HarmoniaNecterePassusTest.createCabinet(location2, location1);

        Location castLocation = new Location(testWorld, 200, 39, 102);

        // we need to set the player facing the cabinet
        caster.setLocation(TestCommon.faceTarget(castLocation, location1));

        // if the cabinets are constructed correctly, we can create the stationary spell
        net.pottercraft.ollivanders2.spell.HARMONIA_NECTERE_PASSUS harmoniaSpell = new net.pottercraft.ollivanders2.spell.HARMONIA_NECTERE_PASSUS(testPlugin, caster, 1.0);
        Ollivanders2API.getSpells().addSpell(caster, harmoniaSpell);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location1, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "harmonia spell killed");
        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location2, O2StationarySpellType.HARMONIA_NECTERE_PASSUS), "twin spell killed");
    }
}

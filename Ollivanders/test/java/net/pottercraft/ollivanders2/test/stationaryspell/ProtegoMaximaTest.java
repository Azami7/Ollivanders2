package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_MAXIMA;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

public class ProtegoMaximaTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.PROTEGO_MAXIMA;
    }

    @Override
    PROTEGO_MAXIMA createStationarySpell(Player caster, Location location) {
        return new PROTEGO_MAXIMA(testPlugin, caster.getUniqueId(), location, PROTEGO_MAXIMA.minRadiusConfig, PROTEGO_MAXIMA.minDurationConfig);
    }

    @Override @Test
    void upkeepTest() {

    }
}

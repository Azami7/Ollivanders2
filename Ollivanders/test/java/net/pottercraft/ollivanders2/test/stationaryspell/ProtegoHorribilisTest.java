package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

public class ProtegoHorribilisTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.PROTEGO_HORRIBILIS;
    }

    @Override
    PROTEGO_HORRIBILIS createStationarySpell(Player caster, Location location) {
        return new PROTEGO_HORRIBILIS(testPlugin, caster.getUniqueId(), location, PROTEGO_HORRIBILIS.minRadiusConfig, PROTEGO_HORRIBILIS.minDurationConfig);
    }

    @Override @Test
    void upkeepTest() {

    }
}

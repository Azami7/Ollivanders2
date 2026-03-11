package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for PROTEGO_DIABOLICA spell.
 *
 * <p>Tests the PROTEGO_DIABOLICA spell functionality including creation, duration/radius calculation,
 * and proper stationary spell setup. The target location is set up with dirt blocks to provide
 * a suitable environment for the spell.</p>
 *
 * @author Azami7
 */
@Isolated
public class ProtegoDiabolicaTest extends StationarySpellTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PROTEGO_DIABOLICA;
    }

    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.PROTEGO_DIABOLICA;
    }

    @Override
    void targetLocationSetup(Location location) {
        World world = location.getWorld();
        assertNotNull(world);

        world.getBlockAt(location).setType(Material.DIRT);
    }
}

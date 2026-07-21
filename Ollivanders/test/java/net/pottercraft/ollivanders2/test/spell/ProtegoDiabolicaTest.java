package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.PROTEGO_DIABOLICA}. Extends {@link StationarySpellTest}
 * for the shared stationary-spell tests.
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA
 */
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

package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MuffliatoTest extends StationarySpellTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MUFFLIATO;
    }

    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.MUFFLIATO;
    }

    @Override
    void targetLocationSetup(Location location) {
        World world = location.getWorld();
        assertNotNull(world);

        world.getBlockAt(location).setType(Material.DIRT);
    }
}

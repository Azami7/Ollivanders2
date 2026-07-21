package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.MOLLIARE} spell. Extends {@link StationarySpellTest} for
 * the shared stationary-spell tests.
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.stationaryspell.MOLLIARE
 */
public class MolliareTest extends StationarySpellTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MOLLIARE;
    }

    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.MOLLIARE;
    }

    /**
     * Sets up a dirt block at the target location for the spell to cast upon.
     *
     * @param location the target location
     */
    @Override
    void targetLocationSetup(Location location) {
        World world = location.getWorld();
        assertNotNull(world);

        world.getBlockAt(location).setType(Material.DIRT);
    }
}

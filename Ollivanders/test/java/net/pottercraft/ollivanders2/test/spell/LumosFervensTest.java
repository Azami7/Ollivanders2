package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.LUMOS_FERVENS} spell.
 *
 * <p>Verifies the spell creates a stationary light source at the target location by testing that
 * a dirt block is properly set up before the spell is cast.</p>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.LUMOS_FERVENS
 * @see net.pottercraft.ollivanders2.stationaryspell.LUMOS_FERVENS
 */
public class LumosFervensTest extends StationarySpellTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LUMOS_FERVENS;
    }

    /** {@inheritDoc} */
    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.LUMOS_FERVENS;
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

package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.PROTEGO_HORRIBILIS} spell.
 *
 * <p>Verifies the spell creates a stationary barrier that destroys spells crossing it by testing that
 * a dirt block is properly set up before the spell is cast.</p>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.PROTEGO_HORRIBILIS
 * @see net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS
 */
public class ProtegoHorribilisTest extends StationarySpellTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PROTEGO_HORRIBILIS;
    }

    /** {@inheritDoc} */
    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.PROTEGO_HORRIBILIS;
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

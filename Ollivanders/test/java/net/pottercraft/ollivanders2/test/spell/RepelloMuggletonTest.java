package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.REPELLO_MUGGLETON} spell.
 *
 * <p>Verifies the spell creates a stationary concealment barrier that hides blocks and players in its radius
 * from those outside of it by testing that a dirt block is properly set up before the spell is cast.</p>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.REPELLO_MUGGLETON
 * @see net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON
 */
public class RepelloMuggletonTest extends StationarySpellTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.REPELLO_MUGGLETON;
    }

    /** {@inheritDoc} */
    @Override
    O2StationarySpellType getStationarySpellType() {
        return O2StationarySpellType.REPELLO_MUGGLETON;
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

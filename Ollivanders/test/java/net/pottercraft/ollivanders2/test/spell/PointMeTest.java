package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.POINT_ME} spell (Four-Point Spell), which rotates the caster
 * to face north.
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.POINT_ME
 */
public class PointMeTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.POINT_ME;
    }

    /**
     * Verifies that the spell rotates the caster to face north and then ends.
     *
     * <p>Starts the caster facing a non-north direction, casts the spell, and confirms the caster's yaw is north and
     * the spell is killed.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        location.setYaw(90); // start facing west so the spell has a non-north yaw to change
        PlayerMock caster = mockServer.addPlayer();

        // cast from == target so castSpell does not re-orient the caster toward a target location
        O2Spell spell = castSpell(caster, location, location);
        mockServer.getScheduler().performTicks(2);

        assertTrue(spell.isKilled(), "spell did not end after rotating the caster");

        // north is yaw 180; a teleport may normalize it to -180, which is the same direction
        assertEquals(180.0f, Math.abs(caster.getLocation().getYaw()), 0.001f, "caster was not rotated to face north");
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
        // POINT_ME has no revert action - the caster's facing is not restored
    }
}
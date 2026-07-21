package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AVIS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Parrot;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link AVIS} spell (Bird-Conjuring Charm), which spawns one bird per tick from the wand tip until its
 * skill-determined bird count is exhausted.
 *
 * @author Azami7
 * @see AVIS
 */
public class AvisTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AVIS;
    }

    /**
     * Count the parrots present in a world.
     *
     * @param world the world to scan
     * @return the number of parrot entities in the world
     */
    private long parrotCount(@NotNull World world) {
        return world.getEntities().stream().filter(entity -> entity instanceof Parrot).count();
    }

    /**
     * Verifies that the spell spawns exactly its computed number of birds and then ends.
     *
     * <p>Reads the bird count the cast resolved to, advances enough ticks for every bird to spawn (one per tick), and
     * confirms that many parrots exist in the world and the spell has been killed.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 1, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        AVIS avis = (AVIS) castSpell(caster, location, targetLocation);
        int expectedBirds = avis.getBirdsRemaining();
        assertTrue(expectedBirds >= 1, "cast should spawn at least one bird");

        // one bird spawns per tick; add a buffer so the spell reaches the kill branch
        mockServer.getScheduler().performTicks(expectedBirds + 5);

        assertTrue(avis.isKilled(), "spell did not finish and end");
        assertEquals(expectedBirds, parrotCount(testWorld), "unexpected number of birds spawned");
    }

    /**
     * Verifies that the bird count scales with caster skill and is limited to [1, maxBirds].
     *
     * <p>The count is read from the spell immediately after creation (before any birds spawn). A very low experience
     * must floor to a single bird, and a very high experience must limit to {@link AVIS#getMaxBirds()}.</p>
     */
    @Test
    void birdCountTest() {
        World testWorld = mockServer.addSimpleWorld("AvisBirdCount");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 1, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // low experience floors at a single bird
        AVIS low = (AVIS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        assertEquals(1, low.getBirdsRemaining(), "low-skill cast should floor to one bird");

        // high experience limits to the maximum
        AVIS high = (AVIS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1000);
        assertEquals(high.getMaxBirds(), high.getBirdsRemaining(), "high-skill cast should limit to maxBirds");
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
        // AVIS has no revert action - conjured birds are not removed
    }
}
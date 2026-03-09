package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.MORSMORDRE;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the MORSMORDRE spell.
 *
 * <p>MORSMORDRE conjures the Dark Mark in the sky, launching a green creeper-effect firework
 * and applying the BAD_OMEN potion effect to nearby players. Tests verify that the spell
 * correctly applies the potion effect within the configured radius and launches the
 * Dark Mark firework as a visual flair effect.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.MORSMORDRE for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
@Isolated
public class MorsmordreTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.MORSMORDRE
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MORSMORDRE;
    }

    /**
     * Test that the Dark Mark firework is spawned when the spell is cast.
     *
     * <p>Verifies that the spell launches the green creeper-effect firework at the
     * spell location as the visual flair effect for the Dark Mark.</p>
     */
    @Test
    void flairTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        MORSMORDRE morsmordre = (MORSMORDRE) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(1);

        Firework firework = null;
        for (Entity entity : morsmordre.getNearbyEntities(2)) { // at 1 tick, the spawned firework should be near the player
            if (entity.getType() == EntityType.FIREWORK_ROCKET)
                firework = (Firework) entity;
        }
        assertNotNull(firework, "Firework was not spawned");
    }
}

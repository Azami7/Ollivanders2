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
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.MORSMORDRE}. Extends {@link AddPotionEffectTest} for the
 * shared potion-effect tests.
 */
public class MorsmordreTest extends AddPotionEffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MORSMORDRE;
    }

    /**
     * Verify casting launches the Dark Mark firework at the spell location.
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

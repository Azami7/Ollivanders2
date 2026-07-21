package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AVADA_KEDAVRA;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AVADA_KEDAVRA}.
 */
public class AvadaKedavraTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AVADA_KEDAVRA;
    }

    /**
     * Verify damage equals the caster's modifier at skill level 1, and a default-level cast kills the target.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock player = mockServer.addPlayer();
        double originalHealth = player.getHealth();
        player.setLocation(targetLocation);

        AVADA_KEDAVRA avadaKedavra = (AVADA_KEDAVRA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        mockServer.getScheduler().performTicks(20);

        assertTrue(avadaKedavra.isKilled(), "avada did not hit player target");
        assertEquals(originalHealth - 1, player.getHealth(), "player health did not go down by expected amount");

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertEquals(0, player.getHealth(), "player health did not go down by expected amount");
        assertTrue(player.isDead(), "player is not dead");

        player.disconnect();
    }

    /**
     * No-op: AVADA_KEDAVRA has no state changes to revert.
     */
    @Override
    @Test
    void revertTest() {
    }
}

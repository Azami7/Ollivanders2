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
 * Tests for the AVADA_KEDAVRA spell.
 *
 * <p>Covers damage scaling by spell level and entity death mechanics.</p>
 */
public class AvadaKedavraTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AVADA_KEDAVRA;
    }

    /**
     * {@inheritDoc}
     *
     * <p>No specific construction tests needed for this spell.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {
    }

    /**
     * Tests damage scaling and target death mechanics.
     *
     * <p>Verifies that:</p>
     *
     * <ul>
     * <li>With spell level 1, damage dealt equals caster's modifier (1 health point)</li>
     * <li>With default spell level, damage is high enough to kill the target</li>
     * </ul>
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

        assertEquals(0, player.getHealth(), "playaer health did not go down by expected amount");
        assertTrue(player.isDead(), "player is not dead");

        player.disconnect();
    }

    /**
     * {@inheritDoc}
     *
     * <p>No state changes to revert for this spell.</p>
     */
    @Override
    @Test
    void revertTest() {
    }
}

package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.LUMOS_DUO;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the LUMOS_DUO spell.
 *
 * <p>Tests spell initialization, duration configuration based on caster skill level, block creation,
 * and proper cleanup when the spell expires or is manually killed.</p>
 *
 * @author test
 */
@Isolated
public class LumosDuoTest extends O2SpellTestSuper {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LUMOS_DUO;
    }

    /**
     * Verifies spell-specific initialization and configuration.
     *
     * <p>Tests that:</p>
     * <ul>
     * <li>Projectile pass-through materials are configured correctly (air only)</li>
     * <li>Spell duration is set to minimum when caster has no experience</li>
     * <li>Spell duration is set to maximum when caster has high skill level</li>
     * </ul>
     */
    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location targetLocation = new Location(testWorld, 110, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        LUMOS_DUO lumosDuo = (LUMOS_DUO) castSpell(caster, location, targetLocation);

        // verify passThrough is only air
        assertEquals(1, lumosDuo.getProjectilePassThroughMaterials().size(), "projectile pass through list not expected size");
        assertTrue(lumosDuo.getProjectilePassThroughMaterials().contains(Material.AIR), "projectile pass through list did not contain Material.AIR");

        // verify spell duration is at minimum when no experience
        assertEquals(LUMOS_DUO.minDuration, lumosDuo.getSpellDuration(), "spell duration not set to expected when skill level at minimum");
        lumosDuo.kill();

        lumosDuo = (LUMOS_DUO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        assertEquals(LUMOS_DUO.maxDuration, lumosDuo.getSpellDuration(), "spell duration not set to expected when skill level at maximum");
        lumosDuo.kill();
    }

    /**
     * Tests spell behavior including block creation and duration countdown.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>No blocks are created in the first 2 ticks</li>
     * <li>One glowstone block is created at tick 3</li>
     * <li>At most maxLineLength blocks are created</li>
     * <li>Spell remains active until full duration expires</li>
     * <li>Spell is killed when duration reaches spellDuration</li>
     * </ul>
     */
    @Override @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 40, 100);
        Location targetLocation = new Location(testWorld, 210, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        LUMOS_DUO lumosDuo = (LUMOS_DUO) castSpell(caster, location, targetLocation);

        // at ticks <= 2, spell does not take effect
        mockServer.getScheduler().performTicks(2);
        assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(lumosDuo).isEmpty(), "blocks changed by lumos duo in the first 2 ticks");

        // at tick 3, 1 block should be changed
        mockServer.getScheduler().performTicks(1);

        assertEquals(1, Ollivanders2API.getBlocks().getBlocksChangedBySpell(lumosDuo).size(), "unexpected number of blocks changed by lumos duo at tick 3");
        assertEquals(Material.GLOWSTONE, Ollivanders2API.getBlocks().getBlocksChangedBySpell(lumosDuo).getFirst().getType(), "block changed to unexpected type");

        mockServer.getScheduler().performTicks(LUMOS_DUO.maxLineLength);
        assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(lumosDuo).size() <= LUMOS_DUO.maxLineLength, "more blocks changed than expected");

        int ticksRemaining = lumosDuo.getSpellDuration() - lumosDuo.getAge();
        mockServer.getScheduler().performTicks(ticksRemaining - 2); // spell should not be killed yet
        assertFalse(lumosDuo.isKilled(), "lumos duo killed before duration expired");

        mockServer.getScheduler().performTicks(3); // make sure spell is killed when duration expires
        assertTrue(lumosDuo.isKilled(), "lumos duo not killed when duration passed");
    }

    /**
     * Tests proper cleanup when the spell is terminated.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>Glowstone blocks created by the spell are reverted to air</li>
     * <li>The spell is removed from the block change tracking</li>
     * <li>Spell is properly marked as killed</li>
     * </ul>
     */
    @Override @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 40, 100);
        Location targetLocation = new Location(testWorld, 310, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        LUMOS_DUO lumosDuo = (LUMOS_DUO) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertFalse(lumosDuo.isKilled());
        assertFalse(Ollivanders2API.getBlocks().getBlocksChangedBySpell(lumosDuo).isEmpty());

        Block block = Ollivanders2API.getBlocks().getBlocksChangedBySpell(lumosDuo).getFirst();

        lumosDuo.kill();
        mockServer.getScheduler().performTicks(1);
        assertTrue(lumosDuo.isKilled());
        assertEquals(Material.AIR, block.getType(), "block not reverted to air when lumos duo killed");
        assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(lumosDuo).isEmpty(), "affected blocks not removed from changed block list");
    }
}

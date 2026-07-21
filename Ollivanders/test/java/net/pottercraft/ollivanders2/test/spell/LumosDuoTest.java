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
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.LUMOS_DUO}.
 */
public class LumosDuoTest extends O2SpellTestSuper {
    @Override @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LUMOS_DUO;
    }

    /**
     * Verify the projectile passes through air only and the duration is set to its min at no skill and its max at
     * high skill.
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
     * Verify no blocks appear in the first 2 ticks, glowstone is laid from tick 3 up to maxLineLength, and the spell
     * stays alive until its duration expires.
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
     * Verify killing the spell reverts its glowstone blocks to air and clears them from block tracking.
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

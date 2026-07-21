package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.OBLIVIATE;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link OBLIVIATE} spell.
 * <p>
 * Verifies OBLIVIATE's behavior: that the projectile is killed when it hits a non-player block,
 * that nearby players lose spell and/or potion knowledge when struck, and that the magnitude of
 * the reduction scales with caster skill. Because OBLIVIATE's behavior is randomized (50/50
 * spell-vs-potion choice, random reduction amount, probabilistic continue), assertions on
 * mastery-level outcomes use bounded comparisons rather than exact end-state.
 * </p>
 *
 * @author Azami7
 */
public class ObliviateTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.OBLIVIATE;
    }

    /**
     * No-op: OBLIVIATE has no spell-specific construction state worth asserting beyond what the
     * casting constructor sets. Skill-based behavior is exercised by {@link #doCheckEffectTest()}.
     */
    @Override
    @Test
    void spellConstructionTest() {
        // no special spell construction code
    }

    /**
     * Cover the projectile and impact paths: a non-player block hit kills the spell; at zero experience a hit reduces
     * a known spell or potion by exactly 1 (the {@code usesModifier < 1} branch); at mastery it reduces the target
     * below its starting level (loose comparison, since the reduction is randomized); at twice mastery both a spell
     * and a potion drop below their starting levels within {@code maxImpact} iterations. Each sub-test resets the
     * target's levels first so assertions don't depend on prior sub-tests.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        OBLIVIATE obliviate = (OBLIVIATE) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(obliviate.isKilled(), "spell not killed when it hit a solid block");

        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);
        O2Player targetO2P = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());
        assertNotNull(targetO2P);
        int level = 5;

        targetO2P.setSpellCount(O2SpellType.LUMOS, level);
        obliviate = (OBLIVIATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        mockServer.getScheduler().performTicks(20);
        assertTrue(obliviate.isKilled(), "spell not killed when player hit");
        assertEquals(level -1, targetO2P.getSpellCount(O2SpellType.LUMOS), "unexpected spell level after obliviate at 0 experience");

        targetO2P.setSpellCount(O2SpellType.LUMOS, level);
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(targetO2P.getSpellCount(O2SpellType.LUMOS) < level, "unexpected spell level after obliviate at mastery experience");

        targetO2P.setPotionCount(O2PotionType.COMMON_ANTIDOTE_POTION, level);
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        mockServer.getScheduler().performTicks(20);
        assertEquals(level -1, targetO2P.getPotionCount(O2PotionType.COMMON_ANTIDOTE_POTION), "unexpected potion level after obliviate at 0 experience");

        targetO2P.setPotionCount(O2PotionType.COMMON_ANTIDOTE_POTION, level);
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(targetO2P.getPotionCount(O2PotionType.COMMON_ANTIDOTE_POTION) < level, "unexpected potion level after obliviate at mastery experience");

        level = O2Spell.spellMasteryLevel;
        targetO2P.resetPotionCount();
        targetO2P.resetSpellCount();
        targetO2P.setPotionCount(O2PotionType.HERBICIDE_POTION, level);
        targetO2P.setSpellCount(O2SpellType.ACCIO, level);
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(targetO2P.getPotionCount(O2PotionType.HERBICIDE_POTION) < level, "unexpected potion count after obliviate at 2x mastery");
        assertTrue(targetO2P.getSpellCount(O2SpellType.ACCIO) < level, "unexpected spell count after obliviate at 2x mastery");
    }

    /**
     * No-op: OBLIVIATE has no revert behavior — the spell-knowledge reduction is permanent.
     */
    @Override
    @Test
    void revertTest() {
        // obliviate has no revert actions
    }
}

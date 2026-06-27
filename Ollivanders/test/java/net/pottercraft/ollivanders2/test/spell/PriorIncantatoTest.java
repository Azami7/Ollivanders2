package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.PRIOR_INCANTATO;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the PRIOR_INCANTATO spell.
 *
 * <p>PRIOR_INCANTATO forces a nearby player's wand to reveal an echo of its last-cast spell. These tests verify
 * the successful reveal (target and nearby witnesses are messaged), the resist path when the caster's skill is too
 * low, and the case where the target's wand has never cast a spell.</p>
 *
 * <p>Success is a percentage roll of {@code usesModifier} against {@code random.nextInt(100)}. The tests make this
 * deterministic by setting the caster's experience: {@code spellMasteryLevel * 2} (modifier 200) always beats the
 * roll, while experience 0 (modifier 0) can never beat it, so the spell always resists.</p>
 *
 * @author Azami7
 */
public class PriorIncantatoTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PRIOR_INCANTATO;
    }

    /**
     * Tests the successful reveal of a target's prior incantation.
     *
     * <p>With the target standing next to the caster and the caster's skill maxed so the success roll always
     * passes, verifies that:</p>
     * <ul>
     * <li>The target is told the echoed spell emits from their own wand</li>
     * <li>The caster (a witness within visible range) is told the echo emits from the target's wand</li>
     * <li>The named spell in both messages is the target's recorded prior incantation</li>
     * <li>The spell is killed after acting on the target</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer("Caster");
        PlayerMock target = mockServer.addPlayer("Target");

        caster.setLocation(location);
        // place the target one block from the caster so the projectile finds them within its default radius
        target.setLocation(TestCommon.getRelativeLocation(location, BlockFace.EAST));

        // record a known prior incantation on the target's wand
        O2Player targetO2P = testPlugin.getO2Player(target);
        targetO2P.setPriorIncantatem(O2SpellType.LUMOS);
        String priorName = O2SpellType.LUMOS.getSpellName();

        // spellMasteryLevel * 2 -> usesModifier 200, which always beats random.nextInt(100)
        PRIOR_INCANTATO priorIncantato = (PRIOR_INCANTATO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(5);

        assertTrue(priorIncantato.isKilled(), "spell was not killed after revealing the prior incantation");

        String targetMessage = TestCommon.getWholeMessage(target);
        assertNotNull(targetMessage, "target did not receive an echo message");
        assertTrue(targetMessage.contains(PRIOR_INCANTATO.echoEmitsFromYourWand), "target did not receive the echo-from-your-wand message");
        assertTrue(targetMessage.contains(priorName), "target's echo message did not name the prior spell");

        String casterMessage = TestCommon.getWholeMessage(caster);
        assertNotNull(casterMessage, "caster did not witness the echo");
        assertTrue(casterMessage.contains(PRIOR_INCANTATO.echoEmitsFrom + target.getName() + PRIOR_INCANTATO.wandPossessiveSuffix), "caster did not witness the echo from the target's wand");
        assertTrue(casterMessage.contains(priorName), "caster's echo message did not name the prior spell");
    }

    /**
     * Tests the resist path when the caster's skill is too low.
     *
     * <p>With the caster's experience at 0 (modifier 0), the success roll can never pass. Verifies that:</p>
     * <ul>
     * <li>The caster is told the target's wand resisted</li>
     * <li>The target receives no echo message</li>
     * <li>The spell is killed after the failed attempt</li>
     * </ul>
     */
    @Test
    void resistTest() {
        World testWorld = mockServer.addSimpleWorld("PriorIncantatoResist");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer("ResistCaster");
        PlayerMock target = mockServer.addPlayer("ResistTarget");

        caster.setLocation(location);
        target.setLocation(TestCommon.getRelativeLocation(location, BlockFace.EAST));

        // a prior incantation is present, but the low-skill roll should prevent it from being revealed
        testPlugin.getO2Player(target).setPriorIncantatem(O2SpellType.LUMOS);

        // experience 0 -> usesModifier 0, which can never beat random.nextInt(100)
        PRIOR_INCANTATO priorIncantato = (PRIOR_INCANTATO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        mockServer.getScheduler().performTicks(5);

        assertTrue(priorIncantato.isKilled(), "spell was not killed after the resisted attempt");

        String casterMessage = TestCommon.getWholeMessage(caster);
        assertNotNull(casterMessage, "caster did not receive the resist message");
        assertTrue(casterMessage.contains(PRIOR_INCANTATO.wandResistsMessage), "caster did not receive the wand-resists message");

        assertNull(TestCommon.getWholeMessage(target), "target received an echo message on a resisted cast");
    }

    /**
     * Tests the case where the target's wand has never cast a spell.
     *
     * <p>With the success roll guaranteed to pass but the target having no recorded prior incantation, verifies
     * that:</p>
     * <ul>
     * <li>The caster is told the target's wand has not cast a spell</li>
     * <li>The target receives no echo message</li>
     * <li>The spell is killed</li>
     * </ul>
     */
    @Test
    void noPriorSpellTest() {
        World testWorld = mockServer.addSimpleWorld("PriorIncantatoNoPrior");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer("NoPriorCaster");
        PlayerMock target = mockServer.addPlayer("NoPriorTarget");

        caster.setLocation(location);
        target.setLocation(TestCommon.getRelativeLocation(location, BlockFace.EAST));

        // target has no prior incantation set (a freshly added player defaults to none)

        PRIOR_INCANTATO priorIncantato = (PRIOR_INCANTATO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(5);

        assertTrue(priorIncantato.isKilled(), "spell was not killed after targeting a wand with no prior spell");

        String casterMessage = TestCommon.getWholeMessage(caster);
        assertNotNull(casterMessage, "caster did not receive the no-prior-spell message");
        assertTrue(casterMessage.contains(PRIOR_INCANTATO.wandNoPriorSpellMessage), "caster did not receive the wand-has-not-cast message");

        assertNull(TestCommon.getWholeMessage(target), "target received an echo message when their wand had no prior spell");
    }

    /**
     * PRIOR_INCANTATO makes no temporary changes that require reversion.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }
}

package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.PYROSVESTIRAS;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for the PYROSVESTIRAS spell (fire extinguishing charm).
 *
 * <p>Tests verify that PYROSVESTIRAS correctly targets and extinguishes fire blocks while
 * rejecting invalid target types. PYROSVESTIRAS converts fire blocks to air (or oak logs for
 * campfires) with a radius determined by player skill level.</p>
 *
 * <p>Test coverage includes:</p>
 * <ul>
 * <li>Valid target materials: FIRE, SOUL_FIRE, CAMPFIRE, SOUL_CAMPFIRE</li>
 * <li>Invalid target materials: Unbreakable materials and non-fire blocks</li>
 * <li>Effect radius scaling based on player skill (formula: usesModifier / 10)</li>
 * <li>Radius clamping to [1, 10] block bounds</li>
 * <li>Permanent transfiguration (extinguished fire does not reignite)</li>
 * <li>Fire conversion behavior (fire → air, campfire → oak log)</li>
 * </ul>
 *
 * @author Azami7
 * @see PYROSVESTIRAS
 */
public class PyrovestirasTest extends BlockTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return PYROSVESTIRAS spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PYROSVESTIRAS;
    }

    /**
     * Returns the valid target material for PYROSVESTIRAS tests.
     *
     * <p>PYROSVESTIRAS only transfigures fire blocks (FIRE, SOUL_FIRE, CAMPFIRE, SOUL_CAMPFIRE).
     * FIRE is used here as the representative valid target.</p>
     *
     * @return FIRE material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.FIRE;
    }

    /**
     * Returns an invalid target material for PYROSVESTIRAS tests.
     *
     * <p>PYROSVESTIRAS cannot transfigure unbreakable materials. Only fire blocks are valid targets.</p>
     *
     * @return First unbreakable material from the global list
     */
    @Override
    @Nullable
    Material getInvalidTargetType() {
        return Ollivanders2Common.getUnbreakableMaterials().getFirst();
    }

    /**
     * Overrides sameMaterialTest because it is not applicable to PYROSVESTIRAS.
     *
     * <p>PYROSVESTIRAS converts fire blocks to air or oak logs. Since air and oak logs
     * are not valid targets for the spell (only fire blocks are in the allow list), this
     * spell can never encounter a situation where it tries to transfigure a block that is
     * already the target material type.</p>
     *
     * <p>Therefore, this test is not applicable and remains empty.</p>
     */
    @Override
    @Test
    void sameMaterialTest() {
        // this cannot happen
    }

    /**
     * Tests that PYROSVESTIRAS effect radius scales correctly with player skill.
     *
     * <p>PYROSVESTIRAS uses the formula: radius = (usesModifier / 10), clamped to [1, 10].
     * This test verifies radius calculation at multiple skill levels:
     * </p>
     * <ul>
     * <li>Skill 5: radius = 0 (clamped to min = 1)</li>
     * <li>Skill 30: radius = 3</li>
     * <li>Skill 200: radius = 20 (clamped to max = 10)</li>
     * </ul>
     */
    @Test
    void pyrovestirasRadiusTest() {
        World testWorld = mockServer.addSimpleWorld("Pyrovestiras Test");
        Location location = new Location(testWorld, 600, 40, 100);
        Location targetLocation = new Location(testWorld, 610, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        testWorld.getBlockAt(targetLocation).setType(Material.CAMPFIRE);
        PYROSVESTIRAS pyrosvestiras = (PYROSVESTIRAS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 5);
        mockServer.getScheduler().performTicks(20);
        assertEquals(pyrosvestiras.getMinRadius(), pyrosvestiras.getEffectRadius(), "radius not set to 1 when skill < 10");
        pyrosvestiras.kill();

        testWorld.getBlockAt(targetLocation).setType(Material.CAMPFIRE);
        pyrosvestiras = (PYROSVESTIRAS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 30);
        mockServer.getScheduler().performTicks(20);
        assertEquals(3, pyrosvestiras.getEffectRadius(), "radius not set to 1 when skill < 10");
        pyrosvestiras.kill();

        testWorld.getBlockAt(targetLocation).setType(Material.CAMPFIRE);
        pyrosvestiras = (PYROSVESTIRAS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 30);
        mockServer.getScheduler().performTicks(20);
        assertEquals(3, pyrosvestiras.getEffectRadius(), "radius not set to 1 when skill < 10");
        pyrosvestiras.kill();

        testWorld.getBlockAt(targetLocation).setType(Material.CAMPFIRE);
        pyrosvestiras = (PYROSVESTIRAS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 200);
        mockServer.getScheduler().performTicks(20);
        assertEquals(pyrosvestiras.getMaxRadius(), pyrosvestiras.getEffectRadius(), "radius not set to 1 when skill < 10");
        pyrosvestiras.kill();
    }
}

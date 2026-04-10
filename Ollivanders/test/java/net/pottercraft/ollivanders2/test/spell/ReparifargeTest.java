package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AGUAMENTI;
import net.pottercraft.ollivanders2.spell.FATUUS_AURUM;
import net.pottercraft.ollivanders2.spell.LAPIFORS;
import net.pottercraft.ollivanders2.spell.MORTUOS_SUSCITATE;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.REPARIFARGE;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link REPARIFARGE}, the untransfiguration counter-spell.
 *
 * <p>Tests cover both block and entity untransfiguration paths, the magic-level gating, and
 * the success-rate clamping:
 * <ul>
 * <li><strong>Non-transfigured block:</strong> Spell hits a normal block — failure message sent</li>
 * <li><strong>Block revert (within level):</strong> Reverts an OWL-level block transfiguration
 *     ({@link FATUUS_AURUM}) from a BEGINNER-level counter-spell (1 level higher, within the +1 tolerance)</li>
 * <li><strong>Block revert blocked (too high):</strong> Cannot revert a NEWT-level block
 *     transfiguration ({@link AGUAMENTI}) — 2 levels above BEGINNER exceeds the +1 tolerance</li>
 * <li><strong>Entity revert (within level):</strong> Reverts an OWL-level entity transfiguration
 *     ({@link LAPIFORS}) via the entity-scanning projectile path</li>
 * <li><strong>Entity revert blocked (too high):</strong> Cannot revert a NEWT-level entity
 *     transfiguration ({@link MORTUOS_SUSCITATE})</li>
 * <li><strong>Success rate clamping:</strong> Verifies {@link REPARIFARGE#getSuccessRate()} is
 *     clamped to [{@link REPARIFARGE#minSuccessRate}, {@link REPARIFARGE#maxSuccessRate}]</li>
 * </ul>
 *
 * @author Azami7
 */
public class ReparifargeTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.REPARIFARGE;
    }

    /**
     * Tests the untransfiguration behavior across block and entity targets at different
     * magic levels.
     *
     * <p>All casts use {@link O2Spell#spellMasteryLevel} experience to guarantee 100% success
     * rate, isolating the level-check logic from the random success check. Sub-tests clean up
     * active transfigurations and message queues between scenarios to prevent state leakage.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        Block targetBlock = targetLocation.getBlock();
        targetBlock.setType(Material.STONE);
        REPARIFARGE reparifarge = (REPARIFARGE) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(reparifarge.isKilled(), "spell not killed when it hit a block");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message when spell hit non-transfigured block");
        TestCommon.clearMessageQueue(caster);

        // can revert a block transfiguration up to 1 level higher
        FATUUS_AURUM fatuusAurum = (FATUUS_AURUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.FATUUS_AURUM);
        mockServer.getScheduler().performTicks(5);
        assertTrue(fatuusAurum.isTransfigured());
        reparifarge = (REPARIFARGE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(reparifarge.isKilled());
        assertTrue(fatuusAurum.isKilled(), "Fatuus Aurum spell not removed by reparifarge");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive success message when spell hit transfigured block");
        TestCommon.clearMessageQueue(caster);

        // cannot revert a block transfiguration 2 levels higher
        targetBlock.setType(Material.AIR);
        AGUAMENTI aguamenti = (AGUAMENTI) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.AGUAMENTI);
        mockServer.getScheduler().performTicks(5);
        assertTrue(aguamenti.isTransfigured());
        Block waterBlock = aguamenti.getTargetBlock();
        assertNotNull(waterBlock);
        reparifarge = (REPARIFARGE) castSpell(caster, location, waterBlock.getLocation(), O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(reparifarge.isKilled());
        assertFalse(aguamenti.isKilled(), "aguamenti spell removed when it should not be");
        aguamenti.kill();
        mockServer.getScheduler().performTicks(5);

        // can revert an entity transfiguration up to 1 level higher
        targetBlock.setType(Material.AIR);
        Item droppedItem = testWorld.dropItem(targetLocation, new ItemStack(Material.IRON_HOE));
        LAPIFORS lapifors = (LAPIFORS) castSpell(caster, location, droppedItem.getLocation(), O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.LAPIFORS);
        mockServer.getScheduler().performTicks(5);
        assertTrue(lapifors.isTransfigured());
        reparifarge = (REPARIFARGE) castSpell(caster, location, lapifors.getLocation(), O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(reparifarge.isKilled());
        assertTrue(lapifors.isKilled(), "lapifors was not removed by reparifarge");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive success message when spell hit transfigured entity");
        TestCommon.clearMessageQueue(caster);

        // cannot revert an entity transfiguration 2 or more levels higher
        droppedItem = testWorld.dropItem(targetLocation, new ItemStack(Material.ROTTEN_FLESH));
        MORTUOS_SUSCITATE mortuosSuscitate = (MORTUOS_SUSCITATE) castSpell(caster, location, droppedItem.getLocation(), O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.MORTUOS_SUSCITATE);
        mockServer.getScheduler().performTicks(5);
        assertTrue(mortuosSuscitate.isTransfigured());
        reparifarge = (REPARIFARGE) castSpell(caster, location, mortuosSuscitate.getLocation(), O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(reparifarge.isKilled());
        assertFalse(mortuosSuscitate.isKilled(), "MORTUOS_SUSCITATE removed by reparifarge");
    }

    /**
     * Tests that the success rate is clamped to the configured min/max bounds.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Experience 0 produces a success rate at or above {@link REPARIFARGE#minSuccessRate}</li>
     * <li>Experience 50 produces exactly 50 (mid-range, no clamping)</li>
     * <li>Experience 150 produces a success rate at or below {@link REPARIFARGE#maxSuccessRate}</li>
     * </ul>
     */
    @Test
    void successRateTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        REPARIFARGE reparifarge = (REPARIFARGE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertTrue(reparifarge.getSuccessRate() >= REPARIFARGE.minSuccessRate);

        reparifarge = (REPARIFARGE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 50);
        assertEquals(50, reparifarge.getSuccessRate());

        reparifarge = (REPARIFARGE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 150);
        assertTrue(reparifarge.getSuccessRate() <= REPARIFARGE.maxSuccessRate);
    }

    /**
     * No-op revert test — REPARIFARGE has no revert actions to undo.
     */
    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}

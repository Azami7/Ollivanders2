package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.LAPIFORS;
import net.pottercraft.ollivanders2.spell.LEPUS_SACCULUM;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link LEPUS_SACCULUM} spell.
 * <p>
 * Verifies LEPUS_SACCULUM's behavior as a permanent rabbit-to-bundle transfiguration. Inherits the
 * shared transfiguration tests from {@link LivingEntityToItemTransfigurationTest} and overrides
 * the cases that do not fit LEPUS_SACCULUM's specific design — most notably the
 * {@link LEPUS_SACCULUM#minSuccessRate 25% minimum success rate} at zero skill, which makes the
 * parent's deterministic-failure assertions inapplicable.
 * </p>
 *
 * @author Azami7
 */
public class LepusSacculumTest extends LivingEntityToItemTransfigurationTest {
    /**
     * {@inheritDoc}
     *
     * @return {@link O2SpellType#LEPUS_SACCULUM}
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LEPUS_SACCULUM;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link EntityType#RABBIT}, the only entity type LEPUS_SACCULUM transfigures
     */
    @NotNull
    @Override
    EntityType getValidEntityType() {
        return EntityType.RABBIT;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link EntityType#CAT}, an arbitrary entity not in LEPUS_SACCULUM's transfiguration map
     */
    @Nullable
    @Override
    EntityType getInvalidEntityType() {
        return EntityType.CAT;
    }

    /**
     * {@inheritDoc}
     *
     * @return null because LEPUS_SACCULUM transfigures rabbits into items, so no valid target entity
     * type is also the spell's output type
     */
    @Nullable
    @Override
    EntityType getSameEntityType() {
        return null;
    }

    /**
     * Test that LEPUS_SACCULUM cannot target a rabbit that is already under an active transfiguration.
     * <p>
     * Because LEPUS_SACCULUM is permanent, the parent's approach of casting the same spell twice on
     * the same target won't work — the first cast removes the original rabbit. Instead, this test
     * uses {@link LAPIFORS} (a non-permanent cat-to-rabbit transfiguration) to produce a transfigured
     * rabbit, then verifies LEPUS_SACCULUM refuses to target it.
     * </p>
     */
    @Override
    @Test
    void canTransfigureAlreadyTransfiguredTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        Item item = testWorld.dropItem(targetLocation, new ItemStack(Material.IRON_HELMET));
        assertNotNull(item);

        LAPIFORS lapifors = (LAPIFORS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.LAPIFORS);
        mockServer.getScheduler().performTicks(20);
        assertTrue(lapifors.isTransfigured());

        Entity rabbit = EntityCommon.getLivingEntityAtLocation(targetLocation);
        assertNotNull(rabbit);
        LEPUS_SACCULUM lepusSacculum = (LEPUS_SACCULUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertFalse(lepusSacculum.isTransfigured(), "transfigured rabbit was targeted by lepus sacculum");
    }

    /**
     * Verify the success rate floor at zero experience.
     * <p>
     * LEPUS_SACCULUM enforces a {@link LEPUS_SACCULUM#minSuccessRate 25% minimum} regardless of
     * caster skill. The parent implementation asserts the spell deterministically fails to transfigure
     * at zero experience, which is not true for LEPUS_SACCULUM (it succeeds ~25% of the time per tick).
     * Instead, this override asserts the success rate equals the configured minimum.
     * </p>
     */
    @Override
    void effectSuccessRateTestZeroExp(PlayerMock caster, Location location, Location targetLocation) {
        LEPUS_SACCULUM lepusSacculum = (LEPUS_SACCULUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertEquals(LEPUS_SACCULUM.minSuccessRate, lepusSacculum.getSuccessRate(), "success rate not set to minSuccessRate when experience is 0");
        lepusSacculum.kill();
    }

    /**
     * No-op override because LEPUS_SACCULUM cannot deterministically fail
     * {@link net.pottercraft.ollivanders2.spell.EntityTransfiguration#canTransfigure(Entity)} at zero
     * experience: the {@link LEPUS_SACCULUM#minSuccessRate 25% floor} means there is always a non-zero
     * chance the success-rate check passes.
     */
    @Override
    void canTransfigureTestZeroExp(PlayerMock caster, Location location, Location targetLocation, Entity target) {
        // lepus sacculum always has at least a 25% chance
    }
}
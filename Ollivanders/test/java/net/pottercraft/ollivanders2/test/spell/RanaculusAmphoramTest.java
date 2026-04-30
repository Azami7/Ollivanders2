package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.RANACULUS_AMPHORAM;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for the {@link RANACULUS_AMPHORAM} spell.
 * <p>
 * Verifies RANACULUS_AMPHORAM's behavior as a permanent tadpole-to-decorated-pot transfiguration.
 * Inherits the shared transfiguration tests from {@link LivingEntityToItemTransfigurationTest} and
 * overrides the cases that do not fit RANACULUS_AMPHORAM's specific design — most notably the
 * {@link RANACULUS_AMPHORAM#minSuccessRate 25% minimum success rate} at zero skill, which makes the
 * parent's deterministic-failure assertions inapplicable.
 * </p>
 *
 * @author Azami7
 */
public class RanaculusAmphoramTest extends LivingEntityToItemTransfigurationTest {
    /**
     * {@inheritDoc}
     *
     * @return {@link O2SpellType#RANACULUS_AMPHORAM}
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.RANACULUS_AMPHORAM;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link EntityType#TADPOLE}, the only entity type RANACULUS_AMPHORAM transfigures
     */
    @NotNull
    @Override
    EntityType getValidEntityType() {
        return EntityType.TADPOLE;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link EntityType#CAT}, an arbitrary entity not in RANACULUS_AMPHORAM's transfiguration map
     */
    @Nullable
    @Override
    EntityType getInvalidEntityType() {
        return EntityType.CAT;
    }

    /**
     * {@inheritDoc}
     *
     * @return null because RANACULUS_AMPHORAM transfigures tadpoles into items, so no valid target
     * entity type is also the spell's output type
     */
    @Nullable
    @Override
    EntityType getSameEntityType() {
        return null;
    }

    /**
     * No-op override because no transfiguration spell produces an {@link EntityType#TADPOLE}, so
     * there is no way to stage an "already transfigured" tadpole target. The trick used in
     * {@code LepusSacculumTest} (cast LAPIFORS to produce a transfigured rabbit before re-targeting)
     * has no analogue here.
     */
    @Override
    @Test
    void canTransfigureAlreadyTransfiguredTest() {
        // not currently testable since no transfiguration results in an EntityType.TADPOLE
    }

    /**
     * Verify the success rate floor at zero experience.
     * <p>
     * RANACULUS_AMPHORAM enforces a {@link RANACULUS_AMPHORAM#minSuccessRate 25% minimum} regardless of
     * caster skill. The parent implementation asserts the spell deterministically fails to transfigure
     * at zero experience, which is not true for RANACULUS_AMPHORAM (it succeeds ~25% of the time per tick).
     * Instead, this override asserts the success rate equals the configured minimum.
     * </p>
     */
    @Override
    void effectSuccessRateTestZeroExp(PlayerMock caster, Location location, Location targetLocation) {
        RANACULUS_AMPHORAM ranaculusAmphoram = (RANACULUS_AMPHORAM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertEquals(RANACULUS_AMPHORAM.minSuccessRate, ranaculusAmphoram.getSuccessRate(), "success rate not set to minSuccessRate when experience is 0");
        ranaculusAmphoram.kill();
    }

    /**
     * No-op override because RANACULUS_AMPHORAM cannot deterministically fail
     * {@link net.pottercraft.ollivanders2.spell.EntityTransfiguration#canTransfigure(Entity)} at zero
     * experience: the {@link RANACULUS_AMPHORAM#minSuccessRate 25% floor} means there is always a
     * non-zero chance the success-rate check passes.
     */
    @Override
    void canTransfigureTestZeroExp(PlayerMock caster, Location location, Location targetLocation, Entity target) {
        // ranaculus amphoram always has at least a 25% chance
    }
}
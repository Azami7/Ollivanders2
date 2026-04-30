package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.SCARABAEUS_FIBULUM;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for the {@link SCARABAEUS_FIBULUM} spell.
 * <p>
 * Verifies SCARABAEUS_FIBULUM's behavior as a permanent endermite-to-polished-blackstone-button
 * transfiguration. Inherits the shared transfiguration tests from
 * {@link LivingEntityToItemTransfigurationTest} and overrides the cases that do not fit
 * SCARABAEUS_FIBULUM's specific design — most notably the
 * {@link SCARABAEUS_FIBULUM#minSuccessRate 25% minimum success rate} at zero skill, which makes
 * the parent's deterministic-failure assertions inapplicable.
 * </p>
 *
 * @author Azami7
 */
public class ScarabaeusFibulumTest extends LivingEntityToItemTransfigurationTest {
    /**
     * {@inheritDoc}
     *
     * @return {@link O2SpellType#SCARABAEUS_FIBULUM}
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.SCARABAEUS_FIBULUM;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link EntityType#ENDERMITE}, the only entity type SCARABAEUS_FIBULUM transfigures
     */
    @NotNull
    @Override
    EntityType getValidEntityType() {
        return EntityType.ENDERMITE;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link EntityType#CAT}, an arbitrary entity not in SCARABAEUS_FIBULUM's transfiguration map
     */
    @Nullable
    @Override
    EntityType getInvalidEntityType() {
        return EntityType.CAT;
    }

    /**
     * {@inheritDoc}
     *
     * @return null because SCARABAEUS_FIBULUM transfigures endermites into items, so no valid target
     * entity type is also the spell's output type
     */
    @Nullable
    @Override
    EntityType getSameEntityType() {
        return null;
    }

    /**
     * No-op override because no transfiguration spell produces an {@link EntityType#ENDERMITE}, so
     * there is no way to stage an "already transfigured" endermite target. The trick used in
     * {@code LepusSacculumTest} (cast LAPIFORS to produce a transfigured rabbit before re-targeting)
     * has no analogue here.
     */
    @Override
    @Test
    void canTransfigureAlreadyTransfiguredTest() {
        // not currently testable since no transfiguration results in an EntityType.ENDERMITE
    }

    /**
     * Verify the success rate floor at zero experience.
     * <p>
     * SCARABAEUS_FIBULUM enforces a {@link SCARABAEUS_FIBULUM#minSuccessRate 25% minimum} regardless of
     * caster skill. The parent implementation asserts the spell deterministically fails to transfigure
     * at zero experience, which is not true for SCARABAEUS_FIBULUM (it succeeds ~25% of the time per tick).
     * Instead, this override asserts the success rate equals the configured minimum.
     * </p>
     */
    @Override
    void effectSuccessRateTestZeroExp(PlayerMock caster, Location location, Location targetLocation) {
        SCARABAEUS_FIBULUM scarabaeusFibulum = (SCARABAEUS_FIBULUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertEquals(SCARABAEUS_FIBULUM.minSuccessRate, scarabaeusFibulum.getSuccessRate(), "success rate not set to minSuccessRate when experience is 0");
        scarabaeusFibulum.kill();
    }

    /**
     * No-op override because SCARABAEUS_FIBULUM cannot deterministically fail
     * {@link net.pottercraft.ollivanders2.spell.EntityTransfiguration#canTransfigure(Entity)} at zero
     * experience: the {@link SCARABAEUS_FIBULUM#minSuccessRate 25% floor} means there is always a
     * non-zero chance the success-rate check passes.
     */
    @Override
    void canTransfigureTestZeroExp(PlayerMock caster, Location location, Location targetLocation, Entity target) {
        // SCARABAEUS_FIBULUM always has at least a 25% chance
    }
}
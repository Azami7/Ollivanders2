package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link ANIMAGUS_INCANTATION}, a passive marker with no tick behavior or event handlers.
 *
 * @see NotPermanentEffectTestSuper
 */
public class AnimagusIncantationTest extends NotPermanentEffectTestSuper {
    /**
     * {@inheritDoc}
     */
    @Override
    ANIMAGUS_INCANTATION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new ANIMAGUS_INCANTATION(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * ANIMAGUS_INCANTATION has no tick behavior to test.
     */
    @Override
    void checkEffectTest() {
    }

    /**
     * ANIMAGUS_INCANTATION has no event handlers to test.
     */
    @Override
    void eventHandlerTests() {}

    /**
     * ANIMAGUS_INCANTATION has no doRemove() cleanup to test.
     */
    @Override
    void doRemoveTest() {}
}

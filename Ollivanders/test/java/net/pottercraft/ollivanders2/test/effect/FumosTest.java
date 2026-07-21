package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FUMOS;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link FUMOS}, a smoke shield that emits campfire smoke particles while blocking projectile spells.
 *
 * @see ShieldSpellEffectTestSuper
 */
public class FumosTest extends ShieldSpellEffectTestSuper {
    /**
     * {@inheritDoc}
     */
    @Override
    FUMOS createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FUMOS(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * FUMOS has no persistent state to clean up when removed.
     */
    @Override
    void doRemoveTest() {
    }
}

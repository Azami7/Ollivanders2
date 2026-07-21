package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.FUMOS_DUO;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link FUMOS_DUO}.
 *
 * @see ShieldSpellEffectTestSuper
 */
public class FumosDuoTest extends ShieldSpellEffectTestSuper {
    @Override
    FUMOS_DUO createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new FUMOS_DUO(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    void doRemoveTest() {
    }
}

package ollivanders.effect;

import net.pottercraft.ollivanders2.effect.O2Effect;

import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class PermanentEffectTestSuper extends EffectTestSuper {
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(10, false, target.getUniqueId());
        assertTrue(effect.isPermanent(), "Effect not permanent when created;");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}

package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.ShieldSpellEffect;
import org.bukkit.entity.Player;

abstract public class ShieldSpellEffectTestSuper extends EffectTestSuper {
    abstract ShieldSpellEffect createEffect(Player target, int durationInTicks, boolean isPermanent);


}

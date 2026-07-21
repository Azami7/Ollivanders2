package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.PotionEffect;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test base for {@link PotionEffect} implementations: verifies the effect applies the expected Minecraft potion
 * type with the correct duration and amplifier, and (via its parent) that it cannot be made permanent.
 *
 * @author Azami7
 * @see NotPermanentEffectTestSuper
 */
abstract public class PotionEffectTest extends NotPermanentEffectTestSuper {
    /**
     * @param isPermanent ignored - potion effects cannot be permanent
     */
    abstract PotionEffect createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * The effect applies its Minecraft potion type to the target with the expected duration and amplifier.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();

        PotionEffect potionEffect = createEffect(target, 100, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(potionEffect);
        int strength = potionEffect.getStrength();

        // age one tick so the effect is processed and the potion is applied
        mockServer.getScheduler().performTicks(1);

        org.bukkit.potion.PotionEffect appliedEffect = target.getPotionEffect(potionEffect.getPotionEffectType());
        assertNotNull(appliedEffect, potionEffect.getPotionEffectType().toString() + " did not apply " + potionEffect.getPotionEffectType());

        assertEquals(potionEffect.getMinDuration(), appliedEffect.getDuration(), "Potion effect did not have expected duration");
        assertEquals(strength, appliedEffect.getAmplifier(), "Potion effect amplifier was not expected value");
    }

    /**
     * Potion effects handle no events.
     */
    void eventHandlerTests() {}

    /**
     * Potion effects have no doRemove() cleanup.
     */
    void doRemoveTest() {}
}

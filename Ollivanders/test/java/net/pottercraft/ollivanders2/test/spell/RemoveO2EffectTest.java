package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.RemoveO2Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Abstract base test class for {@link RemoveO2Effect} spell implementations.
 *
 * <p>Provides shared coverage for spells that remove an {@link O2Effect} from a target player. The test reads the
 * spell's allow list (via {@link RemoveO2Effect#getEffectsAllowList()}) to learn which effect it removes, applies that
 * effect to a target, casts the spell, and verifies the effect is removed. Concrete subclasses only supply the spell
 * type.</p>
 *
 * @see RemoveO2Effect
 * @see O2SpellTestSuper
 */
abstract class RemoveO2EffectTest extends O2SpellTestSuper {
    /**
     * Whether the target currently has the given effect.
     *
     * @param player     the player to check
     * @param effectType the effect type to look for
     * @return true if the player has the effect
     */
    private boolean hasEffect(@NotNull Player player, @NotNull O2EffectType effectType) {
        return Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), effectType);
    }

    /**
     * Apply an effect of the given type to a player for a long, fixed duration.
     *
     * <p>The effect is constructed via the standard {@code (Ollivanders2, int, boolean, UUID)} effect constructor and
     * registered on the player so the spell under test has something to remove.</p>
     *
     * @param player     the player to apply the effect to
     * @param effectType the type of effect to apply
     */
    private void addEffect(@NotNull PlayerMock player, @NotNull O2EffectType effectType) {
        int duration = 60 * Ollivanders2Common.ticksPerSecond; // 60s - longer than the test runs

        try {
            O2Effect effect = (O2Effect) effectType.getClassName()
                    .getConstructor(Ollivanders2.class, int.class, boolean.class, UUID.class)
                    .newInstance(testPlugin, duration, false, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        }
        catch (Exception e) {
            fail("could not construct effect " + effectType + ": " + e.getMessage());
        }
    }

    /**
     * Verifies that the spell removes its allowed effect from a target player.
     *
     * <p>Applies the spell's first allow-listed effect to a target, casts the spell at the target with mastery-level
     * skill (so the success check reliably passes), and confirms the effect is removed and the spell resolves.</p>
     */
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);

        // cast (but do not resolve yet) so the spell's allow list can be read
        RemoveO2Effect spell = (RemoveO2Effect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        List<O2EffectType> allowList = spell.getEffectsAllowList();
        assertFalse(allowList.isEmpty(), "spell has no removable effects configured");
        O2EffectType effectType = allowList.getFirst();

        // give the target the effect so there is something to remove
        addEffect(target, effectType);
        assertTrue(hasEffect(target, effectType), "effect was not applied to the target");

        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not resolve on the target");
        assertFalse(hasEffect(target, effectType), "effect was not removed from the target");
    }

    @Override
    @Test
    void revertTest() {
        // RemoveO2Effect spells have no revert action - removed effects are not restored
    }
}
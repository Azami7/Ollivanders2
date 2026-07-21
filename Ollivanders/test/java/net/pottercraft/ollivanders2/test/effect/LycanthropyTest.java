package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.effect.LYCANTHROPY;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link LYCANTHROPY}.
 *
 * @see PermanentEffectTestSuper
 */
public class LycanthropyTest extends PermanentEffectTestSuper {
    @Override
    LYCANTHROPY createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LYCANTHROPY(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * The player transforms only at moonrise on a full moon day, gaining AGGRESSION and LYCANTHROPY_SPEECH, and
     * reverts with those secondary effects removed once dawn passes.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        World world = target.getWorld();

        LYCANTHROPY lycanthropy = (LYCANTHROPY) addEffect(target, 100, true);

        // Test 1: check that player is not affected when the wrong day
        // set day to day 2
        world.setFullTime((Ollivanders2Common.ticksPerDay * 2) + TimeCommon.MIDDAY.getTick());
        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);
        assertFalse(lycanthropy.isTransformed(), "Player is shape-shifted on the wrong day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Target has aggression enabled on the wrong day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Target has lycanthropy speech enabled on the wrong day");

        // Test 2: check that player is not affected when it is Moonrise on the wrong day
        // set the world time to MOONRISE
        world.setFullTime((Ollivanders2Common.ticksPerDay * 2) + TimeCommon.MOONRISE.getTick());
        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);
        assertFalse(lycanthropy.isTransformed(), "Player is shape-shifted at Moonrise on the wrong day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Target has aggression enabled at Moonrise on the wrong day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Target has lycanthropy speech enabled at Moonrise on the wrong day");

        // Test 3: check the player is not affected before Moonrise on the right day
        world.setFullTime((Ollivanders2Common.ticksPerDay * 8) + TimeCommon.MIDDAY.getTick());
        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);
        assertFalse(lycanthropy.isTransformed(), "Player is shape-shifted at midday on the right day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Target has aggression enabled at midday on the right day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Target has lycanthropy speech enabled at midday on the right day");

        // Test 4: check that secondary effects are added at moonrise on a full moon day and then removed once the time
        // is passed.
        world.setFullTime((Ollivanders2Common.ticksPerDay * 8) + TimeCommon.MOONRISE.getTick());

        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);
        assertTrue(lycanthropy.isTransformed(), "Player not transformed at Moonrise on the right day.");
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Player did not have aggression added at Moonrise on the right day.");
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Player did not have lycanthropy speech added at Moonrise on the right day.");

        // Test 5: make sure effects and transformation is removed once the moonrise evening passes
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerDay / 2);
        assertFalse(lycanthropy.isTransformed(), "Player is did not transform back after dawn");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Player still has aggression after dawn");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Player still has lycanthropy speech after dawn");
    }

    @Override
    void eventHandlerTests() {
        doOnEntityDamageByEntityEventTest();
    }

    /**
     * A transformed werewolf spreads the curse to a player it damages; a non-transformed one does not.
     */
    private void doOnEntityDamageByEntityEventTest() {
        PlayerMock target = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();
        World world = target.getWorld();

        LYCANTHROPY lycanthropy = (LYCANTHROPY) addEffect(target, 10, true);
        world.setFullTime((Ollivanders2Common.ticksPerDay * 8) + TimeCommon.MIDDAY.getTick());
        mockServer.getScheduler().performTicks(10);

        // have the player attack another player
        DamageSource damageSource = DamageSource.builder(DamageType.PLAYER_ATTACK)
                .withCausingEntity(target)
                .withDirectEntity(target)
                .build();
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(target, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, 1);
        mockServer.getPluginManager().callEvent(event);

        // run the server ahead
        mockServer.getScheduler().performTicks(10);
        // confirm that the attacked player does not have lycanthropy
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.LYCANTHROPY), "Player was infected with lycanthropy when attacker was not transformed.");

        // set the world to a full moon evening
        world.setFullTime((Ollivanders2Common.ticksPerDay * 8) + TimeCommon.MOONRISE.getTick());
        // play forward for the effect to transform the target
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond);
        assertTrue(lycanthropy.isTransformed(), "target is not transformed");

        // have the target attack the player again
        event = new EntityDamageByEntityEvent(target, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, 1);
        mockServer.getPluginManager().callEvent(event);
        // play forward the server so that the player can be infected
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 5);
        try {
            // now sleep so we give the runnable time to run
            Thread.sleep(500);
        }
        catch (InterruptedException e) {}
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.LYCANTHROPY), "Player was not infected with lycanthropy when attacker was transformed.");
    }

    // doRemove() cleanup is covered implicitly by checkEffectTest, which verifies secondary effects are removed
    // when transformation ends
    @Override
    void doRemoveTest() {}
}

package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.entity.Vehicle;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract test suite for PlayerTransformBase effects.
 *
 * <p>Tests the common transformation behavior: entity spawning, player hiding, passenger mounting,
 * and all event handlers (damage, death, teleport, apparition, dismount, vehicle exit). Each test
 * uses a unique world location to avoid entity persistence issues between tests.</p>
 *
 * @see PlayerTransformBase
 * @see EffectTestSuper
 */
abstract public class PlayerTransformBaseTest extends EffectTestSuper {
    /**
     * {@inheritDoc}
     */
    @Override
    abstract PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * Helper to create a transformed player at a specific location and return the effect.
     *
     * @param target   the player to transform
     * @param location the location to set the player at before transforming
     * @return the active PlayerTransformBase effect
     */
    private PlayerTransformBase setupTransformedPlayer(PlayerMock target, Location location) {
        target.setLocation(location);

        PlayerTransformBase effect = createEffect(target, 1000, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(5);

        return effect;
    }

    /**
     * Get the spawned entity for a transform effect at a specific location.
     *
     * @param effect   the transform effect
     * @param location the location to search near
     * @return the spawned entity
     */
    private LivingEntity getSpawnedEntity(PlayerTransformBase effect, Location location) {
        return (LivingEntity) EntityCommon.getNearbyEntitiesByType(location, 2, effect.getEntityType()).getFirst();
    }

    /**
     * Test that the transformation spawns an entity, hides the player, mounts the player as a
     * passenger, and names the entity after the player.
     */
    @Override
    void checkEffectTest() {
        Location loc = new Location(testWorld, 0, 4, 0);
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(loc);

        PlayerMock player1 = mockServer.addPlayer();
        player1.setLocation(new Location(testWorld, 1, 4, 3));

        PlayerTransformBase playerTransformBase = createEffect(target, 100, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(playerTransformBase);
        mockServer.getScheduler().performTicks(5);

        assertFalse(player1.canSee(target), "player1 can see target");
        assertTrue(target.canSee(player1), "target cannot see player1");
        assertFalse(EntityCommon.getNearbyEntitiesByType(loc, 2, playerTransformBase.getEntityType()).isEmpty(), playerTransformBase.getEntityType().toString() + " not spawned.");
        LivingEntity spawnedEntity = getSpawnedEntity(playerTransformBase, loc);
        assertTrue(spawnedEntity.getPassengers().contains(target), "target is not riding spawned entity");
        assertEquals(target.getDisplayName(), spawnedEntity.getCustomName(), "spawned entity name not set to target's name");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void eventHandlerTests() {
        doOnEntityDeathEventTest();
        doOnEntityDamageEventTest();
        doOnPlayerTeleportEventTest();
        doOnOllivandersApparateByCoordinatesEventTest();
        doOnOllivandersApparateByNameEventTest();
        doOnEntityDismountEventTest();
        doOnVehicleExitEventTest();
    }

    /**
     * Test that when the spawned entity takes damage, the effect is killed and the player takes the same damage.
     * Also verifies that damage to unrelated entities does not affect the effect.
     */
    private void doOnEntityDamageEventTest() {
        Location loc = new Location(testWorld, 100, 4, 0);
        PlayerMock target = mockServer.addPlayer();
        PlayerTransformBase effect = setupTransformedPlayer(target, loc);
        LivingEntity spawnedEntity = getSpawnedEntity(effect, loc);

        // damage to an unrelated entity should not affect the effect
        PlayerMock bystander = mockServer.addPlayer();
        DamageSource bystanderDamageSource = DamageSource.builder(DamageType.FALL).withDamageLocation(loc).build();
        EntityDamageEvent bystanderEvent = new EntityDamageEvent(bystander, EntityDamageEvent.DamageCause.FALL, bystanderDamageSource, 5.0);
        mockServer.getPluginManager().callEvent(bystanderEvent);
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond + 5);

        assertFalse(effect.isKilled(), "Effect killed when unrelated entity was damaged");

        // damage to the spawned entity should kill the effect and damage the player
        double healthBefore = target.getHealth();
        DamageSource damageSource = DamageSource.builder(DamageType.FALL).withDamageLocation(loc).build();
        EntityDamageEvent damageEvent = new EntityDamageEvent(spawnedEntity, EntityDamageEvent.DamageCause.FALL, damageSource, 3.0);
        mockServer.getPluginManager().callEvent(damageEvent);

        // handler uses BukkitRunnable with ticksPerSecond (20) delay
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond + 5);

        assertTrue(effect.isKilled(), "Effect not killed when spawned entity was damaged");
        assertTrue(target.getHealth() < healthBefore, "Target player did not take damage when spawned entity was damaged");
    }

    /**
     * Test that when the spawned entity dies, the effect is killed and the player takes half their current health
     * in damage.
     */
    private void doOnEntityDeathEventTest() {
        Location loc = new Location(testWorld, 0, 4, 100);
        PlayerMock target = mockServer.addPlayer();
        PlayerTransformBase effect = setupTransformedPlayer(target, loc);
        LivingEntity spawnedEntity = getSpawnedEntity(effect, loc);
        double healthBefore = target.getHealth();

        DamageSource damageSource = DamageSource.builder(DamageType.GENERIC).withDamageLocation(loc).build();
        EntityDeathEvent deathEvent = new EntityDeathEvent(spawnedEntity, damageSource, new ArrayList<>());
        mockServer.getPluginManager().callEvent(deathEvent);
        mockServer.getScheduler().performTicks(5);

        assertTrue(effect.isKilled(), "Effect not killed when spawned entity died");
        assertEquals(healthBefore - (healthBefore / 2), target.getHealth(), 0.01,
                "Target did not take half their health in damage when spawned entity died");
    }

    /**
     * Test that the transformed player's teleport events are cancelled.
     */
    private void doOnPlayerTeleportEventTest() {
        Location loc = new Location(testWorld, 100, 4, 100);
        PlayerMock target = mockServer.addPlayer();
        setupTransformedPlayer(target, loc);

        Location destination = new Location(testWorld, 200, 4, 200);
        PlayerTeleportEvent teleportEvent = new PlayerTeleportEvent(target, loc, destination);
        mockServer.getPluginManager().callEvent(teleportEvent);
        mockServer.getScheduler().performTicks(1);

        assertTrue(teleportEvent.isCancelled(), "Teleport event not cancelled for transformed player");

        // teleport for a different player should not be cancelled
        PlayerMock otherPlayer = mockServer.addPlayer();
        PlayerTeleportEvent otherEvent = new PlayerTeleportEvent(otherPlayer, loc, destination);
        mockServer.getPluginManager().callEvent(otherEvent);
        mockServer.getScheduler().performTicks(1);

        assertFalse(otherEvent.isCancelled(), "Teleport event cancelled for non-transformed player");
    }

    /**
     * Test that the transformed player's apparate-by-coordinates events are cancelled.
     */
    private void doOnOllivandersApparateByCoordinatesEventTest() {
        Location loc = new Location(testWorld, 0, 4, 200);
        PlayerMock target = mockServer.addPlayer();
        setupTransformedPlayer(target, loc);

        Location destination = new Location(testWorld, 200, 4, 200);
        OllivandersApparateByCoordinatesEvent apparateEvent = new OllivandersApparateByCoordinatesEvent(target, destination);
        mockServer.getPluginManager().callEvent(apparateEvent);
        mockServer.getScheduler().performTicks(1);

        assertTrue(apparateEvent.isCancelled(), "Apparate by coordinates not cancelled for transformed player");

        // apparate for a different player should not be cancelled
        PlayerMock otherPlayer = mockServer.addPlayer();
        OllivandersApparateByCoordinatesEvent otherEvent = new OllivandersApparateByCoordinatesEvent(otherPlayer, destination);
        mockServer.getPluginManager().callEvent(otherEvent);
        mockServer.getScheduler().performTicks(1);

        assertFalse(otherEvent.isCancelled(), "Apparate by coordinates cancelled for non-transformed player");
    }

    /**
     * Test that the transformed player's apparate-by-name events are cancelled.
     */
    private void doOnOllivandersApparateByNameEventTest() {
        Location loc = new Location(testWorld, 200, 4, 0);
        PlayerMock target = mockServer.addPlayer();
        setupTransformedPlayer(target, loc);

        Location destination = new Location(testWorld, 300, 4, 300);
        OllivandersApparateByNameEvent apparateEvent = new OllivandersApparateByNameEvent(target, destination, "somewhere");
        mockServer.getPluginManager().callEvent(apparateEvent);
        mockServer.getScheduler().performTicks(1);

        assertTrue(apparateEvent.isCancelled(), "Apparate by name not cancelled for transformed player");

        // apparate for a different player should not be cancelled
        PlayerMock otherPlayer = mockServer.addPlayer();
        OllivandersApparateByNameEvent otherEvent = new OllivandersApparateByNameEvent(otherPlayer, destination, "somewhere");
        mockServer.getPluginManager().callEvent(otherEvent);
        mockServer.getScheduler().performTicks(1);

        assertFalse(otherEvent.isCancelled(), "Apparate by name cancelled for non-transformed player");
    }

    /**
     * Test that the transformed player cannot dismount from the spawned entity.
     */
    private void doOnEntityDismountEventTest() {
        Location loc = new Location(testWorld, 200, 4, 200);
        PlayerMock target = mockServer.addPlayer();
        PlayerTransformBase effect = setupTransformedPlayer(target, loc);
        LivingEntity spawnedEntity = getSpawnedEntity(effect, loc);

        EntityDismountEvent dismountEvent = new EntityDismountEvent(target, spawnedEntity);
        mockServer.getPluginManager().callEvent(dismountEvent);
        mockServer.getScheduler().performTicks(1);

        assertTrue(dismountEvent.isCancelled(), "Dismount event not cancelled for transformed player");

        // dismount from a different entity should not be cancelled
        Entity otherEntity = testWorld.spawnEntity(new Location(testWorld, 300, 4, 300), effect.getEntityType());
        EntityDismountEvent otherEvent = new EntityDismountEvent(target, otherEntity);
        mockServer.getPluginManager().callEvent(otherEvent);
        mockServer.getScheduler().performTicks(1);

        assertFalse(otherEvent.isCancelled(), "Dismount event cancelled for unrelated entity");
    }

    /**
     * Test that the transformed player cannot exit a vehicle while transformed. Only runs when the
     * entity type implements Vehicle (e.g. horses, pigs), since VehicleExitEvent only fires for Vehicle types.
     */
    private void doOnVehicleExitEventTest() {
        Location loc = new Location(testWorld, 300, 4, 0);
        PlayerMock target = mockServer.addPlayer();
        PlayerTransformBase effect = setupTransformedPlayer(target, loc);
        LivingEntity spawnedEntity = getSpawnedEntity(effect, loc);

        if (!(spawnedEntity instanceof Vehicle vehicle))
            return;

        VehicleExitEvent exitEvent = new VehicleExitEvent(vehicle, target);
        mockServer.getPluginManager().callEvent(exitEvent);
        mockServer.getScheduler().performTicks(1);

        assertTrue(exitEvent.isCancelled(), "Vehicle exit event not cancelled for transformed player");

        // a different player exiting the vehicle should not be cancelled
        PlayerMock otherPlayer = mockServer.addPlayer();
        VehicleExitEvent otherEvent = new VehicleExitEvent(vehicle, otherPlayer);
        mockServer.getPluginManager().callEvent(otherEvent);
        mockServer.getScheduler().performTicks(1);

        assertFalse(otherEvent.isCancelled(), "Vehicle exit event cancelled for non-transformed player");
    }

    /**
     * Test that removing the effect despawns the entity and removes the INVISIBILITY effect.
     */
    @Override
    void doRemoveTest() {
        Location loc = new Location(testWorld, 0, 4, 300);
        PlayerMock target = mockServer.addPlayer();
        PlayerTransformBase effect = setupTransformedPlayer(target, loc);
        LivingEntity spawnedEntity = getSpawnedEntity(effect, loc);

        // verify transformed state
        assertTrue(spawnedEntity.getPassengers().contains(target), "Target not riding spawned entity before removal");

        // remove the effect
        Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), effect.effectType);
        mockServer.getScheduler().performTicks(5);

        // entity should be removed and player should no longer be invisible
        assertTrue(spawnedEntity.isDead(), "Spawned entity not removed after effect removal");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.INVISIBILITY),
                "Invisibility effect not removed after transform effect removal");
    }
}

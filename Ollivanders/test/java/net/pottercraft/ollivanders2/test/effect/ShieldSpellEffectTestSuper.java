package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.effect.ShieldSpellEffect;
import net.pottercraft.ollivanders2.spell.ALOHOMORA;
import net.pottercraft.ollivanders2.spell.AVADA_KEDAVRA;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link ShieldSpellEffect} shields. Covers the shared shield behavior — level-based spell
 * blocking, entity-targeting prevention, and logout cleanup — that subclasses inherit; they may override to add
 * variant-specific checks.
 *
 * @see ShieldSpellEffect
 * @see EffectTestSuper
 */
abstract public class ShieldSpellEffectTestSuper extends EffectTestSuper {
    @Override
    abstract ShieldSpellEffect createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * No-op: a shield's only per-tick behavior is particle flair, which MockBukkit cannot observe. Subclasses with
     * other tick behavior (e.g. PROTEGO projectile removal) override this.
     */
    @Override
    void checkEffectTest() {
    }

    /**
     * Runs the shared shield event-handler checks: spell blocking, logout cleanup, and entity-targeting prevention.
     */
    @Override
    void eventHandlerTests() {
        doOnOllivandersSpellProjectileMoveEventTest();
        doOnPlayerQuitEventTest();
        doOnEntityTargetEventTest();
    }

    /**
     * The shield cancels a low-level spell moving inbound into its radius, but not one moving outbound, nor one whose
     * level exceeds the shield's protection.
     */
    void doOnOllivandersSpellProjectileMoveEventTest() {
        PlayerMock target = mockServer.addPlayer();
        Location targetLocation = new Location(testWorld, 0, 4, 0);
        target.setLocation(targetLocation);
        ShieldSpellEffect shieldSpellEffect = (ShieldSpellEffect) addEffect(target, 100, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(shieldSpellEffect);
        mockServer.getScheduler().performTicks(10);

        // create a separate player who will cast the spells
        PlayerMock caster = mockServer.addPlayer();

        // check that a BEGINNER level spell moving from outside to inside the shield is stopped
        Location outsideLocation = new Location(targetLocation.getWorld(), targetLocation.getX() + shieldSpellEffect.getRadius() + 1, targetLocation.getY(), targetLocation.getZ());
        Location insideLocation = new Location(targetLocation.getWorld(), targetLocation.getX() + shieldSpellEffect.getRadius() - 1, targetLocation.getY(), targetLocation.getZ());
        OllivandersSpellProjectileMoveEvent event = new OllivandersSpellProjectileMoveEvent(caster, new ALOHOMORA(testPlugin, caster, 1.0), outsideLocation, insideLocation);
        mockServer.getPluginManager().callEvent(event);
        assertTrue(event.isCancelled(), "OllivandersSpellProjectileMoveEvent was not canceled when ALOHOMORA moved from outside to inside the shield area");

        // check that a spell moving from inside the shield to outside is not stopped
        event = new OllivandersSpellProjectileMoveEvent(caster, new ALOHOMORA(testPlugin, caster, 1.0), insideLocation, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled(), "OllivandersSpellProjectileMoveEvent was canceled when ALOHOMORA moved from inside to outside the shield area");

        // check that a EXPERT level spell moving from outside to inside the shield is not stopped when the shield level is 2 or more lower than EXPERT
        if ((shieldSpellEffect.effectType.getLevel().ordinal() + 1) < MagicLevel.EXPERT.ordinal()) {
            event = new OllivandersSpellProjectileMoveEvent(caster, new AVADA_KEDAVRA(testPlugin, caster, 1.0), insideLocation, outsideLocation);
            mockServer.getPluginManager().callEvent(event);
            assertFalse(event.isCancelled(), "OllivandersSpellProjectileMoveEvent was canceled when AVADA_KEDAVRA moved from inside to outside the shield area");
        }
    }

    /**
     * The shield is killed when the protected player logs off.
     */
    void doOnPlayerQuitEventTest() {
        PlayerMock target = mockServer.addPlayer();
        ShieldSpellEffect effect = (ShieldSpellEffect) addEffect(target, 100, false);

        // log the player off
        target.disconnect();

        assertTrue(effect.isKilled(), effect.effectType.toString() + " not killed when target logged off");
    }

    /**
     * The shield cancels an entity's attempt to target the protected player.
     */
    void doOnEntityTargetEventTest() {
        PlayerMock target = mockServer.addPlayer();
        Location targetLocation = target.getLocation();
        Bee bee1 = testWorld.spawn(new Location(targetLocation.getWorld(), targetLocation.getX() + 2, targetLocation.getY(), targetLocation.getZ()), Bee.class);

        ShieldSpellEffect effect = (ShieldSpellEffect) addEffect(target, 100, false);

        EntityTargetEvent event = new EntityTargetEvent(bee1, target, EntityTargetEvent.TargetReason.UNKNOWN);
        mockServer.getPluginManager().callEvent(event);
        assertTrue(event.isCancelled(), "EntityTargetEvent not canceled by " + effect.effectType);
    }

    /**
     * Create a shielded player at the given coordinates, for distance-based tests.
     *
     * @return {@code [PlayerMock, ShieldSpellEffect]}
     */
    protected Object[] setupShieldedPlayerAtLocation(double x, double y, double z) {
        PlayerMock target = mockServer.addPlayer();
        Location targetLocation = new Location(testWorld, x, y, z);
        target.setLocation(targetLocation);
        ShieldSpellEffect effect = (ShieldSpellEffect) addEffect(target, 100, false);
        return new Object[]{target, effect};
    }

}

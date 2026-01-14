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
 * Parent test class for all shield spell effects.
 *
 * <p>ShieldSpellEffectTestSuper provides a test framework for shield spell effects that block incoming
 * spells and entity targeting. This base class handles common shield mechanics testing such as spell
 * blocking based on level, entity targeting prevention, and player disconnection cleanup.</p>
 *
 * <p>Subclasses implement specific shield variants (passive shields like FUMOS, active shields like PROTEGO)
 * and can override default tests to add effect-specific behavior validation.</p>
 *
 * @see ShieldSpellEffect for the effect base class being tested
 */
abstract public class ShieldSpellEffectTestSuper extends EffectTestSuper {
    /**
     * Create a shield spell effect for testing.
     *
     * <p>This abstract method must be implemented by subclasses to instantiate the specific shield
     * effect type being tested. Subclasses will return their concrete implementation (e.g., FUMOS, PROTEGO).</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new shield spell effect of the subclass's type
     */
    @Override
    abstract ShieldSpellEffect createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * Test basic shield spell effect behavior.
     *
     * <p>Validates shield-specific behavior. Subclasses can override this method to add effect-specific
     * testing (e.g., PROTEGO tests projectile removal in checkEffectTest()).
     * Note: Particle effects cannot be fully tested with MockBukkit.</p>
     */
    @Override
    void checkEffectTest() {
        // cannot test flair with MockBukkit because they haven't implemented checking for particles
    }

    /**
     * Run all event handler tests for shield spell effects.
     *
     * <p>Tests the three common event handlers that shield spells implement: spell blocking,
     * player quit cleanup, and entity targeting prevention. Subclasses can override to add
     * effect-specific event handler tests (e.g., PROTEGO tests projectile launch and hit events).</p>
     */
    @Override
    void eventHandlerTests() {
        doOnOllivandersSpellProjectileMoveEventTest();
        doOnPlayerQuitEventTest();
        doOnEntityTargetEventTest();
    }

    /**
     * Test that spell projectiles are blocked by the shield.
     *
     * <p>Validates three spell blocking scenarios:</p>
     * <ul>
     * <li>BEGINNER level spells moving into the shield radius are cancelled</li>
     * <li>Spells exiting the shield radius are NOT cancelled (only inbound blocking)</li>
     * <li>EXPERT level spells that exceed the shield's protective level are NOT blocked</li>
     * </ul>
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
     * Test that shield effects are cleaned up when the protected player logs off.
     *
     * <p>Validates that the shield effect is properly killed (marked as inactive) when the player
     * it protects disconnects from the server. This ensures that the effect doesn't persist in memory
     * or continue running after the protected player is no longer online, preventing resource leaks
     * and ensuring proper lifecycle management.</p>
     */
    void doOnPlayerQuitEventTest() {
        PlayerMock target = mockServer.addPlayer();
        ShieldSpellEffect effect = (ShieldSpellEffect) addEffect(target, 100, false);

        // log the player off
        target.disconnect();

        assertTrue(effect.isKilled(), effect.effectType.toString() + " not killed when target logged off");
    }

    /**
     * Test that the shield prevents entities from targeting the protected player.
     *
     * <p>Validates that when an entity (such as a bee) attempts to target the shielded player,
     * the shield blocks this targeting by cancelling the EntityTargetEvent. This ensures that
     * the shield provides comprehensive protection not only against spell projectiles but also
     * against mob and creature targeting behavior.</p>
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
     * Helper method to set up a shielded player at a specific location.
     *
     * <p>Creates a new player, sets their location, applies a shield effect, and returns the effect.
     * Used to reduce boilerplate in projectile and other distance-based tests.</p>
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return an array containing [0] the PlayerMock and [1] the ShieldSpellEffect
     */
    protected Object[] setupShieldedPlayerAtLocation(double x, double y, double z) {
        PlayerMock target = mockServer.addPlayer();
        Location targetLocation = new Location(testWorld, x, y, z);
        target.setLocation(targetLocation);
        ShieldSpellEffect effect = (ShieldSpellEffect) addEffect(target, 100, false);
        return new Object[]{target, effect};
    }

}

package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.PlayerChangeSizeSuper;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.attribute.AttributeInstanceMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract test base class for player size-changing effects (SWELLING and SHRINKING).
 *
 * <p>PlayerChangeSizeTestSuper provides comprehensive testing for effects that modify a player's scale attribute.
 * This base class handles the complexity of testing scale modifications, including attribute initialization
 * (since MockBukkit doesn't provide it by default), effect application, stacking behavior, and cleanup.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Scale attribute modification - verifies the effect changes the player's scale to the expected value</li>
 * <li>Effect stacking behavior - verifies that applying a second size effect removes the first and applies the multiplier correctly</li>
 * <li>Effect cleanup - verifies that removing the effect resets the player to normal scale (1.0)</li>
 * <li>Scale bounds - verifies that scales are clamped to [0.25, 4.0] as per implementation</li>
 * </ul>
 *
 * @author Azami7
 * @see PlayerChangeSizeSuper for the effect implementation being tested
 * @see EffectTestSuper for the base testing framework
 */
public abstract class PlayerChangeSizeTestSuper extends EffectTestSuper {
    /**
     * Create a size-changing effect for testing.
     *
     * <p>Abstract method that concrete subclasses must implement to instantiate their specific
     * size-changing effect (SWELLING, SHRINKING, etc.). This allows the base class to test
     * the common size-changing behavior without knowing the specific effect type.</p>
     *
     * @param target          the player to apply the size-changing effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return the newly created PlayerChangeSizeSuper instance
     */
    @Override
    abstract PlayerChangeSizeSuper createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * Test that size-changing effects correctly modify a player's scale attribute.
     * <p>
     * This test validates the core functionality of size-changing effects (SWELLING, SHRINKING):
     * <ul>
     * <li>Creates a player and registers the SCALE attribute (MockBukkit doesn't provide this by default)</li>
     * <li>Records the original scale (1.0, normal size)</li>
     * <li>Applies a size-changing effect with a 5-second duration</li>
     * <li>Advances the server scheduler by 5 ticks to allow the effect's startEffect runnable to execute</li>
     * <li>Verifies the effect's internal state reflects transformation via {@link PlayerChangeSizeSuper#isTransformed()}</li>
     * <li>Verifies the player's actual scale attribute changed</li>
     * <li>Verifies the new scale equals the effect's scale multiplier (when starting from 1.0)</li>
     * <li>Calls {@link #changeSizeEffectStacking()} to validate stacking behavior with multiple effects</li>
     * </ul>
     * </p>
     */
    @Override
    void checkEffectTest() {
        Ollivanders2.debug = true;
        PlayerMock target = mockServer.addPlayer();

        // we need to register the scale attribute since it is not set by default by MockBukkit
        registerScaleAttribute(target);

        AttributeInstance originalScaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(originalScaleAttribute, "originalScaleAttribute was null");
        double originalScale = originalScaleAttribute.getBaseValue();

        PlayerChangeSizeSuper effect = (PlayerChangeSizeSuper) addEffect(target, Ollivanders2Common.ticksPerSecond * 5, false);
        // advance the server make sure the startEffect runnable has executed - it is on a 5-tick delay
        mockServer.getScheduler().performTicks(5);

        // verify the effect thinks the player has transformed
        assertTrue(effect.isTransformed(), effect.effectType.toString() + " did not transform player");

        // check player's actual scale to see if it changed
        AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(scaleAttribute, "scaleAttribute was null");
        assertNotEquals(originalScale, scaleAttribute.getBaseValue(), "Target scale attribute was not changed.");

        // check the player's scale was changed the expected way. When changed from size 1.0, which is the default
        // the new scaleAttribute should be the same as the effect's scaleMultiplier
        assertEquals(effect.getScaleMultiplier(), scaleAttribute.getBaseValue(), "scaleAttribute was " + scaleAttribute + ", expected " + effect.getScaleMultiplier());

        changeSizeEffectStacking();
    }

    /**
     * Test that applying multiple size-changing effects causes proper stacking and conflict resolution.
     * <p>
     * This test validates how size-changing effects interact when multiple effects are applied to the same player:
     * <ul>
     * <li>Creates a player and registers the SCALE attribute</li>
     * <li>Applies a first size effect and verifies it changes the player's scale</li>
     * <li>Records the first effect's multiplier and the resulting scale</li>
     * <li>Applies a second conflicting size effect</li>
     * <li>Verifies the first effect was automatically removed (killed) when the second was applied</li>
     * <li>Verifies the new scale equals: first_effect_scale × second_effect_multiplier (clamped to [0.25, 4.0])</li>
     * <li>Confirms the second effect is now active with its multiplier applied</li>
     * </ul>
     * </p>
     * <p>
     * Key behavior being tested: When a second size effect is applied, it multiplies against the CURRENT scale
     * (which already includes the first effect's change), not the original 1.0 scale. This allows for multiplicative
     * stacking. Conflicting size effects automatically remove the old effect to prevent overlapping transformations.
     * </p>
     */
    void changeSizeEffectStacking() {
        // Test: Applying a new size effect should remove any existing size effect
        // The second effect's multiplier is applied to the CURRENT scale (which includes the first effect's change)
        PlayerMock target = mockServer.addPlayer();
        registerScaleAttribute(target);

        // Apply the first effect
        PlayerChangeSizeSuper effect1 = (PlayerChangeSizeSuper) addEffect(target, Ollivanders2Common.ticksPerSecond * 5, false);
        mockServer.getScheduler().performTicks(5);

        // Verify the first effect applied
        AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(scaleAttribute);
        double scale1 = scaleAttribute.getBaseValue();
        assertNotEquals(1.0, scale1, "First effect did not change scale");
        double effect1Multiplier = effect1.getScaleMultiplier();

        // Apply a second (conflicting) size effect
        PlayerChangeSizeSuper effect2 = (PlayerChangeSizeSuper) addEffect(target, Ollivanders2Common.ticksPerSecond * 5, false);
        mockServer.getScheduler().performTicks(5);

        // Verify the first effect was removed (should be killed)
        assertTrue(effect1.isKilled(), "First size effect was not removed when second size effect was applied");

        // Verify the new scale is effect1's scale * effect2's multiplier
        // (since the second effect multiplies the current scale, which was already modified by the first)
        scaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(scaleAttribute);
        double expectedScale = scale1 * effect2.getScaleMultiplier();
        // Clamp to [0.25, 4.0] to match the implementation
        if (expectedScale < 0.25) expectedScale = 0.25;
        else if (expectedScale > 4.0) expectedScale = 4.0;

        assertEquals(expectedScale, scaleAttribute.getBaseValue(), 0.01,
            "Scale was not correctly calculated. Expected scale1 (" + scale1 + ") * effect2 multiplier (" + effect2.getScaleMultiplier() + ")");

        // Verify the second effect is now active
        assertNotEquals(1.0, effect2.getScaleMultiplier(), "Second effect should have a non-default scale multiplier");
    }

    /**
     * Register the SCALE attribute on a MockBukkit player for testing.
     * <p>
     * MockBukkit does not provide the SCALE attribute by default, so this helper method uses reflection
     * to manually add it to a player's attributes map. This is necessary to test size-changing effects
     * which depend on modifying the player's scale attribute.
     * </p>
     * <p>
     * Implementation details:
     * <ul>
     * <li>Uses reflection to access the private {@code attributes} field in the PlayerMock class hierarchy</li>
     * <li>Searches up the class hierarchy since the field may be inherited from parent classes</li>
     * <li>Creates a new AttributeInstanceMock with SCALE attribute and default value of 1.0 (normal size)</li>
     * <li>Adds the SCALE attribute to the player's attributes map</li>
     * <li>Prints debug output confirming the attribute was added</li>
     * <li>Catches and prints any exceptions that occur during reflection</li>
     * </ul>
     * </p>
     *
     * @param target the MockBukkit player to register the SCALE attribute on
     */
    void registerScaleAttribute(PlayerMock target) {
        // we need to register the scale attribute since it is not set by default by MockBukkit
        // Directly add SCALE attribute with default value of 1.0
        try {
            Class<?> currentClass = target.getClass();
            Field attributesField = null;

            // Search up the class hierarchy for the attributes field
            while (currentClass != null && attributesField == null) {
                try {
                    attributesField = currentClass.getDeclaredField("attributes");
                } catch (NoSuchFieldException e) {
                    currentClass = currentClass.getSuperclass();
                }
            }

            if (attributesField == null) {
                throw new RuntimeException("Could not find attributes field in class hierarchy");
            }

            attributesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Attribute, AttributeInstance> attributes =
                    (Map<Attribute, AttributeInstance>) attributesField.get(target);

            // Add the SCALE attribute
            attributes.put(Attribute.SCALE, new AttributeInstanceMock(Attribute.SCALE, 1.0));

            // Verify it was added
            System.out.println("Attributes map now contains SCALE: " + attributes.containsKey(Attribute.SCALE));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * No event handler tests for size-changing effects.
     * <p>
     * Size-changing effects (SWELLING, SHRINKING) do not respond to player events for triggering
     * or modifying their behavior. They only respond to being added, removed, and aging over time.
     * Therefore, this test method is left empty as there are no event-based interactions to test.
     * </p>
     */
    @Override
    void eventHandlerTests() {}

    /**
     * Verify that doRemove() resets the player's scale back to 1.0 (normal size).
     *
     * <p>Tests that when a size-changing effect is removed, the player is properly restored
     * to their original normal scale of 1.0, undoing any size changes applied by the effect.</p>
     */
    @Override
    void doRemoveTest() {
        PlayerMock target = mockServer.addPlayer();
        registerScaleAttribute(target);

        // Apply a size-changing effect
        PlayerChangeSizeSuper effect = (PlayerChangeSizeSuper) addEffect(target, Ollivanders2Common.ticksPerSecond * 5, false);
        mockServer.getScheduler().performTicks(5);

        // Verify the effect changed the player's scale
        AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(scaleAttribute);
        double changedScale = scaleAttribute.getBaseValue();
        assertNotEquals(1.0, changedScale, "Effect did not change the player's scale");

        // Call doRemove() to clean up
        effect.doRemove();

        // Verify the player's scale was reset to 1.0
        scaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(scaleAttribute);
        assertEquals(1.0, scaleAttribute.getBaseValue(), "Player's scale was not reset to 1.0 when effect was removed");
    }
}

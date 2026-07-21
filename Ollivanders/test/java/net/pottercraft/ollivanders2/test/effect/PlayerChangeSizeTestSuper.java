package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.PlayerChangeSize;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test base for {@link PlayerChangeSize} effects: verifies scale modification, multiplicative stacking of a second
 * size effect (which also kills the first), and reset to 1.0 on removal.
 *
 * @author Azami7
 * @see EffectTestSuper
 */
public abstract class PlayerChangeSizeTestSuper extends EffectTestSuper {
    @Override
    abstract PlayerChangeSize createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * The effect changes the player's scale to its multiplier (from a base of 1.0), then delegates to
     * {@link #changeSizeEffectStacking()}.
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

        PlayerChangeSize effect = (PlayerChangeSize) addEffect(target, Ollivanders2Common.ticksPerSecond * 5, false);
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
     * A second size effect kills the first and multiplies against the current (already-modified) scale, clamped to
     * [0.25, 4.0].
     */
    void changeSizeEffectStacking() {
        // Test: Applying a new size effect should remove any existing size effect
        // The second effect's multiplier is applied to the CURRENT scale (which includes the first effect's change)
        PlayerMock target = mockServer.addPlayer();
        registerScaleAttribute(target);

        // Apply the first effect
        PlayerChangeSize effect1 = (PlayerChangeSize) addEffect(target, Ollivanders2Common.ticksPerSecond * 5, false);
        mockServer.getScheduler().performTicks(5);

        // Verify the first effect applied
        AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(scaleAttribute);
        double scale1 = scaleAttribute.getBaseValue();
        assertNotEquals(1.0, scale1, "First effect did not change scale");

        // Apply a second (conflicting) size effect
        PlayerChangeSize effect2 = (PlayerChangeSize) addEffect(target, Ollivanders2Common.ticksPerSecond * 5, false);
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
     * Add the SCALE attribute (default 1.0) to a MockBukkit player via reflection, since MockBukkit does not provide
     * it by default and size effects depend on it.
     *
     * @param target the MockBukkit player to register the SCALE attribute on
     */
    void registerScaleAttribute(PlayerMock target) {
        try {
            Class<?> currentClass = target.getClass();
            Field attributesField = null;

            // the attributes field may be declared on a superclass of PlayerMock
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

            attributes.put(Attribute.SCALE, new AttributeInstanceMock(Attribute.SCALE, 1.0));

            System.out.println("Attributes map now contains SCALE: " + attributes.containsKey(Attribute.SCALE));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Size-changing effects handle no events.
     */
    @Override
    void eventHandlerTests() {}

    /**
     * doRemove() resets the player's scale back to 1.0.
     */
    @Override
    void doRemoveTest() {
        PlayerMock target = mockServer.addPlayer();
        registerScaleAttribute(target);

        PlayerChangeSize effect = (PlayerChangeSize) addEffect(target, Ollivanders2Common.ticksPerSecond * 5, false);
        mockServer.getScheduler().performTicks(5);

        AttributeInstance scaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(scaleAttribute);
        double changedScale = scaleAttribute.getBaseValue();
        assertNotEquals(1.0, changedScale, "Effect did not change the player's scale");

        effect.doRemove();

        scaleAttribute = target.getAttribute(Attribute.SCALE);
        assertNotNull(scaleAttribute);
        assertEquals(1.0, scaleAttribute.getBaseValue(), "Player's scale was not reset to 1.0 when effect was removed");
    }
}

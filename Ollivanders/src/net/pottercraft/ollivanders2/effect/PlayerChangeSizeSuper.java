package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Parent class for all effects that change the player's scale.
 *
 * <p>PlayerChangeSizeSuper provides the core scaling mechanism used by scaling effects like SWELLING and SHRINKING.
 * Scaling is implemented using a multiplier-based system where the new scale is calculated by multiplying the player's
 * current scale by a scaleMultiplier. For example, a multiplier of 2.0 doubles the player's size, while 0.5 halves it.
 * </p>
 *
 * <p>Effect Lifecycle:</p>
 * <ol>
 * <li>A subclass creates an instance with a specific scaleMultiplier (set before startEffect() is called)</li>
 * <li>The subclass calls startEffect() to schedule the size change via an async BukkitRunnable (5-tick delay)</li>
 * <li>changePlayerSize() executes asynchronously: removes any conflicting scale effects, applies the new scale, and kills
 *     the effect if the player returns to normal size (1.0)</li>
 * <li>checkEffect() ages the effect each tick (only for non-permanent effects)</li>
 * <li>When the effect expires, doRemove() resets the player back to normal scale (1.0)</li>
 * </ol>
 *
 * <p>Scale Bounds:
 * The player's scale is clamped to the range [0.25, 4.0] to prevent the game engine from being overwhelmed by
 * extremely large or small players. Values outside this range are automatically adjusted to the nearest boundary.</p>
 *
 * <p>Stacking Behavior:
 * Multiple scaling effects cannot stack on the same player. When changePlayerSize() executes, it removes any existing
 * SWELLING or SHRINKING effects before applying the new scale. This ensures only one scaling effect is active at a time.</p>
 *
 * @author Azami7
 */
public abstract class PlayerChangeSizeSuper extends O2Effect {
    /**
     * A list of the scale effect types that can affect player size.
     *
     * <p>This list is used to detect and remove conflicting scaling effects when a new scale effect is applied.
     * Since only one scaling effect can be active on a player at a time, when changePlayerSize() runs, it removes
     * any existing effects in this list before applying the new scale. This prevents multiple scaling effects from
     * stacking unexpectedly.</p>
     */
    ArrayList<O2EffectType> scaleEffectTypes = new ArrayList<>() {{
        add(O2EffectType.SWELLING);
        add(O2EffectType.SHRINKING);
    }};

    /**
     * How does this effect change the player's size. Values above 1 will increase size, so if this is set to 2 it will
     * double the player's current size. If this is set to 0.5 it will make the player half their current size.
     */
    double scaleMultiplier = 0;

    /**
     * Is the effect currently changing the target's scale?
     */
    boolean isTransformed = false;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public PlayerChangeSizeSuper(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);
    }

    /**
     * Schedule the size change to occur asynchronously with a 5-tick delay.
     *
     * <p>This method should be called by subclass constructors or initialization methods to apply the size change.
     * The actual size modification is executed asynchronously (5 ticks / 250 milliseconds later) to avoid potential
     * synchronization issues. Child classes must set scaleMultiplier before calling this method.</p>
     *
     * <p>The 5-tick delay allows the player entity to be fully initialized in the server before scale modifications
     * are applied.</p>
     */
    protected void startEffect() {
        // change the player's size
        new BukkitRunnable() {
            @Override
            public void run() {
                changePlayerSize();
            }
        }.runTaskLater(p, 5);
    }

    /**
     * Check this effect's behavior each game tick.
     *
     * <p>This method is called once per tick by the effects system. For non-permanent scaling effects, it ages the
     * effect by 1 tick. The actual size change was already applied asynchronously when startEffect() was called.</p>
     */
    @Override
    public void checkEffect() {
        if (!isTransformed) {
            startEffect();
        }

        if (!permanent) {
            age(1);
        }
    }

    /**
     * Apply the scale change to the affected player.
     *
     * <p>This method executes asynchronously and handles the following:</p>
     * <ol>
     * <li>Retrieves the player's scale attribute (returns early if not found)</li>
     * <li>Removes any existing SWELLING or SHRINKING effects to prevent stacking multiple scaling effects</li>
     * <li>Calculates the new scale by multiplying the current scale by scaleMultiplier</li>
     * <li>Applies the clamped new scale to the player</li>
     * <li>If the player returns to normal scale (1.0), kills the effect since its purpose is fulfilled</li>
     * </ol>
     */
    private void changePlayerSize() {
        // Get the player's scale attribute
        AttributeInstance scaleAttribute = getScaleAttribute();
        if (scaleAttribute == null) {
            kill();
            return;
        }

        // handle stacking scaling effects
        for (O2EffectType effectType : Ollivanders2API.getPlayers().playerEffects.getEffects(targetID)) {
            if (scaleEffectTypes.contains(effectType)) {
                Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, effectType);
            }
        }

        // get their new scale based on their current scale
        double newScale = getNewScale(scaleAttribute.getBaseValue());

        // Make player the new size
        scaleAttribute.setBaseValue(newScale);

        if (newScale == 1.0) { // returned the player to normal size
            // we can just kill the effect because this will set them to normal size, then end and set them to normal size - no point
            kill();
        }

        isTransformed = true;
    }

    /**
     * Get the scale attribute for the affected player.
     *
     * @return the scale attribute instance or null if an error occurred
     */
    @Nullable
    private AttributeInstance getScaleAttribute() {
        Player player = p.getServer().getPlayer(targetID);
        if (player == null) {
            common.printLogMessage("SWELLING.checkEffect: player is null", null, null, true);
            kill();
            return null;
        }

        AttributeInstance scaleAttribute = player.getAttribute(Attribute.SCALE);
        if (scaleAttribute == null) {
            common.printLogMessage("SWELLING.checkEffect: Attribute.SCALE is null", null, null, true);
            kill();
            return null;
        }

        return scaleAttribute;
    }

    /**
     * Calculate the new scale value with clamping.
     *
     * <p>This method multiplies the current scale by the scaleMultiplier, then clamps the result to the valid range
     * [0.25, 4.0] to prevent the Minecraft engine from struggling with extremely large or small players.
     * Values below 0.25 (1/4 scale) are set to 0.25, and values above 4.0 (4x scale) are set to 4.0.</p>
     *
     * @param currentScale the player's current scale before the multiplier is applied
     * @return the new scale value, clamped to [0.25, 4.0]
     */
    double getNewScale(double currentScale) {
        double newScale = currentScale * scaleMultiplier;

        // make sure they do not get too big or too small for the game to handle
        // keep the scale between 0.25 (1/4 scale) and 4.0 (4x scale)
        if (newScale < 0.25)
            newScale = 0.25;
        else if (newScale > 4.0)
            newScale = 4.0;

        return newScale;
    }

    /**
     * Remove this effect and reset the player to normal size.
     *
     * <p>Called when the effect is being removed from the player (either due to expiration or explicit removal).
     * This method retrieves the player's scale attribute and resets it to 1.0 (normal size), effectively undoing
     * any size changes applied by this effect.</p>
     */
    @Override
    public void doRemove() {
        AttributeInstance scaleAttribute = getScaleAttribute();
        if (scaleAttribute == null) {
            kill();
            return;
        }
        // change player back to normal size
        scaleAttribute.setBaseValue(1.0);
    }
}

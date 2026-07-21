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
 * Parent class for effects that change the player's scale, such as {@link SWELLING} and {@link SHRINKING}.
 *
 * <p>Multiplies the player's current scale by a per-subclass multiplier, clamped to [0.25, 4.0] so the engine is not
 * overwhelmed by extreme sizes. Only one scaling effect may be active on a player at a time: applying a new one removes
 * any existing scaling effect. On removal the player is reset to normal scale (1.0).</p>
 *
 * @author Azami7
 */
public abstract class PlayerChangeSize extends O2Effect {
    /**
     * The scaling effect types that are mutually exclusive; any active one is removed before a new scale is applied.
     */
    ArrayList<O2EffectType> scaleEffectTypes = new ArrayList<>() {{
        add(O2EffectType.SWELLING);
        add(O2EffectType.SHRINKING);
    }};

    /**
     * Multiplier applied to the player's current scale. Above 1 increases size (2 doubles it); below 1 decreases it
     * (0.5 halves it). Subclasses must set this before calling {@link #startEffect()}.
     */
    double scaleMultiplier = 0;

    /**
     * Whether the scale change has been applied to the target yet.
     */
    boolean transformed = false;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the ID of the player this effect acts on
     */
    public PlayerChangeSize(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);
    }

    /**
     * Schedule the size change to run asynchronously after a 5-tick delay, giving the player entity time to fully
     * initialize on the server. Subclasses must set {@link #scaleMultiplier} before calling this.
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
     * Apply the size change on the first tick, then age the effect each tick unless it is permanent.
     */
    @Override
    public void checkEffect() {
        if (!transformed) {
            startEffect();
        }

        if (!permanent) {
            age(1);
        }
    }

    /**
     * Remove any conflicting scaling effect and apply the clamped new scale to the player. Kills this effect if the
     * player is left at normal scale (1.0) or has no scale attribute.
     */
    private void changePlayerSize() {
        AttributeInstance scaleAttribute = getScaleAttribute();
        if (scaleAttribute == null) {
            common.printDebugMessage("PlayerChangeSizeSuper.changePlayerSize(): Player's scale attribute was null", null, null, false);
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
        common.printDebugMessage("PlayerChangeSizeSuper.changePlayerSize(): new scale is " + newScale, null, null, false);

        // already at normal size, so end the effect rather than have doRemove reset it again
        if (newScale == 1.0) {
            kill();
        }

        transformed = true;
    }

    /**
     * Get the scale attribute for the affected player. Kills this effect and returns null if the player is offline or
     * has no scale attribute.
     *
     * @return the scale attribute, or null if it could not be retrieved
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
     * Apply the scale multiplier to the current scale, clamped to [0.25, 4.0].
     *
     * @param currentScale the player's current scale before the multiplier is applied
     * @return the new scale value, clamped to [0.25, 4.0]
     */
    double getNewScale(double currentScale) {
        double newScale = currentScale * scaleMultiplier;

        // keep the scale within a range the game can handle
        if (newScale < 0.25)
            newScale = 0.25;
        else if (newScale > 4.0)
            newScale = 4.0;

        return newScale;
    }

    /**
     * Reset the player to normal scale (1.0), undoing this effect's size change.
     */
    @Override
    public void doRemove() {
        AttributeInstance scaleAttribute = getScaleAttribute();
        if (scaleAttribute == null) {
            kill();
            return;
        }
        scaleAttribute.setBaseValue(1.0);
    }

    /**
     * Check whether the size change has been applied to the player yet.
     *
     * @return true once the scale change has been applied, false while it is still pending
     */
    public boolean isTransformed() {
        return transformed;
    }

    /**
     * Get the scale multiplier for this effect. Above 1.0 increases size (2.0 doubles), below 1.0 decreases it
     * (0.5 halves).
     *
     * @return the scale multiplier value
     */
    public double getScaleMultiplier() {
        return scaleMultiplier;
    }
}

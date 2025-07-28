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
 * @author Azami7
 * @since 2.21
 */
public abstract class PlayerChangeSizeSuper extends O2Effect {
    /**
     * A list of the scale effect types
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
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public PlayerChangeSizeSuper(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);
    }

    /**
     * Start this effect - to be called by the non-abstract child classes. This is preferred over the old way of doing a
     * check to see if the effect is running every tick in checkEffect.
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
     * Check to see if the player has been affected and affect them
     */
    @Override
    public void checkEffect() {
        if (!permanent) {
            age(1);
        }
    }

    /**
     * Change the player's scale. This may result in the player returning to normal scale.
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
     * Get the new scale for the player
     *
     * @param currentScale the scale the player is currently
     * @return the new scale for this player
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
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
        AttributeInstance scaleAttribute = getScaleAttribute();
        if (scaleAttribute == null) {
            common.printLogMessage("", null, null, true);
            kill();
            return;
        }
        // change player back to normal size
        scaleAttribute.setBaseValue(1.0);
    }
}

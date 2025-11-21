package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.*;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Parent class for effects that transform a player into another entity form.
 *
 * <p>ShapeShiftSuper provides a transformation mechanism using the libDisguises plugin to disguise players
 * as various Minecraft entities (animals, monsters, etc.). Transformations are visual-only and do not change
 * the player's actual capabilities (they still fly, use commands, etc.).
 * </p>
 *
 * <p>Transformation Lifecycle:</p>
 * <ol>
 * <li>A subclass creates an instance and sets the target form (entity type)</li>
 * <li>checkEffect() calls transform() to apply the disguise using libDisguises</li>
 * <li>transform() creates a MobDisguise, customizes the watcher (appearance), and disguises the player</li>
 * <li>checkEffect() calls upkeep() each tick to maintain the transformation (subclasses can override)</li>
 * <li>When the effect expires or is killed, restore() undisguises the player back to human form</li>
 * </ol>
 *
 * <p>Mutual Exclusion:
 * Only one shape-shift effect can be active on a player at a time. When a new transformation is applied,
 * any existing shape-shift effect is removed by the effects system.</p>
 *
 * <p>Dependencies:
 * This effect requires the LibDisguises plugin to be enabled on the server. If LibDisguises is disabled,
 * the effect is automatically killed with no transformation applied.</p>
 *
 * @author Azami7
 */
public abstract class ShapeShiftSuper extends O2Effect {
    /**
     * Tracks whether the player is currently transformed into another form.
     *
     * <p>This flag is set to true when transform() successfully applies a disguise to the player,
     * and is set to false when restore() removes the disguise. Used to ensure restoration only
     * occurs when a transformation is active.</p>
     */
    boolean transformed = false;

    /**
     * The active libDisguises disguise applied to the player.
     *
     * <p>This reference holds the MobDisguise object that disguises the player. It is used by
     * restore() to undisguise the player when the effect expires. The disguise contains the
     * entity type, appearance modifications, and visual properties.</p>
     */
    TargetedDisguise disguise;

    /**
     * The target entity type for this transformation.
     *
     * <p>Subclasses must set this field to specify which entity the player will transform into
     * (e.g., EntityType.WOLF, EntityType.SPIDER, EntityType.PARROT). This field is checked
     * before transformation and must not be null for the transformation to succeed.</p>
     */
    EntityType form;

    /**
     * The appearance watcher for customizing the disguise.
     *
     * <p>The LivingWatcher provides access to customize specific appearance properties of the
     * transformed entity, such as size, color, equipment, and other visual attributes.
     * Subclasses override customizeWatcher() to modify these properties after the disguise
     * is created but before it is applied to the player.</p>
     */
    LivingWatcher watcher;

    /**
     * Constructor for creating a shape shift effect.
     *
     * <p>Creates an effect that will transform the player into another entity form using
     * the LibDisguises plugin. Requires LibDisguises to be enabled on the server.
     * Subclasses must set the form field to specify the target entity type before
     * this effect is added to the player's effect list.</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect in game ticks
     * @param pid      the unique ID of the player to transform
     */
    public ShapeShiftSuper(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);
    }

    /**
     * Check the effect each game tick and perform transformations.
     *
     * <p>This method executes once per tick and performs the following:</p>
     * <ol>
     * <li>Verifies that LibDisguises is still enabled; kills the effect if disabled</li>
     * <li>Ages the effect by 1 tick (for non-permanent effects)</li>
     * <li>Calls upkeep() to allow subclasses to maintain or modify the transformation</li>
     * </ol>
     */
    @Override
    public void checkEffect() {
        if (!Ollivanders2.libsDisguisesEnabled) {
            transformed = false;

            kill();
            return;
        }

        if (!permanent) {
            age(1);
        }

        upkeep();
    }

    /**
     * Perform upkeep on the active transformation each game tick.
     *
     * <p>This is a template method that subclasses can override to maintain or modify the
     * transformation on each tick. By default, this method does nothing. Subclasses can override
     * to refresh the disguise, apply potion effects, play animations, or other tick-based behavior.</p>
     */
    protected void upkeep() {
        // by default, do nothing, this needs to be written in the child classes
    }

    /**
     * Apply the disguise and transform the player into the specified form.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     * <li>Locates the target player on the server</li>
     * <li>Creates a new MobDisguise based on the form entity type</li>
     * <li>Retrieves the living entity watcher for appearance customization</li>
     * <li>Calls customizeWatcher() to allow subclasses to modify appearance properties</li>
     * <li>Applies the disguise to all players using DisguiseAPI</li>
     * <li>Sets the transformed flag to true</li>
     * </ol>
     * If the player or form is null, kills the effect without applying a disguise.
     */
    protected void transform() {
        Player target = p.getServer().getPlayer(targetID);

        if (target != null && form != null) {
            common.printDebugMessage("transforming " + target.getName(), null, null, false);

            // disguisePlayer the player
            DisguiseType disguiseType = DisguiseType.getType(form);
            disguise = new MobDisguise(disguiseType);
            watcher = (LivingWatcher) disguise.getWatcher();

            customizeWatcher();

            DisguiseAPI.disguiseToAll(target, disguise);
            transformed = true;
        }
        else {
            kill();
        }
    }

    /**
     * Kill this effect and restore the player to human form.
     *
     * <p>Called when the effect expires or is being removed. This method calls restore()
     * to undisguise the player, then marks the effect as killed to prevent further processing.</p>
     */
    @Override
    public void kill() {
        restore();

        kill = true;
    }

    /**
     * Restore the player to their original human form.
     *
     * <p>Undisguises the player using LibDisguises, removing the transformation. Only performs
     * undisguise if the player is currently transformed. Safely handles cases where the
     * disguised entity no longer exists by catching exceptions.</p>
     */
    public void restore() {
        if (transformed) {
            if (disguise != null) {
                Entity entity = disguise.getEntity();
                try {
                    DisguiseAPI.undisguiseToAll(entity);
                }
                catch (Exception e) {
                    // in case entity no longer exists
                }
            }

            transformed = false;
        }
    }

    /**
     * Customize the appearance of the transformed entity.
     *
     * <p>This is a template method that subclasses can override to modify specific appearance
     * properties of the disguise, such as size, color, age, equipment, or other visual attributes
     * provided by the watcher. This method is called during transform() after the disguise is
     * created but before it is applied to the player. By default, this method does nothing
     * (uses the disguise's default appearance).</p>
     */
    protected void customizeWatcher() {
    }
}
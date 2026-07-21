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
 * Base class for effects that visually transform a player into another entity form via the LibDisguises plugin.
 * <p>
 * The disguise is visual-only; the player keeps their real capabilities. Requires LibDisguises to be enabled — the
 * effect kills itself if it is not. Only one shape-shift may be active on a player at a time.
 * </p>
 *
 * @author Azami7
 */
public abstract class ShapeShift extends O2Effect {
    /**
     * Whether a disguise is currently applied; gates {@link #restore()} so it only undisguises an active transformation.
     */
    boolean transformed = false;

    /**
     * The active LibDisguises disguise, used by {@link #restore()} to undisguise the player.
     */
    TargetedDisguise disguise;

    /**
     * The entity the player transforms into. Subclasses must set this before the effect is applied; a null form kills
     * the effect instead of transforming.
     */
    EntityType form;

    /**
     * The disguise's appearance watcher, exposed for {@link #customizeWatcher()} to modify size, color, equipment, etc.
     */
    LivingWatcher watcher;

    /**
     * Constructor
     * <p>
     * Subclasses must set {@link #form} before this effect is applied.
     * </p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to transform
     */
    public ShapeShift(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);
    }

    /**
     * Ages the effect and delegates per-tick upkeep to {@link #doCheckEffect()}. Kills the effect if LibDisguises has
     * been disabled.
     */
    @Override
    public void checkEffect() {
        if (!Ollivanders2.libsDisguisesEnabled) {
            transformed = false;

            kill();
            return;
        }

        if (!permanent)
            age(1);

        doCheckEffect();
    }

    /**
     * Per-tick upkeep of the active transformation, run after aging. Subclasses maintain or refresh the disguise here.
     */
    abstract protected void doCheckEffect();

    /**
     * Disguise the target as {@link #form}, letting {@link #customizeWatcher()} adjust its appearance first. Kills the
     * effect if the target is offline or {@link #form} is unset.
     */
    void transform() {
        Player target = p.getServer().getPlayer(targetID);

        if (target != null && form != null) {
            common.printDebugMessage("transforming " + target.getName(), null, null, false);

            if (!Ollivanders2.testMode) {
                DisguiseType disguiseType = DisguiseType.getType(form);
                disguise = new MobDisguise(disguiseType);
                watcher = (LivingWatcher) disguise.getWatcher();

                customizeWatcher();

                DisguiseAPI.disguiseToAll(target, disguise);
            }

            transformed = true;
        }
        else {
            kill();
        }
    }

    /**
     * Restores the player to human form, then marks the effect killed.
     */
    @Override
    public void kill() {
        restore();

        kill = true;
    }

    /**
     * Undisguise the player back to human form. No-op if not currently transformed; tolerates the disguised entity no
     * longer existing.
     */
    public void restore() {
        if (transformed) {
            if (!Ollivanders2.testMode && disguise != null) {
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
     * Adjust the disguise's appearance via {@link #watcher}. Called during {@link #transform()} before the disguise is
     * applied, letting subclasses set size, color, equipment, etc.
     */
    abstract void customizeWatcher();

    /**
     * Check whether the player is currently disguised.
     *
     * @return true if transformed, false if in human form
     */
    public boolean isTransformed() {
        return transformed;
    }
}
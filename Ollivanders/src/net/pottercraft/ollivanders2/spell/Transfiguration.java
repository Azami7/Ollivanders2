package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for transfiguration spells that change blocks and entities.
 * <p>
 * A transfiguration is either permanent (the change is left in place) or temporary (reverted once its duration
 * expires). Duration and success rate can scale with the caster's skill. Subclasses implement the actual
 * block/entity transformation and its reversal.
 * </p>
 *
 * @author Azami7
 * @see BlockTransfiguration
 * @see EntityTransfiguration
 */
public abstract class Transfiguration extends O2Spell {
    /**
     * True once a target has been successfully transfigured.
     */
    boolean isTransfigured = false;

    /**
     * If true, the change is left in place and the spell is killed on success; if false, it is reverted when
     * {@link #effectDuration} expires.
     */
    boolean permanent = false;

    /**
     * If true, the original block or entity is consumed (removed) by the transfiguration.
     */
    boolean consumeOriginal = false;

    /**
     * Upper limit for {@link #effectDuration}, in ticks.
     */
    int maxDuration = 4 * Ollivanders2Common.ticksPerHour;

    /**
     * Lower limit for {@link #effectDuration}, in ticks.
     */
    int minDuration = Ollivanders2Common.ticksPerMinute;

    /**
     * How long a non-permanent transfiguration lasts, in ticks. Computed by {@link #setDuration()} on the first tick.
     */
    int effectDuration = 0;

    /**
     * Scales how much caster skill affects duration; see {@link #setDuration()} for the formula.
     */
    double durationModifier = 1.0;

    /**
     * Percent chance (0-100) that the transfiguration succeeds on a given target. Defaults to always succeeding.
     */
    protected int successRate = 100;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Transfiguration(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.TRANSFIGURATION;
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public Transfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.TRANSFIGURATION;

        successMessage = "Transfiguration successful.";
    }

    /**
     * Has the transfiguration taken place or not.
     *
     * @return whether the target has transfigured or not
     */
    public boolean isTransfigured() {
        return isTransfigured;
    }

    /**
     * Is this spell permanent.
     *
     * @return true if permanent, false otherwise
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * Does this spell consume the original block or not.
     *
     * @return true if it consumes the block, false otherwise
     */
    public boolean isConsumeOriginal() {
        return consumeOriginal;
    }

    /**
     * Gets the total duration for which transfiguration will remain, if not permanent.
     *
     * @return the spell duration in game ticks
     */
    public int getEffectDuration() {
        return effectDuration;
    }

    /**
     * Gets the minimum spell duration.
     *
     * @return the minimum duration in game ticks
     */
    public int getMinDuration() {
        return minDuration;
    }

    /**
     * Gets the maximum spell duration.
     *
     * @return the maximum duration in game ticks
     */
    public int getMaxDuration() {
        return maxDuration;
    }

    /**
     * Get the success rate for this transfiguration.
     *
     * @return the success rate
     */
    public int getSuccessRate() {
        return successRate;
    }

    /**
     * Run one tick of the spell: seek and apply the transfiguration, then end the spell once it succeeds (immediately
     * if permanent, otherwise after {@link #effectDuration} elapses).
     */
    @Override
    protected void doCheckEffect() {
        if (getAge() == 0) { // set the duration on the first pass
            setDuration();
        }

        // if a target has not transfigured, look for one to transfigure otherwise move the projectile on
        if (!isTransfigured()) {
            common.printDebugMessage("transfiguration base: checking block " + location.getBlock().getType(), null, null, false);
            transfigure();

            if (isTransfigured) {
                // if the spell successfully transfigured something, stop the projectile
                stopProjectile();

                sendSuccessMessage();

                if (permanent)
                    kill();
            }
            else if (isKilled()) {
                sendFailureMessage();
            }
        }
        else {
            // check time to live on the spell
            if (getAge() >= effectDuration)
                kill();
        }

        if (hasHitBlock() && !isTransfigured) {
            // we've hit a block and the projectile is stopped, but we didn't find anything to transfigure
            common.printDebugMessage("Failed to transfigure an entity before projectile stopped", null, null, false);
            sendFailureMessage();

            kill();
        }
    }

    /**
     * Set {@link #effectDuration} to {@code usesModifier * durationModifier * ticksPerSecond}, limited to
     * [{@link #minDuration}, {@link #maxDuration}].
     */
    void setDuration() {
        effectDuration = (int) (usesModifier * Ollivanders2Common.ticksPerSecond * durationModifier);
        if (effectDuration < minDuration)
            effectDuration = minDuration;
        else if (effectDuration > maxDuration)
            effectDuration = maxDuration;
    }

    /**
     * Check whether an entity is free to be transfigured, i.e. not already the target of another active,
     * non-permanent transfiguration. No block equivalent is needed because BlockCommon already tracks changed blocks.
     *
     * @param entity the entity to check
     * @return false if the entity is already transfigured by an active, non-permanent spell
     */
    boolean canTransfigure(Entity entity) {
        for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
            if (spell instanceof Transfiguration) {
                if (((Transfiguration) spell).isTransfigured(entity) && !((Transfiguration) spell).isPermanent()) {
                    common.printDebugMessage(entity.getName() + " is already transfigured", null, null, false);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @param entity the entity to check
     * @return true if this spell has transfigured the given entity
     */
    public abstract boolean isTransfigured(@NotNull Entity entity);

    /**
     * @param block the block to check
     * @return true if this spell has transfigured the given block
     */
    public abstract boolean isTransfigured(@NotNull Block block);

    /**
     * Attempt to transfigure the current target, setting {@link #isTransfigured} on success. Called each tick while
     * the spell is seeking a target.
     */
    abstract void transfigure();

    /**
     * Restore the transfigured block or entity to its original state. Only invoked for non-permanent spells.
     */
    protected abstract void revert();

}

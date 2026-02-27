package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all transfiguration spells that change blocks and entities.
 *
 * <p>Manages core transfiguration mechanics including spell duration, success rates,
 * permanence, and the general lifecycle of transfiguration effects. Subclasses must
 * implement the specific transfiguration behavior for blocks or entities.</p>
 *
 * <p>Transfigurations can be:</p>
 * <ul>
 * <li><strong>Temporary:</strong> Effects last for the spell duration, then revert to original state</li>
 * <li><strong>Permanent:</strong> Effects remain indefinitely after spell completes</li>
 * <li><strong>Skill-based:</strong> Duration and success rate depend on player spell skill (usesModifier)</li>
 * </ul>
 *
 * @author Azami7
 * @see BlockTransfiguration for block-specific transfiguration implementation
 * @see EntityTransfiguration for entity-specific transfiguration implementation
 */
public abstract class TransfigurationBase extends O2Spell {
    /**
     * Whether the transfiguration has successfully occurred on a target.
     *
     * <p>Set to true when a block or entity is successfully transfigured. Used to determine
     * spell lifecycle: whether to continue seeking targets or enter duration phase.</p>
     */
    boolean isTransfigured = false;

    /**
     * Whether this transfiguration spell is permanent or temporary.
     *
     * <p>If true, the spell is immediately killed after successful transfiguration and
     * the effect is not reverted. If false, the spell waits for its duration to expire
     * before reverting the transfiguration.</p>
     */
    boolean permanent = false;

    /**
     * Whether the original block or entity should be removed/consumed by this spell.
     *
     * <p>Some transfiguration spells destroy the original target after changing it.
     * This flag controls that behavior.</p>
     */
    boolean consumeOriginal = false;

    /**
     * Maximum spell duration (4 hours by default).
     *
     * <p>The spell's calculated duration is clamped to not exceed this value.
     * Subclasses can override to set spell-specific maximum durations.</p>
     */
    int maxDuration = 4 * Ollivanders2Common.ticksPerHour;

    /**
     * Minimum spell duration (1 minute by default).
     *
     * <p>The spell's calculated duration is clamped to not go below this value.
     * Subclasses can override to set spell-specific minimum durations.</p>
     */
    int minDuration = Ollivanders2Common.ticksPerMinute;

    /**
     * The actual duration for which this transfiguration will remain if not permanent.
     *
     * <p>Calculated during spell initialization based on player skill (usesModifier)
     * and clamped to [minDuration, maxDuration]. Only used if the spell is non-permanent.</p>
     */
    int effectDuration = 0;

    /**
     * Modifier that scales duration based on player skill level.
     *
     * <p>Final duration = (usesModifier * durationModifier * ticksPerSecond).
     * A modifier of 0.5 means duration is 50% of usesModifier. Subclasses set this
     * to control how much player skill affects spell duration.</p>
     */
    double durationModifier = 1.0;

    /**
     * The success rate for this transfiguration spell (0-100%).
     *
     * <p>Represents the percent chance that the transfiguration will succeed on each
     * attempted block or entity. Defaults to 100% success. Subclasses can override
     * to make transfigurations skill-dependent.</p>
     */
    protected int successRate = 100;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public TransfigurationBase(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.TRANSFIGURATION;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public TransfigurationBase(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
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
     * Main effect loop that manages the transfiguration lifecycle.
     *
     * <p>This method handles two phases:</p>
     * <ol>
     * <li><strong>Transfiguration Phase:</strong> If no target has been transfigured yet,
     *     attempts to transfigure a block or entity via {@link #transfigure()}.
     *     On success, stops the projectile and sends success message. On failure,
     *     sends failure message. Permanent spells are immediately killed after success.</li>
     * <li><strong>Duration Phase:</strong> If transfiguration succeeded, monitors the spell's age
     *     and kills the spell when duration expires (for non-permanent spells).</li>
     * </ol>
     *
     * <p>The spell duration is calculated once on the first tick via {@link #setDuration()}.</p>
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
    }

    /**
     * Calculates and sets the spell duration based on player skill level.
     *
     * <p>Called on the first game tick via {@link #doCheckEffect()}. The duration is calculated as:
     * <code>duration = usesModifier * durationModifier * ticksPerSecond</code></p>
     *
     * <p>The calculated duration is clamped to the range [minDuration, maxDuration].
     * Higher player skill (usesModifier) results in longer spell duration, allowing
     * more time for temporary transfigurations to remain in effect.</p>
     *
     * <p>Example: With usesModifier=100, durationModifier=0.5, the base duration
     * is 50 seconds before clamping to valid bounds.</p>
     */
    void setDuration() {
        effectDuration = (int) (usesModifier * Ollivanders2Common.ticksPerSecond * durationModifier);
        if (effectDuration < minDuration)
            effectDuration = minDuration;
        else if (effectDuration > maxDuration)
            effectDuration = maxDuration;
    }

    /**
     * Determines if a specific entity is currently affected by this transfiguration spell.
     *
     * <p>Used to track which entities have been transfigured by this spell instance.
     * This is particularly important for entity transfigurations where temporary effects
     * need to be tracked and reverted.</p>
     *
     * @param entity the entity to check
     * @return true if the entity is transfigured by this spell, false otherwise
     */
    public abstract boolean isEntityTransfigured(@NotNull Entity entity);

    /**
     * Determines if a specific block is currently affected by this transfiguration spell.
     *
     * <p>Used to track which blocks have been transfigured by this spell instance.
     * For temporary transfigurations, blocks are tracked so they can be reverted when
     * the spell expires.</p>
     *
     * @param block the block to check
     * @return true if the block is transfigured by this spell, false otherwise
     */
    public abstract boolean isBlockTransfigured(@NotNull Block block);

    /**
     * Performs the actual transfiguration on the target.
     *
     * <p>Called each game tick while the spell is active and seeking a target.
     * Implementations should:</p>
     * <ul>
     * <li>Validate the target block or entity meets requirements</li>
     * <li>Apply success rate checks based on player skill</li>
     * <li>Perform the actual transformation</li>
     * <li>Set {@link #isTransfigured} to true if successful</li>
     * </ul>
     *
     * <p>Subclasses must implement to define what specific blocks or entities
     * can be transfigured and how they should be transformed.</p>
     */
    abstract void transfigure();

    /**
     * Reverts temporary transfiguration effects after the spell expires.
     *
     * <p>Called when the spell is killed or duration expires. Only applies to
     * non-permanent spells ({@link #permanent} = false). Permanent transfigurations
     * should not be reverted.</p>
     *
     * <p>Implementations should restore affected blocks and entities to their
     * original state.</p>
     */
    protected abstract void revert();
}

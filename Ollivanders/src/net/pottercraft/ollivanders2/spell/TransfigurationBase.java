package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The base class for all transfiguration spells that change blocks and entities.
 */
public abstract class TransfigurationBase extends O2Spell
{
    /**
     * If the transfiguration has taken place or not.
     */
    boolean isTransfigured = false;

    /**
     * Is this spell permanent
     */
    boolean permanent = false;

    /**
     * Should this spell consume the original entity or block
     */
    boolean consumeOriginal = false;

    /**
     * Set max duration to 4 hours by default
     */
    int maxDuration = 4 * Ollivanders2Common.ticksPerHour;

    /**
     * Set min duration to 1 minute
     */
    int minDuration = Ollivanders2Common.ticksPerMinute;

    /**
     * If this is not permanent, how long it should last.
     */
    int spellDuration = minDuration;

    /**
     * Allows spell variants to change the duration of this spell.
     */
    double durationModifier = 1.0;

    /**
     * The percent chance this spell will succeed each casting.
     */
    protected int successRate = 100;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public TransfigurationBase(Ollivanders2 plugin)
    {
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
    public TransfigurationBase(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.TRANSFIGURATION;

        successMessage = "Transfiguration successful.";
    }

    /**
     * Has the transfiguration taken place or not.
     *
     * @return whether the target has transfigured or not
     */
    public boolean isTransfigured()
    {
        return isTransfigured;
    }

    /**
     * Is this spell permanent.
     *
     * @return true if permanent, false otherwise
     */
    public boolean isPermanent()
    {
        return permanent;
    }

    /**
     * Does this spell consume the original block or not.
     *
     * @return true if it consumes the block, false otherwise
     */
    public boolean isConsumeOriginal()
    {
        return consumeOriginal;
    }

    /**
     * If the target is not transfigured, attempt to transfigure it. If it is transfigured and this is not a permanent spell, age the spell one tick.
     */
    @Override
    protected void doCheckEffect()
    {
        // if a target has not transfigured, look for one to transfigure
        if (!isTransfigured())
        {
            common.printDebugMessage("Attempting to transfigure " + location.getBlock().getType(), null, null, false);
            transfigure();

            if (isTransfigured)
            {
                // if the spell successfully transfigured something, stop the projectile
                stopProjectile();

                if (permanent)
                {
                    kill();
                    return;
                }

                spellDuration = (int) (usesModifier * Ollivanders2Common.ticksPerSecond * durationModifier);
                if (spellDuration > maxDuration)
                    spellDuration = maxDuration;
                else if (spellDuration < minDuration)
                    spellDuration = minDuration;
            }
        }
        else
        {
            // check time to live on the spell
            if (spellDuration <= 0)
                // spell duration is up, kill the spell
                kill();
            else
                spellDuration = spellDuration - 1;
        }
    }

    /**
     * Is this entity transfigured by this spell
     *
     * @param entity the entity to check
     * @return true if transfigured, false otherwise
     */
    public abstract boolean isEntityTransfigured(Entity entity);

    /**
     * Is this block transfigured by this spell
     *
     * @param block the block to check
     * @return true if transfigured, false otherwise
     */
    public abstract boolean isBlockTransfigured(Block block);

    /**
     * Perform the transfiguration.
     */
    abstract void transfigure();

    /**
     * Revert the transfiguration, if not permanent
     */
    protected abstract void revert();
}

package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    static int maxDuration = 4 * Ollivanders2Common.ticksPerHour;

    /**
     * Set min duration to 1 minute
     */
    static int minDuration = Ollivanders2Common.ticksPerMinute;

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
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
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

    public boolean isPermanent()
    {
        return permanent;
    }

    public boolean isConsumeOriginal()
    {
        return consumeOriginal;
    }

    @Override
    protected void doCheckEffect()
    {
        // if a target has not transfigured, look for one to transfigure
        if (!isTransfigured())
        {
            transfigure();

            if (isTransfigured)
            {
                spellDuration = (int) (usesModifier * Ollivanders2Common.ticksPerSecond * durationModifier);
                if (spellDuration > maxDuration)
                    spellDuration = maxDuration;
                else if (spellDuration < minDuration)
                    spellDuration = minDuration;

                // if the spell successfully transfigured something, stop the projectile and return
                stopProjectile();

                if (permanent)
                    kill();
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

package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.jetbrains.annotations.NotNull;

public abstract class ChangeEntitySizeSuper extends O2Spell
{
    static final int maxTargets = 10;
    int targets;

    static final int maxRadius = 20;
    int radius;

    /**
     * Is this spell growing or shrinking the entity?
     */
    boolean growing;

    /**
     * How much can this change a slimes size
     */
    static final int maxSlimeSizeChange = 2;
    static final int minSlimeSize = 1;
    static final int maxSlimeSize = 3;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ChangeEntitySizeSuper(Ollivanders2 plugin)
    {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ChangeEntitySizeSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
    }

    /**
     * Look for entities within the projectile range and change their size, if possible
     */
    @Override
    protected void doCheckEffect()
    {
        if (hasHitTarget())
        {
            kill();
            return;
        }

        for (LivingEntity livingEntity : getLivingEntities(radius))
        {
            if (targets < 1)
            {
                kill();
                return;
            }

            common.printDebugMessage("Checking " + livingEntity.getName(), null, null, false);

            if (livingEntity.getUniqueId() == player.getUniqueId())
                continue;

            if (!entityHarmWGCheck(livingEntity))
                continue;

            if (livingEntity instanceof Ageable)
            {
                if (growing)
                    ((Ageable) livingEntity).setAdult();
                else
                    ((Ageable) livingEntity).setBaby();
            }
            else if (livingEntity instanceof Slime)
            {
                changeSlimeSize((Slime) livingEntity);
            }
            else
                continue;

            targets = targets - 1;
        }
    }

    /**
     * Make the target slime smaller.
     *
     * @param slime the smile to reduce the size of
     */
    private void changeSlimeSize (@NotNull Slime slime)
    {
        int delta = (int)(usesModifier / 25) + 1;
        if (delta > maxSlimeSizeChange)
            delta = maxSlimeSizeChange;

        common.printDebugMessage("Changing slime size by " + delta, null, null, false);

        int newSize = slime.getSize();
        if (growing)
            newSize = newSize + delta;
        else
            newSize = newSize - delta;

        if (newSize > maxSlimeSize)
            newSize = maxSlimeSize;
        else if (newSize < minSlimeSize)
            newSize = minSlimeSize;

        slime.setSize(newSize);
    }
}

package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Bursts a log into a stack of coreless wands, whose number depends on the player's spell level.
 * <p>
 * {@link LIGATIS_COR}
 */
public final class FRANGE_LIGNEA extends O2Spell
{
    /**
     * The maximum number of coreless wands to create
     */
    static int maxAmount = 10;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FRANGE_LIGNEA(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.FRANGE_LIGNEA;
        branch = O2MagicBranch.CHARMS;

        text = "Frange lignea will cause a log to explode into coreless wands if the wood is of a type suitable for wand making.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FRANGE_LIGNEA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FRANGE_LIGNEA;
        branch = O2MagicBranch.CHARMS;

        // material black list
        materialBlockedList.add(Material.WATER);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Break a natural log in to coreless wands
     */
    @Override
    protected void doCheckEffect()
    {
        if (!hasHitTarget())
            return;

        kill();

        Block target = getTargetBlock();
        if (target == null)
            return;

        if (Ollivanders2Common.isNaturalLog(target))
        {
            // break the log in to the correct wand core type
            O2WandWoodType woodType = O2WandWoodType.getWandWoodTypeByMaterial(target.getType());
            if (woodType == null)
            {
                player.sendMessage(Ollivanders2.chatColor + "The targeted log is not suitable for wand making.");
                return;
            }

            World world = target.getLocation().getWorld();
            if (world == null)
                return;

            int amount = (int) (usesModifier * 0.1);
            if (amount > maxAmount)
                amount = maxAmount;
            else if (amount < 1)
                amount = 1;

            // make a stack of coreless wands
            ItemStack corelessWands = Ollivanders2API.getItems().getWands().createCorelessWand(woodType, amount);
            if (corelessWands == null)
            {
                common.printDebugMessage("Frange Lignea: failed to create coreless wands", null, null, true);
                return;
            }

            target.getLocation().getWorld().createExplosion(target.getLocation(), 0);
            player.getWorld().dropItemNaturally(target.getLocation(), corelessWands);

            target.setType(Material.AIR);
        }
    }
}
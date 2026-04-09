package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Spell that converts natural logs into coreless wands.
 *
 * <p>When cast at a suitable log block, the spell breaks it into a stack of coreless wands. The number of wands
 * created depends on the caster's skill level with the spell. Only logs of types suitable for wand making can
 * be transfigured.</p>
 *
 * @author Azami7
 * @see LIGATIS_COR for the complementary core-binding spell
 */
public final class FRANGE_LIGNEA extends O2Spell {
    /**
     * The maximum number of coreless wands to create
     */
    static int maxAmount = 10;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FRANGE_LIGNEA(Ollivanders2 plugin) {
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
    public FRANGE_LIGNEA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FRANGE_LIGNEA;
        branch = O2MagicBranch.CHARMS;

        // material black list
        materialBlockedList.add(Material.WATER);

        // make sure none of these are on the pass-through list
        projectilePassThrough.removeAll(materialBlockedList);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Converts a natural log block into coreless wands.
     *
     * <p>If the target block is a natural log suitable for wand making, the spell calculates the number of
     * coreless wands to create based on the caster's skill (usesModifier * 0.1, capped at maxAmount),
     * creates them, and drops them at the log location. The log block is then destroyed.</p>
     */
    @Override
    protected void doCheckEffect() {
        // projectile has stopped, kill the spell
        if (hasHitBlock())
            kill();

        Block target = getTargetBlock();
        if (target == null)
            return;

        Material blockType = target.getType();

        if (Ollivanders2Common.isNaturalLog(blockType)) {
            if (!O2WandWoodType.isWandWood(blockType)) {
                failureMessage = "The targeted log is not suitable for wand making.";

                sendFailureMessage();
                return;
            }

            // make a stack of coreless wands
            O2WandWoodType woodType = O2WandWoodType.getWandWoodTypeByMaterial(blockType);
            if (woodType == null) {
                common.printDebugMessage("Frange Lignea: null woodType", null, null, false);
                return;
            }

            // determine the amount of coreless wands based on caster's skill
            int amount = (int) Math.floor(usesModifier / 10);
            if (amount > maxAmount)
                amount = maxAmount;
            else if (amount < 1)
                amount = 1;

            ItemStack corelessWands = Ollivanders2API.getItems().getWands().createCorelessWand(woodType, amount);

            if (corelessWands == null) {
                common.printDebugMessage("Frange Lignea: failed to create coreless wands", null, null, true);
                return;
            }

            if (!Ollivanders2.testMode)
                world.createExplosion(location, 0);
            target.setType(Material.AIR);
            world.dropItemNaturally(location, corelessWands);
        }
        else {
            common.printDebugMessage(blockType + " is not a natural log type", null, null, false);
            sendFailureMessage();
        }
    }

    /**
     * Get the maximum number of coreless wands that can be created by the spell.
     *
     * @return the maximum number of coreless wands to create.
     */
    public int getMaxAmount() {
        return maxAmount;
    }
}
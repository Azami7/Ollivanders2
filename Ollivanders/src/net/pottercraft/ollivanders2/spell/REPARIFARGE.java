package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Reparifarge will untransfigure the target block or entity, does not work on Animagus transfigurations.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Reparifarge">https://harrypotter.fandom.com/wiki/Reparifarge</a>
 */
public final class REPARIFARGE extends O2Spell {
    static final int maxSuccessRate = 100;
    static final int minSuccessRate = 10;
    int successRate = minSuccessRate;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public REPARIFARGE(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.REPARIFARGE;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("Incomplete Transfigurations are difficult to put right, but you must attempt to do so. Leaving the head of a rabbit on a footstool is irresponsible and dangerous. Say 'Reparifarge!' and the object or creature should return to its natural state.");
            add("The Untransfiguration Spell");
        }};

        text = "Reparifarge will untransfigure the target block or entity.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public REPARIFARGE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REPARIFARGE;
        branch = O2MagicBranch.TRANSFIGURATION;

        successMessage = "Successfully untransfigured your target.";
        failureMessage = "Nothing seems to happen";

        initSpell();
    }

    /**
     * Set the success rate based on the caster's skill.
     */
    @Override
    void doInitSpell() {
        successRate = (int) usesModifier;
        if (successRate < minSuccessRate)
            successRate = minSuccessRate;
        else if (successRate > maxSuccessRate)
            successRate = maxSuccessRate;
    }

    @Override
    public void doCheckEffect() {
        if (hasHitTarget()) {
            if (getTargetBlock() == null) {
                common.printDebugMessage("Target block null in " + spellType.toString(), null, null, false);
                return;
            }
            if (reparifargeBlock(getTargetBlock()))
                player.sendMessage(Ollivanders2.chatColor + successMessage);
            else
                player.sendMessage(Ollivanders2.chatColor + failureMessage);

            kill();
        }
        else {
            // check the area around the current projectile location for entities that can be targeted
            for (Entity entity : getCloseEntities(1.5)) {
                if (entity.getUniqueId().equals(player.getUniqueId()))
                    continue;

                if (entity instanceof EnderDragonPart) // if this is part of an Ender Dragon, get the parent Dragon entity
                    entity = ((EnderDragonPart) entity).getParent();

                if (reparifargeEntity(entity)) {
                    player.sendMessage(Ollivanders2.chatColor + successMessage);
                    kill();
                    return;
                }
            }
        }
    }

    /**
     * Revert this transfiguration, if it is not permanent and the level of this transfiguration spell is less than the level of Reparifarge.
     *
     * @param target the target transfigured entity
     * @return true if the spell reverted a transfiguration, false otherwise
     */
    public boolean reparifargeEntity(@NotNull Entity target) {
        for (O2Spell spell : p.getProjectiles()) {
            if (spell.isPermanent())
                continue;

            if (spell instanceof TransfigurationBase && ((TransfigurationBase) spell).isEntityTransfigured(target)) {
                if ((spell.getLevel().ordinal() > this.spellType.getLevel().ordinal()) && checkSuccess())
                    spell.kill();

                return true;
            }
        }

        return false;
    }

    /**
     * Revert this transfiguration, if it is not permanent and the level of this transfiguration spell is less than the level of Reparifarge.
     *
     * @param target the target transfigured block
     * @return true if the spell reverted a transfiguration, false otherwise
     */
    public boolean reparifargeBlock(@NotNull Block target) {
        for (O2Spell spell : p.getProjectiles()) {
            if (spell.isPermanent())
                continue;

            if (spell instanceof TransfigurationBase && ((TransfigurationBase) spell).isBlockTransfigured(target)) {
                if (checkSuccess())
                    spell.kill();
                return true;
            }
        }

        return false;
    }

    /**
     * Check the success rate for this spell
     *
     * @return true if succeeded, false otherwise
     */
    boolean checkSuccess() {
        int success = Math.abs(Ollivanders2Common.random.nextInt()) % 100;

        return (success < successRate);
    }
}
package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The Reparifarge spell - an untransfiguration counter-spell.
 *
 * <p>Reparifarge reverses active transfigurations cast by other spells, returning transfigured blocks
 * and entities back to their original state. This spell is primarily useful for countering incomplete
 * or unwanted transfigurations. It does not work on permanent transfigurations or Animagus transformations.</p>
 *
 * <p><strong>Success Mechanics:</strong> The spell has a variable success rate based on the caster's
 * skill level with Reparifarge (number of uses). The success rate ranges from {@link #minSuccessRate}%
 * to {@link #maxSuccessRate}%. Block transfigurations can always be reverted with a successful cast,
 * but entity transfigurations can only be reverted if the source transfiguration spell has an equal or
 * lower level than Reparifarge.</p>
 *
 * <p><strong>Target Detection:</strong> The spell projectile searches for both blocks and entities
 * at the impact location. Ender Dragon parts are automatically converted to their parent dragon entity
 * for targeting consistency.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Reparifarge">Reparifarge on Harry Potter Wiki</a>
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

    /**
     * Executes the Reparifarge spell effect.
     *
     * <p>When the projectile hits a target block, attempts to revert any active transfiguration on that block.
     * When the projectile is in flight near entities, searches nearby entities and attempts to revert
     * transfigurations on the first valid target found. Ender Dragon parts are automatically converted to
     * their parent dragon entity before checking for transfigurations.</p>
     *
     * <p>Sends success or failure message to the caster and terminates the spell after attempting reversion.</p>
     */
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
            for (Entity entity : getCloseEntities(defaultRadius)) {
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
     * Attempts to revert any active entity transfiguration affecting the target entity.
     *
     * <p>Searches for active transfiguration spells that have transfigured the target entity.
     * If a matching transfiguration is found, checks if Reparifarge's level is equal to or greater than
     * the source transfiguration spell's level. If so, performs a success check and kills the source spell
     * if successful, reverting the transfiguration.</p>
     *
     * <p>Returns true if a transfiguration was found (regardless of whether it was reverted), false otherwise.
     * This distinguishes between "no transfiguration found" and "transfiguration found but revert failed".</p>
     *
     * @param target the target entity to check for transfiguration
     * @return true if any transfiguration was found affecting this entity, false otherwise
     */
    public boolean reparifargeEntity(@NotNull Entity target) {
        for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
            if (spell instanceof TransfigurationBase && ((TransfigurationBase) spell).isEntityTransfigured(target)) {
                if ((spell.getLevel().ordinal() > this.spellType.getLevel().ordinal()) && checkSuccess())
                    spell.kill();

                return true;
            }
        }

        return false;
    }

    /**
     * Attempts to revert any active block transfiguration affecting the target block.
     *
     * <p>Searches for active transfiguration spells that have transfigured the target block.
     * If a matching transfiguration is found, performs a success check and kills the source spell
     * if successful, reverting the transfiguration. Unlike entity transfigurations, block transfigurations
     * have no level requirement - they can always be reverted if the success check passes.</p>
     *
     * <p>Returns true if a transfiguration was found (regardless of whether it was reverted), false otherwise.
     * This distinguishes between "no transfiguration found" and "transfiguration found but revert failed".</p>
     *
     * @param target the target block to check for transfiguration
     * @return true if any transfiguration was found affecting this block, false otherwise
     */
    public boolean reparifargeBlock(@NotNull Block target) {
        for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
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
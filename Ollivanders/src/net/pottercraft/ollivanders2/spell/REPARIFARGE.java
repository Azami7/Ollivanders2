package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.Map;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Untransfiguration counter-spell that reverses active transfigurations on blocks and entities.
 * <p>
 * Reparifarge can target both block transfigurations (via block-hit) and entity transfigurations
 * (via entity scanning along the projectile path). A transfiguration can be reverted if the source
 * spell's {@link net.pottercraft.ollivanders2.common.MagicLevel} is at most one level above
 * Reparifarge's own level. If the level check passes, the revert is subject to a random success
 * check that scales from {@link #minSuccessRate}% to {@link #maxSuccessRate}% based on caster
 * experience.
 * </p>
 * <p>
 * Water is removed from the projectile pass-through list so the spell can stop on water blocks
 * created by transfigurations like {@link TERGEO} → {@link AGUAMENTI}.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Reparifarge">Reparifarge</a>
 */
public final class REPARIFARGE extends O2Spell {
    /**
     * Upper bound for the success rate percentage (100 = always succeeds at mastery).
     */
    public static final int maxSuccessRate = 100;

    /**
     * Lower bound for the success rate percentage (10 = 10% chance even at minimum skill).
     */
    public static final int minSuccessRate = 10;

    /**
     * The current success rate for this cast, clamped to [{@link #minSuccessRate}, {@link #maxSuccessRate}].
     * Set in {@link #doInitSpell()} based on caster experience.
     */
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

        projectilePassThrough.remove(Material.WATER);

        successMessage = "Successfully untransfigured your target.";

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
     * Attempt to revert a transfiguration on the target block or a nearby entity.
     * <p>
     * When the projectile hits a block, the block is checked for an active transfiguration via
     * {@link #reparifargeBlock(Block)}. When the projectile is in flight, nearby entities are
     * scanned via {@link #reparifargeEntity(Entity)} (caster excluded, {@link EnderDragonPart}
     * resolved to parent). In both cases, a success or failure message is sent to the caster
     * and the spell is killed after the first attempt.
     * </p>
     */
    @Override
    public void doCheckEffect() {
        if (hasHitBlock()) {
            if (getTargetBlock() == null)
                common.printDebugMessage("Target block null in " + spellType.toString(), null, null, false);
            else if (reparifargeBlock(getTargetBlock()))
                sendSuccessMessage();
            else
                sendFailureMessage();

            kill();
        }
        else {
            // check the area around the current projectile location for entities that can be targeted
            for (Entity entity : getNearbyEntities(defaultRadius)) {
                if (entity.getUniqueId().equals(caster.getUniqueId()))
                    continue;

                if (entity instanceof EnderDragonPart) // if this is part of an Ender Dragon, get the parent Dragon entity
                    entity = ((EnderDragonPart) entity).getParent();

                if (reparifargeEntity(entity)) {
                    sendSuccessMessage();
                    kill();
                    return;
                }
            }
        }
    }

    /**
     * Attempts to revert any active entity transfiguration affecting the target entity.
     *
     * @param target the target entity to check for transfiguration
     * @return true if any transfiguration was found affecting this entity, false otherwise
     */
    public boolean reparifargeEntity(@NotNull Entity target) {
        for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
            if (spell instanceof Transfiguration && ((Transfiguration) spell).isTransfigured(target)) {
                if ((spell.getLevel().ordinal() <= this.spellType.getLevel().ordinal() + 1) && checkSuccess())
                    spell.kill();

                return true;
            }
        }

        return false;
    }

    /**
     * Attempts to revert any active block transfiguration affecting the target block.
     *
     * @param target the target block to check for transfiguration
     * @return true if any transfiguration was found affecting this block, false otherwise
     */
    public boolean reparifargeBlock(@NotNull Block target) {
        for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
            if (spell instanceof Transfiguration && ((Transfiguration) spell).isTransfigured(target)) {
                if ((spell.getLevel().ordinal() <= this.spellType.getLevel().ordinal() + 1) && checkSuccess())
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
        int success = Math.abs(Ollivanders2Common.random.nextInt(100));

        return (success < successRate);
    }

    /**
     * Get the success rate for this cast, as a percentage clamped to
     * [{@link #minSuccessRate}, {@link #maxSuccessRate}].
     *
     * @return the success rate percentage (10–100)
     */
    public int getSuccessRate() {
        return successRate;
    }
}
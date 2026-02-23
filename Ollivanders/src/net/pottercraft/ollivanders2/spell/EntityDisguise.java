package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for disguise-based entity transfigurations.
 *
 * <p>Uses the LibsDisguises plugin to transform entities into other entity types. Handles applying and
 * reverting disguises to entities when the spell duration expires.</p>
 *
 * @author Azami7
 * @see <a href="https://www.spigotmc.org/wiki/lib-s-disguises-disguising-the-entity/">LibsDisguises Documentation</a>
 */
public abstract class EntityDisguise extends EntityTransfiguration {
    /**
     * The libDisguises disguise type
     */
    protected DisguiseType disguiseType;

    /**
     * The libsDisguises disguise
     */
    protected TargetedDisguise disguise;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EntityDisguise(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EntityDisguise(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Transfigure the entity.
     *
     * @param entity the entity to transfigure
     */
    @Override
    @Nullable
    protected Entity transfigureEntity(@NotNull Entity entity) {
        DisguiseAPI.disguiseToAll(entity, disguise);

        return entity;
    }

    /**
     * Determine if this entity can be transfigured by this spell.
     *
     * <p>Checks if LibsDisguises is enabled for spells that require it, then delegates to parent validation.
     * Entity can be transfigured if:
     * <ul>
     * <li>The entity is not already the target type</li>
     * <li>It is not in the blocked list</li>
     * <li>It is in the allowed list, if the allowed list exists</li>
     * </ul></p>
     *
     * @param entity the entity to check
     * @return true if the entity can be transfigured, false otherwise
     */
    @Override
    protected boolean canTransfigure(@NotNull Entity entity) {
        if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.requiresLibsDisguises(spellType)) {
            common.printDebugMessage("LibsDisguises not enabled.", null, null, false);
            return false;
        }

        return super.canTransfigure(entity);
    }

    /**
     * Revert the entity back to their original form.
     */
    @Override
    public void revert() {
        if (Ollivanders2.testMode)
            return;

        Entity entity = disguise.getEntity();
        try {
            DisguiseAPI.undisguiseToAll(entity);
        }
        catch (Exception e) {
            // in case entity no longer exists
        }
    }
}

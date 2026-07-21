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
 * Base class for transfiguration spells that disguise an entity as another entity type using the LibsDisguises plugin.
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
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EntityDisguise(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Disguise the entity as {@link #disguiseType} using LibsDisguises.
     *
     * @param entity the entity to transfigure
     * @return the same entity, now disguised
     */
    @Override
    @Nullable
    protected Entity transfigureEntity(@NotNull Entity entity) {
        DisguiseAPI.disguiseToAll(entity, disguise);

        return entity;
    }

    /**
     * Check whether an entity is an eligible target: in addition to the base entity checks, LibsDisguises must be
     * enabled when this spell requires it.
     *
     * @param entity the entity to check
     * @return true if the entity can be transfigured, false otherwise
     */
    @Override
    public boolean canTransfigure(@NotNull Entity entity) {
        if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.requiresLibsDisguises(spellType)) {
            common.printDebugMessage("LibsDisguises not enabled.", null, null, false);
            return false;
        }

        return super.canTransfigure(entity);
    }

    /**
     * Remove the entity's disguise, restoring its original appearance.
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

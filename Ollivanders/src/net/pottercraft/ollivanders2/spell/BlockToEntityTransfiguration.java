package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Transform a block in to an entity.
 */
public abstract class BlockToEntityTransfiguration extends BlockTransfiguration implements Listener {
    /**
     * The entity that has been transfigured by this spell
     */
    Entity transfiguredEntity = null;

    /**
     * The entity type to transfigure the target in to, a sheep by default
     */
    EntityType entityType = EntityType.SHEEP;

    /**
     * If this is populated, a map of materials and what entity type to change them in to.
     * <p>
     * For use with spells that can do more than one type of change. Add each material that can
     * be changed to this map. Any missing material will fall back to the default entityType.
     * <p>
     * If the spell can only target one or more specific material types and they all change to
     * the same thing, add that to materialWhitelist and set entityType.
     * <p>
     * If the spell can target any material, make materialWhitelist blank and set entityType.
     */
    protected Map<Material, EntityType> transfigurationMap = new HashMap<>();

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public BlockToEntityTransfiguration(Ollivanders2 plugin) {
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
    public BlockToEntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Register the listeners for this spell
     */
    @Override
    void initSpell() {
        super.initSpell();

        // register listeners
        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Transfigure the block in to the desired entity type.
     */
    @Override
    protected void transfigure() {
        Block target = getTargetBlock();
        if (target == null)
            // we have not hit a target yet, continue projectile
            return;

        if (!canTransfigure(target)) {
            common.printDebugMessage("Transfiguration not allowed", null, null, false);
            sendFailureMessage();
            kill();
            return;
        }

        if (!permanent)
            changedBlocks.put(target, target.getType());

        // get the type to change this material
        if (transfigurationMap.containsKey(target.getType()))
            entityType = transfigurationMap.get(target.getType());

        // change the block to air
        target.setType(Material.AIR);
        isTransfigured = true;

        // spawn the entity in this location
        if (entityType != null) {
            transfiguredEntity = target.getWorld().spawnEntity(location, entityType);
            customizeEntity();
        }
        else {
            kill();
            common.printDebugMessage("Entity type was null in " + spellType.toString(), null, null, true);
            sendFailureMessage();
            return;
        }

        // register listeners
        p.getServer().getPluginManager().registerEvents((Listener) this, p);

        sendSuccessMessage();
    }

    /**
     * Set duration, including making the spell permanent, based on caster's skill.
     */
    void setDuration() {
        if (usesModifier >= 200) {
            O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
            if (o2p == null)
                common.printDebugMessage("Null o2player in BlockToEntityTransfiguration.setDuration()", null, null, true);

            if (!Ollivanders2.useYears || (o2p != null && o2p.getYear().getHighestLevelForYear().ordinal() >= this.spellType.getLevel().ordinal()))
                permanent = true;
        }
        else {
            permanent = false;

            // spell duration
            spellDuration = (int) (usesModifier * Ollivanders2Common.ticksPerSecond * durationModifier);
            if (spellDuration < minDuration)
                spellDuration = minDuration;
            else if (spellDuration > maxDuration)
                spellDuration = maxDuration;
        }
    }

    /**
     * Despawn the created entity unless this spell is permanent.
     */
    @Override
    void doRevert() {
        if (permanent)
            return;

        if (transfiguredEntity != null)
            transfiguredEntity.remove();
    }

    /**
     * Is this entity transfigured by this spell
     *
     * @param entity the entity to check
     * @return true if transfigured, false otherwise
     */
    @Override
    public boolean isEntityTransfigured(@NotNull Entity entity) {
        if (permanent)
            return false;

        if (transfiguredEntity == null)
            return false;

        return transfiguredEntity.getUniqueId().equals(entity.getUniqueId());
    }

    /**
     * Is this block transfigured by this spell
     *
     * @param block the block to check
     * @return true if transfigured, false otherwise
     */
    @Override
    public boolean isBlockTransfigured(@NotNull Block block) {
        return false;
    }

    /**
     * Let child spells optionally customize the spawned entity. This must be overridden by the child classes.
     */
    void customizeEntity() {
    }

    /**
     * Handle when the transfigured entity is killed.
     *
     * @param event the entity death event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (transfiguredEntity == null)
            return;

        Entity entity = event.getEntity();
        if (entity.getUniqueId().equals(transfiguredEntity.getUniqueId()))
            // the entity was killed, kill this spell
            kill();
    }
}

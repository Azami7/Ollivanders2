package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Aqua Eructo — a water-based spell that extinguishes burning entities and items.
 *
 * <p>AQUA_ERUCTO shoots water from the caster's wand and targets any on-fire entity or item in its path.
 * When the spell finds a burning target, it extinguishes the fire and places a water block at the target's
 * eye location (for living entities) or center location (for items). The water block persists temporarily
 * to create a visual effect, then reverts to its original state.</p>
 *
 * <p>Spell behavior:
 * <ul>
 * <li><strong>Target Detection:</strong> Searches for on-fire entities and items as the projectile travels</li>
 * <li><strong>Extinguishing:</strong> Removes fire ticks from any burning target found</li>
 * <li><strong>Water Effect:</strong> Places a temporary water block at the target's eye level</li>
 * <li><strong>Duration:</strong> Water block remains for {@link #waterBlockTTL} (1 second)</li>
 * <li><strong>Caster Protection:</strong> Does not extinguish the caster if they are on fire</li>
 * </ul>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Aqua_Eructo">Aqua Eructo on Harry Potter Wiki</a>
 */
public class AQUA_ERUCTO extends O2Spell {
    /**
     * Time-to-live for the water block placed by this spell, in ticks.
     *
     * <p>Default is 1 second (20 ticks). After this duration, the water block is reverted
     * to its original state via the revert system.</p>
     */
    public static final int waterBlockTTL = Ollivanders2Common.ticksPerSecond;

    /**
     * Whether this spell successfully extinguished a target.
     *
     * <p>Set to true after finding and extinguishing a burning entity or item. Used to determine
     * whether the spell should continue alive (maintaining the water block) or kill itself
     * (if it hit a target but didn't extinguish anything).</p>
     */
    private boolean extinguished = false;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AQUA_ERUCTO(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AQUA_ERUCTO;
        branch = O2MagicBranch.CHARMS;

        text = "Shoots water from your wand and extinguishes an entity or item that is on fire.";
    }

    /**
     * Constructor for casting the spell.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using (correctness factor)
     */
    public AQUA_ERUCTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        moveEffectData = Material.BLUE_ICE;

        spellType = O2SpellType.AQUA_ERUCTO;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * Applies the water extinguishing effect to targets each tick.
     *
     * <p>This spell operates in three states:
     * <ul>
     * <li><strong>Traveling:</strong> Searches for nearby on-fire entities and items; extinguishes the first
     * one found and places a water block at its location</li>
     * <li><strong>Water Active:</strong> Projectile has hit and extinguished a target; waits for the
     * water block TTL to expire before killing the spell</li>
     * <li><strong>No Target:</strong> Projectile hit a location but found no on-fire target; kills the spell</li>
     * </ul>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget()) {
            for (Entity entity : getNearbyEntities(1.5)) {
                if (entity.getUniqueId().equals(player.getUniqueId())) // don't target the caster
                    continue;

                if (canTarget(entity)) {
                    stopProjectile();

                    World world = location.getWorld();
                    Block targetBlock;

                    if (entity instanceof LivingEntity)
                        targetBlock = ((LivingEntity) entity).getEyeLocation().getBlock();
                    else
                        targetBlock = entity.getLocation().getBlock();

                    if (BlockCommon.isAirBlock(targetBlock) && !Ollivanders2API.getBlocks().isTemporarilyChangedBlock(targetBlock)) {
                        Ollivanders2API.getBlocks().addTemporarilyChangedBlock(targetBlock, this);
                        targetBlock.setType(Material.WATER);
                    }

                    if (world != null)
                        world.playSound(location, Sound.ENTITY_GENERIC_SPLASH, 1, 0);

                    effectEntity(entity);

                    extinguished = true;
                    break;
                }
            }
        }
        else if (extinguished) {
            if (getAge() > waterBlockTTL)
                kill();
        }
        else { // hasHitTarget && !extinguished
            kill();
        }
    }

    /**
     * Determines whether this spell can target a given entity.
     *
     * <p>An entity is a valid target if it is currently on fire (has fire ticks > 0).
     * This method can be overridden by subclasses to implement different targeting logic.</p>
     *
     * @param entity the entity to check
     * @return true if the entity is on fire and can be targeted, false otherwise
     */
    boolean canTarget(Entity entity) {
        common.printDebugMessage("AQUA_ERUCTO.canTarget(): checking " + entity.getType(), null, null, false);

        if (entity.getFireTicks() > 0) { // entity is on fire
            common.printDebugMessage("AQUA_ERUCTO.canTarget(): entity is on fire", null, null, false);
            return true;
        }

        return false;
    }

    /**
     * Applies the extinguishing effect to a target entity, assumes canTarget() has been checked.
     *
     * <p>Removes all fire from the entity by setting its fire ticks to 0.
     * This method can be overridden by subclasses to implement different effect behaviors.</p>
     *
     * @param entity the entity to extinguish
     */
    void effectEntity(Entity entity) {
        entity.setFireTicks(0);
    }

    /**
     * Reverts the water block back to its original state.
     *
     * <p>Called when the spell ends or the water block TTL expires. Removes the temporary water block
     * that was placed at the target location and restores it to its original material type.</p>
     */
    @Override
    protected void revert() {
        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }

    /**
     * Whether this spell successfully extinguished a target.
     *
     * @return true if the spell found and extinguished a burning entity or item, false otherwise
     */
    public boolean isExtinguished() {
        return extinguished;
    }
}
package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Aqua Eructo: shoots water that extinguishes the first burning entity or item in its path, briefly placing a water
 * block at the target. The caster is never targeted, even if on fire.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Aqua_Eructo">Harry Potter Wiki - Aqua Eructo</a>
 */
public class AQUA_ERUCTO extends O2Spell {
    /**
     * How long the placed water block persists before it is reverted, in ticks.
     */
    public static final int waterBlockTTL = Ollivanders2Common.ticksPerSecond;

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
     * While traveling, extinguish the first valid burning target in range and place a temporary water block on it;
     * once something has been extinguished, end the spell after the water block's TTL; end immediately if the
     * projectile hits a block without extinguishing anything.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock()) {
            for (Entity entity : getNearbyEntities(1.5)) {
                if (entity.getUniqueId().equals(caster.getUniqueId())) // don't target the caster
                    continue;

                if (canTarget(entity)) {
                    stopProjectile();

                    Block targetBlock;

                    if (entity instanceof LivingEntity)
                        targetBlock = ((LivingEntity) entity).getEyeLocation().getBlock();
                    else
                        targetBlock = entity.getLocation().getBlock();

                    if (BlockCommon.isAirBlock(targetBlock) && !Ollivanders2API.getBlocks().isTemporarilyChangedBlock(targetBlock)) {
                        Ollivanders2API.getBlocks().addTemporarilyChangedBlock(targetBlock, this);
                        targetBlock.setType(Material.WATER);
                    }

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
     * Whether this spell can target the given entity. Subclasses override this to change what counts as a target.
     *
     * @param entity the entity to check
     * @return true if the entity is on fire
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
     * Apply this spell's effect to a target that has passed {@link #canTarget}. The base spell extinguishes the
     * entity; subclasses override for other effects.
     *
     * @param entity the entity to affect
     */
    void effectEntity(Entity entity) {
        entity.setFireTicks(0);
    }

    /**
     * Revert the temporary water block this spell placed back to its original material.
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
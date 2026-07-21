package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Piertotum Locomotor - The Animation Charm: transfigures an iron block into an iron golem or a snow block into a snow
 * golem that is loyal to the caster — it will not attack them and retaliates against anyone who does.
 *
 * <p>The transfiguration lasts a skill-scaled duration, or becomes permanent for a sufficiently skilled caster.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Animation_Charm">Harry Potter Wiki - Animation Charm</a>
 */
public final class PIERTOTUM_LOCOMOTOR extends BlockToEntityTransfiguration {
    private static final int minDurationConfig = 30 * Ollivanders2Common.ticksPerSecond;
    private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PIERTOTUM_LOCOMOTOR(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PIERTOTUM_LOCOMOTOR;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("And all along the corridor the statues and suits of armour jumped down from their plinths, and from the echoing crashes from the floors above and below, Harry knew that their fellows throughout the castle had done the same... Cheering and yelling, the horde of moving statues stampeded past Harry; some of them smaller, others larger than life.");
            add("They were standing on the edge of a huge chessboard, behind the black chessmen, which were all taller than they were and carved from what looked like black stone. Facing them, way across the chamber, were white pieces — the towering white chessmen had no faces.");
            add("Next second he had reappeared behind Voldemort and waved his wand toward the remnants of the fountain; the other statues sprang to life too.");
        }};

        text = "Piertotum locomotor, if cast at an iron or snow block, will transfigure that block into an iron or snow golem. This transfiguration's duration depends on your experience and will be permanent for the most skilled caster.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PIERTOTUM_LOCOMOTOR(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PIERTOTUM_LOCOMOTOR;
        branch = O2MagicBranch.TRANSFIGURATION;

        consumeOriginal = true;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.MOB_SPAWNING);

        // materials this transfiguration works on
        materialAllowList.add(Material.IRON_BLOCK);
        materialAllowList.add(Material.SNOW_BLOCK);

        // map of entity type to change each material to
        transfigurationMap.put(Material.IRON_BLOCK, EntityType.IRON_GOLEM);
        transfigurationMap.put(Material.SNOW_BLOCK, EntityType.SNOW_GOLEM);

        initSpell();
    }

    @Override
    void doInitSpell() {
        successRate = 100;
    }

    /**
     * Set the transfiguration duration from the caster's skill, or make it permanent at 1.5x spell mastery (subject to
     * the caster's house/year level when years are enabled). Below that threshold the duration is floored and capped
     * at the configured min/max bounds.
     */
    @Override
    void setDuration() {
        if (usesModifier >= O2Spell.spellMasteryLevel * 1.5) {
            if (!Ollivanders2.useYears || (casterO2P.getYear().getHighestLevelForYear().ordinal() >= this.spellType.getLevel().ordinal()))
                permanent = true;
        }
        else {
            permanent = false;

            effectDuration = (int) (usesModifier * Ollivanders2Common.ticksPerSecond * durationModifier);
            if (effectDuration < minDuration)
                effectDuration = minDuration;
            else if (effectDuration > maxDuration)
                effectDuration = maxDuration;
        }
    }

    /**
     * Keep the golem loyal to its creator: cancel any attack the golem makes on the caster, and make the golem
     * retaliate against anyone who attacks the caster.
     *
     * @param event the entity damage by entity event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (transfiguredEntity == null)
            return;

        Entity attacker = event.getDamager();
        Entity target = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (!EntityCommon.isDamageCausedByAttack(cause))
            return;

        if (attacker.getUniqueId().equals(transfiguredEntity.getUniqueId()) && target.getUniqueId().equals(caster.getUniqueId()))
            event.setCancelled(true);
        else if (target.getUniqueId().equals(caster.getUniqueId()) && transfiguredEntity instanceof LivingEntity)
            ((LivingEntity) transfiguredEntity).attack(attacker);
    }

    /**
     * Prevent the golem from targeting its creator.
     *
     * @param event the entity target event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTargetEvent(EntityTargetEvent event) {
        if (transfiguredEntity == null)
            return;

        Entity attacker = event.getEntity();
        Entity target = event.getTarget();

        if (target == null)
            return;

        if (attacker.getUniqueId().equals(transfiguredEntity.getUniqueId()) && target.getUniqueId().equals(caster.getUniqueId()))
            event.setCancelled(true);
    }
}
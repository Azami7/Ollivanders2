package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Transfigures an iron golem from a block of iron, and snow golem from block of snow.
 *
 * @author lownes
 * @author Azami7
 */
public final class PIERTOTUM_LOCOMOTOR extends BlockToEntityTransfiguration implements Listener
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public PIERTOTUM_LOCOMOTOR (Ollivanders2 plugin)
   {
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
   public PIERTOTUM_LOCOMOTOR(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PIERTOTUM_LOCOMOTOR;
      branch = O2MagicBranch.TRANSFIGURATION;

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.MOB_SPAWNING);

      // materials this transfiguration works on
      materialWhitelist.add(Material.IRON_BLOCK);
      materialWhitelist.add(Material.SNOW_BLOCK);

      // map of entity type to change each material to
      transfigurationMap.put(Material.IRON_BLOCK, EntityType.IRON_GOLEM);
      transfigurationMap.put(Material.SNOW_BLOCK, EntityType.SNOWMAN);

      successRate = 100;

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      if (usesModifier >= 200)
         permanent = true;
      else
      {
         permanent = false;

         // spell duration
         spellDuration = (int) usesModifier * Ollivanders2Common.ticksPerSecond;
         if (spellDuration < minDuration)
            spellDuration = minDuration;
         else if (spellDuration > maxDuration)
            spellDuration = maxDuration;
      }
   }

   /**
    * Prevent the golem from harming its creator and have it attack anyone who does
    *
    * @param event the entity damage event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onEntityDamage(EntityDamageByEntityEvent event)
   {
      Entity attacker = event.getDamager();
      Entity target = event.getEntity();
      EntityDamageEvent.DamageCause cause = event.getCause();

      if (!Ollivanders2Common.attackDamageCauses.contains(cause))
         return;

      if (attacker.getUniqueId() == transfiguredEntity.getUniqueId() && target.getUniqueId() == player.getUniqueId())
         // prevent the golem attacking its creator
         event.setCancelled(true);
      else if (target.getUniqueId() == player.getUniqueId() && transfiguredEntity instanceof LivingEntity)
         // attack anyone who attacks the creator
         ((LivingEntity)transfiguredEntity).attack(attacker);
   }

   /**
    * Prevent golem from targeting its creator.
    *
    * @param event the entity target event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onEntityTarget(EntityTargetEvent event)
   {
      Entity attacker = event.getEntity();
      Entity target = event.getTarget();

      if (target == null)
         return;

      if (attacker.getUniqueId() == transfiguredEntity.getUniqueId() && target.getUniqueId() == player.getUniqueId())
         event.setCancelled(true);
   }

   /**
    * Handle when the golem is killed.
    *
    * @param event the entity death event
    */
   @EventHandler(priority = EventPriority.LOWEST)
   public void onEntityDeath(EntityDeathEvent event)
   {
      Entity entity = event.getEntity();
      if (entity.getUniqueId() == transfiguredEntity.getUniqueId())
         // the golem was killed, kill this spell
         kill();
   }
}
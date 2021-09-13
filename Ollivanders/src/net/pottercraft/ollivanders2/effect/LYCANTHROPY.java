package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Turns player into a werewolf during the full moon. Doesn't go away until death (if deathExpLoss is set to
 * true).
 *
 * @author azami7
 * @since 2.2.8
 */
public class LYCANTHROPY extends ShapeShiftSuper
{
   ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public LYCANTHROPY(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.LYCANTHROPY;
      legilimensText = "is a werewolf";

      form = EntityType.WOLF;
      permanent = true;
      transformed = false;
   }

   /**
    * Transfigure the player back to human form and kill this effect.
    */
   @Override
   public void kill ()
   {
      restore();
      removeAdditionalEffect();

      kill = true;
   }

   /**
    * Change player in to a wolf for 1 day when the full moon occurs.
    *
    * See https://minecraft.gamepedia.com/Moon
    */
   @Override
   protected void upkeep ()
   {
      Player target = p.getServer().getPlayer(targetID);

      if (target == null)
      {
         kill();
         return;
      }

      long curTime = target.getWorld().getTime();
      if (!transformed)
      {
         // only need to check after sunset
         if (curTime > 13000)
         {
            long day = target.getWorld().getFullTime() / 24000;
            if ((day % 8) == 0)
            {
               // moonrise on a full moon day
               transform();

               addAdditionalEffects();

               target.playSound(target.getEyeLocation(), Sound.ENTITY_WOLF_HOWL, 1, 0);
            }
         }
      }
      else
      {
         long day = target.getWorld().getFullTime()/24000;
         boolean restore = false;

         if ((day % 8) == 0)
         {
            // if it is a full moon day before moonrise or after sunrise
            if (curTime < 13000 || curTime > 23500)
            {
               restore = true;
            }
         }
         else
         {
            // it is not a full moon day
            restore = true;
         }

         if (restore)
         {
            restore();
            removeAdditionalEffect();
         }
      }
   }

   /**
    * Add additional effects of lycanthropy such as aggression and speaking like a wolf
    */
   private void addAdditionalEffects ()
   {
      AGGRESSION aggression = new AGGRESSION(p, 5, targetID);
      aggression.setAggressionLevel(10);
      Ollivanders2API.getPlayers(p).playerEffects.addEffect(aggression);
      additionalEffects.add(O2EffectType.AGGRESSION);

      LYCANTHROPY_SPEECH speech = new LYCANTHROPY_SPEECH(p, 5, targetID);
      Ollivanders2API.getPlayers(p).playerEffects.addEffect(speech);
      additionalEffects.add(O2EffectType.LYCANTHROPY_SPEECH);
   }

   /**
    * Remove additional effects of Lycanthropy
    */
   private void removeAdditionalEffect()
   {
      for (O2EffectType effectType : additionalEffects)
      {
         Ollivanders2API.getPlayers(p).playerEffects.removeEffect(targetID, effectType);
      }
   }

   /**
    * Override setPermanent so that no code can inadvertently make lycanthropy effect age.
    *
    * @param perm true if this is permanent, false otherwise
    */
   @Override
   public void setPermanent(boolean perm) { }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove () { }

   /**
    * Do any on damage effects
    */
   @Override
   public void doOnDamage (@NotNull EntityDamageByEntityEvent event)
   {
      if (!O2EffectType.LYCANTHROPY.isEnabled())
         return;

      if (event.getDamager() instanceof Wolf)
      {
         Wolf wolf = (Wolf) event.getDamager();
         Player damaged = p.getServer().getPlayer(targetID);
         if (damaged == null)
         {
            common.printDebugMessage("Null player in Lycanthropy.doOnDamage", null, null, true);
            return;
         }

         if (wolf.isAngry())
         {
            new BukkitRunnable()
            {
               @Override
               public void run()
               {
                  if (!event.isCancelled())
                     infectPlayer(damaged);
               }
            }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
         }
      }
   }

   /**
    * Infect this player with Lycanthropy
    *
    * @param player the player to infect
    */
   private void infectPlayer (Player player)
   {
      if (!Ollivanders2API.getPlayers(p).playerEffects.hasEffect(player.getUniqueId(), O2EffectType.LYCANTHROPY))
      {
         LYCANTHROPY effect = new LYCANTHROPY(p, 100, player.getUniqueId());
         Ollivanders2API.getPlayers(p).playerEffects.addEffect(effect);
      }
   }

   /**
    * Do any on player interact effects
    */
   @Override
   public void doOnPlayerInteract (@NotNull PlayerInteractEvent event) {}

   /**
    * Do any on player player chat effects
    */
   @Override
   public void doOnPlayerChat (@NotNull AsyncPlayerChatEvent event) {}

   /**
    * Do any effects when player sleeps
    *
    * @param event the player bed enter event
    */
   @Override
   public void doOnPlayerSleep (@NotNull PlayerBedEnterEvent event) {}

   /**
    * Do any effects when player toggles flight
    *
    * @param event the player toggle flight event
    */
   @Override
   public void doOnPlayerToggleFlight (@NotNull PlayerToggleFlightEvent event) {}

   /**
    * Do any effects when player toggles sneaking
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerToggleSneak (@NotNull PlayerToggleSneakEvent event) {}

   /**
    * Do any effects when player toggles sneaking
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerToggleSprint (@NotNull PlayerToggleSprintEvent event) {}

   /**
    * Do any effects when player velocity changes
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerVelocityEvent (@NotNull PlayerVelocityEvent event) {}

   /**
    * Do any effects when player picks up an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerPickupItemEvent (@NotNull EntityPickupItemEvent event) {}

   /**
    * Do any effects when player holds an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerItemHeldEvent (@NotNull PlayerItemHeldEvent event) {}

   /**
    * Do any effects when player consumes an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerItemConsumeEvent (@NotNull PlayerItemConsumeEvent event) {}

   /**
    * Do any effects when player drops an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerDropItemEvent (@NotNull PlayerDropItemEvent event) {}

   /**
    * Do any effects when player drops an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerMoveEvent (@NotNull PlayerMoveEvent event) {}
}
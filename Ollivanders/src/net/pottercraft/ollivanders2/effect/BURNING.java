package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BURNING extends O2Effect
{
   private final double minDamage = 0.5;
   private final double maxDamage = 10;
   private double damage = 0.0;

   private int damageFrequencyInSeconds = 3;

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the player this effect acts on
    */
   public BURNING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.BURNING;
      informousText = legilimensText = "is afflicted with a terrible burning";

      divinationText.add("shall be cursed");
      divinationText.add("will be consumed by fire");
      divinationText.add("will burn from within");

      damage = (double) duration / 100;
   }

   @Override
   public void checkEffect()
   {
      Player target = p.getServer().getPlayer(targetID);

      if (target == null)
      {
         kill();
         return;
      }

      long curTime = target.getWorld().getFullTime();

      if (((curTime % (Ollivanders2Common.ticksPerSecond * damageFrequencyInSeconds)) == 0))
      {
         doDamage();
      }
   }

   private void doDamage()
   {
      Player target = p.getServer().getPlayer(targetID);
      if (target != null)
      {
         if ((target.getHealth() - damage) > 1)
         {
            target.damage(damage);
         }

         target.getWorld().playEffect(target.getLocation(), Effect.SMOKE, 4);
         target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, 0);
      }
      else
         kill();
   }

   public void addDamage(double d)
   {
      damage = damage + d;

      if (damage < minDamage)
         damage = minDamage;
      else if (damage > maxDamage)
         damage = maxDamage;

      // copy damage to duration so we can get it back after serialization
      duration = (int) damage * 100;
   }

   public void removeDamage(double d)
   {
      addDamage(d * -1.0);
   }

   public double getDamage()
   {
      return damage;
   }

   /**
    * Override default set permanent to make sure it doesnt get called and alter the duration since this is
    * already a permanent effect.
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
    *
    * @param event the event
    */
   @Override
   public void doOnDamage (@NotNull EntityDamageByEntityEvent event) {}

   /**
    * Do any on player interact effects
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerInteract (@NotNull PlayerInteractEvent event) {}

   /**
    * Do any on player player chat effects
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerChat (@NotNull AsyncPlayerChatEvent event) {}

   /**
    * Do any effects when player sleeps
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerSleep (@NotNull PlayerBedEnterEvent event) {}

   /**
    * Do any effects when player toggles flight
    *
    * @param event the event
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

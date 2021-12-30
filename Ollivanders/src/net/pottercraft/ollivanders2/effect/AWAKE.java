package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This effect keeps a player awake when other effects would cause them to sleep.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class AWAKE extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the player this effect acts on
    */
   public AWAKE(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.AWAKE;
      informousText = legilimensText = "is unnaturally alert";
   }

   /**
    * Age this effect each game tick.
    */
   @Override
   public void checkEffect()
   {
      age(1);
   }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove() {}

   /**
    * Do any effects when player sleeps
    *
    * @param event the player bed enter event
    */
   @Override
   void doOnPlayerBedEnterEvent(@NotNull PlayerBedEnterEvent event)
   {
      event.setCancelled(true);
      common.printDebugMessage("AWAKE: cancelling PlayerBedEnterEvent", null, null, false);

   }
}

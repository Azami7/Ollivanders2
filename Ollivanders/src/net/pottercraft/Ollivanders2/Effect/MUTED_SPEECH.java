package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MUTED_SPEECH extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public MUTED_SPEECH (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.MUTED_SPEECH;
   }

   /**
    * Age the effect by 1 every game tick.
    */
   public void checkEffect ()
   {
      age(1);
   }

   /**
    * Remove all recepients from chat if the player.
    *
    * @param event the player chat event
    */
   public void doSilencio (AsyncPlayerChatEvent event)
   {
      Player sender = event.getPlayer();

      if (Ollivanders2.debug)
      {
         p.getLogger().info("onPlayerChat: SILENCIO");
      }

      if (sender.isPermissionSet("Ollivanders2.BYPASS"))
      {
         if (!sender.hasPermission("Ollivanders2.BYPASS"))
         {
            event.getRecipients().clear();
         }
      }
      else
      {
         event.getRecipients().clear();
      }
   }
}
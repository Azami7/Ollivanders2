package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SILENCIO extends OEffect implements Effect
{
   public SILENCIO (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   public void checkEffect (Ollivanders2 p, Player owner)
   {
      age(1);
   }

   public void doSilencio (Ollivanders2 p, AsyncPlayerChatEvent event)
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
            return;
         }
      }
      else
      {
         event.getRecipients().clear();
         return;
      }
   }
}
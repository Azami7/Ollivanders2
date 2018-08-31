package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.ANIMAGUS_INCANTATION;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Consumed after successfully casting the Animagus incantation, this will turn a player in to an Animagus.
 *
 * @author Azami7
 */
public final class ANIMAGUS_POTION extends O2Potion
{
   public ANIMAGUS_POTION (Ollivanders2 plugin, O2PotionType potionType)
   {

      super(plugin, potionType);

      ingredients.put(Material.WATER_LILY, 1);
      ingredients.put(Material.CHORUS_FRUIT, 2);
      ingredients.put(Material.EYE_OF_ENDER, 1);
      ingredients.put(Material.SUGAR, 3);

      name = "Animagus Potion";
      text = "An Animagus is a wizard who elects to turn into an animal. This potion, if brewed and consumed correctly, " +
            "will transform the drinker in to their animal form. Thereafter, the Animagus can transform without the " +
            "potion, however it will take considerable practice to change forms consistently at will." + getIngredientsText();
      flavorText.add("\"You know that I can disguise myself most effectively.\" -Peter Pettigrew");
      flavorText.add("\"Normally, I have a very sweet disposition as a dog; in fact, more than once, James suggested I make the change permanent. The tail I could live with...but the fleas, they're murder.\" -Sirius Black");
   }

   @Override
   public void drink (O2Player o2p, Player player)
   {
      if (o2p.isAnimagus())
      {
         // they are already an Animagus so this has no effect
         return;
      }

      if (!player.getWorld().isThundering())
      {
         // potion only works in a thunderstorm
         player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
               + "Nothing seems to happen.");
         return;
      }

      for (O2Effect effect : o2p.getEffects())
      {
         if (effect instanceof ANIMAGUS_INCANTATION)
         {
            o2p.setIsAnimagus();
            o2p.animagusForm();

            player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
                  + "You feel transformed.");
         }
      }
   }
}

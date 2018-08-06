package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.Effects;
import net.pottercraft.Ollivanders2.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Relieve the symptoms of Lycanthropy
 *
 * @author Azami7
 * @author cakenggt
 */
public final class WOLFSBANE_POTION extends O2Potion
{
   public WOLFSBANE_POTION (Ollivanders2 plugin)
   {
      super(plugin);

      ingredients.put(Material.SPIDER_EYE, 2);
      ingredients.put(Material.ROTTEN_FLESH, 3);
      ingredients.put(Material.POISONOUS_POTATO, 1);
      ingredients.put(Material.SUGAR, 3);

      name = "Wolfsbane Potion";
      text = "This potion will relieve, though not cure, the symotoms of Lycanthropy. It is a complex potion and requires"
            + "the most advanced potion-making skills." + getIngredientsText();

      flavorText.add("\"There is no known cure, although recent developments in potion-making have to a great extent alleviated the worst symptoms.\" —Newton Scamander");
   }

   @Override
   public void drink (O2Player o2p, Player player)
   {
      if (!extendEffect(o2p))
      {
         o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.WOLFSBANE_POTION(player, Effects.WOLFSBANE_POTION, duration));
      }

      player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
            + "You feel a sense of relief.");
   }
}

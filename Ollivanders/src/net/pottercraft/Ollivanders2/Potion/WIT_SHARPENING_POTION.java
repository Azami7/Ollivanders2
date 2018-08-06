package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.Effects;
import net.pottercraft.Ollivanders2.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * The Wit-Sharpening Potion is a potion which allows the drinker to think more clearly. Due to this, it acts a a
 * counteragent to the Confundus Charm.
 *
 * @author Azami7
 */
public final class WIT_SHARPENING_POTION extends O2Potion
{
   //TODO add uses for this potion
   public WIT_SHARPENING_POTION (Ollivanders2 plugin)
   {
      super(plugin);

      ingredients.put(Material.BEETROOT, 2);
      ingredients.put(Material.FERMENTED_SPIDER_EYE, 1);
      ingredients.put(Material.GHAST_TEAR, 2);
      ingredients.put(Material.SUGAR, 2);

      name = "Wit-Sharpening Potion";
      text = "The Wit-Sharpening Potion is a potion which allows the drinker to think more clearly. Due to this, it acts"
            + "as a counteragent to the Confundus Charm." + getIngredientsText();
      flavorText.add("\"Some of you will benefit from today's assignment: Wit-Sharpening Potion. Perhaps you should begin immediately.\" -Severus Snape");
   }

   @Override
   public void drink (O2Player o2p, Player player)
   {
      if (!extendEffect(o2p))
      {
         o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.WIT_SHARPENING_POTION(player, Effects.WIT_SHARPENING_POTION, duration));
      }

      player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
            + "You feel ready to learn.");
   }
}

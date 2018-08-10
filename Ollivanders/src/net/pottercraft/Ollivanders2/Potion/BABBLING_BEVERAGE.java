package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.Effects;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Babbling Beverage is a potion that caused the drinker to babble nonsense.
 *
 * @since 2.2.7
 * @author Azami7
 */
public class BABBLING_BEVERAGE extends O2Potion
{
   public BABBLING_BEVERAGE (Ollivanders2 plugin)
   {
      super(plugin);

      ingredients.put(Material.VINE, 2);
      ingredients.put(Material.YELLOW_FLOWER, 1);
      ingredients.put(Material.WATER_LILY, 1);
      ingredients.put(Material.LAPIS_ORE, 2);
      ingredients.put(Material.SUGAR, 2);

      potionColor = Color.FUCHSIA;

      name = "Babbling Beverage";
      text = "Babbling Beverage is a potion that causes the drinker to babble nonsense." + getIngredientsText();
      flavorText.add("\"Potter, when I want nonsense shouted at me I shall give you a Babbling Beverage.\" -Severus Snape");
   }

   public void drink (O2Player o2p, Player player)
   {
      if (!extendEffect(o2p))
      {
         o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.BABBLING_EFFECT(player, Effects.BABBLING_EFFECT, duration));
      }

      player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
            + "You tongue feels fuzzy.");
   }
}
